package bank.panel;


import javax.swing.*;

import bank.form.db;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class userpanel extends JPanel {
    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton addButton, updateButton, deleteButton, searchButton;
    private JTable userTable;
    private JScrollPane scrollPane;

    public  userpanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        setupUI();
        loadUserData();
    }

    private void initializeComponents() {
        // Form components
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        emailField = new JTextField(15);
        roleComboBox = new JComboBox<>(new String[]{"CUSTOMER", "STAFF", "ADMIN"});

        // Buttons
        addButton = new JButton("Add User");
        updateButton = new JButton("Update User");
        deleteButton = new JButton("Delete User");
        searchButton = new JButton("Search");

        // Table
        userTable = new JTable();
        scrollPane = new JScrollPane(userTable);
    }

    private void setupUI() {
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("User Management"));

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleComboBox);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);

        // Main layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add event listeners
        addButton.addActionListener(new AddUserListener());
        updateButton.addActionListener(new UpdateUserListener());
        deleteButton.addActionListener(new DeleteUserListener());
        searchButton.addActionListener(new SearchUserListener());
        
        
       // userTable.getSelectionModel().addListSelectionListener(e -> populateFormFromTable());
    }

    private void loadUserData() {
        try (Connection con = db.getConnection()) {
            String sql = "SELECT UserID, Username, Role, Email, CreatedAt FROM User ORDER BY CreatedAt DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            userTable.setModel(buildTableModel(rs));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void populateFormFromTable() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            usernameField.setText(userTable.getValueAt(selectedRow, 1).toString());
            emailField.setText(userTable.getValueAt(selectedRow, 3).toString());
            roleComboBox.setSelectedItem(userTable.getValueAt(selectedRow, 2).toString());
            passwordField.setText(""); // Clear password for security
        }
    }

    // TableModel builder utility method
    private javax.swing.table.DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        java.sql.ResultSetMetaData metaData = rs.getMetaData();

        // Get column names
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int column = 1; column <= columnCount; column++) {
            columnNames[column - 1] = metaData.getColumnName(column);
        }

        // Get data rows
        java.util.Vector<Object[]> data = new java.util.Vector<>();
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = rs.getObject(i);
            }
            data.add(row);
        }
        
        
        
        
        
  javax.swing.table.DefaultTableModel buildTableModel(ResultSet rs)  throws SQLException {
            java.sql.ResultSetMetaData metaData = rs.getMetaData();

            // Get column names
            int columnCount = metaData.getColumnCount();
            Vector<String> columnNames = new Vector<>();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }

            // Get data rows
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            return new javax.swing.table.DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };
        }
        
        
        
        
        
    }

    // Action Listeners
    private class AddUserListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try (Connection con = db.getConnection()) {
                String sql = "INSERT INTO User (Username, PasswordHash, Role, Email) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, usernameField.getText());
                ps.setString(2, new String(passwordField.getPassword())); // In production, hash the password
                ps.setString(3, roleComboBox.getSelectedItem().toString());
                ps.setString(4, emailField.getText());
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(userpanel.this, "User added successfully!");
                    clearForm();
                    loadUserData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(userpanel.this, "Error adding user: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private class UpdateUserListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(userpanel.this, "Please select a user to update");
                return;
            }

            int userID = (int) userTable.getValueAt(selectedRow, 0);
            
            try (Connection con = db.getConnection()) {
                String sql = "UPDATE User SET Username=?, Role=?, Email=? WHERE UserID=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, usernameField.getText());
                ps.setString(2, roleComboBox.getSelectedItem().toString());
                ps.setString(3, emailField.getText());
                ps.setInt(4, userID);
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(userpanel.this, "User updated successfully!");
                    loadUserData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(userpanel.this, "Error updating user: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private class DeleteUserListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(userpanel.this, "Please select a user to delete");
                return;
            }

            int userID = (int) userTable.getValueAt(selectedRow, 0);
            String username = userTable.getValueAt(selectedRow, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(userpanel.this, 
                "Are you sure you want to delete user: " + username + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection con = db.getConnection()) {
                    String sql = "DELETE FROM User WHERE UserID=?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, userID);
                    
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(userpanel.this, "User deleted successfully!");
                        clearForm();
                        loadUserData();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(userpanel.this, "Error deleting user: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    private class SearchUserListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchTerm = JOptionPane.showInputDialog(userpanel.this, "Enter username to search:");
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                try (Connection con = db.getConnection()) {
                    String sql = "SELECT UserID, Username, Role, Email, CreatedAt FROM User WHERE Username LIKE ? ORDER BY Username";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, "%" + searchTerm + "%");
                    ResultSet rs = ps.executeQuery();
                    
                    userTable.setModel(buildTableModel(rs));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(userpanel.this, "Error searching users: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        roleComboBox.setSelectedIndex(0);
    }
}
