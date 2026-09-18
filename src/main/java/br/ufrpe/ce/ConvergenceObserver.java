package br.ufrpe.ce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;

public class ConvergenceObserver implements Observer<Map<String, Object>> {

    public record Point(int evaluations, double bestSoFar, double populationMean) {}

    private final List<Point> history = new ArrayList<>();
    private double bestSoFar = Double.MAX_VALUE;

    @Override
    @SuppressWarnings("unchecked")
    public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
        Integer evaluations = (Integer) data.get("EVALUATIONS");
        List<Solution<?>> population = (List<Solution<?>>) data.get("POPULATION");
        if (evaluations == null || population == null || population.isEmpty()) return;

        double best = population.stream().mapToDouble(s -> s.objectives()[0]).min().orElse(Double.MAX_VALUE);
        double mean = population.stream().mapToDouble(s -> s.objectives()[0]).average().orElse(Double.NaN);

        bestSoFar = Math.min(bestSoFar, best);
        history.add(new Point(evaluations, bestSoFar, mean));
    }

    public List<Point> history() { return history; }

    public int evaluationsToReach(double targetFitness) {
        for (Point p : history) {
            if (p.bestSoFar() <= targetFitness) return p.evaluations();
        }
        return -1;
    }

    public String getName() { return "Convergence observer"; }
}