package Plot;

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

    public PlotDataSet(String title) {
        this.title = title;
        sets = new TreeMap<>();
    }

//<editor-fold defaultstate="collapsed" desc="getters and setters">
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Add and remove data">
    public void addData(String title, double[] xAxis, double[] yAxis) {
        addData(new PlotData(title, xAxis, yAxis));
    }
    
    public void addData(PlotData data) {
        sets.put(data.title, data);
    }
    
    public PlotData removeData(PlotData data) {
        return sets.remove(data.title);
    }
    
    public PlotData removeData(String title) {
        return sets.remove(title);
    }
//</editor-fold>

    public Collection<PlotData> getValues() {
        return sets.values();
    }

}
