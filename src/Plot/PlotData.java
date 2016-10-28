package Plot;

/**
 *
 * @author Reuel
 */
public class PlotData {

    public final String title;
    public final double[] xAxis;
    public final double[] yAxis;

    public PlotData(String title, double[] xAxis, double[] yAxis) {
        this.title = title;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public PlotData(String title, int[] xAxis, int[] yAxis) {
        double[] newXAxis = new double[xAxis.length];
        double[] newYAxis = new double[yAxis.length];

        for (int i = 0; i < xAxis.length; i++) {
            newXAxis[i] = xAxis[i];
        }

        for (int i = 0; i < yAxis.length; i++) {
            newYAxis[i] = yAxis[i];
        }
        
        this.title = title;
        this.xAxis = newXAxis;
        this.yAxis = newYAxis;
    }

}
