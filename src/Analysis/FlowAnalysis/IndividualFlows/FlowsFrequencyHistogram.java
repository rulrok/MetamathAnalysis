package Analysis.FlowAnalysis.IndividualFlows;

import Plot.Gnuplot;
import Plot.PlotData;
import Plot.PlotDataSet;
import Utils.HistogramUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.DoubleStream;

/**
 *
 * @author Reuel
 */
public class FlowsFrequencyHistogram {

    public static void main(String[] args) throws IOException {
        final Path inputFilePath = Paths.get("metamath-nouserboxes_halved_individual-flow_axiom-theorem_all_maxflows.txt");

        List<String> allLines = Files.readAllLines(inputFilePath);

        Map<Integer, Integer> frequencyHistogram = new TreeMap<>();
        allLines.forEach(line -> {
            String[] split = line.split(" ");
            Double flow = Double.parseDouble(split[split.length - 1]);

            Integer frequencyCount = frequencyHistogram.getOrDefault(flow.intValue(), 0);
            frequencyCount++;
            frequencyHistogram.put(flow.intValue(), frequencyCount);
        });

        HistogramUtils.PrintHistogram(frequencyHistogram);

        double[] xAxis = frequencyHistogram.entrySet().stream().flatMapToDouble((Entry<Integer, Integer> t) -> DoubleStream.of(t.getKey())).toArray();

        double[] yAxis = frequencyHistogram.entrySet().stream().flatMapToDouble((Entry<Integer, Integer> t) -> DoubleStream.of(t.getValue())).toArray();

        PlotDataSet plotDataSet = new PlotDataSet("Frequency histogram");
        PlotData plotData = new PlotData("Individual flows", xAxis, yAxis);
        plotDataSet.addData(plotData);
        Gnuplot gnuplot = new Gnuplot(plotDataSet);
        gnuplot
                .setFilename("frequency_histogram.png")
                .setxLabel("Flow size from source")
                .setyLabel("Flow count")
                .setyLogScale()
                .plot();
    }
}
