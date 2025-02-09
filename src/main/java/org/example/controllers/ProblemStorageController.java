package org.example.controllers;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.enums.DifficultyEnum;
import org.example.enums.LanguageEnum;
import org.example.models.*;
import org.example.repository.DriverCodeRepository;
import org.example.repository.TestCaseRepository;
import org.example.service.ProblemService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProblemStorageController {

    private final ProblemService problemService;
    private final TestCaseRepository testCaseRepository;
    private final DriverCodeRepository driverCodeRepository;

    ProblemStorageController(ProblemService problemService, TestCaseRepository testCaseRepository,
                             DriverCodeRepository driverCodeRepository) {
        this.problemService = problemService;
        this.testCaseRepository = testCaseRepository;
        this.driverCodeRepository = driverCodeRepository;
    }
    @PostMapping("/storeProblem")
    public String storeProblem(
            @RequestParam("jsonRequest") MultipartFile jsonRequestFile,
            @RequestParam("inputCsv") MultipartFile inputCsvFile) {
        try {
            String jsonRequest = new String(jsonRequestFile.getBytes(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonRequest);

            String problemStatement = rootNode.get("problemStatement").asText();
            FileComponent idealSolution = parseFileComponent(rootNode.get("idealSolution"));
            FileComponent driverCode = parseFileComponent(rootNode.get("driverCode"));

            // Save Problem
            Problem problem = new Problem(problemStatement, DifficultyEnum.EASY, new ArrayList<>(), new ArrayList<>());
            problem = problemService.saveProblem(problem);

            // Save DriverCode
            FileLeaf mainFile = findMainFile(driverCode);
            FileLeaf problemInterface = findProblemInterface(driverCode);
            DriverCode driverCodeEntity = new DriverCode(problem.getId(), LanguageEnum.JAVA, mainFile, problemInterface);
            driverCodeRepository.save(driverCodeEntity);

            // Read Input CSV
            List<String> inputCsv = readCsv(inputCsvFile);

            // Save code to temp directory
            File tempDir = new File("temp/" + problem.getId());
            tempDir.mkdirs();
            saveFileStructure(rootNode.get("idealSolution"), tempDir);
            saveFileStructure(rootNode.get("driverCode"), tempDir);

            // Execute in Docker container
            List<String> outputCsv = executeSolution(tempDir, inputCsv);

            // Save test cases
            for (int i = 0; i < outputCsv.size(); i++) {
                testCaseRepository.save(new TestCase(problem.getId(), inputCsv.get(i), outputCsv.get(i)));
            }

            // Cleanup
            deleteDirectory(tempDir);

            return "{\"message\": \"Problem stored successfully\"}";
        } catch (Exception e) {
            return "{\"message\": \"Error: " + e.getMessage() + "\"}";
        }
    }

    private FileLeaf findProblemInterface(FileComponent driverCode) {
        if (driverCode instanceof FileLeaf && driverCode.getName().equals("ProblemInterface.java")) {
            return (FileLeaf) driverCode;
        }
        if (driverCode instanceof DirectoryComposite) {
            for (FileComponent child : ((DirectoryComposite) driverCode).getChildren()) {
                FileLeaf problemInterfaceFile = findProblemInterface(child);
                if (problemInterfaceFile != null) {
                    return problemInterfaceFile;
                }
            }
        }
        return null;
    }

    private FileLeaf findMainFile(FileComponent driverCode) {
        if (driverCode instanceof FileLeaf && driverCode.getName().equals("Main.java")) {
            return (FileLeaf) driverCode;
        }
        if (driverCode instanceof DirectoryComposite) {
            for (FileComponent child : ((DirectoryComposite) driverCode).getChildren()) {
                FileLeaf mainFile = findMainFile(child);
                if (mainFile != null) {
                    return mainFile;
                }
            }
        }
        throw new RuntimeException("Main.java file not found in driver code");
    }

    private List<String> executeSolution(File tempDir, List<String> inputCsv) throws IOException {
        List<String> outputCsv = new ArrayList<>();
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        try {
            for (String input : inputCsv) {
                // Write input to file
                Files.write(Paths.get(tempDir.getAbsolutePath(), "input.txt"), input.getBytes());

                // Create and start container
                CreateContainerResponse container = dockerClient.createContainerCmd("code-sandbox")
                        .withBinds(new Bind(tempDir.getAbsolutePath(), new Volume("/app")))
                        .withNetworkDisabled(true)
                        .withMemory(256 * 1024 * 1024L)
                        .withHostConfig(new HostConfig().withCpuQuota(100000L))
                        .exec();

                dockerClient.startContainerCmd(container.getId()).exec();
                dockerClient.waitContainerCmd(container.getId()).start().awaitCompletion();

                // Read output
                String output = new String(Files.readAllBytes(Paths.get(tempDir.getAbsolutePath(), "output.txt")));
                outputCsv.add(output);

                // Cleanup
                dockerClient.removeContainerCmd(container.getId()).exec();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error executing solution: " + e.getMessage());
        } finally {
            dockerClient.close();
        }

        return outputCsv;
    }

    private FileComponent parseFileComponent(JsonNode fileNode) {
        if (fileNode.get("type").asText().equals("file")) {
            return new FileLeaf(fileNode.get("name").asText(), fileNode.get("content").asText());
        } else {
            DirectoryComposite dir = new DirectoryComposite(fileNode.get("name").asText());
            for (JsonNode child : fileNode.get("children")) {
                dir.add(this.parseFileComponent(child));
            }
            return dir;
        }
    }

    private void saveFileStructure(JsonNode fileNode, File parent) throws IOException {
        if (fileNode.get("type").asText().equals("file")) {
            File file = new File(parent, fileNode.get("name").asText());
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(fileNode.get("content").asText());
            }
        } else {
            File dir = new File(parent, fileNode.get("name").asText());
            dir.mkdirs();
            for (JsonNode child : fileNode.get("children")) {
                saveFileStructure(child, dir);
            }
        }
    }

    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }
    private List<String> readCsv(MultipartFile csvFile) throws IOException {
        List<String> rows = new ArrayList<>();
        try (Reader reader = new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT)) {
            for (CSVRecord record : parser) {
                rows.add(String.join(",", record));
            }
        }
        return rows;
    }

}