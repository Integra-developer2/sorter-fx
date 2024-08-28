package app;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class modelEtichette {
    private final StringProperty row;
    private final StringProperty A;
    private final StringProperty B;
    private final StringProperty C;
    private final StringProperty D;
    private final StringProperty E;

    public modelEtichette(Integer row, String A, String B, String C, String D, String E) {
        this.row = new SimpleStringProperty(row.toString());
        this.A = new SimpleStringProperty(A);
        this.B = new SimpleStringProperty(B);
        this.C = new SimpleStringProperty(C);
        this.D = new SimpleStringProperty(D);
        this.E = new SimpleStringProperty(E);
    }
    @SuppressWarnings("exports")
    public StringProperty row() { return row;}
    @SuppressWarnings("exports")
    public StringProperty A() { return A;}
    @SuppressWarnings("exports")
    public StringProperty B() { return B;}
    @SuppressWarnings("exports")
    public StringProperty C() { return C;}
    @SuppressWarnings("exports")
    public StringProperty D() { return D;}
    @SuppressWarnings("exports")
    public StringProperty E() { return E;}
}
