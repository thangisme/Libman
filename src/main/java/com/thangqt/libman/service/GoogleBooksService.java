package com.thangqt.libman.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thangqt.libman.model.Book;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;

public class GoogleBooksService {

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    public Book fetchBookInfo(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        String url = GOOGLE_BOOKS_API_URL + encodedQuery;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                return makeMaterialFromJsonInfo(jsonObject);
            }
        }
    }

    public Book makeMaterialFromJsonInfo(JsonObject jsonObject) {
        JsonArray items = jsonObject.getAsJsonArray("items");
        if (items.size() == 0) {
            return null;
        }
        JsonObject item = items.get(0).getAsJsonObject();
        JsonObject volumeInfo = item.getAsJsonObject("volumeInfo");
        String title = volumeInfo.get("title").getAsString();
        String author = volumeInfo.getAsJsonArray("authors").get(0).getAsString();
        String description = volumeInfo.get("description").getAsString();
        String isbn = volumeInfo.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject().get("identifier").getAsString();
        String imageUrl = volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString();
        return new Book(title, author, description, isbn, imageUrl);
    }
}