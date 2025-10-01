package app.objects;

import java.io.File;

public class objValidTiff {
    public String file;
    public String group;
    public Integer index;
    public String barcode;
    public String logic;
    public String prefix;
    public String stockNumber;
    public String agency;
    public String obs;
    public String stockLabel;
    public String passo;
    public objValidTiff(String file, String group, Integer index,String barcode,String agency) {
        this.file = file;
        this.group = group;
        this.index = index;
        this.barcode = barcode;
        this.agency = agency;
        this.passo = new File(file).getParentFile().getParentFile().getName();

    }
}