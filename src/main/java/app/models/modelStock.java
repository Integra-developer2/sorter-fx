package app.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class modelStock {
    private final StringProperty row;
    private final StringProperty A;
    private final StringProperty B;
    private final StringProperty C;
    private final StringProperty D;
    private final StringProperty E;
    private final StringProperty F;
    private final StringProperty G;
    private final StringProperty H;
    private final StringProperty I;

    public modelStock(Integer row, String A, String B, String C, String D, String E, String F, String G, String H, String I) {
        this.row = new SimpleStringProperty(String.format("%04d",row));
        this.A = new SimpleStringProperty(A);
        this.B = new SimpleStringProperty(B);
        this.C = new SimpleStringProperty(C);
        this.D = new SimpleStringProperty(D);
        this.E = new SimpleStringProperty(E);
        this.F = new SimpleStringProperty(F);
        this.G = new SimpleStringProperty(G);
        this.H = new SimpleStringProperty(H);
        this.I = new SimpleStringProperty(I);
    }
    public StringProperty row() { return row;}
    public StringProperty A() { return A;}
    public StringProperty B() { return B;}
    public StringProperty C() { return C;}
    public StringProperty D() { return D;}
    public StringProperty E() { return E;}
    public StringProperty F() { return F;}
    public StringProperty G() { return G;}
    public StringProperty H() { return H;}
    public StringProperty I() { return I;}
}
