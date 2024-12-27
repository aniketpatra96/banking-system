package bank.acctypes.savingsaccount;

import bankdatabase.BankServerConnecter;

import java.sql.*;
import java.time.LocalDateTime;

public abstract class SavingsAccountCreator {
    public static void openAccount(String fname,String lname,String nominee,char gender,double balance){
        Connection con = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        LocalDateTime now = LocalDateTime.now();
        long accNo;
        try{
            con = BankServerConnecter.getDatabaseConnection();
            psmt = con.prepareStatement("insert into savingsaccount (fname,lname,gender,balance,nominee,savingsopeningdate) values (?,?,?,?,?,?)");
            psmt.setString(1,fname);
            psmt.setString(2,lname);
            psmt.setString(3,String.valueOf(gender));
            psmt.setDouble(4,balance);
            psmt.setString(5,nominee);
            psmt.setTimestamp(6, Timestamp.valueOf(now));
            int rows = psmt.executeUpdate();
            if(rows > 0){
                System.out.println("Account Opened Successfully.....");
                psmt = con.prepareStatement("select saccNo from savingsaccount where fname = ? and lname = ?");
                psmt.setString(1,fname);
                psmt.setString(2,lname);
                rs = psmt.executeQuery();
                if(rs.next()) {
                    accNo = rs.getLong("saccNo");
                    System.out.println("Please Note your Savings A/c No. for future Reference ....");
                    System.out.println("Your A/c No. is " + accNo);
                }
            }else{
                System.out.println("Unable to add the customer !!!");
            }
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            try{
                rs.close();
                psmt.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

}
