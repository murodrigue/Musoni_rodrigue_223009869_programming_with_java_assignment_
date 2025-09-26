package bank.panel;



import javax.swing.*;

import bank.form.db;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class paymentpanel extends JPanel {
    private JTextField referenceNoField, amountField;
    private JComboBox<String> methodComboBox, statusComboBox, transactionComboBox, loanComboBox;
    private JFormattedTextField dateField;
    private JButton addButton, updateButton, deleteButton, searchButton;
    private JTable paymentTable;
    private JScrollPane scrollPane;
    private int currentUserID;

    public paymentpanel() {
        this.currentUserID = -1;
        initializeComponents();
        setupUI();
        loadPaymentData();
        loadTransactionData();
        loadLoanData();
    }

    public paymentpanel(int userid) {
        this.currentUserID = userid;
        initializeComponents();
        setupUI();
        loadPaymentData();
        loadTransactionData();
        loadLoanData();
    }

    private void initializeComponents() {
        referenceNoField = new JTextField(15);
        amountField = new JTextField(15);
        methodComboBox = new JComboBox<>(new String[]{"CASH", "CARD", "TRANSFER", "OTHER"});
        statusComboBox = new JComboBox<>(new String[]{"PENDING", "COMPLETED", "FAILED"});
        transactionComboBox = new JComboBox<>();
        loanComboBox = new JComboBox<>();
        
        dateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        dateField.setValue(new Date());

        addButton = new JButton("Add Payment");
        updateButton = new JButton("Update Payment");
        deleteButton = new JButton("Delete Payment");
        searchButton = new JButton("Search");

        paymentTable = new JTable();
        scrollPane = new JScrollPane(paymentTable);
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Payment Management"));

        formPanel.add(new JLabel("Reference No:"));
        formPanel.add(referenceNoField);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Date (yyyy-mm-dd):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Method:"));
        formPanel.add(methodComboBox);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        formPanel.add(new JLabel("Transaction:"));
        formPanel.add(transactionComboBox);
        formPanel.add(new JLabel("Loan (Optional):"));
        formPanel.add(loanComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(new AddPaymentListener());
        updateButton.addActionListener(new UpdatePaymentListener());
        deleteButton.addActionListener(new DeletePaymentListener());
        searchButton.addActionListener(new SearchPaymentListener());
       // paymentTable.getSelectionModel().addListSelectionListener(e -> populateFormFromTable());
    }

    private void loadPaymentData() {
        try (Connection con = db.getConnection()) {
            String sql;
            PreparedStatement ps;
            
            if (currentUserID == -1) {
                sql = "SELECT p.PaymentID, p.ReferenceNo, p.Amount, p.Date, p.Method, p.Status, " +
                      "t.Title as TransactionTitle, l.LoanName, u.Username " +
                      "FROM Payment p " +
                      "JOIN Transaction t ON p.TransactionID = t.TransactionID " +
                      "LEFT JOIN Loan l ON p.LoanID = l.LoanID " +
                      "JOIN User u ON p.CreatedBy = u.UserID " +
                      "ORDER BY p.Date DESC";
                ps = con.prepareStatement(sql);
            } else {
                sql = "SELECT p.PaymentID, p.ReferenceNo, p.Amount, p.Date, p.Method, p.Status, " +
                      "t.Title as TransactionTitle, l.LoanName " +
                      "FROM Payment p " +
                      "JOIN Transaction t ON p.TransactionID = t.TransactionID " +
                      "LEFT JOIN Loan l ON p.LoanID = l.LoanID " +
                      "WHERE p.CreatedBy = ? " +
                      "ORDER BY p.Date DESC";
                ps = con.prepareStatement(sql);
                ps.setInt(1, currentUserID);
            }
            
            ResultSet rs = ps.executeQuery();
            paymentTable.setModel(buildTableModel(rs));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading payments: " + ex.getMessage());
        }
    }

    private void loadTransactionData() {
        try (Connection con = db.getConnection()) {
            String sql;
            PreparedStatement ps;
            
            if (currentUserID == -1) {
                sql = "SELECT TransactionID, Title, Amount FROM Transaction ORDER BY Date DESC";
                ps = con.prepareStatement(sql);
            } else {
                sql = "SELECT t.TransactionID, t.Title, t.Amount " +
                      "FROM Transaction t " +
                      "JOIN Account a ON t.AccountID = a.AccountID " +
                      "WHERE a.UserID = ? ORDER BY t.Date DESC";
                ps = con.prepareStatement(sql);
                ps.setInt(1, currentUserID);
            }
            
            ResultSet rs = ps.executeQuery();
            transactionComboBox.removeAllItems();
            transactionComboBox.addItem("Select Transaction");
            while (rs.next()) {
                transactionComboBox.addItem(rs.getString("Title") + " - $" + rs.getBigDecimal("Amount") + 
                                           " (ID: " + rs.getInt("TransactionID") + ")");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + ex.getMessage());
        }
    }

    private void loadLoanData() {
        try (Connection con = db.getConnection()) {
            String sql;
            PreparedStatement ps;
            
            if (currentUserID == -1) {
                sql = "SELECT LoanID, LoanName, Amount FROM Loan ORDER BY LoanName";
                ps = con.prepareStatement(sql);
            } else {
                sql = "SELECT l.LoanID, l.LoanName, l.Amount " +
                      "FROM Loan l " +
                      "JOIN Account a ON l.AccountID = a.AccountID " +
                      "WHERE a.UserID = ? ORDER BY l.LoanName";
                ps = con.prepareStatement(sql);
                ps.setInt(1, currentUserID);
            }
            
            ResultSet rs = ps.executeQuery();
            loanComboBox.removeAllItems();
            loanComboBox.addItem("No Loan (Optional)");
            while (rs.next()) {
                loanComboBox.addItem(rs.getString("LoanName") + " - $" + rs.getBigDecimal("Amount") + 
                                    " (ID: " + rs.getInt("LoanID") + ")");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading loans: " + ex.getMessage());
        }
    }

    private void populateFormFromTable() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow >= 0) {
            referenceNoField.setText(paymentTable.getValueAt(selectedRow, 1).toString());
            amountField.setText(paymentTable.getValueAt(selectedRow, 2).toString());
            dateField.setValue(paymentTable.getValueAt(selectedRow, 3));
            methodComboBox.setSelectedItem(paymentTable.getValueAt(selectedRow, 4).toString());
            statusComboBox.setSelectedItem(paymentTable.getValueAt(selectedRow, 5).toString());
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

    private class AddPaymentListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try (Connection con = db.getConnection()) {
                int transactionID = extractIDFromCombo(transactionComboBox);
                int loanID = extractIDFromCombo(loanComboBox);
                
                if (transactionID == -1) {
                    JOptionPane.showMessageDialog(paymentpanel.this, "Please select a valid transaction");
                    return;
                }

                String sql = "INSERT INTO Payment (TransactionID, LoanID, ReferenceNo, Amount, Date, Method, Status, CreatedBy) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, transactionID);
                ps.setInt(2, loanID == -1 ? null : loanID);
                ps.setString(3, referenceNoField.getText());
                ps.setBigDecimal(4, new java.math.BigDecimal(amountField.getText()));
                ps.setDate(5, java.sql.Date.valueOf(dateField.getText()));
                ps.setString(6, methodComboBox.getSelectedItem().toString());
                ps.setString(7, statusComboBox.getSelectedItem().toString());
                ps.setInt(8, currentUserID != -1 ? currentUserID : 1);
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(paymentpanel.this, "Payment added successfully!");
                    clearForm();
                    loadPaymentData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(paymentpanel.this, "Error adding payment: " + ex.getMessage());
            }
        }
    }

    private class UpdatePaymentListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = paymentTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(paymentpanel.this, "Please select a payment to update");
                return;
            }

            int paymentID = Integer.parseInt(paymentTable.getValueAt(selectedRow, 0).toString());
            
            try (Connection con = db.getConnection()) {
                String sql = "UPDATE Payment SET ReferenceNo=?, Amount=?, Date=?, Method=?, Status=? WHERE PaymentID=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, referenceNoField.getText());
                ps.setBigDecimal(2, new java.math.BigDecimal(amountField.getText()));
                ps.setDate(3, java.sql.Date.valueOf(dateField.getText()));
                ps.setString(4, methodComboBox.getSelectedItem().toString());
                ps.setString(5, statusComboBox.getSelectedItem().toString());
                ps.setInt(6, paymentID);
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(paymentpanel.this, "Payment updated successfully!");
                    loadPaymentData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(paymentpanel.this, "Error updating payment: " + ex.getMessage());
            }
        }
    }

    private class DeletePaymentListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = paymentTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(paymentpanel.this, "Please select a payment to delete");
                return;
            }

            int paymentID = Integer.parseInt(paymentTable.getValueAt(selectedRow, 0).toString());
            String referenceNo = paymentTable.getValueAt(selectedRow, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(paymentpanel.this, 
                "Are you sure you want to delete payment: " + referenceNo + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection con = db.getConnection()) {
                    String sql = "DELETE FROM Payment WHERE PaymentID=?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, paymentID);
                    
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(paymentpanel.this, "Payment deleted successfully!");
                        clearForm();
                        loadPaymentData();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(paymentpanel.this, "Error deleting payment: " + ex.getMessage());
                }
            }
        }
    }

    private class SearchPaymentListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchTerm = JOptionPane.showInputDialog(paymentpanel.this, "Enter reference number to search:");
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                try (Connection con =db.getConnection()) {
                    String sql = "SELECT p.PaymentID, p.ReferenceNo, p.Amount, p.Date, p.Method, p.Status, " +
                                "t.Title as TransactionTitle, l.LoanName " +
                                "FROM Payment p " +
                                "JOIN Transaction t ON p.TransactionID = t.TransactionID " +
                                "LEFT JOIN Loan l ON p.LoanID = l.LoanID " +
                                "WHERE p.ReferenceNo LIKE ? ORDER BY p.Date DESC";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, "%" + searchTerm + "%");
                    ResultSet rs = ps.executeQuery();
                    
                    paymentTable.setModel(buildTableModel(rs));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(paymentpanel.this, "Error searching payments: " + ex.getMessage());
                }
            }
        }
    }

    private void clearForm() {
        referenceNoField.setText("");
        amountField.setText("");
        dateField.setValue(new Date());
        methodComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedIndex(0);
        transactionComboBox.setSelectedIndex(0);
        loanComboBox.setSelectedIndex(0);
    }
}