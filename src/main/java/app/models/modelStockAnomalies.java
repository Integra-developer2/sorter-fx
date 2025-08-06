package app.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class modelStockAnomalies {
    private final StringProperty A;
    private final StringProperty B;
    private final StringProperty C;
    private final StringProperty D;
    private final StringProperty E;
    private final StringProperty F;
    private final StringProperty G;

    public modelStockAnomalies(Integer prog, String group, String barcode, String file, String logic, String prefix, String agency) {
        this.A = new SimpleStringProperty(String.valueOf(prog));
        this.B = new SimpleStringProperty(group);
        this.C = new SimpleStringProperty(barcode);
        this.D = new SimpleStringProperty(file);
        this.E = new SimpleStringProperty(logic);
        this.F = new SimpleStringProperty(prefix);
        this.G = new SimpleStringProperty(agency);
    }

    public StringProperty A1() { return A;}
    public StringProperty B1() { return B;}
    public StringProperty C1() { return C;}
    public StringProperty D1() { return D;}
    public StringProperty E1() { return E;}
    public StringProperty F1() { return F;}
    public StringProperty G1() { return G;}


}
