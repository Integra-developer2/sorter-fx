package app.objects;

public class objOverlap {
    public int currentStart;
    public int currentEnd;
    public boolean onLapStart;
    public boolean onLapEnd;
    public objOverlap(int currentStart, int currentEnd,boolean onLapStart,boolean onLapEnd) {
        this.currentStart = currentStart;
        this.currentEnd = currentEnd;
    }
}
