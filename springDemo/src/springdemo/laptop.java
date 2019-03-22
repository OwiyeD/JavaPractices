
package springdemo;

import org.springframework.stereotype.Component;


@Component
public class laptop implements computer {

    @Override
    public void features() {
        System.out.println("Core i7, 4GB RAM, 4.2MHz");
    }
    
}
