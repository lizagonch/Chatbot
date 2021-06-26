/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fcapackage;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.ListUtils;

/**
 *
 * @author Asus
 */
public class QueryRefinement {

    private ConceptLattice lattice;
    private ArrayList<FormalConcept> concepts;
    private ArrayList<FormalConcept> upConceptList; // upper-level concepts
    private DescriptionConceptLattice descrlattice;

    public QueryRefinement(ConceptLattice cl) {
        lattice = cl;
        concepts = cl.getLattice();
        upConceptList = new ArrayList<FormalConcept>();

    }

    public ArrayList<FormalConcept> getConcepts() {
        return concepts;
    }

    public ArrayList<FormalConcept> sortByDelta(ArrayList<FormalConcept> c) {
        //ArrayList<Double> measureList = new ArrayList<Double>();
        //for (FormalConcept concept : concepts) {
        //    measureList.add(concept.getIntLogStabilityUp());
        //}
        Collections.sort(c);
        return c;
    }

    //creating upper-level concepts
    public ArrayList<FormalConcept> buildUpperLevelConcepts(int h1, int h2) {
        for (FormalConcept concept : concepts) {
            double h = concept.getHomogeneity(lattice.getAttributesCount());
            int s = concept.getIntent().size();
            double h_d = h1 / (double) lattice.getAttributesCount();
            double h_u = h2 / (double) lattice.getAttributesCount();
            if (h >= h_d && h <= h_u) {
                upConceptList.add(concept);
            }
        }
        return upConceptList;
    }

    public ArrayList<FormalConcept> getMostGeneralConcept(List<FormalConcept> conc) {
        ArrayList<FormalConcept> fcList = new ArrayList<FormalConcept>();
        for (FormalConcept fc : conc) {
            if (fc.getParents().isEmpty()) {
                fcList.add(fc);
            }
        }
        return fcList;
    }

    public void specifyConcept(FormalConcept A_initial, FormalConcept A_parent, FormalConcept A, Set keyWords, ArrayList<FormalConcept> varF, ArrayList<FormalConcept> varF_new, HashMap<Integer, Integer> hashMap) {
        boolean fl;
        if (A.getCategories().containsAll(keyWords)) {
            //System.out.println(A.getCategories());
            HashSet<Integer> neighbors = (HashSet<Integer>) A.childs;
            for (Integer child : neighbors) {
                if (concepts.get(child).getExtent().size() > 0) {
                    //varF_new.add(concepts.get(child));
                    specifyConcept(A_initial, A, concepts.get(child), keyWords, varF, varF_new, hashMap);
                }
                hashMap.put(A_parent.position, A.position);
//                A.setModified();
//                varF_new.add(A);
//                if (varF.contains(A)) {
//                    if (varF.contains(A_parent)) {
//                        fl = varF.remove(A_parent);
//                        System.out.println(fl);
//                    }
//                } else {
//                    varF.add(A);
//                    if (varF.contains(A_parent)) {
//                        fl = varF.remove(A_parent);
//                        System.out.println(fl);
//                    }
//                }
            }

        }
//        else {
//            A.setModified();
//            varF_new.add(A);
//            if (varF.contains(A)) {
//                if (varF.contains(A_parent)) {
//                    fl = varF.remove(A_parent);
//                    System.out.println(fl);
//                }
//            } else {
//                varF.add(A);
//                if (varF.contains(A_parent)) {
//                    fl = varF.remove(A_parent);
//                    System.out.println(fl);
//                }
//            }
//        }
    }

