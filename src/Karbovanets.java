import javax.swing.*;
import java.util.ArrayList;

public class Karbovanets extends JFrame {
    private JPanel panel;
    private JLabel walletLabel;
    private JLabel label1;

    private ArrayList<Category> categories;
    private ArrayList<Transaction> transactions;
    private int funds;

    public Karbovanets() {
        this.categories = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.funds = 0;
        this.initCategories();

        for (Transaction tr : this.transactions) {
            if (tr.isOutgo()) {
                this.funds -= tr.getSum();
            } else {
                this.funds += tr.getSum();
            }
        }
        this.label1.setText(Integer.toString(this.funds));

        this.setContentPane(panel);

        this.setTitle("Karbovanets Wallet");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
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
    }
}
