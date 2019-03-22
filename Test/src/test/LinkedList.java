
package test;

/**
 *
 * @author CET
 */
import java.util.*;
class Book{
int id;
String name, author, publisher;
int quantity;
public Book(int id, String name, String author, String publisher, int quantity){
this.id = id;
this.name = name;
this.author = author;
this.publisher = publisher;
this.quantity = quantity;
}

}

public class LinkedList {
    public static void main(String []args){
        List<Book> list=new LinkedList<Book>();
        Book b1 = new Book(101, "Let us C","Yashwant Kanetaka","KF",8);
        Book b2=new Book(102, "Data Comm and Networking", "Dr Mugo","O'Rilley",4);
        list.add(b1);
        list.add(b2);
        for(Book b: list){
            System.out.println(b.id);
        }
    }
    
    
}
