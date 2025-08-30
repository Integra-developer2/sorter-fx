package app.models;

import app.objects.objStock;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class modelStockNumber {
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
    private final StringProperty J;
    private final StringProperty K;
    private final StringProperty L;


    public modelStockNumber(objStock obj) {
        this.row = new SimpleStringProperty(String.format("%04d",obj.row));
        this.A = new SimpleStringProperty(obj.firstBarcode);
        this.B = new SimpleStringProperty(obj.lastBarcode);
        this.C = new SimpleStringProperty(obj.stockLabel);
        this.D = new SimpleStringProperty(obj.obs);
        this.E = new SimpleStringProperty(obj.cassetto);
        this.F = new SimpleStringProperty(obj.group);
        this.G = new SimpleStringProperty(obj.progStart);
        this.H = new SimpleStringProperty(obj.progEnd);
        this.I = new SimpleStringProperty(obj.logic);
        this.J = new SimpleStringProperty(obj.prefix);
        this.K = new SimpleStringProperty(obj.stockNumber);
        this.L = new SimpleStringProperty(obj.agency);
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
    public StringProperty J() { return J;}
    public StringProperty K() { return K;}
    public StringProperty L() { return L;}
}
