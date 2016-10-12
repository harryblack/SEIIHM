package ssiemens.ss16.softwareEngineering.praktikum1;

public class PrintBook extends Book {
    int pageNr;

    public PrintBook(String isbn, Editorial editorial, Author author, int pageNr) {
        super(isbn, editorial, author);
        this.pageNr = pageNr;
    }
}
