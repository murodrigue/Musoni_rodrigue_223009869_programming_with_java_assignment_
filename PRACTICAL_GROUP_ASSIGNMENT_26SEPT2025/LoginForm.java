package bankform;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblMessage;

    public LoginForm() {
        setTitle("Banking System - Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(30, 30, 80, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(120, 30, 150, 25);
        add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(30, 70, 80, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(120, 70, 150, 25);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(120, 110, 150, 25);
        add(btnLogin);

        lblMessage = new JLabel("");
        lblMessage.setBounds(30, 140, 300, 25);
        add(lblMessage);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    private void login() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
           
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb", "root", "");

            String sql = "SELECT Role FROM Users WHERE Username=? AND PasswordHash=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); 

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("Role");
                JOptionPane.showMessageDialog(this, 
                    "Login Successful! Welcome " + role);

                // 🔑 Role-based access
                if (role.equals("Admin")) {
                    new AdminDashboard();
                } else if (role.equals("Teller")) {
                    new TellerDashboard();
                } else {
                    new UserDashboard();
                }
                this.dispose();
            } else {
                lblMessage.setText("Invalid username or password!");
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            lblMessage.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginForm().setVisible(true);
    }
}