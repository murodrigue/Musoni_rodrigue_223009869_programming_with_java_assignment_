package bank.form;





import java.awt.BorderLayout;
import javax.swing.*;

import bank.panel.accountpanel;
import bank.panel.loanpanel;
import bank.panel.paymentpanel;
import bank.panel.userpanel;
import bank.panel.transactionpanel;

public class banksystem extends JFrame {

    JTabbedPane tabs = new JTabbedPane();

    public banksystem(String role, int userid) {
        setTitle("Banking Management System");
        setSize(900, 600);
        setLayout(new BorderLayout());

        // Role-based tabs
        if (role.equalsIgnoreCase("ADMIN")) {
            tabs.add("Users", new userpanel());
            
            
            tabs.add("Accounts", new accountpanel(userid));
            tabs.add("Transactions", new transactionpanel());
            tabs.add("Loans", new loanpanel());
            tabs.add("Payments", new paymentpanel());
        } else if (role.equalsIgnoreCase("STAFF")) {
            tabs.add("Accounts", new accountpanel(userid));
            tabs.add("Transactions", new transactionpanel());
            tabs.add("Loans", new loanpanel());
            tabs.add("Payments", new paymentpanel());
        } else if (role.equalsIgnoreCase("CUSTOMER")) {
            tabs.add("My Accounts", new accountpanel(userid));
            tabs.add("My Transactions", new transactionpanel());
            tabs.add("My Loans", new loanpanel());
            tabs.add("My Payments", new paymentpanel());
        }

        // Top panel with welcome message and logout
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome! Role: " + role + " | User ID: " + userid);
        JButton logoutBtn = new JButton("Logout");

        /* Uncomment and implement login form
        logoutBtn.addActionListener(e -> {
            dispose();
            new Loginform();
        });
        */

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
