package net.pla1.srssmonitor;

public class Geometry {
    private String type;
    private double[] coordinates;

    public String toString() {
        return String.format("Longitude:%f Latitude:%f", coordinates[0], coordinates[1]);
    }

    public String getType() {
        return type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public void setType(String type) {
        this.type = type;
    }


}
