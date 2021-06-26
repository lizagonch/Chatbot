/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fcapackage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.collections.ListUtils;

/**
 *
 * @author Asus
 */
public class DescriptionConceptLattice {

    int objectCount;
    int attributeCount;
    ArrayList<PatternConcept> conceptList;
    int[][] binaryContext;
    HashMap<Integer, ArrayList<Description>> context; // key - object, value - description list
    static Measures ms;
    static BasicLevelMetrics blm;
    private ArrayList<String> objNames;
    private ArrayList<String> atNames;

    public DescriptionConceptLattice(PatternConcept topConcept, int objCount, int attrCount, HashMap<Integer, ArrayList<Description>> context, boolean stats) {
        this.objectCount = objCount;
        this.attributeCount = attrCount;
        //this.binaryContext = binaryContext;
        this.context = context;
        this.conceptList = new ArrayList<PatternConcept>();
        PatternConcept bottom = new PatternConcept();
        bottom.setPosition(0);
        conceptList.add(bottom);
        //ms = new Measures(this);
        //blm = new BasicLevelMetrics(this);
//        if (this.binaryContext != null) {
//            createLatticeFromBinaryContext();
//        }
        if (this.context != null) {
            createLatticeFromContext(topConcept);
        }
        if (stats) {
            ms.logStabilityExt();
            ms.separation();
            ms.probability();
            blm.cueValidity();
            blm.categoryFeatureCollocation();
            blm.categoryUtility();
            blm.predictability();
            blm.similarityGoguenNorm();
        }
    }

    public DescriptionConceptLattice() {
        this.conceptList = new ArrayList<PatternConcept>();
        atNames = new ArrayList<String>();
        objNames = new ArrayList<String>();
        this.conceptList = new ArrayList<PatternConcept>();
        PatternConcept bottom = new PatternConcept();
        bottom.setPosition(0);
        conceptList.add(bottom);
        context = new HashMap<Integer, ArrayList<Description>>();
    }

//    public DescriptionConcept(String filename, boolean stats) throws FileNotFoundException, IOException {
//
//        FcaReader fr = new FcaReader();
//        fr.ReadContextFromCxt(filename);
//        this.objectCount = fr.getObjectsCount();
//        this.attributeCount = fr.getAttributesCount();
//        this.binaryContext = fr.getBinaryContext();
//
//        this.conceptList = new ArrayList<FormalConcept>();
//        FormalConcept bottom = new FormalConcept();
//        bottom.setPosition(0);
//        conceptList.add(bottom);
//        ms = new Measures(this);
//        blm = new BasicLevelMetrics(this);
//        if (this.binaryContext != null) {
//            createLatticeFromBinaryContext();
//        }
//        if (stats) {
//            ms.logStabilityExt();
//            ms.separation();
//            ms.probability();
//            blm.cueValidity();
//            blm.categoryFeatureCollocation();
//            blm.categoryUtility();
//            blm.predictability();
//            blm.similarityGoguenNorm();
//        }
//    }
    public ArrayList<String> getAtNames() {
        return atNames;
    }

    public ArrayList<String> getObjNames() {
        return objNames;
    }

    public int GetMaximalConcept(List<Description> intent, int Generator) {
        boolean parentIsMaximal = true;
        while (parentIsMaximal) {
            parentIsMaximal = false;
            for (int parent : conceptList.get(Generator).getParents()) {
                //if (conceptList.get(parent).getIntent().containsAll(intent)) {
                if (DescriptionListUtils.containsAll(conceptList.get(parent).getIntent(), (ArrayList<Description>) intent)) {
                    Generator = parent;
                    parentIsMaximal = true;
                    break;
                }
            }
        }
        return Generator;
    }

    public void AddExtentToAncestors(LinkedHashSet<Integer> extent, int curNode) {
        if (conceptList.get(curNode).parents.size() > 0) {
            for (int parent : conceptList.get(curNode).parents) {
                conceptList.get(parent).addExtents(extent);
                AddExtentToAncestors(extent, parent);
            }
        }
    }

