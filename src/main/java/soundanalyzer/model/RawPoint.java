package soundanalyzer.model;

public class RawPoint {
    public double value;
    public long time;

    public RawPoint(double value) {
        this.value = value;
        this.time = System.currentTimeMillis();
    }
}
