package ssiemens.ss16.softwareEngineering.praktikum1;

public class EBook extends Book {
    int size;
    Format format;

    public EBook(String isbn, Editorial editorial, Author author, int size, Format format) {
        super(isbn, editorial, author);
        this.size = size;
        this.format = format;
    }

    enum Format {
        EPUB, PDF, AZW, KF8;
    }
}
