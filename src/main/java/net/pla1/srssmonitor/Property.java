package net.pla1.srssmonitor;

public class Property {
    private String type;
    private String quality;
    private double quality_percent;
    private double quality_double;
    private double temperature;
    private String last_updated;
    private String imported_at;
    private String valid_at;
    private String source;
    private double distance;
    private Dusk dusk;

    public Dusk getDusk() {
        return dusk;
    }

    public void setDusk(Dusk dusk) {
        this.dusk = dusk;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public double getQuality_percent() {
        return quality_percent;
    }

    public void setQuality_percent(double quality_percent) {
        this.quality_percent = quality_percent;
    }

    public double getQuality_double() {
        return quality_double;
    }

    public void setQuality_double(double quality_double) {
        this.quality_double = quality_double;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getImported_at() {
        return imported_at;
    }

    public void setImported_at(String imported_at) {
        this.imported_at = imported_at;
    }

    public String getValid_at() {
        return valid_at;
    }

    public void setValid_at(String valid_at) {
        this.valid_at = valid_at;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
