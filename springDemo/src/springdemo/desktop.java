
package springdemo;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;


@Component
@Primary
public class desktop implements computer{

    @Override
    public void features() {
        System.out.println("Core i7, 16GB, 4.5GHZ");
    }
    
}
