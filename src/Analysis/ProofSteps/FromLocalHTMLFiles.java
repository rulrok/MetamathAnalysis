package Analysis.ProofSteps;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author reuel
 */
public class FromLocalHTMLFiles {

    public static void main(String[] args) throws IOException {
        System.setErr(new PrintStream("errors.txt"));

        Path metamathSite = Paths.get("/home/reuel/Bureau/MetaMath/us.metamath.org/", "mpeuni");

        if (!Files.isDirectory(metamathSite)) {
            return;
        }

        File folder = new File(metamathSite.toString());

        System.out.println("Reading files...");
        File[] pages = folder.listFiles((File pathname) -> {
            return pathname.getName().endsWith(".html");
        });

        System.out.println("Reading individual pages...");
        for (File page : pages) {

            try {
                Document doc = Jsoup.parse(page, "utf-8");

                String title = doc.title();
                if (title.toLowerCase().contains("mathbox")) {
                    continue;
                }

                Element headTitle = doc.select("body hr + center").first();

                String typeOfElement = headTitle.select("font").html().toLowerCase();
                if (!typeOfElement.contains("theorem")) {
                    continue;
                }

                String elementName = headTitle.select("font font").html();
                String pageElementName = page.getName().replace(".html", "");

                if (!pageElementName.equals(elementName)) {
                    continue;
                }

                final Element centerElem = doc
                        .body()
                        .select("center span")
                        .first();
                String id = centerElem.html();

                final Element lastRowElem = doc
                        .select("table[summary=\"Proof of theorem\"]")
                        .select("tbody")
                        .select("tr")
                        .last()
                        .select("td")
                        .first();
                String lastStep = lastRowElem.html();

                System.out.print(elementName);

                Integer.parseInt(id);
                Integer.parseInt(lastStep);

                System.out.print("  " + id + " " + lastStep);
            } catch (IOException ex) {
                System.err.println("Oops...");
            } catch (NullPointerException ex) {
                System.err.println("Falha ao processar página" + page.getName());
            } catch (NumberFormatException ex) {
                System.err.println("Number format");
            } finally {
                System.out.println("");
            }

        }
    }

}
