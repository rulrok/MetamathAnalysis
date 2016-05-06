package Plot;

/**
 *
 * @author Reuel
 */
public abstract class Plotter {

    protected final PlotDataSet dataSet;

    //strings
    protected String filename = "plot";
    protected String xLabel = "X axis";
    protected String yLabel = "Y axis";

    //ints
    protected int minxRange = Integer.MIN_VALUE;
    protected int maxxRange = Integer.MAX_VALUE;
    protected int minyRange = Integer.MIN_VALUE;
    protected int maxyRange = Integer.MAX_VALUE;

    //booleans
    protected boolean xLogScale = false;
    protected boolean yLogScale = false;

    public Plotter(PlotDataSet dataSet) {
        this.dataSet = dataSet;

        filename = dataSet.getTitle();
    }

    abstract public void plot();

    //<editor-fold defaultstate="collapsed" desc="Fluent setters">
    public Plotter setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public Plotter setxLabel(String xLabel) {
        this.xLabel = xLabel;
        return this;
    }

    public Plotter setyLabel(String yLabel) {
        this.yLabel = yLabel;
        return this;
    }

    public Plotter setxRange(int min, int max) {
        this.minxRange = min;
        this.maxxRange = max;
        return this;
    }

    public Plotter setyRange(int min, int max) {
        this.minyRange = min;
        this.maxyRange = max;
        return this;
    }

    public Plotter setxLogScale() {
        return setxLogScale(true);
    }

    public Plotter setxLogScale(boolean value) {
        this.xLogScale = value;
        return this;
    }

    public Plotter setyLogScale() {
        return setyLogScale(true);
    }

    public Plotter setyLogScale(boolean value) {
        this.yLogScale = value;
        return this;
    }
    //</editor-fold>

}
