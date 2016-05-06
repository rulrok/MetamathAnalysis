package Plot;

import java.io.File;
import org.leores.plot.JGnuplot;
import org.leores.util.data.DataTableSet;

/**
 *
 * @author Reuel
 */
public class Gnuplot extends Plotter {

    public Gnuplot(PlotDataSet dataSet) {
        super(dataSet);
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

        //UPPER AND LOWER BOUNDARIES FOR AXES
        String xlb;
        String xub;
        xlb = (minxRange == Integer.MIN_VALUE) ? "*" : Integer.toString(minxRange);
        xub = (maxxRange == Integer.MAX_VALUE) ? "*" : Integer.toString(maxxRange);

        String ylb;
        String yub;
        ylb = (minyRange == Integer.MIN_VALUE) ? "*" : Integer.toString(minyRange);
        yub = (maxyRange == Integer.MAX_VALUE) ? "*" : Integer.toString(maxyRange);

        extraBuilder.append(String.format("set xrange[%s:%s]; ", xlb, xub));
        extraBuilder.append(String.format("set yrange[%s:%s]; ", ylb, yub));

        //LOG SCALE
        if (xLogScale) {
            extraBuilder.append("set log x; ");
        }
        if (yLogScale) {
            extraBuilder.append("set log y; ");
        }

        return extraBuilder.toString();
    }

    @Override
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
         * Remove any previous .plt file
         * jg.compile will append to the existing file rather than overwriting it
         */
        File file = new File(filename + ".plt");
        if (file.exists()) {
            file.delete();
        }

        /*
        * Plot graphics
         */
        jg.execute(plot, jg.plot2d);
        jg.compile(plot, jg.plot2d, filename + ".plt");
    }
}
