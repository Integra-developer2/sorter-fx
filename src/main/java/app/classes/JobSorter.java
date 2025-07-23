package app.classes;

import app.objects.objGlobals;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static app.functions.logTime;

public class JobSorter {
    public static List<String> titles;
    public static boolean hasData = false;
    public static final HashMap<String,String[]> barcodeRow=new HashMap<>();
    private static final HashMap<String,String> barcodeAlternative=new HashMap<>();
    private static final HashMap<String,String> alternativeBarcode=new HashMap<>();
    private static final int barcodeIndex = letterToIndex(objGlobals.colonnaBarcode);
    private static final int groupIndex = letterToIndex(objGlobals.raggruppamentoJobSorter);
    private static final Path filePath = Paths.get(objGlobals.jobSorterFolder);

    public static String barcodeAlternative(String barcode) {
        getData();
        if(barcodeAlternative.containsKey(barcode)){
            return barcodeAlternative.get(barcode);
        }else if(alternativeBarcode.containsKey(barcode)){
            return alternativeBarcode.get(barcode);
        }
        return "";
    }

    public static String[] barcodeRow(String barcode) {
        getData();
        return barcodeRow.get(barcode);
    }

    public static String alternativeBarcode(String alternative){
        getData();
        return alternativeBarcode.get(alternative);
    }

    public static String barcodeGroup(String barcode){
        getData();
        String[] ret = barcodeRow.get(barcode);
        if(ret!=null){
            return ret[groupIndex];
        }
        return null;
    }

    private static void loadFromFile() throws IOException {
        logTime("JobSorter loadFromFile start");
        Files.walkFileTree(filePath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
                    boolean isFirst = true;
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] row = line.split(";");
                        if (row.length > 0) {
                            if (isFirst) {
                                isFirst = false;
                                titles = Arrays.asList(row);
                            }
                            else {
                                barcodeRow.put(row[barcodeIndex], row);
                                if (row.length > 7 && !row[7].isEmpty()) {
                                    barcodeAlternative.put(row[barcodeIndex], row[7]);
                                    alternativeBarcode.put(row[7], row[barcodeIndex]);
                                }
                            }
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        logTime("JobSorter loadFromFile end");
        hasData = true;
    }

    public static void getData(){
        if(!hasData){
            try {
                loadFromFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static int letterToIndex(String letter) {
        return Character.toUpperCase(letter.charAt(0)) - 'A';
    }

}
