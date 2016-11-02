package Utils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author reuel
 */
public class OpenMetamathLinks {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        String names = "emcllem2, eflegeo, lemulge12, lemulge11";
        String[] pages = names.split(",");

        for (String page : pages) {
            Thread.sleep(300);
            URI site = new URI("http://us.metamath.org/mpeuni/" + page.trim() + ".html");
            Desktop.getDesktop().browse(site);
        }
    }

}
