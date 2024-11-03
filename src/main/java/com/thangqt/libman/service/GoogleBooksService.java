package com.thangqt.libman.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thangqt.libman.model.Book;
import java.io.IOException;
import java.net.URLEncoder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class GoogleBooksService {

  private static final String GOOGLE_BOOKS_API_URL =
      "https://www.googleapis.com/books/v1/volumes?q=";

  public static Book fetchBookInfo(String query) throws IOException {
    String encodedQuery = URLEncoder.encode(query, "UTF-8");
    String url = GOOGLE_BOOKS_API_URL + encodedQuery;
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(url);
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        if (response.getStatusLine().getStatusCode() != 200) {
          System.out.println(
              "Failed to fetch book info: " + response.getStatusLine().getReasonPhrase());
          return null;
        }
        String jsonResponse = EntityUtils.toString(response.getEntity());
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        return makeMaterialFromJsonInfo(jsonObject);
      }
    }
  }

  public static Book makeMaterialFromJsonInfo(JsonObject jsonObject) {
    JsonArray items = jsonObject.getAsJsonArray("items");
    if (items == null || items.isEmpty()) {
      return null;
    }
    JsonObject item = items.get(0).getAsJsonObject();
    JsonObject volumeInfo = item.getAsJsonObject("volumeInfo");
    String title = volumeInfo.get("title").getAsString();
    String author = volumeInfo.getAsJsonArray("authors").get(0).getAsString();
    String description = null;
    String publisher = null;
    if (volumeInfo.get("publisher") != null) {
      publisher = volumeInfo.get("publisher").getAsString();
    }
    if (volumeInfo.get("description") != null) {
      description = volumeInfo.get("description").getAsString();
    }
    String isbn = null;
    JsonArray industryIdentifiers = volumeInfo.getAsJsonArray("industryIdentifiers");
    if (industryIdentifiers != null) {
      for (JsonElement element : industryIdentifiers) {
        JsonObject identifier = element.getAsJsonObject();
        if ("ISBN_13".equals(identifier.get("type").getAsString())) {
          isbn = identifier.get("identifier").getAsString();
          break;
        }
      }
    }
    String imageUrl = null;
    if (volumeInfo.getAsJsonObject("imageLinks") != null) {
      imageUrl = volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString();
    }
    Book book = new Book(title, author, description, isbn, imageUrl);
    book.setPublisher(publisher);
    return book;
  }
}
