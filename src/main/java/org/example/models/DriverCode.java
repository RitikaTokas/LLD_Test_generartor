package org.example.models;
import org.example.enums.LanguageEnum;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "driverCode")
public class DriverCode {
    private int problemId;
    private LanguageEnum language;
    private FileLeaf mainFile;
    private FileLeaf problemInterface;

    public DriverCode(int problemId, LanguageEnum language, FileLeaf mainFile, FileLeaf problemInterface) {
        this.problemId = problemId;
        this.language = language;
        this.mainFile = mainFile;
        this.problemInterface = problemInterface;
    }

    public int getProblemId() {
        return problemId;
    }

    public LanguageEnum getLanguage() {
        return language;
    }

    public FileLeaf getMainFile() {
        return mainFile;
    }

    public FileLeaf getProblemInterface() {
        return problemInterface;
    }

    public void run() {
        // Entry point for input
        System.out.println("Running DriverCode for problem ID: " + problemId + " in language: " + language);
        System.out.println("Main file content: " + mainFile.getContent());
        System.out.println("Problem interface content: " + problemInterface.getContent());
    }
}