    public int AddIntent(List<Description> intent, LinkedHashSet<Integer> extent, int generator) {
        //System.out.println("add intent "+intent+extent+generator);
        int generator_tmp = GetMaximalConcept(intent, generator);
        generator = generator_tmp;
        //System.out.println("	max gen "+generator);
        if (conceptList.get(generator).getIntent().equals(intent)) {
            conceptList.get(generator).addExtents(extent);
            AddExtentToAncestors(extent, generator);
            return generator;
        }
        Set<Integer> generatorParents = conceptList.get(generator).getParents();
        Set<Integer> newParents = new HashSet<Integer>();
        for (int candidate : generatorParents) {
            //if (!intent.containsAll(conceptList.get(candidate).getIntent())) {
            if (!DescriptionListUtils.containsAll((ArrayList<Description>) intent, conceptList.get(candidate).getIntent())) {
                List<Description> intersection = DescriptionListUtils.intersect((ArrayList<Description>) intent, conceptList.get(candidate).getIntent());
                LinkedHashSet<Integer> new_extent = new LinkedHashSet<Integer>();
                new_extent.addAll(conceptList.get(candidate).extent);
                new_extent.addAll(extent);
                candidate = AddIntent(intersection, new_extent, candidate);
            }

            boolean addParents = true;
            Iterator<Integer> iterator = newParents.iterator();
            while (iterator.hasNext()) {
                Integer parent = iterator.next();
                if (DescriptionListUtils.containsAll(conceptList.get(parent).getIntent(), conceptList.get(candidate).getIntent())) {
                    //if (conceptList.get(parent).getIntent().containsAll(conceptList.get(candidate).getIntent())) {
                    addParents = false;
                    break;
                } else if (DescriptionListUtils.containsAll(conceptList.get(candidate).getIntent(), conceptList.get(parent).getIntent())) {//(conceptList.get(candidate).getIntent().containsAll(conceptList.get(parent).getIntent())) {
                    iterator.remove();
                }
            }
            if (addParents) {
                newParents.add(candidate);
            }
            System.out.println(conceptList.size());
            if (conceptList.size() > 2000){
                int z =0;
            }
        }

        PatternConcept newConcept = new PatternConcept();

        newConcept.setIntent(intent);
        LinkedHashSet<Integer> new_extent = new LinkedHashSet<Integer>();

        new_extent.addAll(conceptList.get(generator).extent);
        new_extent.addAll(extent);

        newConcept.addExtents(new_extent);

        newConcept.setPosition(conceptList.size());
        conceptList.add(newConcept);

        conceptList.get(generator).getParents().add(newConcept.position);
        conceptList.get(newConcept.position).childs.add(generator);
        for (int newParent : newParents) {
            if (conceptList.get(generator).getParents().contains(newParent)) {
                conceptList.get(generator).getParents().remove(newParent);
                conceptList.get(newParent).childs.remove(generator);
            }
            conceptList.get(newConcept.position).getParents().add(newParent);
            conceptList.get(newParent).addExtents(new_extent);
            AddExtentToAncestors(new_extent, newParent);
            conceptList.get(newParent).childs.add(newConcept.position);
        }

        return newConcept.position;
    }

    public void printLatticeStats() {
        System.out.println("Lattice stats");
        System.out.println("max_object_index = " + objectCount);
        System.out.println("max_attribute_index = " + attributeCount);
        System.out.println("Current concept count = " + conceptList.size());
    }

    public void printLattice() {
        for (int i = 0; i < conceptList.size(); ++i) {
            printConceptByPosition(i);
        }
    }

    public void printLatticeFull() {
        for (int i = 0; i < conceptList.size(); ++i) {
            printConceptByPositionFull(i);
        }
    }

    public void printContext() {
        for (Integer obj : context.keySet()) {
            System.out.print("Объект номер " + obj + "  ");
            int i = 0;
            for (Description descr : context.get(obj)) {
                System.out.println(atNames.get(i) + "   " + descr + "  ");
                i++;
            }
            System.out.println("________________________________");
        }
    }

    public ArrayList<PatternConcept> getConceptList() {
        return conceptList;
    }

    public HashMap<Integer, ArrayList<Description>> getContext() {
        return context;
    }

    public void printConceptByPosition(int index) {
        System.out.println("Concept at position " + index);
        conceptList.get(index).printConcept();
    }

