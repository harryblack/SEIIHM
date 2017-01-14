package ssiemens.ss16.softwareEngineering.praktikum1;

public class Author {
    String name;
    Book[] publications;

    public Author(String name) {
        this.name = name;
    }

    public void setPublications(Book[] publications) {
        this.publications = publications;
    }
}
