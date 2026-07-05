import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Karbovanets extends JFrame {
    private JPanel panel;
    private JLabel walletLabel;
    private JLabel label1;
    private JTextArea textArea1;
    private JButton addTransaction;

    private ArrayList<Category> categories;
    private ArrayList<Transaction> transactions;
    private int funds;

    public Karbovanets() {
        //Components
        this.categories = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.label1.setText(Integer.toString(this.funds));
        this.addTransaction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddTransaction addT = new AddTransaction(Karbovanets.this.categories, Karbovanets.this.transactions);
                Karbovanets.this.updateTransactions();
            }
        });

        //methods
        this.initCategories();
        this.calculateFunds();
        this.updateTransactions();


        this.setContentPane(panel);

        this.setTitle("Karbovanets Wallet");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);

    }

    public void initCategories() {
        //outgo categories
        this.categories.add(new Category("Products", true));
        this.categories.add(new Category("Dwelling", true));
        this.categories.add(new Category("Transport", true));
        this.categories.add(new Category("Clothes", true));
        this.categories.add(new Category("Sport", true));
        this.categories.add(new Category("Car", true));
        this.categories.add(new Category("Education", true));
        this.categories.add(new Category("Free time", true));
        this.categories.add(new Category("Restaurants", true));
        this.categories.add(new Category("Health", true));
        this.categories.add(new Category("Beauty", true));
        this.categories.add(new Category("Taxes", true));
        //income categories
        this.categories.add(new Category("Salary", false));
        this.categories.add(new Category("Gift", false));
        this.categories.add(new Category("Scholarship", false));
    }

    public void calculateFunds() {
        this.funds = 0;
        for (Transaction tr : this.transactions) {
            if (tr.getCategory().isOutgo()) {
                this.funds -= tr.getSum();
            } else {
                this.funds += tr.getSum();
            }
        }
    }

    public void updateTransactions() {
        this.textArea1.setText("");
        this.textArea1.append("Transactions:");
        for (Transaction tr : this.transactions) {
            this.textArea1.append("   " + tr.getCategory());
            if (tr.getCategory().isOutgo()) {
                this.textArea1.append("     -" + tr.getSum());
            } else {
                this.textArea1.append("     +" + tr.getSum());
            }
        }
    }
}
