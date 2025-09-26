package bank.panel;



import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class accountpanel extends JPanel {
    private int userId;
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtType, txtBalance;
    private JButton btnAddAccount;

    public accountpanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        // Table
        model = new DefaultTableModel(new String[]{"AccountID", "AccountNumber", "Type", "Balance", "CreatedAt"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        txtType = new JTextField();
        txtBalance = new JTextField();
        btnAddAccount = new JButton("Add Account");

        formPanel.add(new JLabel("Account Type:"));
        formPanel.add(txtType);
        formPanel.add(new JLabel("Initial Balance:"));
        formPanel.add(txtBalance);
        formPanel.add(new JLabel(""));
        formPanel.add(btnAddAccount);

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        // Load existing accounts
        loadAccounts();

        // Add account
        //btnAddAccount.addActionListener(e -> addAccount());
    }

    private void loadAccounts() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/banking_system?useSSL=false&serverTimezone=UTC",
                "root", "yourpass")) { // change credentials
            String sql = "SELECT AccountID, AccountNumber, Type, Balance, CreatedAt FROM Accounts WHERE UserID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("AccountID"));
                row.add(rs.getString("AccountNumber"));
                row.add(rs.getString("Type"));
                row.add(rs.getDouble("Balance"));
                row.add(rs.getTimestamp("CreatedAt"));
                model.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading accounts: " + ex.getMessage());
        }
    }

    private void addAccount() {
        String type = txtType.getText().trim();
        String balanceStr = txtBalance.getText().trim();

        if (type.isEmpty() || balanceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields.");
            return;
        }

        try {
            double balance = Double.parseDouble(balanceStr);

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/banking_system?useSSL=false&serverTimezone=UTC",
                    "root", "yourpass")) {

                String sql = "INSERT INTO Accounts (UserID, AccountNumber, Type, Balance, CreatedAt) VALUES (?, ?, ?, ?, NOW())";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, userId);
                ps.setString(2, "AC-" + System.currentTimeMillis()); // simple unique number
                ps.setString(3, type);
                ps.setDouble(4, balance);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Account added successfully!");
                loadAccounts(); // refresh
                txtType.setText("");
                txtBalance.setText("");

            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid balance.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding account: " + ex.getMessage());
        }
    }
}
