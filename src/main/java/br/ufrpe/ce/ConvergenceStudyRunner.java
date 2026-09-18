package br.ufrpe.ce;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.singleobjective.GeneticAlgorithmBuilder;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
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

public class ConvergenceStudyRunner {

    static int queens = 8;
    static int populationSize = 100;
    static final int OFFSPRING_SIZE = 2;
    static final int TOURNAMENT_SIZE = 5;
    static int maxEvaluations = 10000;
    static final int INDEPENDENT_RUNS = 30;

    public static void main(String[] args) throws IOException {
        if (args.length >= 1) queens = Integer.parseInt(args[0]);
        if (args.length >= 2) populationSize = Integer.parseInt(args[1]);
        if (args.length >= 3) maxEvaluations = Integer.parseInt(args[2]);
        String csv = args.length >= 4 ? args[3] : "convergencia.csv";

        List<ConvergenceObserver> observers = new ArrayList<>();
        List<Integer> evaluationsToSolve = new ArrayList<>();

        for (int run = 0; run < INDEPENDENT_RUNS; run++) {
            ConvergenceObserver observer = new ConvergenceObserver();
            EvolutionaryAlgorithm<PermutationSolution<Integer>> algorithm = buildAlgorithm();
            algorithm.observable().register(observer);
            algorithm.run();
            observers.add(observer);
            int evals = observer.evaluationsToReach(0.0);
            if (evals >= 0) evaluationsToSolve.add(evals);
        }

        int solved = evaluationsToSolve.size();
        double mean = evaluationsToSolve.stream().mapToInt(Integer::intValue).average().orElse(Double.NaN);
        double sd = Math.sqrt(evaluationsToSolve.stream()
                .mapToDouble(e -> (e - mean) * (e - mean)).sum() / Math.max(1, solved - 1));

        System.out.printf(Locale.ROOT, "%d rainhas | pop %d | %d avaliacoes | %d execucoes%n",
                queens, populationSize, maxEvaluations, INDEPENDENT_RUNS);
        System.out.printf(Locale.ROOT, "  taxa de sucesso ...........: %.1f%% (%d/%d)%n",
                100.0 * solved / INDEPENDENT_RUNS, solved, INDEPENDENT_RUNS);
        System.out.printf(Locale.ROOT, "  avaliacoes ate 0 conflitos : media %.1f, desvio %.1f%n", mean, sd);

        writeAveragedCurve(observers, Path.of(csv));

        String png = csv.endsWith(".csv") ? csv.substring(0, csv.length() - 4) : csv;
        ConvergenceChart.savePng(observers,
            queens + " rainhas, populacao " + populationSize, png);

        System.out.println("  curva media ...............: " + csv);
    }

    static EvolutionaryAlgorithm<PermutationSolution<Integer>> buildAlgorithm() {
        var problem = new EightQueensProblem(queens);
        CrossoverOperator<PermutationSolution<Integer>> crossover = new PMXCrossover(0.9);
        MutationOperator<PermutationSolution<Integer>> mutation = new PermutationSwapMutation<>(1.0 / queens);
        Variation<PermutationSolution<Integer>> variation =
                new CrossoverAndMutationVariation<>(OFFSPRING_SIZE, crossover, mutation);

        return new GeneticAlgorithmBuilder<>("GA", problem, populationSize, OFFSPRING_SIZE, crossover, mutation)
                .setVariation(variation)
                .setSelection(new NaryTournamentSelection<>(
                        TOURNAMENT_SIZE, variation.matingPoolSize(), new ObjectiveComparator<>(0)))
                .setReplacement(new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0)))
                .setTermination(new TerminationByEvaluations(maxEvaluations))
                .build();
    }

    static void writeAveragedCurve(List<ConvergenceObserver> observers, Path file) throws IOException {
        int points = observers.stream().mapToInt(o -> o.history().size()).min().orElse(0);
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            out.println("avaliacoes,melhor_conflitos,media_populacao");
            for (int i = 0; i < points; i++) {
                int evaluations = observers.get(0).history().get(i).evaluations();
                double best = 0, popMean = 0;
                for (ConvergenceObserver o : observers) {
                    best += o.history().get(i).bestSoFar();
                    popMean += o.history().get(i).populationMean();
                }
                out.printf(Locale.ROOT, "%d,%.4f,%.4f%n",
                        evaluations, best / observers.size(), popMean / observers.size());
            }
        }
    }
}