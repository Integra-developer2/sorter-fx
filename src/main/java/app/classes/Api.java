package app.classes;

import java.io.*;
import java.net.*;
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

    public static JsonObject prefix(List<String>barcodes) throws IOException {
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