    //Check
    public ArrayList navigateToConcept(Set keyWords, List<FormalConcept> varA) { // varA is a set of the most general concepts

        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();

        ArrayList<FormalConcept> varF = new ArrayList<FormalConcept>();//Concepts
//        for (FormalConcept fc : varA) {
//            varF.add(fc);
//        }
        //varF = (ArrayList<FormalConcept>) varA; //copy
        ArrayList<FormalConcept> varF_new = new ArrayList<FormalConcept>();
        HashSet<Integer> neighbors = new HashSet<Integer>();

        varF = getMostGeneralConcept(varA);

        for (FormalConcept A : varA) {
            if (A.getCategories().containsAll(keyWords)) {
                //System.out.println(A.getCategories());
                neighbors = (HashSet<Integer>) A.childs;
                for (Integer child : neighbors) {
                    //if (concepts.get(child).getIntent().size() > 0) {
                    //varF_new.add(concepts.get(child));

                    specifyConcept(A, A, concepts.get(child), keyWords, varF, varF_new, hashMap);
                    //}
                }
//                varF.addAll(varF_new);
//                varF.remove(A);
            }
        }

        for (Map.Entry<Integer, Integer> entry : hashMap.entrySet()) {
//            FormalConcept concept = concepts.get(entry.getKey());
//            int index = varF.indexOf(concept);
//            if (index != -1) {
//                if (varF.contains(concepts.get(entry.getValue()))) {
//                    varF.remove(concepts.get(entry.getKey()));
//                } else if (hashMap.containsKey(entry.getValue())) {
//                    varF.set(index, concepts.get(hashMap.get(entry.getValue())));
//                } else {
//                    varF.set(index, concepts.get(entry.getValue()));
//                }
//            }
////            else if (hashMap.containsKey(entry.getValue())) {
////                varF.add(concepts.get(hashMap.get(entry.getValue())));
////            }

            System.out.println(entry.getKey() + " = " + entry.getValue());
            if (hashMap.containsKey(entry.getValue())) {
                entry.setValue(hashMap.get(entry.getValue()));
            }
//            System.out.println("Mod");
//            System.out.println(entry.getKey() + " = " + entry.getValue());
            FormalConcept concept = concepts.get(entry.getKey());
            int index = varF.indexOf(concept);
            if (index != -1) {
                if (varF.contains(concepts.get(entry.getValue()))) {
                    varF.remove(concepts.get(entry.getKey()));
                } else {
                    varF.set(index, concepts.get(entry.getValue()));
                }
            }
//            else if (hashMap.containsKey(entry.getValue())) {
//                varF.add(concepts.get(hashMap.get(entry.getValue())));
//            }
        }

        return varF;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        Random random = new Random();
        LinkedHashSet<String> keyWords = new LinkedHashSet<String>();
        ArrayList<Double> V = new ArrayList<Double>();
        keyWords.add("Laptop");

        ConceptLattice cl = new ConceptLattice("Data_laptop.cxt", false);
        cl.setCategories("Categories.txt");

        DescriptionConceptLattice dc = new DescriptionConceptLattice();
        dc.readDescriptionContext("C:\\Users\\Елизавета\\Documents\\NetBeansProjects\\FCA\\FCA\\Data_tablet.csv", "C:\\Users\\Елизавета\\Documents\\NetBeansProjects\\FCA\\FCA\\Data_tablet.txt");

        dc.createLatticeFromContext(null);

        QueryRefinement qr = new QueryRefinement(cl);

        qr.refineQuery(V, dc);

        //ArrayList<FormalConcept> formalConcepts = qr.navigateToConcept(keyWords, qr.getConcepts());
        //printConceptListtoFile("navigatetoconcept.txt", formalConcepts);
        //int k = 0;

//        cl.getCategories();
//        FormalConcept fc = new FormalConcept();
//
//        do {
//            fc = cl.getLattice().get(random.nextInt(cl.getSize()));
//        } while (fc.getIntent().size() < 4);
//
//        keyWords.add("Skirt");
//        //keyWords.add(fc.getCategories().get(0));
//        //keyWords.add(fc.getCategories().get(1));
//
//        QueryRefinement qr = new QueryRefinement(cl);
//        ArrayList<FormalConcept> Mu = qr.buildUpperLevelConcepts(1, 2);
//        ArrayList<FormalConcept> varF = qr.navigateToConcept(keyWords, Mu);
//
//        System.out.println(keyWords);
//
//        printConceptListtoFile("Initial.txt", varF);
//        printConceptListtoFile("Modified.txt", Mu);
//
//        for (FormalConcept concept : varF) {
//            concept.printConcept();
//        }
//        System.out.println("New");
//        for (FormalConcept concept : Mu) {
//            concept.printConcept();
//        }
    }

//    public void printBinContext() {
//        for (int i = 0; i < binaryContext.length; i++) {
//            System.out.println(Arrays.toString(binaryContext[i]));
//        }
//    }
//
    //public FormalConcept findtheMostSpecificConcept(ArrayList<Double> V, ArrayList<Double> patterns) {
    //    
    //}
//    public void refineQuery(ArrayList<Double> V, LinkedHashSet<Integer> patterns) {
//        Boolean run = true;
//        ArrayList<Integer> relevant = new ArrayList<Integer>(); //set of relevant objects
//        ArrayList<Integer> irrelevant = new ArrayList<Integer>(); //attributes
//
//        ArrayList<FormalConcept> neighbors = new ArrayList<FormalConcept>();
//
//        ArrayList<FormalConcept> rankedFCList = new ArrayList<FormalConcept>();
//        FormalConcept specific
//                = //findtheMostSpecificConcept(V, patterns);
//                HashSet < Integer > indices = (HashSet<Integer>) specific.childs;
//        for (Integer child : indices) {
//            neighbors.add(conceptList.get(child));
//        }
//        rankedFCList = sortByDelta(neighbors);
//        FormalConcept cur = new FormalConcept();
//        while (run && rankedFCList.isEmpty() == true) {
//            cur = rankedFCList.get(rankedFCList.size() - 1);
//            rankedFCList.remove(rankedFCList.size() - 1);
//            relevant = cur;
//
//        }
//
//    }
    public ArrayList<Integer> isVaried(DescriptionConceptLattice cl, LinkedHashSet<Integer> objects, ArrayList<Description> attributes) {
        double n1 = 2;
        double n2 = 5;
        double b1 = 0.5;
        double b2 = 0.6;
        double v = 0.0;
        double rate;

        ArrayList<Integer> M_spec = new ArrayList<Integer>();
        ArrayList<Integer> nominal = new ArrayList<Integer>();
        ArrayList<Double> real = new ArrayList<Double>();

        HashMap<Integer, ArrayList<Description>> context = cl.getContext();

        int k;
        for (int j = 0; j < attributes.size(); j++) {
            int key = attributes.get(j).getKey();
            switch (key) {
                case 0:
                    for (Integer i : objects) {
                        real.add(context.get(i).get(j).getIntervalStart());
                    }
                    rate = Stat.calculateSD(real);
                    if (rate >= v) {
                        M_spec.add(attributes.get(j).getAttribute());
                    }
                    break;
                case 1:
                    for (Integer i : objects) {
                        nominal.addAll((ArrayList<Integer>) ListUtils.subtract(nominal, context.get(i).get(j).getNominal()));
                    }
                    rate = (double) nominal.size();
                    if (rate >= n1 && rate <= n2) {
                        M_spec.add(attributes.get(j).getAttribute());
                    }
                    break;
                case 2:
                    k = 0;
                    for (Integer i : objects) {
                        if (context.get(i).get(j).getBool().get(0) == 1) {
                            k++;
                        }
                    }
                    rate = (double) k / (double) objects.size();
                    if (rate >= b1 && rate <= b2) {
                        M_spec.add(attributes.get(j).getAttribute());
                    }
                    break;
            }
        }
        return M_spec;
    }

