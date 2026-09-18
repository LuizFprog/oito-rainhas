package br.ufrpe.ce;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EightQueensProblemTest {
    private static final Integer NUMBER_OF_QUEENS = 8;
    private final EightQueensProblem problem = new EightQueensProblem(NUMBER_OF_QUEENS);

    private PermutationSolution<Integer> solutionOf(List<Integer> permutation) {
        PermutationSolution<Integer> solution = problem.createSolution();
        for (int i = 0; i < permutation.size(); i++) {
            solution.variables().set(i, permutation.get(i));
        }
        return solution;
    }

    @Test
    void mainDiagonalHasAllPairsInConflict() {
        var solution = solutionOf(List.of(0, 1, 2, 3, 4, 5, 6, 7));
        assertEquals(28, problem.countDiagonalConflicts(solution));
    }

    @Test
    void knowSolutionHasNoConflicts() {
        var solution = solutionOf(List.of(3, 1, 6, 2, 5, 7, 4, 0));
        assertEquals(0, problem.countDiagonalConflicts(solution));
    }

    @Test
    void evaluateWritesConflictsIntoObjectiveZero() {
        var solution = solutionOf(List.of(3, 1, 6, 2, 5, 7, 4, 0));
        problem.evaluate(solution);
        assertEquals(0.0, solution.objectives()[0]);
    }
}
