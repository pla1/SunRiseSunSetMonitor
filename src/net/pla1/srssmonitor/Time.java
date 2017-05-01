package net.pla1.srssmonitor;

public class Time {
    private TimeResult results;
    private String status;

    public String toString() {
        if (results == null) {
            System.out.println("Results are null.");
            return String.format("Status: %s\n", status);
        }
        return String.format("Status: %s Sunrise: %s Sunset: %s\n", status, results.getSunriseDateDisplay(), results.getSunsetDateDisplay());
    }

    public TimeResult getResults() {
        return results;
    }

    public void setResults(TimeResult results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
