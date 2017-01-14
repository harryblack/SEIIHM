package ssiemens.ss16.softwareEngineering.praktikum1;

public abstract class Book {
    String isbn;
    Editorial editorial;
    Author author;

    public Book(String isbn, Editorial editorial, Author author) {
        this.isbn = isbn;
        this.editorial = editorial;
        this.author = author;
    }
}
