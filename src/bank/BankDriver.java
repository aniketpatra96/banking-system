package bank;

import bank.acctypes.fixeddeposit.FixedDeposit;
import bank.acctypes.fixeddeposit.FixedDepositCreator;
import bank.acctypes.loanaccount.LoanAccount;
import bank.acctypes.loanaccount.LoanAccountCreator;
import bank.acctypes.savingsaccount.SavingsAccount;
import bank.acctypes.savingsaccount.SavingsAccountCreator;

import java.util.Scanner;

public class BankDriver {
    public static void startSession(){
        try(Scanner sc = new Scanner(System.in)) {
            try{
                do {
                    System.out.println("******** Welcome to Bank Menu ********");
                    System.out.println("1. Open New Savings Account");
                    System.out.println("2. Open New Fixed Deposit");
                    System.out.println("3. Apply for Loan");
                    System.out.println("4. Accounts Enquiry");
                    System.out.println("5. Close Savings Account");
                    System.out.println("6. End Session");
                    System.out.print("Enter your Choice :- ");
                    switch (sc.nextInt()) {
                        case 3 -> {
                            double amount,termPeriod;
                            int loanType;
                            long saccNo;
                            System.out.println("******** Loan Account Application ********");
                            System.out.print("Enter your Savings A/c No. :- ");
                            saccNo = sc.nextLong();
                            if(! new SavingsAccount(saccNo).checkSavingsAccount()){
                                System.out.println("No Savings A/c found for the given account No.\n Note You must have to open a Savings A/c to create a New Loan Account !!");
                                break;
                            }
                            System.out.println("Choose one of the Loan Type given Below");
                            System.out.println("------------------------------------------------");
                            System.out.println("1. Apply for House Loan");
                            System.out.println("2. Apply for Car Loan");
                            System.out.println("3. Apply for Business Loan");
                            System.out.println("4. Apply for Education Loan");
                            System.out.println("5. Apply for Travel Loan");
                            System.out.print("Enter your choice :- ");
                            loanType = sc.nextInt();
                            if(loanType < 1 || loanType > 5) {
                                System.out.println("Invalid Loan Type !!");
                                break;
                            }
                            System.out.print("Enter the amount of Loan required :- ");
                            amount = sc.nextDouble();
                            System.out.print("Enter the term Period for the Loan :- ");
                            termPeriod = sc.nextDouble();
                            LoanAccountCreator.createLoanAccount(loanType,amount,termPeriod,saccNo);
                        }
                        case 5 -> {
                            System.out.print("Enter your Saving A/c No. :- ");
                            long saccNo = sc.nextLong();
                            SavingsAccount sacc = new SavingsAccount(saccNo);
                            sacc.closeSavingsAccount();
                        }
                        case 2 -> {
                            String nominee;
                            double deposit,termPeriod;
                            long saccNo;
                            System.out.println("******** Fixed Deposit Registration ********");
                            System.out.print("Enter Savings A/c No :- ");
                            saccNo = sc.nextLong();
                            if(! new SavingsAccount(saccNo).checkSavingsAccount()) {
                                System.out.println("First Create a Savings A/c to create a Fixed Deposit !!!");
                                break;
                            }
                            System.out.print("Enter the Amount to be deposited (in Rs) :- ");
                            deposit = sc.nextDouble();
                            System.out.print("Enter the Term period for the deposit :- ");
                            termPeriod = sc.nextDouble();
                            sc.skip("\\n");
                            System.out.print("Enter Nominee Name :- ");
                            nominee = sc.nextLine();
                            FixedDepositCreator.openFixedDeposit(saccNo,nominee,deposit,termPeriod);
                        }
                        case 1 -> {
                            String fname,lname,nominee;
                            char gender;
                            double balance;
                            sc.skip("\\n");
                            System.out.println("******** Savings A/c Registration ********");
                            System.out.print("Enter Customer First Name :- ");
                            fname = sc.nextLine();
                            System.out.print("Enter Customer Last Name :- ");
                            lname = sc.nextLine();
                            System.out.print("Enter Nominee Name :- ");
                            nominee = sc.nextLine();
                            System.out.print("Enter Customer's Gender :- ");
                            gender = sc.next().toUpperCase().charAt(0);
                            System.out.print("Enter the Opening Balance to be deposited (in Rs) :- ");
                            balance = sc.nextDouble();
                            SavingsAccountCreator.openAccount(fname,lname,nominee,gender,balance);
                        }
                        case 4 -> {
                            System.out.println("***** Welcome to Accounts Section *****");
                            System.out.println("1. Savings Account");
                            System.out.println("2. Fixed Deposit");
                            System.out.println("3. Loan Account");
                            System.out.print("Enter your choice :- ");
                            switch (sc.nextInt()) {
                                case 3 -> {
                                    System.out.print("Enter your  Loan Account A/c No. :- ");
                                    long laccNo = sc.nextLong();
                                    LoanAccount l;
                                    if(!LoanAccount.checkLoanAccount(laccNo)){
                                        System.out.println("Loan Account does not exist !!!");
                                        break;
                                    }
                                    l = new LoanAccount(laccNo);
                                    while(true){
                                        System.out.println("******** Loan Account Menu ********");
                                        System.out.println("1. Loan status");
                                        System.out.println("2. Go to previous Menu");
                                        System.out.print("Enter your choice :- ");
                                        switch (sc.nextInt()){
                                            case 2 -> startSession();
                                            case 1 -> {
                                                System.out.println("******** Loan Account Details ********");
                                                l.getLoanDetails();
                                            }
                                            default -> System.out.println("Wrong Choice !! Please Try Again !!!");
                                        }
                                    }
                                }
                                case 1 -> {
                                    System.out.print("Enter your Savings A/c No. :- ");
                                    long accNo = sc.nextLong();
                                    SavingsAccount saac = new SavingsAccount(accNo);
                                    if(!saac.checkSavingsAccount()){
                                        System.out.println("Savings A/c does not exist for the given A/c No Provided !!!");
                                        break;
                                    }
                                    while(true){
                                        System.out.println("******** Savings Account Menu ********");
                                        System.out.println("1. Deposit Cash");
                                        System.out.println("2. Withdraw Cash");
                                        System.out.println("3. Check Savings A/c Balance");
                                        System.out.println("4. Check Account Details");
                                        System.out.println("5. Go to previous Menu");
                                        System.out.print("Enter your choice :- ");
                                        switch(sc.nextInt()){
                                            case 1 -> {
                                                System.out.print("Enter the amount to be deposit :- ");
                                                double amount = sc.nextDouble();
                                                saac.deposit(amount);
                                                System.out.printf("Current Balance : Rs %.2f\n",saac.fetchBalance());
                                            }
                                            case 2 -> {
                                                System.out.print("Enter the amount to be withdrwan :- ");
                                                double amt = sc.nextDouble();
                                                saac.withdraw(amt);
                                                System.out.printf("Current Balance : Rs %.2f\n",saac.fetchBalance());
                                            }
                                            case 3 -> {
                                                double balance = saac.fetchBalance();
                                                if (balance == Double.NEGATIVE_INFINITY)
                                                    break;
                                                System.out.printf("Current Balance : Rs %.2f\n", balance);
                                            }
                                            case 4 -> saac.getAccountDetails();
                                            case 5 -> startSession();
                                            default -> System.out.println("Wrong choice !! Try Again...");
                                        }
                                    }
                                }
                                case 2 -> {
                                    System.out.print("Enter your Fixed Deposit A/c No. :- ");
                                    long fdaccNo = sc.nextLong();
                                    FixedDeposit fd;
                                    if(!FixedDeposit.checkFixedDeposit(fdaccNo)){
                                        System.out.println("Fixed Deposit does not exist !!!");
                                        break;
                                    }
                                    fd = new FixedDeposit(fdaccNo);
                                    while(true){
                                        System.out.println("******** Fixed Deposit Menu ********");
                                        System.out.println("1. FD status");
                                        System.out.println("2. Go to previous Menu");
                                        System.out.print("Enter your choice :- ");
                                        switch (sc.nextInt()){
                                            case 2 -> startSession();
                                            case 1 -> {
                                                System.out.println("******** Fixed Deposit Details ********");
                                                fd.getFDDetails();
                                            }
                                            default -> System.out.println("Wrong Choice !! Please Try Again !!!");
                                        }
                                    }
                                }
                                default -> System.out.println("Wrong choice !! Try Again ...");
                            }
                        }
                        case 6 -> {
                            System.out.println("\n *************** \n Closing Current User Session  \n*********\n");
                            System.out.println("************* \n Restarting a new Session ....... \n *************\n");
                            MainDriver.startProcess();
                        }
                        default -> System.out.println("Wrong choice !! Try Again ...");
                    }
                } while (true);
            } catch (Exception e) {
                System.out.println("\n***** Some unusual glitch occurred ..... \n" + e.getMessage());
                MainDriver.startProcess();
            }
        }
    }
}
