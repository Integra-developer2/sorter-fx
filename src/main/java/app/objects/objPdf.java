package app.objects;

public class objPdf {
    public String prefix;
    public String barcode;
    public Integer number;
    public String filePdf;
    public String fileTiff;

    public objPdf(String prefix, String barcode, Integer number, String filePdf, String fileTiff) {
        this.prefix = prefix;
        this.barcode = barcode;
        this.number = number;
        this.filePdf = filePdf;
        this.fileTiff = fileTiff;
    }
}
