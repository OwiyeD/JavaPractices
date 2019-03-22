
package ephy;

import java.util.Scanner;
public class calculatorUsingSwitch {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("INPUT THE TWO NUMBERS");
        System.out.println("First Value: ");
        int var_One = scan.nextInt();
        System.out.println("Second Value: ");
        int var_Two = scan.nextInt();
        Scanner operator = new Scanner(System.in);
        System.out.println("Enter operator(+-*/)");
        String oper = operator.nextLine();
        if (oper.equals("+") || oper.equals("-") || oper.equals("*") || oper.equals("/")) {          
            switch (oper) {
                case "+":
                    System.out.println("the addition of: " + var_One + " and " + var_Two + " = "
                            + (var_One + var_Two));
                    break;
                case "-":
                    System.out.println("Substratction of " + var_One + " and " + var_Two + " = "
                            + (var_One - var_Two));
                    break;
                case "*":
                    System.out.println("multiplication of " + var_One + " and " + var_Two + " = "
                            + (var_One * var_Two));
                    break;
                case "/":
                    System.out.println("Division of " + var_One + " and " + var_Two + " = "
                            + (var_One / var_Two));
            }
        } else {
            System.out.println("Invalid Operator");
            System.exit(0);
        }
    }

}
