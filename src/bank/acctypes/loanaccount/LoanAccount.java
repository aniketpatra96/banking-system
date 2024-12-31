package bank.acctypes.loanaccount;

import bankdatabase.BankServerConnecter;

import java.sql.*;
import java.time.LocalDateTime;

public class LoanAccount {
    private long loanNo;
    Connection con;
    PreparedStatement psmt;
    ResultSet rs;

    public LoanAccount(long loanNo) {
        this.loanNo = loanNo;
    }

    public static boolean checkLoanAccount(long laccNo) {
        Connection con = null;
        Statement smt = null;
        ResultSet rs = null;
        try{
            con = BankServerConnecter.getDatabaseConnection();
            smt = con.createStatement();
            rs = smt.executeQuery("SELECT * FROM LOAN_ACCOUNT WHERE LOAN_NO = " + laccNo);
            return rs.next();
        }catch (SQLException e) {
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



    public void getLoanDetails() {
        double amount,termPeriod,interestRate;
        String loanName,fname,lname;
        Timestamp tsmp;
        char gender;
        try {
            con = BankServerConnecter.getDatabaseConnection();
            psmt = con.prepareStatement("SELECT * FROM LOAN_ACCOUNT L JOIN LOAN_TYPES T ON L.LOAN_TYPE = T.LOAN_TYPE JOIN SAVINGSACCOUNT S ON L.saccNo = S.saccNo AND LOAN_NO = ?");
            psmt.setLong(1,loanNo);
            rs = psmt.executeQuery();
            if(rs.next()) {
                gender = rs.getString("gender").charAt(0);
                fname = rs.getString("fname");
                lname = rs.getString("lname");
                amount = rs.getDouble("AMOUNT");
                termPeriod = rs.getDouble("TERM_PERIOD");
                interestRate = rs.getDouble("INTEREST_RATE");
                loanName = rs.getString("LOAN_NAME");
                tsmp = rs.getTimestamp("LOAN_DATE");
                LocalDateTime localDateTime = tsmp.toLocalDateTime();
                int day = localDateTime.getDayOfMonth();
                int month = localDateTime.getMonthValue();
                int year = localDateTime.getYear();
                int h = localDateTime.getHour();
                int m = localDateTime.getMinute();
                int s = localDateTime.getSecond();
                if(gender == 'M')
                    System.out.println("Account Holder :- Mr. " + fname + " " + lname);
                else
                    System.out.println("Account Holder :- Mrs./Ms. " + fname + " " + lname);
                System.out.println("Loan Type :- " + loanName);
                System.out.println("Loan Amount :- Rs " + amount);
                System.out.println("Term Period :- " + termPeriod);
                System.out.println("Interest Rate :- " + interestRate);
                System.out.println("Date of Opening Account :- " + day + "/" + month + "/" + year);
                System.out.println("Time of Opening Account :- " + h + ":" + m + ":" + s);
                System.out.println("********      ********");
            }else {
                System.out.println("Loan Account Does not exist !!!");
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                rs.close();
                psmt.close();
                con.close();
            }catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
