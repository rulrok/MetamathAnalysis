package Tests;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Reuel
 */
public class PlotDataSet {

    private String title;
    private final Map<String, PlotData> sets;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PlotDataSet(String title) {
        this.title = title;
        sets = new TreeMap<>();
    }

    public void addData(String title, double[] xAxis, double[] yAxis) {
        addData(new PlotData(title, xAxis, yAxis));
    }

    public Collection<PlotData> getValues() {
        return sets.values();
    }

    public void addData(PlotData data) {
        sets.put(data.title, data);
    }

    public void removeData(PlotData data) {
        sets.remove(data.title);
    }

    public void removeData(String title) {
        sets.remove(title);
    }

}
