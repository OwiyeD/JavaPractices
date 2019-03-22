/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springdemo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class SpringDemo {

    
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(appConfig.class);
        
        alien a = context.getBean(alien.class);
        a.show();
        
    }
    
}
