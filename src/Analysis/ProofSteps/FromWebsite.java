package Analysis.ProofSteps;

import Graph.GraphFactory;
import Graph.Label;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author reuel
 */
public class FromWebsite {

    public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
        System.setErr(new PrintStream("errors.txt"));

        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();

        System.out.println("Reading individual pages...");

        try (Transaction tx = graph.beginTx(); FileWriter fw = new FileWriter("theorems_steps_crawl.txt")) {

            fw.write("node_id;node_name;proof_steps" + System.lineSeparator());

            ResourceIterator<Node> theoremsIter = graph.findNodes(Label.THEOREM);
            for (; theoremsIter.hasNext();) {

                Node theorem = theoremsIter.next();
                String theoremName = theorem.getProperty("name").toString();

                try {

                    Document doc = Jsoup.connect("http://us.metamath.org/mpeuni/" + theoremName + ".html").get();

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
                    if (!theoremName.equals(elementName)) {
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
                    fw.write(String.join(";", id, elementName, lastStep) + System.lineSeparator());

                } catch (NullPointerException ex) {
                    System.err.println("Falha ao processar teorema " + theoremName);
                } catch (NumberFormatException ex) {
                    System.err.println("Number format for " + theoremName);
                } catch (SocketTimeoutException ex) {
                    System.err.println("Timeout for " + theoremName);
                } catch (MalformedURLException ex) {
                    System.err.println("URL problem for " + theoremName);
                } catch (IOException ex) {
                    System.err.println("IO Problem for " + theoremName);
                } finally {
                    System.out.println("");
                }

            } //for each theorem from neo4j

            tx.failure();
        } catch (Exception ex) {
            System.err.println("Neo4j exception" + ex);
        }
    }
}