    public LinkedHashSet<Integer> refineQuery(ArrayList<Double> V, DescriptionConceptLattice patternConcepts) {
        Boolean run = true;

        ArrayList<Integer> irrelFeatures = new ArrayList<Integer>(); // irrelevant features
        ArrayList<Integer> features = new ArrayList<Integer>(); // features
        ArrayList<Integer> specFeatures = new ArrayList<Integer>(); // specified features

        ArrayList<Description> S = new ArrayList<Description>();

        LinkedHashSet<Integer> relevant = new LinkedHashSet<Integer>(); //set of relevant objects

        ArrayList<PatternConcept> lowerNeighbors = new ArrayList<PatternConcept>();

        ArrayList<PatternConcept> rankedPCList = new ArrayList<PatternConcept>();
        ArrayList<PatternConcept> rankedPCList_initial = new ArrayList<PatternConcept>();

        PatternConcept specific = patternConcepts.findtheMostSpecificConcept(V);

        HashMap<Integer, Description> Wspec = new HashMap<Integer, Description>();

        for (Integer i : specific.getChilds()) {
            lowerNeighbors.add(patternConcepts.getConceptList().get(i));
        }

        rankedPCList = patternConcepts.sortByDelta(lowerNeighbors);
        for (PatternConcept pc : rankedPCList) {
            rankedPCList_initial.add(pc);
        }

        PatternConcept cur = new PatternConcept();
        while (!rankedPCList_initial.isEmpty()) {
            System.out.println("New!");
            for (PatternConcept pc : rankedPCList_initial) {
                rankedPCList.add(pc);
            }
            rankedPCList_initial.remove(rankedPCList_initial.size() - 1);
            while (run && !rankedPCList.isEmpty()) {
                cur = rankedPCList.get(rankedPCList.size() - 1);
                rankedPCList.remove(rankedPCList.size() - 1);

                relevant = cur.getExtent();
                features = isVaried(patternConcepts, cur.getExtent(), cur.getIntent());
                features = (ArrayList<Integer>) ListUtils.subtract(features, irrelFeatures);
                for (Integer o : relevant) {
                    System.out.println(patternConcepts.getObjNames().get(o));
                }
                System.out.println(relevant + " Position = " + cur.getPosition());
                if (!features.isEmpty()) {

                    specFeatures = ask(features, patternConcepts);
                    irrelFeatures.addAll(specFeatures); //(ArrayList<Integer>) ListUtils.subtract(features, specFeatures);
                    for (Integer f : specFeatures) {
                        S.add(cur.getIntent().get(f));
                    }
                    Wspec = userChoice(S, patternConcepts);
                    if (!Wspec.isEmpty()) {
                        specific = patternConcepts.findMostGeneralConcept(Wspec, cur);
                        if (specific.getChilds().size() != 1) {
                            for (Integer i : specific.getChilds()) {
                                lowerNeighbors.add(patternConcepts.getConceptList().get(i));
                            }
                            //lowerNeighbors.add(specific);
                            rankedPCList = patternConcepts.sortByDelta(lowerNeighbors);
                        } else {
                            rankedPCList.clear();
                            rankedPCList.add(specific);
                        }
                    }
                }
            }
        }
        return relevant;
    }

