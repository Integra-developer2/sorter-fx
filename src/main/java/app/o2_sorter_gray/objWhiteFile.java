package app.o2_sorter_gray;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import static app.functions.logError;
import static app.functions.mkdir;
import app.objGlobals;

public class objWhiteFile extends functions{
    private static final Double FRONT_PERCENT = 0.3D;
    private static final Double BACK_PERCENT = 0.5D;
    private static final double ADD_PERCENT = 0.04D;
    private static final int REPEAT = 10;
    private final double percent;
    private int count = 0;
    private File file;
    private File imageFile;
    private File outputFolder;
    private BufferedImage image;
    private int w;
    private int h;
    public ArrayList<String> list = new ArrayList<>();
    public objWhiteFile(String path, String frontOrBack){
        file = new File(path);
        imageFile = new File(path);
        outputFolder = new File(objGlobals.bwDir);
        percent = switch (frontOrBack) {
            case "front" -> FRONT_PERCENT;
            default -> BACK_PERCENT;
        };
        loadGrayImage();
    }
    public boolean hasNext(){
        return count < REPEAT;
    }
    public void close(){
        image.flush();
        for(String path:list){ delete(path);}
    }
    public String next() {
        try {
            BufferedImage bwImage;
            String bwPath = bwPath();
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.CEILING);
            bwImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
            for(int x = 0; x < w; ++x) {
                for(int y = 0; y < h; ++y) {
                    Color mycolor = new Color(image.getRGB(x, y), true);
                    double val = 255.0D * (percent + ADD_PERCENT * (double)count);
                    if (!((double)mycolor.getAlpha() >= val) || !((double)mycolor.getGreen() <= val) && !((double)mycolor.getRed() <= val) && !((double)mycolor.getBlue() <= val)) {
                        bwImage.setRGB(x, y, Color.WHITE.getRGB());
                    } else {
                        bwImage.setRGB(x, y, Color.BLACK.getRGB());
                    }
                }
            }            
            ImageIO.write(bwImage, "png", new File(bwPath));
            list.add(bwPath);
            bwImage.flush();
            return bwPath;
        } catch (Exception e) {
            logError("objWhiteFile",e);
        }
        return null;
    }
    private String bwPath() throws Exception{
        try {
            File outputFile = new File(outputFolder, file.getName().replace("_gray.bmp", "_bw"+(++count)+".png"));
            mkdir(outputFile.getPath());
            return outputFile.toString();
        } catch (Exception e) {
            throw e;
        }
        
    }
    private void loadGrayImage(){
        try {
            image = ImageIO.read(imageFile);
            w = image.getWidth();
            h = image.getHeight();           
        } catch (IOException e) {
            logError("objWhiteFile",e);
        }
    }
}
