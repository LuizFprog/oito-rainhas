package br.ufrpe.ce;

import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

public class EightQueensProblem extends AbstractIntegerPermutationProblem {
    private final int numberOfQueens;

    public EightQueensProblem(int numberOfQueens) {
        this.numberOfQueens = numberOfQueens;
    }

    @Override public int numberOfVariables() { return numberOfQueens; }
    @Override public int numberOfObjectives() { return 1; }
    @Override public int numberOfConstraints() { return 0; }
    @Override public String name() { return numberOfQueens + "-Queens"; }
    @Override public int length() { return numberOfQueens; }

    @Override
    public PermutationSolution<Integer> evaluate(PermutationSolution<Integer> solution) {
        solution.objectives()[0] = countDiagonalConflicts(solution);
        return solution;
    }

    public int countDiagonalConflicts(PermutationSolution<Integer> solution) {
        int conflicts = 0;
        for (int i = 0; i < numberOfQueens - 1; i++) {
            for (int j = i + 1; j < numberOfQueens; j++) {
                int rowi = solution.variables().get(i);
                int rowj = solution.variables().get(j);
                if(Math.abs(rowi - rowj) == j - i) {
                    conflicts++;
                }
            }
        }
        return conflicts;
    }
}