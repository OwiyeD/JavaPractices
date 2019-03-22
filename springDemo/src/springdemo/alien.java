
package springdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class alien {
    @Autowired
    @Qualifier("laptop")
    private computer comp;
    
      public computer getComp(){
          return comp;
      }
      
      public void setComp(computer comp){
          this.comp = comp;
      }
      
    public void show(){
        System.out.println("Hello! World");
        comp.features();
    }
   
    
}
