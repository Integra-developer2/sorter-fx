package app.objects;
import java.util.ArrayList;
import java.util.List;

public class objStockToShoot {

    public String group;
    public Integer row1;
    public String box1;
    public String pallet1;
    public Integer row2;
    public String box2;
    public String pallet2;
    public List<objValidTiff> objValidTiff;

    public objStockToShoot(
        String group,
        Integer row1,
        String box1,
        String pallet1,
        Integer row2,
        String box2,
        String pallet2
    ) {
        this.group = group;
        this.row1 = row1;
        this.box1 = box1;
        this.pallet1 = pallet1;
        this.row2 = row2;
        this.box2 = box2;
        this.pallet2 = pallet2;
        this.objValidTiff = new ArrayList<>();
    }
    
}
