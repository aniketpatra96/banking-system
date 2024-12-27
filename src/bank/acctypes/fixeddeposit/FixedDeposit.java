package bank.acctypes.fixeddeposit;

import bank.BankDriver;
import bankdatabase.BankServerConnecter;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class FixedDeposit {
    private long fdaccNo;
    private Connection con;
    private Statement smt;
    private ResultSet rs;
    private PreparedStatement psmt;
    public FixedDeposit(long fdaccNo) {
        this.fdaccNo = fdaccNo;
        generateCompoundInterest();
        closeFixedDeposit();
    }

    private void closeFixedDeposit() {
        Timestamp tsmp;
        double termPeriod,amount,interestRate,newAmount;
        long saccNo;
        try{
            con = BankServerConnecter.getDatabaseConnection();
            psmt = con.prepareStatement("select termperiod,amount,fixedopeningdate,saccNo,interestrate from fixeddeposit where fdaccNo = ?");
            psmt.setLong(1,fdaccNo);
            rs = psmt.executeQuery();
            if(rs.next()){
                tsmp = rs.getTimestamp("fixedopeningdate");
                termPeriod = rs.getDouble("termperiod");
                amount = rs.getDouble("amount");
                saccNo = rs.getLong("saccNo");
                interestRate = rs.getDouble("interestrate");
                Date timestamp1 = new Date();
                Date timestamp2 = new Date(tsmp.getTime());
                LocalDate date1 = timestamp1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate date2 = timestamp2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                long yearsSaved = ChronoUnit.YEARS.between(date2, date1);
                if(yearsSaved == termPeriod){
                    newAmount = amount * Math.pow((1 + interestRate), yearsSaved);
                    psmt = con.prepareStatement("delete from fixeddeposit where fdaccNo = ?");
                    psmt.setLong(1,fdaccNo);
                    int status = psmt.executeUpdate();
                    if(status > 0){
                        con.close();
                        smt.close();
                        rs.close();
                        con = BankServerConnecter.getDatabaseConnection();
                        psmt = con.prepareStatement("update savingsaccount set balance = ? where saccNo = ?");
                        psmt.setDouble(1,newAmount);
                        psmt.setLong(2,saccNo);
                        int stats = psmt.executeUpdate();
                        if(stats > 0) {
                            System.out.println("Your Fixed Deposit has been Successfully Closed !!!\n Your amount was added to your Savings A/c registered with us !!!");
                            BankDriver.startSession();
                        }
                    }
                }
            }
        }catch (SQLException e){
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

    public void getFDDetails(){
        try{
            con = BankServerConnecter.getDatabaseConnection();
            smt = con.createStatement();
            rs = smt.executeQuery("select * from fixeddeposit where fdaccNo = " + fdaccNo);
            if(rs.next()){
                String nominee = rs.getString("nominee");
                double amount = rs.getDouble("amount");
                Timestamp eventTime = rs.getTimestamp("fixedopeningdate");
                double termPeriod = rs.getDouble("termperiod");
                double interestRate = rs.getDouble("interestrate");
                long saccNo = rs.getLong("saccNo");
                LocalDateTime localDateTime = eventTime.toLocalDateTime();
                int day = localDateTime.getDayOfMonth();
                int month = localDateTime.getMonthValue();
                int year = localDateTime.getYear();
                int h = localDateTime.getHour();
                int m = localDateTime.getMinute();
                int s = localDateTime.getSecond();
                System.out.println("Deposit Amount :- Rs " + amount);
                System.out.println("Nominee Name :- " + nominee);
                System.out.println("Term Period :- " + termPeriod);
                System.out.println("Interest Rate :- " + interestRate);
                System.out.println("Date of Opening Account :- " + day + "/" + month + "/" + year);
                System.out.println("Time of Opening Account :- " + h + ":" + m + ":" + s);
                System.out.println("Savings Account No. :- " + saccNo);
                System.out.println("********      ********");
            }else{
                System.out.println("No Fixed Deposit Found for the given A/c No. !!!!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            try{
                rs.close();
                smt.close();
                con.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    public static boolean checkFixedDeposit(long fdaccNo) {
        Connection con = null;
        Statement smt = null;
        ResultSet rs = null;
        try{
            con = BankServerConnecter.getDatabaseConnection();
            smt = con.createStatement();
            rs = smt.executeQuery("select * from fixeddeposit where fdaccNo = " + fdaccNo);
            return rs.next();
        }catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            try{
                if(rs != null)
                    rs.close();
                if(smt != null)
                    smt.close();
                if(con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateCompoundInterest(){
        try{
            double balance = 0,newBalance,interestRate = 0.0,termPeriod = 0.0;
            Timestamp t = null;
            con = BankServerConnecter.getDatabaseConnection();
            psmt = con.prepareStatement("select amount,fixedopeningdate,interestrate,termperiod from fixeddeposit where fdaccNo = ?");
            psmt.setLong(1,fdaccNo);
            rs  = psmt.executeQuery();
            if(rs.next()) {
                balance = rs.getDouble("amount");
                t = rs.getTimestamp("fixedopeningdate");
                interestRate = rs.getDouble("interestrate");
                termPeriod = rs.getDouble("termperiod");
            }
            assert t != null;
            long ms = t.getTime();
            Date timestamp1 = new Date();
            Date timestamp2 = new Date(ms);
            LocalDate date1 = timestamp1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate date2 = timestamp2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            long yearsSaved = ChronoUnit.YEARS.between(date2, date1);
            if(yearsSaved >= 1 && yearsSaved <= termPeriod) {
                newBalance = balance * Math.pow((1 + interestRate), yearsSaved);
                psmt = con.prepareStatement("update fixeddeposit set amount = ? where fdaccNo = ?");
                psmt.setDouble(1,newBalance);
                psmt.setLong(2,fdaccNo);
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

}
