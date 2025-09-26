package bank.panel;


import javax.swing.*;

import bank.form.db;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class loanpanel extends JPanel {
    private JTextField loanNameField, loanTypeField, amountField;
    private JComboBox<String> statusComboBox, accountComboBox, categoryComboBox;
    private JFormattedTextField startDateField, endDateField;
    private JButton addButton, updateButton, deleteButton, searchButton, payButton;
    private JTable loanTable;
    private JScrollPane scrollPane;
    private int currentUserID; // For customer view

    // Constructor for admin/staff (all loans)
    public loanpanel () {
        this.currentUserID = -1; // -1 indicates admin/staff view
        initializeComponents();
        setupUI();
        loadLoanData();
        loadAccountData();
        loadCategoryData();
    }

    // Constructor for customer (only their loans)
    public loanpanel (int userid) {
        this.currentUserID = userid;
        initializeComponents();
        setupUI();
        loadLoanData();
        loadAccountData();
        loadCategoryData();
    }

    private void initializeComponents() {
        // Form components
        loanNameField = new JTextField(15);
        loanTypeField = new JTextField(15);
        amountField = new JTextField(15);
        
        statusComboBox = new JComboBox<>(new String[]{"ACTIVE", "CLOSED", "DEFAULTED"});
        accountComboBox = new JComboBox<>();
        categoryComboBox = new JComboBox<>();
        
        // Date fields
        startDateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        endDateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        startDateField.setValue(new Date());
        endDateField.setValue(new Date());

        // Buttons
        addButton = new JButton("Add Loan");
        updateButton = new JButton("Update Loan");
        deleteButton = new JButton("Delete Loan");
        searchButton = new JButton("Search");
        payButton = new JButton("Make Payment");

        // Table
        loanTable = new JTable();
        scrollPane = new JScrollPane(loanTable);
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Loan Management"));

        formPanel.add(new JLabel("Loan Name:"));
        formPanel.add(loanNameField);
        formPanel.add(new JLabel("Loan Type:"));
        formPanel.add(loanTypeField);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Account:"));
        formPanel.add(accountComboBox);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryComboBox);
        formPanel.add(new JLabel("Start Date (yyyy-mm-dd):"));
        formPanel.add(startDateField);
        formPanel.add(new JLabel("End Date (yyyy-mm-dd):"));
        formPanel.add(endDateField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(payButton);

        // Main layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add event listeners
        addButton.addActionListener(new AddLoanListener());
        updateButton.addActionListener(new UpdateLoanListener());
        deleteButton.addActionListener(new DeleteLoanListener());
        searchButton.addActionListener(new SearchLoanListener());
        payButton.addActionListener(new MakePaymentListener());
        //loanTable.getSelectionModel().addListSelectionListener(e -> populateFormFromTable());
    }

    private void loadLoanData() {
        try (Connection con = db.getConnection()) {
            String sql;
            PreparedStatement ps;
            
            if (currentUserID == -1) {
                // Admin/Staff view - all loans
                sql = "SELECT l.LoanID, l.LoanName, l.LoanType, l.Amount, l.StartDate, l.EndDate, " +
                      "l.Status, a.AccountName, lc.CategoryName, u.Username " +
                      "FROM Loan l " +
                      "JOIN Account a ON l.AccountID = a.AccountID " +
                      "JOIN LoanCategory lc ON l.LoanCategoryID = lc.LoanCategoryID " +
                      "JOIN User u ON a.UserID = u.UserID " +
                      "ORDER BY l.CreatedAt DESC";
                ps = con.prepareStatement(sql);
            } else {
                // Customer view - only their loans
                sql = "SELECT l.LoanID, l.LoanName, l.LoanType, l.Amount, l.StartDate, l.EndDate, " +
                      "l.Status, a.AccountName, lc.CategoryName " +
                      "FROM Loan l " +
                      "JOIN Account a ON l.AccountID = a.AccountID " +
                      "JOIN LoanCategory lc ON l.LoanCategoryID = lc.LoanCategoryID " +
                      "WHERE a.UserID = ? " +
                      "ORDER BY l.CreatedAt DESC";
                ps = con.prepareStatement(sql);
                ps.setInt(1, currentUserID);
            }
            
            ResultSet rs = ps.executeQuery();
            loanTable.setModel(buildTableModel(rs));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading loans: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadAccountData() {
        try (Connection con = db.getConnection()) {
            String sql;
            PreparedStatement ps;
            
            if (currentUserID == -1) {
                // Admin/Staff view - all accounts
                sql = "SELECT AccountID, AccountName FROM Account ORDER BY AccountName";
                ps = con.prepareStatement(sql);
            } else {
                // Customer view - only their accounts
                sql = "SELECT AccountID, AccountName FROM Account WHERE UserID = ? ORDER BY AccountName";
                ps = con.prepareStatement(sql);
                ps.setInt(1, currentUserID);
            }
            
            ResultSet rs = ps.executeQuery();
            accountComboBox.removeAllItems();
            accountComboBox.addItem("Select Account");
            while (rs.next()) {
                accountComboBox.addItem(rs.getString("AccountName") + " (ID: " + rs.getInt("AccountID") + ")");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading accounts: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadCategoryData() {
        try (Connection con = db.getConnection()) {
            String sql = "SELECT LoanCategoryID, CategoryName FROM LoanCategory ORDER BY CategoryName";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            categoryComboBox.removeAllItems();
            categoryComboBox.addItem("Select Category");
            while (rs.next()) {
                categoryComboBox.addItem(rs.getString("CategoryName") + " (ID: " + rs.getInt("LoanCategoryID") + ")");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void populateFormFromTable() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow >= 0) {
            loanNameField.setText(loanTable.getValueAt(selectedRow, 1).toString());
            loanTypeField.setText(loanTable.getValueAt(selectedRow, 2).toString());
            amountField.setText(loanTable.getValueAt(selectedRow, 3).toString());
            // Note: Account and Category would need more complex handling
        }
    }

    private javax.swing.table.DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        java.sql.ResultSetMetaData metaData = rs.getMetaData();

        int columnCount = metaData.getColumnCount();
        java.util.Vector<String> columnNames = new java.util.Vector<>();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        java.util.Vector<java.util.Vector<Object>> data = new java.util.Vector<>();
        while (rs.next()) {
            java.util.Vector<Object> row = new java.util.Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }

        return new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // Helper method to extract ID from combo box selection
    private int extractIDFromCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected != null && selected.contains("(ID: ")) {
            try {
                return Integer.parseInt(selected.split("\\(ID: ")[1].replace(")", ""));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    // Action Listeners
    private class AddLoanListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try (Connection con = db.getConnection()) {
                int accountID = extractIDFromCombo(accountComboBox);
                int categoryID = extractIDFromCombo(categoryComboBox);
                
                if (accountID == -1 || categoryID == -1) {
                    JOptionPane.showMessageDialog(loanpanel .this, "Please select valid account and category");
                    return;
                }

                String sql = "INSERT INTO Loan (AccountID, LoanCategoryID, LoanName, LoanType, StartDate, EndDate, Status, Amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, accountID);
                ps.setInt(2, categoryID);
                ps.setString(3, loanNameField.getText());
                ps.setString(4, loanTypeField.getText());
                ps.setDate(5, java.sql.Date.valueOf(startDateField.getText()));
                ps.setDate(6, java.sql.Date.valueOf(endDateField.getText()));
                ps.setString(7, statusComboBox.getSelectedItem().toString());
                ps.setBigDecimal(8, new java.math.BigDecimal(amountField.getText()));
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(loanpanel .this, "Loan added successfully!");
                    clearForm();
                    loadLoanData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loanpanel .this, "Error adding loan: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private class UpdateLoanListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = loanTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(loanpanel .this, "Please select a loan to update");
                return;
            }

            int loanID = Integer.parseInt(loanTable.getValueAt(selectedRow, 0).toString());
            
            try (Connection con = db.getConnection()) {
                String sql = "UPDATE Loan SET LoanName=?, LoanType=?, Amount=?, Status=? WHERE LoanID=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, loanNameField.getText());
                ps.setString(2, loanTypeField.getText());
                ps.setBigDecimal(3, new java.math.BigDecimal(amountField.getText()));
                ps.setString(4, statusComboBox.getSelectedItem().toString());
                ps.setInt(5, loanID);
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(loanpanel .this, "Loan updated successfully!");
                    loadLoanData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loanpanel .this, "Error updating loan: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private class DeleteLoanListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = loanTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(loanpanel .this, "Please select a loan to delete");
                return;
            }

            int loanID = Integer.parseInt(loanTable.getValueAt(selectedRow, 0).toString());
            String loanName = loanTable.getValueAt(selectedRow, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(loanpanel .this, 
                "Are you sure you want to delete loan: " + loanName + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection con = db.getConnection()) {
                    String sql = "DELETE FROM Loan WHERE LoanID=?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, loanID);
                    
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(loanpanel .this, "Loan deleted successfully!");
                        clearForm();
                        loadLoanData();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(loanpanel .this, "Error deleting loan: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    private class SearchLoanListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchTerm = JOptionPane.showInputDialog(loanpanel .this, "Enter loan name to search:");
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                try (Connection con = db.getConnection()) {
                    String sql = "SELECT l.LoanID, l.LoanName, l.LoanType, l.Amount, l.StartDate, l.EndDate, " +
                                "l.Status, a.AccountName, lc.CategoryName " +
                                "FROM Loan l " +
                                "JOIN Account a ON l.AccountID = a.AccountID " +
                                "JOIN LoanCategory lc ON l.LoanCategoryID = lc.LoanCategoryID " +
                                "WHERE l.LoanName LIKE ? ORDER BY l.LoanName";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, "%" + searchTerm + "%");
                    ResultSet rs = ps.executeQuery();
                    
                    loanTable.setModel(buildTableModel(rs));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(loanpanel .this, "Error searching loans: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    private class MakePaymentListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = loanTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(loanpanel .this, "Please select a loan to make payment");
                return;
            }

            int loanID = Integer.parseInt(loanTable.getValueAt(selectedRow, 0).toString());
            String loanName = loanTable.getValueAt(selectedRow, 1).toString();
            double amount = Double.parseDouble(loanTable.getValueAt(selectedRow, 3).toString());

            String paymentAmount = JOptionPane.showInputDialog(loanpanel .this, 
                "Enter payment amount for loan: " + loanName + "\nLoan Amount: " + amount);
            
            if (paymentAmount != null && !paymentAmount.trim().isEmpty()) {
                try {
                    double payAmount = Double.parseDouble(paymentAmount);
                    // Here you would implement the payment logic
                    JOptionPane.showMessageDialog(loanpanel .this, 
                        "Payment of " + payAmount + " for loan " + loanName + " processed successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(loanpanel .this, "Please enter a valid amount");
                }
            }
        }
    }

    private void clearForm() {
        loanNameField.setText("");
        loanTypeField.setText("");
        amountField.setText("");
        startDateField.setValue(new Date());
        endDateField.setValue(new Date());
        statusComboBox.setSelectedIndex(0);
        accountComboBox.setSelectedIndex(0);
        categoryComboBox.setSelectedIndex(0);
    }
}