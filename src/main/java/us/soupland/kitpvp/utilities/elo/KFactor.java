package us.soupland.kitpvp.utilities.elo;

public class KFactor {

    private int startIndex;
    private int endIndex;
    private double value;

    public KFactor(int startIndex, int endIndex, double value) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.value = value;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public double getValue() {
        return value;
    }
}
