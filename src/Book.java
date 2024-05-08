import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String authors;
    private double price;
    private String isbn;
    private String genre;
    private int year;

    public Book(String title, String authors, double price, String isbn, String genre, int year) {
        this.title = title;
        this.authors = authors;
        this.price = price;
        this.isbn = isbn;
        this.genre = genre;
        this.year = year;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public double getPrice() {
        return price;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setYear(int year) {
        this.year = year;
    }

    // Override equals() method
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Book book = (Book) obj;
        return Double.compare(book.price, price) == 0 &&
                year == book.year &&
                title.equals(book.title) &&
                authors.equals(book.authors) &&
                isbn.equals(book.isbn) &&
                genre.equals(book.genre);
    }

    // Override toString() method
    @Override
    public String toString() {
        return "\tBook \n" +
                "\t\ttitle='" + title + '\n' +
                "\t\tauthors='" + authors + '\n' +
                "\t\tprice=" + price + '\n' +
                "\t\tisbn='" + isbn + '\n' +
                "\t\tgenre='" + genre + '\n' +
                "\t\tyear=" + year + '\n'; 
            }
}
