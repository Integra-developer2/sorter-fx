package app.classes;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import app.objects.objGlobals;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.nio.charset.StandardCharsets;

import static app.functions.logError;

public class Api {
    public static HashMap<String,String> urls = new HashMap<>();
    public static String siteUrl = "https://integraa.net";
    final static String baseUrl = "/procedure/api/java_sorter/";

    public static JsonObject prefix(List<String> barcodes) throws IOException {
        Path cacheFile = Paths.get(objGlobals.ApiResponseTxt);
        if (Files.exists(cacheFile) && Files.size(cacheFile) > 0) {
            try (BufferedReader br = Files.newBufferedReader(cacheFile, StandardCharsets.UTF_8)) {
                JsonElement el = JsonParser.parseReader(br);
                if (el.isJsonObject()) return el.getAsJsonObject();
                throw new IOException("Cached JSON object expected");
            }
        }

        JsonObject res = hitApi(barcodes);

        for (Map.Entry<String, JsonElement> entry : res.entrySet()) {
            JsonObject val = entry.getValue().getAsJsonObject();

            if (val.has("auto_stock_number")) {
                int oldValue = val.get("auto_stock_number").getAsInt();
                val.addProperty("auto_stock_number", oldValue + 1);
            }

            for (Map.Entry<String, JsonElement> inner : val.entrySet()) {
                if (inner.getValue().isJsonObject()) {
                    JsonObject obj2 = inner.getValue().getAsJsonObject();
                    if (obj2.has("auto_stock_number")) {
                        int oldValue = obj2.get("auto_stock_number").getAsInt();
                        obj2.addProperty("auto_stock_number", oldValue + 1);
                    }
                }
            }
        }

        Files.createDirectories(cacheFile.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(cacheFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            bw.write(res.toString());
        }
        return res;
    }

    private static JsonObject hitApi(List<String>barcodes) throws IOException {
        String phpUrl = siteUrl + baseUrl + "get_prefix.php";

        Gson gson = new Gson();
        String json = gson.toJson(Map.of("barcodes", barcodes));

        URI uri = URI.create(phpUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String response = br.lines().reduce("", (acc, line) -> acc + line);
            JsonElement jsonElement = JsonParser.parseReader(new StringReader(response));
            if (jsonElement.isJsonObject()) {
                return jsonElement.getAsJsonObject();
            }
            else{
                throw new IOException("JSON object expected");
            }
        }
    }


    public static void setApiUrl(){
        loadUrls();
        try(BufferedReader br = new BufferedReader(new FileReader(objGlobals.urlFile))){
            String line = br.readLine();
            if (line != null) {
                String option = line.trim();
                switch (option) {
                    case "prod": {
                        Platform.runLater(()->UI.main.prod.setSelected(true));
                        Api.siteUrl = urls.get(option);
                        objGlobals.apiOption = option;
                        break;
                    }
                    case "preprod": {
                        Platform.runLater(()->UI.main.preprod.setSelected(true));
                        Api.siteUrl = urls.get(option);
                        objGlobals.apiOption = option;
                        break;
                    }
                    case "dev": {
                        Platform.runLater(()->UI.main.dev.setSelected(true));
                        Api.siteUrl = urls.get(option);
                        objGlobals.apiOption = option;
                        break;
                    }
                }

            }
        } catch (Exception e) {
            logError("viewMain",e);
        }
    }

    public static void updateApiFile(){
        Thread thread = new Thread(new Task<>() {
            @Override
            protected Void call() {
                if(objGlobals.shouldUpdateUrlFile.get() && objGlobals.urlFile != null){
                    loadUrls();
                    objGlobals.shouldUpdateUrlFile.set(false);
                    if(objGlobals.urlFile.exists()){
                        if(!objGlobals.urlFile.delete()){
                            logError("setApiSiteUrl",new Exception("could not delete file"));
                        }
                    }
                    try(BufferedWriter bw = new BufferedWriter(new FileWriter(objGlobals.urlFile))) {
                        bw.write(objGlobals.apiOption);
                    }
                    catch(Exception e){
                        logError("setApiSiteUrl",e);
                    }

                }
                return null;
            }
        });
        thread.setDaemon(true);
        thread.start();

    }

    public static void setApiSiteUrl(String option) {
        loadUrls();
        Api.siteUrl = urls.get(option);
        objGlobals.shouldUpdateUrlFile.set(true);
        objGlobals.apiOption = option;
    }

    public static void loadUrls(){
        if(urls.isEmpty()){
            urls.put("dev","http://thierry.integraa.net");
            urls.put("preprod","https://integraaposta.net");
            urls.put("prod","https://integraa.net");
        }
    }
}