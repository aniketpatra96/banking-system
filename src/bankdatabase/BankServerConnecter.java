package bankdatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BankServerConnecter {
    public static Connection getDatabaseConnection(){
        try{
           Class.forName("com.mysql.cj.jdbc.Driver");
           return DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb","Aniket","Aniket@420");
        }catch (ClassNotFoundException | SQLException e){
            throw new RuntimeException(e);
        }
    }
}
