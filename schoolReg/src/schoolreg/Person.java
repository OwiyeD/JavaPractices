
package schoolreg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Person {
    @Autowired
    @Qualifier("Student")
    private Persons pers;

    public Persons getPerson() {
        return pers;
    }

    public void setPerson(Persons person) {
        this.pers = person;
    }
    
    public void show(){
        System.out.println("Welcome to Programming");
        
    }
}
