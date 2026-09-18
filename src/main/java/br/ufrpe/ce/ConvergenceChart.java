package br.ufrpe.ce;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

/** Gera a curva de convergencia como PNG a partir das execucoes independentes. */
public final class ConvergenceChart {

  private ConvergenceChart() {}

  public static void savePng(List<ConvergenceObserver> observers, String title, String fileName)
      throws IOException {
    int points = observers.stream().mapToInt(o -> o.history().size()).min().orElse(0);

    List<Double> evaluations = new ArrayList<>();
    List<Double> bestCurve = new ArrayList<>();
    List<Double> meanCurve = new ArrayList<>();

    for (int i = 0; i < points; i++) {
      double best = 0;
      double mean = 0;
      for (ConvergenceObserver o : observers) {
        best += o.history().get(i).bestSoFar();
        mean += o.history().get(i).populationMean();
      }
      evaluations.add((double) observers.get(0).history().get(i).evaluations());
      bestCurve.add(best / observers.size());
      meanCurve.add(mean / observers.size());
    }

    XYChart chart = new XYChartBuilder()
        .width(900).height(560)
        .title(title)
        .xAxisTitle("Avaliacoes")
        .yAxisTitle("Conflitos (media de " + observers.size() + " execucoes)")
        .build();

    var styler = chart.getStyler();
    styler.setYAxisMin(0.0);
    styler.setChartTitleBoxVisible(false);
    styler.setPlotGridVerticalLinesVisible(false);
    styler.setChartBackgroundColor(Color.WHITE);
    styler.setPlotBackgroundColor(Color.WHITE);
    styler.setPlotBorderVisible(false);
    styler.setPlotGridLinesColor(new Color(225, 224, 217));
    styler.setLegendPosition(LegendPosition.InsideNE);
    styler.setLegendBorderColor(new Color(225, 224, 217));
    styler.setLegendBackgroundColor(Color.WHITE);
    styler.setAxisTickLabelsColor(new Color(137, 135, 129));
    styler.setChartFontColor(new Color(11, 11, 11));

    XYSeries bestSeries = chart.addSeries("Melhor da populacao", evaluations, bestCurve);
    bestSeries.setMarker(SeriesMarkers.NONE);
    bestSeries.setLineColor(new Color(42, 120, 214));
    bestSeries.setLineStyle(new BasicStroke(2f));

    XYSeries meanSeries = chart.addSeries("Media da populacao", evaluations, meanCurve);
    meanSeries.setMarker(SeriesMarkers.NONE);
    meanSeries.setLineColor(new Color(235, 104, 52));
    meanSeries.setLineStyle(new BasicStroke(2f));

    BitmapEncoder.saveBitmap(chart, fileName, BitmapFormat.PNG);
  }
}
