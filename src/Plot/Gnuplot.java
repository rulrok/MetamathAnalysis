package Plot;

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
        * Plot graphics
         */
        jg.execute(plot, jg.plot2d);
        jg.compile(plot, jg.plot2d, "degree.plt");
    }
}