    public ArrayList<Description> getValues(ArrayList<Integer> features, DescriptionConceptLattice dcl, PatternConcept cur) {
        ArrayList<Description> val = new ArrayList<Description>();
        for (Integer f : features) {
            val.add(cur.getIntent().get(f));
        }
        return val;
    }

    public ArrayList<Integer> ask(ArrayList<Integer> features, DescriptionConceptLattice dcl) {
        Scanner in = new Scanner(System.in);
        ArrayList<Integer> newFeatures = new ArrayList<Integer>();

        System.out.println("Do you want to specify some features from the list?");
        for (Integer f : features) {
            System.out.println(dcl.getAtNames().get(f));
        }
        String data = in.nextLine();
        String[] fArray = data.split(", ");

        for (int i = 0; i < fArray.length; i++) {
            newFeatures.add(dcl.getAtNames().indexOf(fArray[i]));
        }
        return newFeatures;
    }

    public HashMap<Integer, Description> userChoice(ArrayList<Description> S, DescriptionConceptLattice dcl) {
        Scanner in = new Scanner(System.in);
        HashMap<Integer, Description> newFeatures = new HashMap<Integer, Description>();
        ArrayList<Integer> list = new ArrayList<Integer>();

        System.out.println("Choose the values:");
        for (Description d : S) {
            System.out.print(dcl.getAtNames().get(d.getAttribute()) + " - ");
            System.out.println(d);
        }

        while (!in.hasNext("1")) {
            String data = in.nextLine();
            String[] fArray = data.split(" - ");

            int attr = dcl.getAtNames().indexOf(fArray[0]);
            Description d = DescriptionListUtils.findByAttribute(S, attr);

            if (d.getKey() == 0) {
                fArray[1] = fArray[1].replace("[", "");
                fArray[1] = fArray[1].replace("]", "");
                String[] t = fArray[1].split(", ");

                newFeatures.put(attr, new Description(d.getKey(), d.getAttribute(), Double.valueOf(t[0]), Double.valueOf(t[1])));
            }
            if (d.getKey() == 1 || d.getKey() == 2) {
                fArray[1] = fArray[1].replace("[", "");
                fArray[1] = fArray[1].replace("]", "");
                String[] t = fArray[1].split(", ");
                for (int i = 0; i < t.length; i++) {
                    list.add(Integer.valueOf(t[i]));
                }

                newFeatures.put(attr, new Description(d.getKey(), d.getAttribute(), list));
            }
        }
        return newFeatures;
    }

//        //my code
    public ArrayList<Integer> two_stageQueryRefinement(Set<String> keyWords, ArrayList<Double> values, ArrayList<FormalConcept> Mu) {
        ArrayList<FormalConcept> formalConcepts = navigateToConcept(keyWords, Mu);
        ArrayList<FormalConcept> formalConceptsOrdered;
        formalConceptsOrdered = sortByDelta(formalConcepts);
        ArrayList<Integer> A_new = new ArrayList();
        LinkedHashSet<Integer> A = new LinkedHashSet();
        int i = 0;
//        while (A_new.isEmpty()) {
//            A = formalConceptsOrdered.get(i).getExtent();
//            A_new = refineQuery(values, A);
//            i++;
//        }
        return A_new;
    }

    public static void printConceptListtoFile(String fileName, ArrayList<FormalConcept> fcList) {
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"));
            for (FormalConcept fc : fcList) {
                writer.write("Concept position:" + fc.getPosition() + "\n");
                writer.write("Concept intent:" + fc.getIntent() + "\n");
                writer.write("Concept extent:" + fc.getExtent() + "\n");
                writer.write("Concept parents:" + fc.getParents() + "\n");
                writer.write("Concept childs:" + fc.getChilds() + "\n");
                writer.write("Concept categories:" + fc.getCategories() + "\n");
                writer.write("--------------------" + "\n");
            }
            writer.flush();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(QueryRefinement.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QueryRefinement.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QueryRefinement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
