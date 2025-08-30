package app.objects;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class objProgressItem {
    public ProgressBar progressBar;
    public Label label;
    public String labelText;
    public Integer total;
    public Integer count;
    public Integer id;

    public objProgressItem(ProgressBar progressBar, Label label, String labelText, Integer total, Integer count, Integer id){
        this.progressBar = progressBar;
        this.label = label;
        this.labelText = labelText;
        this.total = total;
        this.count = count;
        this.id = id;
    }

}