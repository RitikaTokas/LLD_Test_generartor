package org.example.models;
import lombok.Getter;
import org.example.enums.LanguageEnum;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "idealSolution")
@Getter
public class IdealSolution {
    private int problemId;
    private LanguageEnum language;
    private FileComponent solution;

    public void run() {
        // Entry point for input
        System.out.println("Running DriverCode for problem ID: " + problemId + " in language: " + language);
    }

    public IdealSolution(int problemId, LanguageEnum language, FileComponent solution) {
        this.problemId = problemId;
        this.language = language;
        this.solution = solution;
    }
    public int getProblemId() {
        return problemId;
    }
    public LanguageEnum getLanguage() {
        return language;
    }
    public FileComponent getSolution() {
        return solution;
    }
}