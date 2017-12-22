package net.pla1.srssmonitor;

import java.util.ArrayList;
import java.util.List;

public class Quality {
    private String type;
    private List<Feature> features = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Feature> features) {
        this.features = features;
    }

}

