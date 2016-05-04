package Plot;

import org.leores.plot.JGnuplot;
import org.leores.util.data.DataTableSet;

/**
 *
 * @author Reuel
 */
public class Gnuplot {

    public static void plot(String filename, String xLabel, String yLabel, PlotDataSet set) {
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

        DataTableSet dts = plot.addNewDataTableSet(set.getTitle());
        for (PlotData data : set.getValues()) {
            dts.addNewDataTable(data.title, data.xAxis, data.yAxis);
        }

        /*
        * Plot graphics
         */
        jg.execute(plot, jg.plot2d);
        jg.compile(plot, jg.plot2d, "degree.plt");
    }
}
