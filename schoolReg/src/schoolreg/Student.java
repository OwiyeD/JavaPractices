
package schoolreg;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class Student implements Persons {

    @Override
    public void information() {
        
        System.out.println("Student Name: Douglas Owiye");
        System.out.println("Registration Number: B135/10201/2014");
        System.out.println("Course: BSC Computer Science");
    }
    
}
