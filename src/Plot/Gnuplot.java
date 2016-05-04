package Plot;

import java.util.stream.IntStream;
import org.leores.plot.JGnuplot;
import org.leores.util.data.DataTableSet;

/**
 *
 * @author Reuel
 */
public class Gnuplot {

    private final PlotDataSet dataSet;

    private String filename = "plot";
    private String xLabel = "X axis";
    private String yLabel = "Y axis";
    private int minxRange = 0;
    private int maxxRange = 100;
    private int minyRange = 0;
    private int maxyRange = 100;
    private boolean xLogScale = false;
    private boolean yLogScale = false;

    public Gnuplot(PlotDataSet dataSet) {
        this.dataSet = dataSet;

        filename = dataSet.getTitle();

    }

    private String configureExtras() {
        StringBuilder extraBuilder = new StringBuilder();

        //RANGES
        if (xLogScale && minxRange <= 0) {
            minxRange = 1;
        }
        if (yLogScale && minyRange <= 0) {
            minyRange = 1;
        }
        extraBuilder.append(String.format("set xrange[%d:%d]; ", minxRange, maxxRange));
        extraBuilder.append(String.format("set yrange[%d:%d]; ", minyRange, maxyRange));

        //LOG SCALE
        if (xLogScale) {
            extraBuilder.append("set log x; ");
        }
        if (yLogScale) {
            extraBuilder.append("set log y; ");
        }

        return extraBuilder.toString();
    }

    public void plot() {

        String extraParams = configureExtras();

        JGnuplot jg = new JGnuplot() {
            {
                terminal = "pngcairo enhanced dashed";
                output = filename;
                extra = extraParams;
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

    public Gnuplot setxRange(int min, int max) {
        this.minxRange = min;
        this.maxxRange = max;
        return this;
    }

    public Gnuplot setyRange(int min, int max) {
        this.minyRange = min;
        this.maxyRange = max;
        return this;
    }

    public Gnuplot setxLogScale() {
        return setxLogScale(true);
    }

    public Gnuplot setxLogScale(boolean value) {
        this.xLogScale = value;
        return this;
    }

    public Gnuplot setyLogScale() {
        return setyLogScale(true);
    }

    public Gnuplot setyLogScale(boolean value) {
        this.yLogScale = value;
        return this;
    }
//</editor-fold>

}
