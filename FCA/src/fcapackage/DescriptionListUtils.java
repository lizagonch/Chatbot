/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fcapackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.collections.ListUtils;

/**
 *
 * @author Asus
 */
public class DescriptionListUtils {

    public static Description findByAttribute(final ArrayList<Description> list, int attr) {
        for (Description d : list) {
            if (d.getAttribute() == attr) {
                return d;
            }
        }
        return null;
    }

    public static ArrayList<Description> intersect(final ArrayList<Description> list1, final ArrayList<Description> list2) {

        final ArrayList<Description> result = new ArrayList<Description>();

        for (int i = 0; i < list1.size(); i++) {
            result.add(list2.get(i).intersection(list1.get(i)));
        }

        return result;
    }

    public static boolean containsAll(final ArrayList<Description> list1, final ArrayList<Description> list2) {

        if (list1.size() != list2.size()) {
            return false;
        }

        int key;
        int k = 0;

        for (int i = 0; i < list1.size(); i++) {
            key = list1.get(i).getKey();
            switch (key) {
                case 0:
                    if (list1.get(i).getIntervalStart() >= list2.get(i).getIntervalStart() && list1.get(i).getIntervalEnd() <= list2.get(i).getIntervalEnd()) {
                        k++;
                    } else {
                        return false;
                    }
                    break;
                case 1:
                    if (list1.get(i).getNominal().containsAll(list2.get(i).getNominal())) {
                        k++;
                    } else {
                        return false;
                    }
                    break;
                case 2:
                    if (list1.get(i).getBool().containsAll(list2.get(i).getBool())) {
                        k++;
                    } else {
                        return false;
                    }
                    break;
            }

        }
        return k == list1.size();
    }

    public static void main(String[] args) {
        ArrayList<Description> list1 = new ArrayList<Description>();
        ArrayList<Integer> nominal = new ArrayList<Integer>();
        nominal.add(1);
        nominal.add(0);
        nominal.add(2);
        nominal.add(3);
        ArrayList<Integer> bool = new ArrayList<Integer>();
        bool.add(1);

        list1.add(new Description(0, 0, 3.6));
        list1.add(new Description(2, 1, bool));
        list1.add(new Description(1, 2, nominal));

        ArrayList<Description> list2 = new ArrayList<Description>();
        ArrayList<Integer> nominal1 = new ArrayList<Integer>();
        nominal1.add(1);
        nominal1.add(2);
        nominal1.add(0);
        nominal1.add(3);
        nominal1.add(4);
        ArrayList<Integer> bool1 = new ArrayList<Integer>();
        bool1.add(0);
        list2.add(new Description(0, 0, 4.5));
        list2.add(new Description(2, 1, bool1));
        list2.add(new Description(1, 2, nominal1));

        System.out.println("List1 " + list1);
        System.out.println("List2 " + list2);

        ArrayList<Description> intersection = DescriptionListUtils.intersect(list1, list2);
        System.out.println("Intersection " + intersection);

        System.out.println(intersection.contains(list2.get(1)));

        ArrayList<Integer> l = nominal1;
        l.addAll((ArrayList<Integer>) ListUtils.subtract(nominal, l));

        Scanner in = new Scanner(System.in);
        HashMap<Integer, Description> newFeatures = new HashMap<Integer, Description>();

        System.out.println("Choose the values:");

        while (!in.hasNext("1")) {
            String data = in.nextLine();
            String[] fArray = data.split(" - ");

            fArray[1] = fArray[1].replace("[", "");
            fArray[1] = fArray[1].replace("]", "");
            String[] t = fArray[1].split(", ");
            for (int k = 0; k < t.length; k++) {
                System.out.println(t[k]);
            }
        }
    }
}
