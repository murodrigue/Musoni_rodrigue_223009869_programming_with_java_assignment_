package bank.form;

import java.sql.Connection;
import java.sql.DriverManager;

public class db {
    public static Connection getConnection() throws Exception {
       try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Class.forName("com.mysql.jdbc.Driver");
        }
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/banking_system", "root", "");
    }
}