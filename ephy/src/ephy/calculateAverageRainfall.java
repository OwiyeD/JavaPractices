
package ephy;

import java.util.Scanner;
public class calculateAverageRainfall {
    public static void main(String [] args){
        Scanner scan = new Scanner(System.in);
        System.out.println("How many years do you want to calculate?");
        int number_Of_Years = scan.nextInt();
        while(number_Of_Years < 1)System.out.println("Invalid number of years");
        double totalAnnualRainfall = 0;
        double rainfall = 0;
        int total_Months = 0;        
        for(int i = 1; i<= number_Of_Years; i++){
            System.out.println();
            System.out.println("ENTER RAINFALL INCHES FOR THE MONTHS OF YEAR: "+i);
            for (int j = 1; j <= 12; j++){
                System.out.println("Enter the raifall for Month " +j+ " of Year "+i);
                rainfall = scan.nextDouble();
                if(rainfall < 1){
                    System.out.println("Invalid inches of rainfall");
                    System.exit(0);
                }
                totalAnnualRainfall +=rainfall;
                total_Months = total_Months +1;                        
            }
            System.out.println("Total rainfall for Year "+i+" = " +totalAnnualRainfall);
            System.out.println("Average total rainfall for Year "+i+" = " +totalAnnualRainfall/total_Months);
        }
        
    }
}
