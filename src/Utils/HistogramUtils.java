package Utils;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Reuel
 */
public class HistogramUtils {

    public static Integer SumKeyEntries(Map<Integer, Integer> histogram) {
        return histogram
                .entrySet()
                .stream()
                .collect(Collectors.summingInt(Entry<Integer, Integer>::getKey));
    }

    public static Integer SumValueEntries(Map<Integer, Integer> histogram) {
        return histogram
                .entrySet()
                .stream()
                .collect(Collectors.summingInt(Entry<Integer, Integer>::getValue));
    }

    public static void PrintHistogram(Map<Integer, Integer> histogram) {
        Entry<?, Integer> largestHistogramFoundEntry = FindMaxValue(histogram);
        Integer largestHistogramValue = largestHistogramFoundEntry.getValue();

        histogram.entrySet().stream().forEach((entry) -> {
            Integer key = entry.getKey();
            Integer value = entry.getValue();

            String column = "";

            for (int i = 0; i < ((double) value / largestHistogramValue) * 50.0; i++) {
                column = column.concat("#");
            }
            System.out.printf("%02d : % 5d %s\n", key, value, column);
        });
    }

    public static Entry<?, Integer> FindMaxValue(Map<?, Integer> histogram) {
        Optional<Entry<?, Integer>> collect = histogram.entrySet().stream()
                .collect(
                        Collectors.maxBy(Comparator.comparingInt((Entry<?, Integer> e) -> {
                            return e.getValue();
                        }))
                );

        return collect.orElse(null);
    }

    public static Entry<?, Integer> FindMinValue(Map<?, Integer> histogram) {
        Optional<Entry<?, Integer>> collect = histogram.entrySet().stream()
                .collect(
                        Collectors.minBy(Comparator.comparingInt((Entry<?, Integer> e) -> {
                            return e.getValue();
                        }))
                );

        return collect.orElse(null);
    }

    public static <K, V> Map<Integer, Integer> CreateHistogramFromMapBasedOn(Map<K, Integer> data) {
        return CreateHistogramFromMapBasedOn(data, (Integer i) -> {
            return i;
        });
    }

    public static <K, V> Map<Integer, Integer> CreateHistogramFromMapBasedOn(Map<K, V> data, Function<V, Integer> entryProcess) {

        Map<Integer, Integer> histogram = new TreeMap<>();

        data
                .entrySet()
                .stream()
                .forEach((Entry<K, V> entry) -> {
                    V value = entry.getValue();
                    final int valueEntry = entryProcess.apply(value);

                    histogram.putIfAbsent(valueEntry, 0);

                    int count = histogram.get(valueEntry);
                    count++;
                    histogram.put(valueEntry, count);

                });

        return histogram;
    }

    public static void PlotFrequencyHistogram(Map<?, Integer> data) {

        Entry<?, Integer> maxValueEntry = FindMaxValue(data);
        Entry<?, Integer> minValueEntry = FindMinValue(data);

        if (maxValueEntry == null || minValueEntry == null) {
            return;
        }

        double maxValue = maxValueEntry.getValue();
        double minValue = minValueEntry.getValue();

        double dataRange = maxValue - minValue;
        Integer dataClasses = (int) Math.sqrt(data.size());

        double classWidth = dataRange / dataClasses;

        int[] histogram = new int[dataClasses];

        data.entrySet().stream().forEach(e -> {
            Integer value = e.getValue();

            int index = (int) ((value / maxValue) * dataClasses);
            if (index < 0) {
                index = 0;
            } else if (index >= dataClasses) {
                index = dataClasses - 1;
            }

            histogram[index]++;

        });

        System.out.println("");
    }
}
