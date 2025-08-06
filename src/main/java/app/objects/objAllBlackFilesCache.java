package app.objects;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class objAllBlackFilesCache implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public HashMap<String,String>barcodeFile = new HashMap<>();
    public ArrayList<String> barcodesFromFiles=new ArrayList<>();
    public ArrayList<String>all=new ArrayList<>();
    public HashMap<String,Integer>barcodeIndex=new HashMap<>();
}
