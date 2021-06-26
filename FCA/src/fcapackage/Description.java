/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fcapackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import org.apache.commons.collections.ListUtils;

/**
 *
 * @author Asus
 */
public class Description {

    private int attribute;
    private int key; // 0 - interval; 1 - nominal; 2 - boolean
    private double intervalStart;
    private double intervalEnd;
    private ArrayList<Integer> nominal;
    private ArrayList<Integer> bool;

    public Description(int key, int attr) {
        this.key = key;
        this.attribute = attr;
        nominal = new ArrayList<Integer>();
        bool = new ArrayList<Integer>();
    }

    public Description(int key, int attr, double intervalStart, double intervalEnd) {
        if (key == 0) {
            this.key = key;
            attribute = attr;
            this.intervalStart = intervalStart;
            this.intervalEnd = intervalEnd;
        }
    }

    public Description(int key, int attr, double realNumber) {
        if (key == 0) {
            this.key = key;
            attribute = attr;
            this.intervalStart = realNumber;
            this.intervalEnd = realNumber;
        }
    }

    public Description(int key, int attr, ArrayList<Integer> feature) {
        if (key == 1) {
            this.key = key;
            attribute = attr;
            this.nominal = feature;
        }
        if (key == 2) {
            this.key = key;
            attribute = attr;
            this.bool = feature;
        }
    }

//    public Description(int key, int attr, ArrayList<Integer> bool) {
//        if (key == 2) {
//            this.key = key;
//            attribute = attr;
//            this.bool = bool;
//        }
//    }
    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public double getIntervalStart() {
        return intervalStart;
    }

    public void setIntervalStart(double intervalStart) {
        this.intervalStart = intervalStart;
    }

    public double getIntervalEnd() {
        return intervalEnd;
    }

    public void setIntervalEnd(double intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    public ArrayList<Integer> getNominal() {
        return nominal;
    }

    public void setNominal(ArrayList<Integer> nominal) {
        this.nominal = nominal;
    }

    public ArrayList<Integer> getBool() {
        return bool;
    }

    public void setBool(ArrayList<Integer> bool) {
        this.bool = bool;
    }

    public int compareTo(Description d) {
        switch (key) {
            case 0:
                if (intervalStart == d.getIntervalStart() && intervalEnd == d.getIntervalEnd()) {
                    return 0;
                } else if (intervalStart >= d.getIntervalStart() && intervalEnd <= d.getIntervalEnd()) {
                    return 1;
                } else {
                    return -1;
                }
            case 1:
                if (nominal.containsAll(d.getNominal()) && nominal.size() == d.getNominal().size()) {
                    return 0;
                } else if (nominal.containsAll(d.getNominal()) && nominal.size() > d.getNominal().size()) {
                    return 1;
                } else {
                    return -1;
                }
            case 2:
                if (bool.containsAll(d.getBool()) && bool.size() == d.getBool().size()) {
                    return 1;
                } else {
                    return -1;
                }
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Description other = (Description) obj;
        if (this.key != other.key) {
            return false;
        }
        if (this.attribute != other.getAttribute()) {
            return false;
        }
        switch (key) {
            case 0:
                if (this.getIntervalStart() == other.getIntervalStart() && this.getIntervalEnd() == other.getIntervalEnd()) {
                    return true;
                } else {
                    return false;
                }
            case 1:
                return other.getNominal().equals(this.getNominal());
            case 2:
                return other.getBool().equals(this.getBool());
        }
        return false;
    }

    public Description intersection(Object obj) {
        final Description other = (Description) obj;
        if (this.key != other.key) {
            return null;
        }

        Description intersect = new Description(other.getKey(), other.getAttribute());

        switch (intersect.getKey()) {
            case 0:
                if (other.getIntervalStart() >= this.getIntervalStart()) {
                    intersect.setIntervalStart(this.getIntervalStart());
                } else {
                    intersect.setIntervalStart(other.getIntervalStart());
                }
                if (other.getIntervalEnd() >= this.getIntervalEnd()) {
                    intersect.setIntervalEnd(other.getIntervalEnd());
                } else {
                    intersect.setIntervalEnd(this.getIntervalEnd());
                }
                break;
            case 1:
                intersect.setNominal((ArrayList<Integer>) ListUtils.intersection(this.getNominal(), other.getNominal()));
                break;
            case 2:
                intersect.setBool((ArrayList<Integer>) ListUtils.intersection(this.getBool(), other.getBool()));
                break;
        }
        return intersect;
    }

    @Override
    public String toString() {
        if (key == 0) {
            return "\n [" + intervalStart + "; " + intervalEnd + "]";
        }
        if (key == 1) {
            return "\n" + nominal.toString();
        }
        if (key == 2) {
            return "\n" + bool.toString();
        }
        return "Empty";
    }

}
