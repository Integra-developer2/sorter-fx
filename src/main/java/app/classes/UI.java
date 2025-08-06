package app.classes;

import app.objects.objGlobals;
import app.objects.objProgressBar;
import app.views.viewMain;
import app.views.viewStatusBar;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;

import static app.functions.printError;

public class UI  {
    public static HashMap<Integer,String> pages=new HashMap<>();
    public static viewMain main;
    public static viewStatusBar controller;

    public static void setPages(){
        pages.put(0,"/app/views/viewMain.fxml");
        pages.put(1,"/app/views/viewWorkingFolder.fxml");
        pages.put(2,"/app/views/viewStepChoice.fxml");
        pages.put(3,"/app/views/viewInputs.fxml");
        pages.put(4,"/app/views/viewStatusBar.fxml");
        pages.put(5,"/app/views/viewGrayAnomalies.fxml");
        pages.put(6,"/app/views/viewStockAnomalies.fxml");
        pages.put(7,"/app/views/viewStockNumber.fxml");
        pages.put(8,"/app/views/viewStockToShoot.fxml");
        pages.put(9,"/app/views/viewStockShooting.fxml");
        pages.put(10,"/app/views/viewEnd.fxml");

    }

    public static String page(Integer index){
        objProgressBar.objProgressItems.clear();
        return pages.get(index);
    }

    public static void loadViewStatusBar(Integer index, String title) {
        if(!objGlobals.stop){
            try {
                Thread.sleep(300);
                main.titleLabel.setText(title);
                FXMLLoader loader = new FXMLLoader(UI.class.getResource(UI.page(index)));
                VBox root = loader.load();
                controller = loader.getController();
                main.setContent(root);
            }
            catch (IOException | InterruptedException e) {
                printError(e,true);
            }
        }
    }

    public static void loadDefault(Integer index, String title) {
        try {
            Thread.sleep(300);
            main.titleLabel.setText(title);
            FXMLLoader loader = new FXMLLoader(UI.class.getResource(UI.page(index)));
            VBox root = loader.load();
            main.setContent(root);
        }
        catch (IOException | InterruptedException e) {
            printError(e, true);
        }
    }

}
