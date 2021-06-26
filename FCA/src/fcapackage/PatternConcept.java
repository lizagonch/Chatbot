/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fcapackage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Asus
 */
public class PatternConcept implements Comparable< PatternConcept> {

    int position;
    ArrayList<Description> intent;
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

    public PatternConcept() {
        position = -1;
        intent = new ArrayList<Description>();
        extent = new LinkedHashSet<Integer>();
        parents = new HashSet<Integer>();
        childs = new HashSet<Integer>();
    }

    public int getPosition() {
        return position;
    }

    public ArrayList<Description> getIntent() {
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

    public void setIntent(List<Description> newIntent) {
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

    public void setIntent(ArrayList<Description> intent) {
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

    //my code
    public double getHomogeneity(int m) {
        return intent.size() / (double) m;
    }

    @Override
    public int compareTo(PatternConcept o) {
        return 0;//(int) (intLogStabilityUp - o.getIntLogStabilityUp());
    }

    @Override
    public boolean equals(Object obj) {
        return (this.position == ((PatternConcept) obj).position);
    }
}
