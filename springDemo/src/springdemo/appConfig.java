package springdemo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan("springDemo")
public class appConfig {
    public alien getAlien(){
        return new alien();
    }
    
}
