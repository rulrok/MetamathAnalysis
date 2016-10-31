/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests.Traversals;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author reuel
 */
public class randomValuesHistogram {

    public static void main(String[] args) {
        try (FileWriter fw = new FileWriter("rand_values.txt")) {
            Random r = new Random(2011_1_08_021);

            for (int i = 0; i < 1000; i++) {
                fw.write(Double.toString(r.nextGaussian()).concat("\n"));
            }
        } catch (IOException ex) {
            Logger.getLogger(randomValuesHistogram.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
