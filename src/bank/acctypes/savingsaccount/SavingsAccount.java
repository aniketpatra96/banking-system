package bank.acctypes.savingsaccount;

import bankdatabase.BankServerConnecter;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class SavingsAccount {
    private Connection con;
    private PreparedStatement psmt;
    private ResultSet rs;
    private Statement smt;
    private final float interestRate;
    private long accNo;

    public SavingsAccount(long accNo) {
        this();
        this.accNo = accNo;
        if(checkSavingsAccount())
            generateCompoundInterest(accNo);
    }

    public double closeFixedDeposits() {
        double deposit = 0.0,termPeriod;
        Timestamp tsmp;
        long fdaccNo,yearsDeposited;
        Date timestamp1,timestamp2;
        LocalDate date1,date2;
        try{
            con = BankServerConnecter.getDatabaseConnection();
            psmt = con.prepareStatement("select fdaccNo,amount,fdaccNo,termperiod,fixedopeningdate from fixeddeposit where saccNo = ?");
            psmt.setLong(1,accNo);
            rs = psmt.executeQuery();
            while(rs.next()){
                deposit += rs.getDouble("amount");
                termPeriod = rs.getDouble("termperiod");
                tsmp = rs.getTimestamp("fixedopeningdate");
                fdaccNo = rs.getLong("fdaccNo");
                timestamp1 = new Date();
                timestamp2 = new Date(tsmp.getTime());
                date1 = timestamp1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                date2 = timestamp2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                yearsDeposited = ChronoUnit.YEARS.between(date2, date1);
                if(termPeriod - yearsDeposited <= 0) {
                    psmt = con.prepareStatement("delete from fixeddeposit where fdaccNo = ?");
                    psmt.setLong(1, rs.getLong("fdaccNo"));
                    psmt.executeUpdate();
                }else{
                    System.out.println("Unable to close the fixed deposit with fdNo :- " + fdaccNo + " as it has not reached its maturity period.");
                    return Double.NEGATIVE_INFINITY;
                }
            }
            return deposit;
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                rs.close();
                smt.close();
                con.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    public void closeSavingsAccount() {
        double deposit,currentBalance,newBalance;
        try{
            deposit = closeFixedDeposits();
            if(deposit == Double.NEGATIVE_INFINITY){
                System.out.println("Unable to close Savings account as One of the Fixed deposit is pending to be matured.\nPlease wait for the maturity of the termperiod of the FD !!");
                return;
            }
            currentBalance = fetchBalance();
            newBalance = currentBalance + deposit;
            con = BankServerConnecter.getDatabaseConnection();
            psmt = con.prepareStatement("update savingsaccount set balance = ? where saccNo = ?");
            psmt.setDouble(1,newBalance);
            psmt.setLong(2,accNo);
            int status = psmt.executeUpdate();
            if(status > 0){
                psmt = con.prepareStatement("delete from savingsaccount where saccNo = ?");
                psmt.setLong(1,accNo);
                int stats = psmt.executeUpdate();
                if(stats > 0) {
                    System.out.println("Your Savings A/c has been closed Successfully !!!");
                    System.out.println("You can collect your Outstanding Balance of Rs " + newBalance + " from the Withdrawal Counter...");
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                rs.close();
                smt.close();
                con.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public SavingsAccount() {
        interestRate = 2.75f;
    }

    public void deposit(double amt) {
        if(!checkSavingsAccount()){
            System.out.println("Savings A/c does not exist for the given A/c No Provided !!!");
            return;
        }
        try{
           con = BankServerConnecter.getDatabaseConnection();
           smt = con.createStatement();
           rs = smt.executeQuery("select balance from savingsaccount where saccNo = " + accNo);
           if(rs.next()){
               double presentBalance = rs.getDouble("balance");
               double newBalance = presentBalance + amt;
               int status = smt.executeUpdate("update savingsaccount set balance = " + newBalance + " where saccNo = " + accNo);
               if(status > 0){
                   System.out.println("Amount deposited Successfully!!!");
               }else{
                   System.out.println("Unable to deposit money in Account... Please Try Again!!!");
               }
           }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                rs.close();
                smt.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void withdraw(double amt) {
        if(!checkSavingsAccount()){
            System.out.println("Savings A/c does not exist for the given A/c No Provided !!!");
            return;
        }
        try{
            con = BankServerConnecter.getDatabaseConnection();
            smt = con.createStatement();
            rs = smt.executeQuery("select balance from savingsaccount where saccNo = " + accNo);
            if(rs.next()){
                double presentBalance = rs.getDouble("balance");
                double newBalance = presentBalance;
                if(amt < presentBalance && presentBalance - amt >= 1000)
                    newBalance = presentBalance - amt;
                else{
                    System.out.println("Account has insufficient funds ...\n A minimum balance 0f Rs 1000 is needed .....");
                    return;
                }
                int status = smt.executeUpdate("update savingsaccount set balance = " + newBalance + " where saccNo = " + accNo);
                if(status > 0){
                    System.out.println("Amount Withdrawn Successfully!!!");
                }else{
                    System.out.println("Unable to withdraw money in Account... Please Try Again!!!");
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                rs.close();
                smt.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateCompoundInterest(long accNo){
        try{
            double balance = 0,newBalance;
            Timestamp t = null;
            con = BankServerConnecter.getDatabaseConnection();
            psmt = con.prepareStatement("select balance,savingsopeningdate from savingsaccount where saccNo = ?");
            psmt.setLong(1,accNo);
            rs  = psmt.executeQuery();
            if(rs.next()) {
                balance = rs.getDouble("balance");
                t = rs.getTimestamp("savingsopeningdate");
            }
            assert t != null;
            long ms = t.getTime();
            Date timestamp1 = new Date();
            Date timestamp2 = new Date(ms);
            LocalDate date1 = timestamp1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate date2 = timestamp2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            long yearsSaved = ChronoUnit.YEARS.between(date2, date1);
            if(yearsSaved >= 1) {
                newBalance = balance * Math.pow((1 + interestRate), yearsSaved);
                psmt = con.prepareStatement("update savingsaccount set balance = ? where saccNo = ?");
                psmt.setDouble(1,newBalance);
                psmt.setLong(2,accNo);
                psmt.executeUpdate();
            }
        }catch (SQLException | NullPointerException e){
            e.printStackTrace();
        }finally {
            try{
                rs.close();
                psmt.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public double fetchBalance() {
        if(!checkSavingsAccount()){
            System.out.println("Savings A/c does not exist for the given A/c No Provided !!!");
            return 0.0;
        }
        try{
            con = BankServerConnecter.getDatabaseConnection();
            smt = con.createStatement();
            rs = smt.executeQuery("select balance,savingsopeningdate from savingsaccount where saccNo = " + accNo);
            if(rs.next()){
                return rs.getDouble("balance");
            }else{
                System.out.println("Account does not exist !!!");
                return Double.NEGATIVE_INFINITY;
            }
        }catch (SQLException e){
            throw new RuntimeException(e.getMessage());
        }finally {
            try{
                rs.close();
                smt.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void getAccountDetails(){
        if(!checkSavingsAccount()){
            System.out.println("Savings A/c does not exist for the given A/c No Provided !!!");
            return;
        }
        try{
            con = BankServerConnecter.getDatabaseConnection();
            psmt = con.prepareStatement("select * from savingsaccount where saccNo = ?");
            psmt.setLong(1,accNo);
            rs = psmt.executeQuery();
            if(rs.next()){
                String fname = rs.getString("fname");
                String lname = rs.getString("lname");
                char gender = rs.getString("gender").charAt(0);
                double balance = rs.getDouble("balance");
                String nominee = rs.getString("nominee");
                Timestamp eventTime = rs.getTimestamp("savingsopeningdate");
                LocalDateTime localDateTime = eventTime.toLocalDateTime();
                int day = localDateTime.getDayOfMonth();
                int month = localDateTime.getMonthValue();
                int year = localDateTime.getYear();
                int h = localDateTime.getHour();
                int m = localDateTime.getMinute();
                int s = localDateTime.getSecond();
                System.out.println("******** Account Details ******");
                if(gender == 'M')
                    System.out.println("Account Holder Name :- Mr. " + fname + " " + lname);
                else if(gender == 'F')
                    System.out.println("Account Holder Name :- Ms. " + fname + " " + lname);
                System.out.println("Current Balance :- Rs " + balance);
                System.out.println("Nominee Name :- " + nominee);
                System.out.println("Date of Opening Account :- " + day + "/" + month + "/" + year);
                System.out.println("Time of Opening Account :- " + h + ":" + m + ":" + s);
                System.out.println("********      ********");
            }
            else{
                System.out.println("Account does not exist !!!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            try{
                rs.close();
                psmt.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean checkSavingsAccount() {
        try{
            con = BankServerConnecter.getDatabaseConnection();
            psmt = con.prepareStatement("select * from savingsaccount where saccNo = ?");
            psmt.setLong(1,accNo);
            rs = psmt.executeQuery();
            return rs.next();
        }catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            try{
                rs.close();
                psmt.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
