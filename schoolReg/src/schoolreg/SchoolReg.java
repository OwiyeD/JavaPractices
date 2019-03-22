
package schoolreg;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class SchoolReg {
    
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(appConfig.class);
        Person p = context.getBean(Person.class);
        p.show();
    }
    
}
