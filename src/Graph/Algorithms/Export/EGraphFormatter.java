package Graph.Algorithms.Export;

import Graph.Algorithms.Export.Formatters.*;

/**
 *
 * @author Reuel
 */
public enum EGraphFormatter {

    SIMPLE(Simple.class), NAMES(GraphNames.class), HIPR(HIPR.class);

    private EGraphFormatter(Class<? extends IGraphFormatter> formatter) {
        this.formatter = formatter;
    }

    private final Class<? extends IGraphFormatter> formatter;

    public <T extends IGraphFormatter> T getFormatter() {
        try {
            return (T) formatter.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }
}
