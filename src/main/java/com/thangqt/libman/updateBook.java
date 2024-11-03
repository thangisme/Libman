package com.thangqt.libman;

import com.thangqt.libman.model.Book;
import com.thangqt.libman.model.Material;
import com.thangqt.libman.service.GoogleBooksService;
import com.thangqt.libman.service.MaterialManager;
import com.thangqt.libman.service.ServiceFactory;
import java.io.IOException;
import java.sql.SQLException;

public class updateBook {
    public static void main(String[] args) throws SQLException, IOException {
        MaterialManager materialManager = ServiceFactory.getInstance().getMaterialManager();
        for (Material book : materialManager.getAllMaterials()) {
            Book fetchedBook = GoogleBooksService.fetchBookInfo(book.getTitle() + " - " + book.getAuthor());
            if (fetchedBook == null) {
                continue;
            }
            book.setDescription(fetchedBook.getDescription());
            book.setCoverImageUrl(fetchedBook.getCoverImageUrl());
            book.setPublisher(fetchedBook.getPublisher());
            if (book instanceof Book) {
                ((Book) book).setIsbn(fetchedBook.getIsbn());
            }
            System.out.println("Updating book: " + book.getTitle() + " " + fetchedBook.getDescription() + " " + fetchedBook.getCoverImageUrl());
            materialManager.updateMaterial(book);
        }
    }
}
