package org.example.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.example.enums.DifficultyEnum;
import java.util.List;

@Document(collection = "problems")
public class Problem {
    @Id
    private int problemId;
    private String problemStatement;
    private DifficultyEnum difficulty;
    List<Tag> companyTags;
    List<Tag> problemTypeTags;

    public Problem(
     String problemStatement,
     DifficultyEnum difficulty,
    List<Tag> companyTags,
    List<Tag> problemTypeTags
    ){
        this.problemStatement = problemStatement;
        this.difficulty = difficulty;
        this.companyTags = companyTags;
        this.problemTypeTags = problemTypeTags;
    }
    public int getId() {
        return problemId;
    }
    public void setId(int id){
        problemId = id;
    }
}
