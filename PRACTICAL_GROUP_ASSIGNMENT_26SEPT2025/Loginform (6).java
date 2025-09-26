package bank.form;





import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.*;


public class Loginform extends JFrame implements ActionListener {
    JTextField usertxt = new JTextField("Enter username"); 
    JPasswordField passtxt = new JPasswordField(); 
    JButton loginbtn = new JButton("login"); 
    JButton cancelbtn = new JButton("cancel");

    public Loginform() {
        setTitle("Banking System Login");
        setBounds(100, 100, 300, 200);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        
        userLabel.setBounds(50, 10, 100, 20);
        usertxt.setBounds(50, 30, 200, 25);
        passLabel.setBounds(50, 60, 100, 20);
        passtxt.setBounds(50, 80, 200, 25);
        loginbtn.setBounds(30, 120, 100, 30);
        cancelbtn.setBounds(150, 120, 100, 30);
        
        add(userLabel);
        add(usertxt);
        add(passLabel);
        add(passtxt);
        add(loginbtn);
        add(cancelbtn);
        
        loginbtn.addActionListener(this);
        cancelbtn.addActionListener(this);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelbtn) {
            System.exit(0);
        }
        
        if (e.getSource() == loginbtn) {
            Connection con = null;
            try {
                con = db.getConnection();
                String sql = "SELECT * FROM User WHERE Username = ? AND PasswordHash = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, usertxt.getText());
                ps.setString(2, new String(passtxt.getPassword()));
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    String role = rs.getString("Role");
                    int userid = rs.getInt("UserID");
                    JOptionPane.showMessageDialog(this, "Login successful! Welcome " + role);
                    dispose();
                    new BankingSystem(role, userid);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage());
            } finally {
                // Close connection
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        new Loginform();
    }
}

class BankingSystem extends JFrame {
    public BankingSystem(String role, int userid) {
        setTitle("Banking System - Welcome " + role);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel welcomeLabel = new JLabel("Welcome to Banking System! Role: " + role + ", UserID: " + userid, SwingConstants.CENTER);
        welcomeLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setText("Banking System Features:\n\n" +
                        "• User Management\n" +
                        "• Account Management\n" +
                        "• Transaction Processing\n" +
                        "• Loan Management\n" +
                        "• Payment Processing\n\n" +
                        "Logged in as: " + role + "\n" +
                        "User ID: " + userid);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Loginform();
            }
        });
        
        setLayout(new java.awt.BorderLayout());
        add(welcomeLabel, java.awt.BorderLayout.NORTH);
        add(new JScrollPane(infoArea), java.awt.BorderLayout.CENTER);
        add(logoutBtn, java.awt.BorderLayout.SOUTH);
        
        setVisible(true);
    }
}