package bank.panel;

import javax.swing.*;

import bank.form.db;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class transactionpanel extends JPanel {
    private JTextField titleField, amountField;
    private JComboBox<String> statusComboBox, accountComboBox;
    private JTextArea notesArea;
    private JFormattedTextField dateField;
    private JButton addButton, updateButton, deleteButton, searchButton;
    private JTable transactionTable;
    private JScrollPane scrollPane;
    private int currentUserID;

    public transactionpanel() {
        this.currentUserID = -1;
        initializeComponents();
        setupUI();
        loadTransactionData();
        loadAccountData();
    }

    public transactionpanel(int userid) {
        this.currentUserID = userid;
        initializeComponents();
        setupUI();
        loadTransactionData();
        loadAccountData();
    }

    private void initializeComponents() {
        titleField = new JTextField(15);
        amountField = new JTextField(15);
        notesArea = new JTextArea(3, 15);
        statusComboBox = new JComboBox<>(new String[]{"PENDING", "COMPLETED", "FAILED"});
        accountComboBox = new JComboBox<>();
        
        dateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        dateField.setValue(new Date());

        addButton = new JButton("Add Transaction");
        updateButton = new JButton("Update Transaction");
        deleteButton = new JButton("Delete Transaction");
        searchButton = new JButton("Search");

        transactionTable = new JTable();
        scrollPane = new JScrollPane(transactionTable);
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Transaction Management"));

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Date (yyyy-mm-dd):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        formPanel.add(new JLabel("Account:"));
        formPanel.add(accountComboBox);
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(new JScrollPane(notesArea));

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

        addButton.addActionListener(new AddTransactionListener());
        updateButton.addActionListener(new UpdateTransactionListener());
        deleteButton.addActionListener(new DeleteTransactionListener());
        searchButton.addActionListener(new SearchTransactionListener());
       // transactionTable.getSelectionModel().addListSelectionListener(e -> populateFormFromTable());
    }

    private void loadTransactionData() {
        try (Connection con = db.getConnection()) {
            String sql;
            PreparedStatement ps;
            
            if (currentUserID == -1) {
                sql = "SELECT t.TransactionID, t.Title, t.Amount, t.Date, t.Status, " +
                      "a.AccountName, u.Username, t.Notes " +
                      "FROM Transaction t " +
                      "JOIN Account a ON t.AccountID = a.AccountID " +
                      "JOIN User u ON a.UserID = u.UserID " +
                      "ORDER BY t.Date DESC";
                ps = con.prepareStatement(sql);
            } else {
                sql = "SELECT t.TransactionID, t.Title, t.Amount, t.Date, t.Status, " +
                      "a.AccountName, t.Notes " +
                      "FROM Transaction t " +
                      "JOIN Account a ON t.AccountID = a.AccountID " +
                      "WHERE a.UserID = ? " +
                      "ORDER BY t.Date DESC";
                ps = con.prepareStatement(sql);
                ps.setInt(1, currentUserID);
            }
            
            ResultSet rs = ps.executeQuery();
            transactionTable.setModel(buildTableModel(rs));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + ex.getMessage());
        }
    }

    private void loadAccountData() {
        try (Connection con = db.getConnection()) {
            String sql;
            PreparedStatement ps;
            
            if (currentUserID == -1) {
                sql = "SELECT AccountID, AccountName FROM Account ORDER BY AccountName";
                ps = con.prepareStatement(sql);
            } else {
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
        }
    }

    private void populateFormFromTable() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow >= 0) {
            titleField.setText(transactionTable.getValueAt(selectedRow, 1).toString());
            amountField.setText(transactionTable.getValueAt(selectedRow, 2).toString());
            dateField.setValue(transactionTable.getValueAt(selectedRow, 3));
            statusComboBox.setSelectedItem(transactionTable.getValueAt(selectedRow, 4).toString());
            if (transactionTable.getColumnCount() > 7) {
                notesArea.setText(transactionTable.getValueAt(selectedRow, 7) != null ? 
                    transactionTable.getValueAt(selectedRow, 7).toString() : "");
            }
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

    private class AddTransactionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try (Connection con = db.getConnection()) {
                int accountID = extractIDFromCombo(accountComboBox);
                
                if (accountID == -1) {
                    JOptionPane.showMessageDialog(transactionpanel.this, "Please select a valid account");
                    return;
                }

                String sql = "INSERT INTO Transaction (AccountID, Title, Date, Status, Amount, Notes) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, accountID);
                ps.setString(2, titleField.getText());
                ps.setDate(3, java.sql.Date.valueOf(dateField.getText()));
                ps.setString(4, statusComboBox.getSelectedItem().toString());
                ps.setBigDecimal(5, new java.math.BigDecimal(amountField.getText()));
                ps.setString(6, notesArea.getText());
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(transactionpanel.this, "Transaction added successfully!");
                    clearForm();
                    loadTransactionData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(transactionpanel.this, "Error adding transaction: " + ex.getMessage());
            }
        }
    }

    private class UpdateTransactionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(transactionpanel.this, "Please select a transaction to update");
                return;
            }

            int transactionID = Integer.parseInt(transactionTable.getValueAt(selectedRow, 0).toString());
            
            try (Connection con = db.getConnection()) {
                String sql = "UPDATE Transaction SET Title=?, Date=?, Status=?, Amount=?, Notes=? WHERE TransactionID=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, titleField.getText());
                ps.setDate(2, java.sql.Date.valueOf(dateField.getText()));
                ps.setString(3, statusComboBox.getSelectedItem().toString());
                ps.setBigDecimal(4, new java.math.BigDecimal(amountField.getText()));
                ps.setString(5, notesArea.getText());
                ps.setInt(6, transactionID);
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(transactionpanel.this, "Transaction updated successfully!");
                    loadTransactionData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(transactionpanel.this, "Error updating transaction: " + ex.getMessage());
            }
        }
    }

    private class DeleteTransactionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(transactionpanel.this, "Please select a transaction to delete");
                return;
            }

            int transactionID = Integer.parseInt(transactionTable.getValueAt(selectedRow, 0).toString());
            String transactionTitle = transactionTable.getValueAt(selectedRow, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(transactionpanel.this, 
                "Are you sure you want to delete transaction: " + transactionTitle + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection con = db.getConnection()) {
                    String sql = "DELETE FROM Transaction WHERE TransactionID=?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, transactionID);
                    
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(transactionpanel.this, "Transaction deleted successfully!");
                        clearForm();
                        loadTransactionData();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(transactionpanel.this, "Error deleting transaction: " + ex.getMessage());
                }
            }
        }
    }

    private class SearchTransactionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchTerm = JOptionPane.showInputDialog(transactionpanel.this, "Enter transaction title to search:");
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                try (Connection con = db.getConnection()) {
                    String sql = "SELECT t.TransactionID, t.Title, t.Amount, t.Date, t.Status, " +
                                "a.AccountName, t.Notes " +
                                "FROM Transaction t " +
                                "JOIN Account a ON t.AccountID = a.AccountID " +
                                "WHERE t.Title LIKE ? ORDER BY t.Date DESC";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, "%" + searchTerm + "%");
                    ResultSet rs = ps.executeQuery();
                    
                    transactionTable.setModel(buildTableModel(rs));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(transactionpanel.this, "Error searching transactions: " + ex.getMessage());
                }
            }
        }
    }

    private void clearForm() {
        titleField.setText("");
        amountField.setText("");
        dateField.setValue(new Date());
        statusComboBox.setSelectedIndex(0);
        accountComboBox.setSelectedIndex(0);
        notesArea.setText("");
    }
}