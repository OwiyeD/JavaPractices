
package schoolreg;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan("SchoolReg")
public class appConfig {
    public Person getperson(){
        return new Person();
    }
}
