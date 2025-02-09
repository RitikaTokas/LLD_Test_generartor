package org.example.service;

import org.example.models.Problem;
import org.example.repository.ProblemRepository;
import org.springframework.stereotype.Service;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    public Problem getProblemById(int problemId) {
        return problemRepository.findByProblemId(problemId);
    }

    public Problem saveProblem(Problem problem) {
        problem.setId(0);
        return problem;
    }
}
