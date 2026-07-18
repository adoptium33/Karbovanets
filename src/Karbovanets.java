import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class Karbovanets extends JFrame {
    private JPanel panel;
    private JLabel walletLabel;
    private JLabel label1;
    private JTextArea textArea1;
    private JButton addTransaction;
    private JLabel transactionsLabel;

    private ArrayList<Category> categories;
    private ArrayList<Transaction> transactions;
    private int funds;
    private long transactionLineCount;

    public Karbovanets() throws FileNotFoundException {
        //Components
        this.categories = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.label1.setText(Integer.toString(this.funds));
        this.addTransaction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddTransaction addT = new AddTransaction(Karbovanets.this.categories);
            }
        });

        //attributes
        try (java.util.stream.Stream<String> lines = Files.lines(Path.of("cache/transactions"))) {
            this.transactionLineCount = lines.count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //methods
        this.initCategories();
        this.initTransactions();
        this.calculateFunds();
        this.updateTransactions();

        //Swing
        this.setContentPane(panel);
        this.setTitle("Karbovanets Wallet");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);

    }

    public void initCategories() throws FileNotFoundException {
        Scanner s = new Scanner(new File("cache/categories"));
        String outgo = s.next();
        if (outgo.equals("outgo")) {
            while (s.hasNext()) {
                String word = s.nextLine();
                if (word.equals("income")) {
                    break;
                }
                this.categories.add(new Category(word, true));
            }
        }
        while (s.hasNext()) {
            this.categories.add(new Category(s.nextLine(), false));
        }
    }

    public void initTransactions() throws FileNotFoundException {
        Scanner s1 = new Scanner(new File("cache/transactions"));
        while (s1.hasNext()) {
            int sum = Integer.parseInt(s1.next());
            String cat = s1.next();
            for (Category  category : this.categories) {
                if (category.toString().equals(cat)) {
                    this.transactions.add(new Transaction(sum, category));
                    break;
                }
            }
        }
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
        this.label1.setText(Integer.toString(this.funds));
    }

    public void updateTransactions() {
        this.textArea1.setText("");
        for (Transaction tr : this.transactions) {
            this.textArea1.append("\n   " + tr.getCategory());
            if (tr.getCategory().isOutgo()) {
                this.textArea1.append("\n     -" + tr.getSum());
            } else {
                this.textArea1.append("\n     +" + tr.getSum());
            }
        }
    }
}
