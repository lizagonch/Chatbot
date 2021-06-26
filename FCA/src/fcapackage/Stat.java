/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fcapackage;

import java.util.List;

/**
 *
 * @author Asus
 */
public class Stat {

    public static double calculateSD(List<Double> numArray) {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.size();
        for (double num : numArray) {
            sum += num;
        }
        double mean = sum / length;
        for (double num : numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        return Math.sqrt(standardDeviation / length);
    }
}
