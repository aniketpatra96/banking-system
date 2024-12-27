package bank;

import java.util.Scanner;

public class MainDriver {
    public static void startProcess(){
        Scanner sc = null;
        try {
            do {
                sc = new Scanner(System.in);
                System.out.println("******** Welcome to Aniketronics Private Bank ********");
                System.out.println("1. Start Session");
                System.out.println("2. Quit");
                System.out.print("Enter your Choice : ");
                switch(sc.nextInt()){
                    case 1 -> BankDriver.startSession();
                    case 2 -> {
                        System.out.println("***************************** \n " +
                                "Thank You for Visiting Us.... \n Please Visit us Again .....\n" +
                                "************************************");
                        System.exit(0);
                    }
                    default -> System.out.println("Wrong choice !! Try Again ...");
                }
            } while(true);
        } catch (Exception e) {
            System.out.println("\nSome unusual glitch occurred.....");
        } finally {
            try{
                assert sc != null;
                sc.close();
            }catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
