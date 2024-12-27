package bank.acctypes.fixeddeposit;

import bankdatabase.BankServerConnecter;

import java.sql.*;
import java.time.LocalDateTime;

public abstract class FixedDepositCreator {

    private static double calculateInterestRate(double termPeriod) {
        if(termPeriod >= 2.5 && termPeriod < 5) {
            return 5.25;
        }
        else if(termPeriod >= 5 && termPeriod < 10) {
            return 7.15;
        }
        else if(termPeriod >= 10 && termPeriod < 20) {
            return 9.1;
        }
        else if(termPeriod >=20) {
            return 10.25;
        }
        else {
            return 0.0;
        }
    }

    public static void openFixedDeposit(long saccNo,String nominee,double amount,double termPeriod){
        if(amount < 200000 && termPeriod < 2.5) {
            System.out.println("Minimum amount for fixed deposit is Rs 200000 and Term Period must be greater than 2.5 !!! \n Please Try Again !!!!");
            return;
        }
        Connection con = BankServerConnecter.getDatabaseConnection();
        PreparedStatement psmt = null;
        Statement smt = null;
        ResultSet rs = null;
        LocalDateTime now = LocalDateTime.now();
        Timestamp today = Timestamp.valueOf(now);
        long fdAccNo = 0L;
        double interestRate;
        try{
            psmt = con.prepareStatement("insert into fixeddeposit (amount,nominee,fixedopeningdate,termperiod,interestrate,saccNo) values (?,?,?,?,?,?)");
            psmt.setDouble(1,amount);
            psmt.setString(2,nominee);
            psmt.setTimestamp(3, today);
            psmt.setDouble(4,termPeriod);
            interestRate = calculateInterestRate(termPeriod);
            if(interestRate < 5.25) {
                System.out.println("Unable to open Fixed Deposit for the specified Term Period !!!");
                return;
            }
            psmt.setDouble(5,interestRate);
            psmt.setLong(6,saccNo);
            int rows = psmt.executeUpdate();
            if(rows > 0){
                System.out.println("Fixed Deposit Opened Successfully.....");
                psmt = con.prepareStatement("select fdaccNo from fixeddeposit where saccNo = ? order by fixedopeningdate desc limit 1");
                psmt.setLong(1,saccNo);
                rs = psmt.executeQuery();
                if(rs.next())
                    fdAccNo = rs.getLong("fdaccNo");
                System.out.println("Please Note your Fixed Deposit A/c No. for future Reference ....");
                System.out.println("Your A/c No. is " + fdAccNo);
            }else{
                System.out.println("Unable to add the customer !!!");
            }
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            try{
                if(rs != null)
                    rs.close();
                if(smt != null)
                    smt.close();
                if(psmt != null)
                    psmt.close();
                if(con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