    public void printConceptByPositionFull(int index) {
        System.out.println("Concept at position " + index);
        conceptList.get(index).printConceptFull();
    }

    public PatternConcept getMostGeneralConcept() {
        for (PatternConcept pc : conceptList) {
            if (pc.getParents().isEmpty()) {
                return pc;
            }
        }
        return null;
    }

    //Заглушка
    public PatternConcept findtheMostSpecificConcept(ArrayList<Double> values) {
        if (values.isEmpty() || values == null) {
            return getMostGeneralConcept();
        } else {
            //to create code here
            return null;
        }
    }

    public PatternConcept findMostGeneralConcept(HashMap<Integer, Description> Wspec, PatternConcept concept) {
        PatternConcept generalPattern = new PatternConcept();
        ArrayList<PatternConcept> generalCandidates = new ArrayList<PatternConcept>();

        int max = 0;
        findtheMostGeneralConcept(generalCandidates, Wspec, concept);
        if (!generalCandidates.isEmpty()) {
            if (generalCandidates.size() == 1) {
                generalPattern = generalCandidates.get(0);
            } else {
                max = generalCandidates.get(0).getExtent().size();
                generalPattern = generalCandidates.get(0);
                //generalCandidates.remove(0);
                for (PatternConcept pc : generalCandidates) {
                    if (pc.getExtent().size() > max) {
                        max = pc.getExtent().size();
                        generalPattern = pc;
                    }
                }
            }

            max = 0;
            ArrayList<PatternConcept> newGeneralCandidates = new ArrayList<PatternConcept>();
            for (Integer ind : generalPattern.getParents()) {
                newGeneralCandidates.add(conceptList.get(ind));

            }
            for (PatternConcept pc : newGeneralCandidates) {
                int k = 0;
                for (Integer key : Wspec.keySet()) {
                    if (pc.getIntent().get(key).compareTo(Wspec.get(key)) == 1 || pc.getIntent().get(key).compareTo(Wspec.get(key)) == 0) {
                        k++;
                    }
                }
                if (k == Wspec.size()) {
                    if (pc.getExtent().size() > max) {
                        max = pc.getExtent().size();
                        generalPattern = pc;
                    }
                }
            }
        }
        return generalPattern;
    }

    public void findtheMostGeneralConcept(ArrayList<PatternConcept> generalCandidates, HashMap<Integer, Description> Wspec, PatternConcept concept) {

        //ArrayList<PatternConcept> generalCandidates = new ArrayList<PatternConcept>();
        ArrayList<PatternConcept> newGeneralCandidates = new ArrayList<PatternConcept>();
        PatternConcept generalPattern = new PatternConcept();
        for (Integer ind : concept.getChilds()) {
            if (ind != 0) {
                newGeneralCandidates.add(conceptList.get(ind));
            } else {
                int z = 0;
            }
        }
        for (PatternConcept pc : newGeneralCandidates) {
            if (pc.getPosition() == 39) {
                int z = 0;
            }
            int k = 0;
            for (Integer key : Wspec.keySet()) {
                if (pc.getIntent().get(key).compareTo(Wspec.get(key)) == 1 || pc.getIntent().get(key).compareTo(Wspec.get(key)) == 0) {
                    k++;
                }
            }
            if (k == Wspec.size()) {
                generalCandidates.add(pc);
            }
        }

        if (!generalCandidates.isEmpty()) {
            if (generalCandidates.size() == 1) {
                //return generalCandidates.get(0);
            } else {
                int min = generalCandidates.get(0).getParents().size();
                generalPattern = generalCandidates.get(0);
                //generalCandidates.remove(0);
                for (PatternConcept pc : generalCandidates) {
                    if (pc.getParents().size() < min) {
                        min = pc.getParents().size();
                        generalPattern = pc;
                    }
                }
                //return generalPattern;
            }
        } else {
            for (PatternConcept newPC : newGeneralCandidates) {
                findtheMostGeneralConcept(generalCandidates, Wspec, newPC);
                if (generalPattern.getPosition() != -1) {
                    //return generalPattern;
                }
            }
        }
        //return generalPattern;
    }

