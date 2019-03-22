
package schoolreg;

import org.springframework.stereotype.Component;

@Component
public class Staff implements Persons {

    @Override
    public void information() {
        System.out.println("Staff Name: Mark Otieno");
        //System.out.println("Staff Number: 1673672B");
        //System.out.println("Job Title: Student Registar");
    }
    
}
