package bankform;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class BankingSystem extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable userTable, accountTable, loanTable, transTable, paymentTable;
    private DefaultTableModel userModel, accountModel, loanModel, transModel, paymentModel;
    private JTextField txtUser, txtPass, txtPhone, txtEmail, txtRole;
    private JTextField txtAccNum, txtAccType, txtBalance;
    private JTextField txtLoanType, txtLoanAmount, txtLoanStatus;
    private JTextField txtTransAcc, txtTransAmount, txtTransType;
    private JTextField txtPayAcc, txtPayAmount, txtPayMethod;

    Connection conn;

    public BankingSystem() {
        setTitle("Banking System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

      
        JPanel userPanel = new JPanel(null);
        JLabel lbl1 = new JLabel("Username:"); lbl1.setBounds(20, 20, 80, 25);
        txtUser = new JTextField(); txtUser.setBounds(120, 20, 150, 25);
        JLabel lbl2 = new JLabel("Password:"); lbl2.setBounds(20, 60, 80, 25);
        txtPass = new JTextField(); txtPass.setBounds(120, 60, 150, 25);
        JLabel lbl3 = new JLabel("Phone:"); lbl3.setBounds(20, 100, 80, 25);
        txtPhone = new JTextField(); txtPhone.setBounds(120, 100, 150, 25);
        JLabel lbl4 = new JLabel("Email:"); lbl4.setBounds(20, 140, 80, 25);
        txtEmail = new JTextField(); txtEmail.setBounds(120, 140, 150, 25);
        JLabel lbl5 = new JLabel("Role:"); lbl5.setBounds(20, 180, 80, 25);
        txtRole = new JTextField(); txtRole.setBounds(120, 180, 150, 25);

        JButton btnAddUser = new JButton("Add"); btnAddUser.setBounds(300, 20, 100, 25);
        JButton btnLoadUser = new JButton("Load"); btnLoadUser.setBounds(300, 60, 100, 25);
        JButton btnUpdateUser = new JButton("Update"); btnUpdateUser.setBounds(300, 100, 100, 25);
        JButton btnDeleteUser = new JButton("Delete"); btnDeleteUser.setBounds(300, 140, 100, 25);

        userModel = new DefaultTableModel(new String[]{"ID","Username","Password","Phone","Email","Role"},0);
        userTable = new JTable(userModel);
        JScrollPane userScroll = new JScrollPane(userTable); userScroll.setBounds(20, 230, 920, 300);

        userPanel.add(lbl1); userPanel.add(txtUser);
        userPanel.add(lbl2); userPanel.add(txtPass);
        userPanel.add(lbl3); userPanel.add(txtPhone);
        userPanel.add(lbl4); userPanel.add(txtEmail);
        userPanel.add(lbl5); userPanel.add(txtRole);
        userPanel.add(btnAddUser); userPanel.add(btnLoadUser);
        userPanel.add(btnUpdateUser); userPanel.add(btnDeleteUser);
        userPanel.add(userScroll);

        tabbedPane.addTab("Users", userPanel);


        JPanel accountPanel = new JPanel(null);
        JLabel acc1 = new JLabel("Account No:"); acc1.setBounds(20,20,100,25);
        txtAccNum = new JTextField(); txtAccNum.setBounds(120,20,150,25);
        JLabel acc2 = new JLabel("Type:"); acc2.setBounds(20,60,100,25);
        txtAccType = new JTextField(); txtAccType.setBounds(120,60,150,25);
        JLabel acc3 = new JLabel("Balance:"); acc3.setBounds(20,100,100,25);
        txtBalance = new JTextField(); txtBalance.setBounds(120,100,150,25);

        JButton btnAddAcc = new JButton("Add"); btnAddAcc.setBounds(300,20,100,25);
        JButton btnLoadAcc = new JButton("Load"); btnLoadAcc.setBounds(300,60,100,25);
        JButton btnUpdateAcc = new JButton("Update"); btnUpdateAcc.setBounds(300,100,100,25);
        JButton btnDeleteAcc = new JButton("Delete"); btnDeleteAcc.setBounds(300,140,100,25);

        accountModel = new DefaultTableModel(new String[]{"AccID","AccNo","Type","Balance"},0);
        accountTable = new JTable(accountModel);
        JScrollPane accScroll = new JScrollPane(accountTable); accScroll.setBounds(20, 200, 920, 300);

        accountPanel.add(acc1); accountPanel.add(txtAccNum);
        accountPanel.add(acc2); accountPanel.add(txtAccType);
        accountPanel.add(acc3); accountPanel.add(txtBalance);
        accountPanel.add(btnAddAcc); accountPanel.add(btnLoadAcc);
        accountPanel.add(btnUpdateAcc); accountPanel.add(btnDeleteAcc);
        accountPanel.add(accScroll);

        tabbedPane.addTab("Accounts", accountPanel);

        
        JPanel loanPanel = new JPanel(null);
        JLabel l1 = new JLabel("Type:"); l1.setBounds(20,20,80,25);
        txtLoanType = new JTextField(); txtLoanType.setBounds(120,20,150,25);
        JLabel l2 = new JLabel("Amount:"); l2.setBounds(20,60,80,25);
        txtLoanAmount = new JTextField(); txtLoanAmount.setBounds(120,60,150,25);
        JLabel l3 = new JLabel("Status:"); l3.setBounds(20,100,80,25);
        txtLoanStatus = new JTextField(); txtLoanStatus.setBounds(120,100,150,25);

        JButton btnAddLoan = new JButton("Add"); btnAddLoan.setBounds(300,20,100,25);
        JButton btnLoadLoan = new JButton("Load"); btnLoadLoan.setBounds(300,60,100,25);
        JButton btnUpdateLoan = new JButton("Update"); btnUpdateLoan.setBounds(300,100,100,25);
        JButton btnDeleteLoan = new JButton("Delete"); btnDeleteLoan.setBounds(300,140,100,25);

        loanModel = new DefaultTableModel(new String[]{"LoanID","Type","Amount","Status"},0);
        loanTable = new JTable(loanModel);
        JScrollPane loanScroll = new JScrollPane(loanTable); loanScroll.setBounds(20, 200, 920, 300);

        loanPanel.add(l1); loanPanel.add(txtLoanType);
        loanPanel.add(l2); loanPanel.add(txtLoanAmount);
        loanPanel.add(l3); loanPanel.add(txtLoanStatus);
        loanPanel.add(btnAddLoan); loanPanel.add(btnLoadLoan);
        loanPanel.add(btnUpdateLoan); loanPanel.add(btnDeleteLoan);
        loanPanel.add(loanScroll);

        tabbedPane.addTab("Loans", loanPanel);

     
        JPanel transPanel = new JPanel(null);
        JLabel t1 = new JLabel("Account No:"); t1.setBounds(20,20,100,25);
        txtTransAcc = new JTextField(); txtTransAcc.setBounds(120,20,150,25);
        JLabel t2 = new JLabel("Amount:"); t2.setBounds(20,60,100,25);
        txtTransAmount = new JTextField(); txtTransAmount.setBounds(120,60,150,25);
        JLabel t3 = new JLabel("Type:"); t3.setBounds(20,100,100,25);
        txtTransType = new JTextField(); txtTransType.setBounds(120,100,150,25);

        JButton btnAddTrans = new JButton("Add"); btnAddTrans.setBounds(300,20,100,25);
        JButton btnLoadTrans = new JButton("Load"); btnLoadTrans.setBounds(300,60,100,25);
        JButton btnUpdateTrans = new JButton("Update"); btnUpdateTrans.setBounds(300,100,100,25);
        JButton btnDeleteTrans = new JButton("Delete"); btnDeleteTrans.setBounds(300,140,100,25);

        transModel = new DefaultTableModel(new String[]{"TransID","AccNo","Amount","Type"},0);
        transTable = new JTable(transModel);
        JScrollPane transScroll = new JScrollPane(transTable); transScroll.setBounds(20, 200, 920, 300);

        transPanel.add(t1); transPanel.add(txtTransAcc);
        transPanel.add(t2); transPanel.add(txtTransAmount);
        transPanel.add(t3); transPanel.add(txtTransType);
        transPanel.add(btnAddTrans); transPanel.add(btnLoadTrans);
        transPanel.add(btnUpdateTrans); transPanel.add(btnDeleteTrans);
        transPanel.add(transScroll);

        tabbedPane.addTab("Transactions", transPanel);


        JPanel payPanel = new JPanel(null);
        JLabel p1 = new JLabel("Account No:"); p1.setBounds(20,20,100,25);
        txtPayAcc = new JTextField(); txtPayAcc.setBounds(120,20,150,25);
        JLabel p2 = new JLabel("Amount:"); p2.setBounds(20,60,100,25);
        txtPayAmount = new JTextField(); txtPayAmount.setBounds(120,60,150,25);
        JLabel p3 = new JLabel("Method:"); p3.setBounds(20,100,100,25);
        txtPayMethod = new JTextField(); txtPayMethod.setBounds(120,100,150,25);

        JButton btnAddPay = new JButton("Add"); btnAddPay.setBounds(300,20,100,25);
        JButton btnLoadPay = new JButton("Load"); btnLoadPay.setBounds(300,60,100,25);
        JButton btnUpdatePay = new JButton("Update"); btnUpdatePay.setBounds(300,100,100,25);
        JButton btnDeletePay = new JButton("Delete"); btnDeletePay.setBounds(300,140,100,25);

        paymentModel = new DefaultTableModel(new String[]{"PaymentID","AccNo","Amount","Method"},0);
        paymentTable = new JTable(paymentModel);
        JScrollPane payScroll = new JScrollPane(paymentTable); payScroll.setBounds(20, 200, 920, 300);

        payPanel.add(p1); payPanel.add(txtPayAcc);
        payPanel.add(p2); payPanel.add(txtPayAmount);
        payPanel.add(p3); payPanel.add(txtPayMethod);
        payPanel.add(btnAddPay); payPanel.add(btnLoadPay);
        payPanel.add(btnUpdatePay); payPanel.add(btnDeletePay);
        payPanel.add(payScroll);

        tabbedPane.addTab("Payments", payPanel);

    
        add(tabbedPane);
        connect();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Connection Failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new BankingSystem().setVisible(true);
    }
}