    // new code
    public void createLatticeFromContext(PatternConcept topConcept) {

        //int attrCount = topConcept.getExtent().size();
        //int objCount = topConcept.getIntent().size();
        int objCount = getObjectCount();
        int attrCount = getAttributesCount();

        LinkedHashSet<Integer> obj;
        ArrayList<Description> intent;
        // attributes list (of descriptions)
        ArrayList<Description> descriptions = new ArrayList<Description>();
        ArrayList<Integer> attributes = new ArrayList<Integer>();
        for (int i = 0; i < attrCount; i++) {
            attributes.add(i);
        }

        // objects set
        LinkedHashSet<Integer> objects = new LinkedHashSet<Integer>();
        for (int i = 0; i < objCount; i++) {
            objects.add(i);
        }

        Description descr = new Description(0, 0, 200000.0, 15.0);
        ArrayList<Description> ad = new ArrayList<Description>();
        ad.add(descr);

        this.conceptList.get(0).setIntent(ad);
        for (int i = 0; i < objectCount; i++) {
            intent = new ArrayList<Description>();
            obj = new LinkedHashSet<Integer>();
            obj.add(i);
            //for (int j = 0; j < attributeCount; j++) {
            intent.addAll(context.get(i));
            //}
            this.AddIntent(intent, obj, 0);
        }
        

    }

//    public void createLatticeFromBinaryContext() {
//        LinkedHashSet<Integer> obj;
//        ArrayList<Integer> intent;
//        // attributes list
//        ArrayList<Integer> attributes = new ArrayList<Integer>();
//        for (int i = 0; i < attributeCount; i++) {
//            attributes.add(i);
//        }
//        // objects set
//        LinkedHashSet<Integer> objects = new LinkedHashSet<Integer>();
//        for (int i = 0; i < objectCount; i++) {
//            objects.add(i);
//        }
//
//        this.conceptList.get(0).setIntent(attributes);
//        for (int i = 0; i < objectCount; i++) {
//            intent = new ArrayList<Integer>();
//            obj = new LinkedHashSet<Integer>();
//            obj.add(i);
//            for (int j = 0; j < attributeCount; j++) {
//                if (binaryContext[i][j] == 1) {
//                    intent.add(j);
//                }
//            }
//            this.AddIntent(intent, obj, 0);
//        }
//    }
    public List<Integer> getAttributeExtByID(int ind) {
        ArrayList<Integer> attrExt = new ArrayList<Integer>();
        for (int i = 0; i < objectCount; i++) {
            if (binaryContext[i][ind] == 1) {
                attrExt.add(i);
            }
        }
        return attrExt;
    }

    public ArrayList<Integer> getObjectIntByID(int ind) {
        ArrayList<Integer> objInt = new ArrayList<Integer>();
        for (int i = 0; i < attributeCount; i++) {
            if (binaryContext[ind][i] == 1) {
                objInt.add(i);
            }
        }
        return objInt;
    }

    public ArrayList<PatternConcept> getLattice() {
        return conceptList;
    }

    public int getAttributesCount() {
        return attributeCount;
    }

    public int getObjectCount() {
        return objectCount;
    }

