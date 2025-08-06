package app.objects;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public record objProgressItem(ProgressBar progressBar, Label label, String labelText, Integer total) { }