package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "testCases")
@Data
public class TestCase {
    private int problemId;
    private String input;
    private String output;

    public TestCase(int problemId, String input, String output) {
        this.problemId = problemId;
        this.input = input;
        this.output = output;
    }
    @Override
    public String toString() {
        return "TestCase{" +
                "input='" + input + '\'' +
                ", output='" + output + '\'' +
                ", problemId=" + problemId +
                '}';
    }
}