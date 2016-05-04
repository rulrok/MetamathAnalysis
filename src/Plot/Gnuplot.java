package Plot;

import org.leores.plot.JGnuplot;
import org.leores.util.data.DataTableSet;

/**
 *
 * @author Reuel
 */
public class Gnuplot {

    private String filename;
    private String xLabel;
    private String yLabel;
    private final PlotDataSet dataSet;

    public Gnuplot(PlotDataSet set) {
        dataSet = set;

    }

    public void plot() {
        JGnuplot jg = new JGnuplot() {
            {
                terminal = "pngcairo enhanced dashed";
                output = filename;
                extra = "set xrange[0:500]; set yrange[0:1000];";
            }
        };
        JGnuplot.Plot plot = new JGnuplot.Plot("") {
            {
                xlabel = xLabel;
                ylabel = yLabel;
            }
        };

        DataTableSet dts = plot.addNewDataTableSet(dataSet.getTitle());
        dataSet.getValues().stream().forEach((data) -> {
            dts.addNewDataTable(data.title, data.xAxis, data.yAxis);
        });

        /*
        * Plot graphics
         */
        jg.execute(plot, jg.plot2d);
        jg.compile(plot, jg.plot2d, "degree.plt");
    }

//<editor-fold defaultstate="collapsed" desc="Fluent setters">
    public Gnuplot setFilename(String filename) {
        this.filename = filename;
        return this;
    }
    
    public Gnuplot setxLabel(String xLabel) {
        this.xLabel = xLabel;
        return this;
    }
    
    public Gnuplot setyLabel(String yLabel) {
        this.yLabel = yLabel;
        return this;
    }
//</editor-fold>

}
