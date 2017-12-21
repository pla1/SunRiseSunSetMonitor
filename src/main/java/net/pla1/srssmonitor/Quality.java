package net.pla1.srssmonitor;

import java.util.ArrayList;

public class Quality {
    private String type;
    private ArrayList<Feature> features = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Feature> features) {
        this.features = features;
    }

}

