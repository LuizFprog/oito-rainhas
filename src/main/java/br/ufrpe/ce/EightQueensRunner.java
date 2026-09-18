package br.ufrpe.ce;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.singleobjective.GeneticAlgorithmBuilder;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.MuPlusLambdaReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.observer.impl.FitnessPlotObserver;

public class EightQueensRunner {
    public static void main(String[] args) {
        int numberOfQueens = 8;
        int populationSize = 100;
        int offspringPopulationSize = 2;
        int tournamentSize = 5;
        int maxEvaluations = 10000;
        double crossoverProbability = 0.9;
        double mutationProbability = 1.0 / numberOfQueens;

        var problem = new EightQueensProblem(numberOfQueens);

        CrossoverOperator<PermutationSolution<Integer>> crossover = new PMXCrossover(crossoverProbability);
        MutationOperator<PermutationSolution<Integer>> mutation = new PermutationSwapMutation<>(mutationProbability);
        Variation<PermutationSolution<Integer>> variation =
                new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);
        EvolutionaryAlgorithm<PermutationSolution<Integer>> algorithm =
                new GeneticAlgorithmBuilder<>(
                        "GA", problem, populationSize, offspringPopulationSize, crossover, mutation)
                        .setVariation(variation)
                        .setSelection(new NaryTournamentSelection<>(
                                tournamentSize, variation.matingPoolSize(), new ObjectiveComparator<>(0)
                        ))
                        .setReplacement(new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0)))
                        .build();
        algorithm.run();
        PermutationSolution<Integer> best = algorithm.result().get(0);

        System.out.println("Avaliacoes ....: " + algorithm.numberOfEvaluations());
        System.out.println("Tempo (ms) ....: " + algorithm.totalComputingTime());
        System.out.println("Conflitos .....: " + (int) best.objectives()[0]);
        System.out.println("Permutacao ....: " + best.variables());
        System.out.println();
        System.out.print(BoardPrinter.render(best.variables()));
        algorithm.observable().register(new FitnessPlotObserver<PermutationSolution<Integer>>(
                "Convergência", "Avaliações", "Conflitos", "melhor fitness", 100));
    }
}
