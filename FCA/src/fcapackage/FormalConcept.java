/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fcapackage;

import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.collections.ListUtils;

public class FormalConcept implements Comparable< FormalConcept> {

    int position;
    boolean isModified = false;
    ArrayList<String> keyWords = new ArrayList<String>();
    ArrayList<String> categories;
    ArrayList<Integer> intent;
    Set<Integer> childs;
    Set<Integer> parents;
    Set<Integer> extent;
    double intLogStabilityBottom = 0,
            intLogStabilityUp = 0,
            separation = 0,
            probability = 0,
            blS_Jaa = 0,
            blS_SMCaa = 0,
            blS_Jam = 0,
            blS_SMCam = 0,
            blS_Jmm = 0,
            blS_Jma = 0,
            blS_SMCma = 0,
            blS_SMCmm = 0,
            blCV = 0,
            blCFC = 0,
            blCU = 0,
            blP = 0;

    double cohAvgJ = 0,
            cohMinJ = 0,
            cohAvgSMC = 0,
            cohMinSMC = 0;

    public FormalConcept() {
        keyWords.add("Watches");
        keyWords.add("Dress");
        keyWords.add("Shirt");
        keyWords.add("Skirt");
        keyWords.add("Book");
        keyWords.add("Cup");
        keyWords.add("Glasses");
        keyWords.add("Jeans");
        keyWords.add("Umbrella");
        keyWords.add("Coat");
        keyWords.add("Ring");
        keyWords.add("T-shirt");
        keyWords.add("Kettle");
        keyWords.add("Socks");
        keyWords.add("Cell phone");
        position = -1;
        categories = new ArrayList<String>();
        intent = new ArrayList<Integer>();
        extent = new LinkedHashSet<Integer>();
        parents = new HashSet<Integer>();
        childs = new HashSet<Integer>();
    }

    public int getPosition() {
        return position;
    }

    public ArrayList<Integer> getIntent() {
        return intent;
    }

    public void setPosition(int newPosition) {
        position = newPosition;
    }

    public void addExtents(LinkedHashSet<Integer> ext) {
        extent.addAll(ext);
    }

    public Set<Integer> getChilds() {
        return childs;
    }

    public LinkedHashSet<Integer> getExtent() {
        return (LinkedHashSet<Integer>) extent;
    }

    public void setIntent(List<Integer> newIntent) {
        intent.clear();
        intent.addAll(newIntent);
    }

    public void setChilds(Set<Integer> newChilds) {
        childs.clear();
        childs.addAll(newChilds);
    }

    public void addChild(Integer child) {
        childs.add(child);
    }

    public void setParents(Set<Integer> newParents) {
        //parents = newParents;
        parents.clear();
        parents.addAll(newParents);
    }

    public void printConcept() {
        System.out.println("Concept position:" + position);
        System.out.println("Concept intent:" + getIntent());
        System.out.println("Concept extent:" + extent);
        System.out.println("Concept parents:" + getParents());
        System.out.println("Concept childs:" + childs);
        System.out.println("Concept categories:" + getCategories());
        System.out.println("Is modified:" + isModified);
        System.out.println("--------------------");
    }

    public void printConceptFull() {
        System.out.println("Concept position:" + position);
        System.out.println("Concept intent:" + getIntent());
        System.out.println("Concept extent:" + extent);
        System.out.println("Concept parents:" + getParents());
        System.out.println("Concept childs:" + childs);
        System.out.format("Prob.  blSaaJ   blSaaSMC blSmaJ   blSmmJ   blSmaSMC  blSamJ   blSamSMC  blCV     blCFC    blCU     blP      separ.\n");
        System.out.format("%5.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f\n", probability, blS_Jaa, blS_SMCaa,
                blS_Jma, blS_Jmm, blS_SMCma, blS_Jam, blS_SMCam, blCV, blCFC, blCU, blP, separation);
        System.out.format("Concept cohAvgJ: %.3f \n", cohAvgJ);
        System.out.format("Concept cohAvgSMC: %.3f \n", cohAvgSMC);
        System.out.format("Concept cohMinJ: %.3f \n", cohMinJ);
        System.out.format("Concept cohMinSMC: %.3f \n", cohMinSMC);
        //System.out.format("Concept stability: [ %.3f; %.3f] \n", getIntLogStabilityBottom(), getIntLogStabilityUp());
        System.out.println("--------------------");
    }

    public static void main(String[] args) {
        FormalConcept c = new FormalConcept();
        c.printConcept();
    }

    public void setIntent(ArrayList<Integer> intent) {
        this.intent = intent;
    }

    public Set<Integer> getParents() {
        return parents;
    }

    public double getIntLogStabilityBottom() {
        return intLogStabilityBottom;
    }

    public void setIntLogStabilityBottom(double intLogStabilityBottom) {
        this.intLogStabilityBottom = intLogStabilityBottom;
    }

    public double getIntLogStabilityUp() {
        return intLogStabilityUp;
    }

    public void setIntLogStabilityUp(double intLogStabilityUp) {
        this.intLogStabilityUp = intLogStabilityUp;
    }

    @Override
    public int compareTo(FormalConcept o) {
        return (int) (intLogStabilityUp - o.getIntLogStabilityUp());
    }

    //my code
    public double getHomogeneity(int m) {
        return intent.size() / (double) m;
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    public void addCategories(ArrayList<String> category) {
        categories.addAll(ListUtils.subtract(category, categories));
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setModified() {
        isModified = true;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.position == ((FormalConcept) obj).position);
    }

}
