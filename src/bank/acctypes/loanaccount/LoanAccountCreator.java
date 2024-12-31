package bank.acctypes.loanaccount;

import bankdatabase.BankServerConnecter;

import java.sql.*;
import java.time.LocalDateTime;

public class LoanAccountCreator {
    public static void createLoanAccount(int loanType,double amount,double termPeriod,long saccNo) {
        Connection con = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        LocalDateTime now = LocalDateTime.now();
        long loanNo;
        try{
            con = BankServerConnecter.getDatabaseConnection();
            psmt = con.prepareStatement("INSERT INTO LOAN_ACCOUNT(AMOUNT,TERM_PERIOD,LOAN_DATE,LOAN_TYPE,saccNo) VALUES(?,?,?,?,?)");
            psmt.setDouble(1,amount);
            psmt.setDouble(2,termPeriod);
            psmt.setTimestamp(3, Timestamp.valueOf(now));
            psmt.setInt(4,loanType);
            psmt.setLong(5,saccNo);
            int status = psmt.executeUpdate();
            if(status > 0) {
                System.out.println("Loan Account Opened Successfully !!");
                psmt = con.prepareStatement("SELECT LOAN_NO FROM LOAN_ACCOUNT ORDER BY LOAN_NO DESC LIMIT 1");
                rs = psmt.executeQuery();
                if(rs.next()){
                    loanNo = rs.getLong("LOAN_NO");
                    System.out.println("Please Note your Loan Account No. " + loanNo + " for Future Reference !!");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try{
                rs.close();
                psmt.close();
                con.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }
}