    public int getSize() {
        return conceptList.size();
    }

//    public void getCategories() {
//        for (PatternConcept concept : conceptList) {
//            for (Integer i : concept.getExtent()) {
//                concept.addCategory(keyWords.get(i));
//            }
//        }
////        ArrayList<Integer> num = new ArrayList<Integer>();
////        for (int i = 0; i < conceptList.size(); i++) {
////            num.add(i);
////        }
////        int k = 0;
////        while (!num.isEmpty()) {
////            FormalConcept concept = conceptList.get(num.get(k));
////            defineCategories(concept, num);
////            num.remove((Integer)concept.position);
////            //k++;
////        }
//    }
//
//    //Заглушка
//    private void defineCategories(FormalConcept concept, ArrayList<Integer> num) {
//        Random random = new Random();
//        if (!concept.childs.isEmpty()) {
//            int a = (Integer) concept.childs.toArray()[0];
//            for (int i = 0; i < concept.getIntent().size(); i++) {
//                if (!conceptList.get(a).getCategories().isEmpty()) {
//                    concept.addCategory(conceptList.get(a).getCategories().get(i));
//                } else {
//                    defineCategories(conceptList.get(a), num);
//                }
//            }
//            num.remove((Integer) a);
//        } else {
//            int k = 0;
//            for (Integer intent : concept.getIntent()) {
//                concept.addCategory(keyWords.get(k));//random.nextInt(9)));
//                //keyWords.remove(k);
//                k++;
//            }
//        }
//
//    }
    public void readDescriptionContext(String fileName, String fileNameTypes) throws FileNotFoundException, IOException {
        String row;

        ArrayList<Integer> nominal;
        ArrayList<Integer> bool;
        ArrayList<Double> real;

        BufferedReader csvReader = new BufferedReader(new FileReader(fileName));
        BufferedReader typeReader = new BufferedReader(new FileReader(fileNameTypes));

        // Считываем количество признаков и их тип
        attributeCount = Integer.valueOf(typeReader.readLine());
        int[] types = new int[attributeCount];
        for (int i = 0; i < attributeCount; i++) {
            types[i] = Integer.valueOf(typeReader.readLine());
            System.out.println(types[i]);
        }

        row = csvReader.readLine();
        String[] data = row.split(";");
        for (int i = 1; i < data.length; i++) {
            atNames.add(data[i]);
        }

        int k = -1;
        while ((row = csvReader.readLine()) != null) {
            ArrayList<Description> descr = new ArrayList<Description>();
            k++;
            data = row.split(";");
            objNames.add(data[0]);
            for (int i = 1; i < data.length; i++) {
                if (!"null".equals(data[i])) {
                    if (types[i - 1] == 0) {
                        descr.add(new Description(types[i - 1], i - 1, Double.valueOf(data[i])));
                    }
                    if (types[i - 1] == 1) {
                        String[] dataLow = data[i].split(", ");
                        ArrayList<Integer> features = new ArrayList<Integer>();
                        for (String feature : dataLow) {
                            features.add(Integer.valueOf(feature));
                        }
                        descr.add(new Description(types[i - 1], i - 1, features));
                    }
                    if (types[i - 1] == 2) {
                        String[] dataLow = data[i].split(", ");
                        ArrayList<Integer> features = new ArrayList<Integer>();
                        for (String feature : dataLow) {
                            features.add(Integer.valueOf(feature));
                        }
                        descr.add(new Description(types[i - 1], i - 1, features));
                    }
                } else {
                    descr.add(null);
                }
            }

            context.put(k, descr);
            // do something with the data
        }

        csvReader.close();
        objectCount = k + 1;
//        obNames = new ArrayList<String>();
//        atNames = new ArrayList<String>();
//
//        BufferedReader br = new BufferedReader(new FileReader(fileName));
//        try {
//            String line;
//            br.readLine(); //B
//            br.readLine();
//            objectsNumber = Integer.parseInt(br.readLine());
//            attributesNumber = Integer.parseInt(br.readLine());
//            br.readLine();
//
//            context = new int[objectsNumber][attributesNumber];
//
//            for (int i = 0; i < objectsNumber; i++) {
//                obNames.add(br.readLine());
//            }
//
//            for (int i = 0; i < attributesNumber; i++) {
//                atNames.add(br.readLine());
//            }
//
//            int i = 0;
//            while ((line = br.readLine()) != null) {
//                for (int j = 0; j < line.length(); j++) {
//                    if (line.charAt(j) == '.') {
//                        binContext[i][j] = 0;
//                    } else {
//                        binContext[i][j] = 1;
//                    }
//                }
//                i += 1;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public ArrayList<PatternConcept> sortByDelta(ArrayList<PatternConcept> c) {
        //ArrayList<Double> measureList = new ArrayList<Double>();
        //for (FormalConcept concept : concepts) {
        //    measureList.add(concept.getIntLogStabilityUp());
        //}
        Collections.sort(c);
        return c;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        DescriptionConceptLattice dc = new DescriptionConceptLattice();
        dc.readDescriptionContext("C:\\Users\\eliza\\Documents\\NetBeansProjects\\FCA\\FCA\\tabl_note.csv", "C:\\Users\\eliza\\Documents\\NetBeansProjects\\FCA\\FCA\\tabl_note.txt");

        dc.printContext();
        dc.createLatticeFromContext(null);
        dc.printLattice();
    }

}
