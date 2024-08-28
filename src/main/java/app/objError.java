package app;

import java.io.File;

public class objError {
    public String path;
    public boolean moveFile;

    public objError(String baseDir, boolean moveFile) {
        File pathFile = new File(baseDir);
        this.path = pathFile.getPath();
        this.moveFile = moveFile;
    }
}
