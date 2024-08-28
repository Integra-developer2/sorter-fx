package app;

import static app.functions.load;
import static app.functions.printError;
import javafx.application.Application;
import javafx.stage.Stage;
/**
 * JavaFX App
 */
public class App extends Application {
    @Override
    public void start(@SuppressWarnings("exports") Stage _stage) {
        try {
            load(_stage,"viewWorkingFolder",510,300);
        } catch (Exception e) {
            printError(e,true);
        }
    }

    public static void main(String[] args) {
        try {
            launch();
        } catch (Exception e) {
            printError(e,true);
        }
    }
}