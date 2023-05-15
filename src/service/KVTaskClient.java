package service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    
    String url; // http://localhost:8078
    String apiToken;
    HttpClient client = HttpClient.newHttpClient();

    public KVTaskClient(String url) {
        this.url = url;
        this.apiToken = register();
    }

    private String register() {
        String registerUrl = url + "/register";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(registerUrl)).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
//            JsonElement jsonElement = JsonParser.parseString(response.body());
//            if (!jsonElement.isJsonObject()) {
//                System.out.println("Ответ от сервера не соответствует ожиданиям");
//                return null;
//            }
//            JsonObject jsonObject = jsonElement.getAsJsonObject();
//            return jsonObject.getAsString();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка во время запроса");
        }
        return null;
    }

    public void put(String key, String json) {
        String registerUrl = url + "/save/" + key + "?API_TOKEN=" + apiToken;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(registerUrl))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка во время запроса");
        }

    }

    public String load(String key) {

        return "";
    }
}
