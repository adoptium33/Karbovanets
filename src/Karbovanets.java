import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Karbovanets extends JFrame {
    private JPanel panel;
    private JLabel walletLabel;
    private JLabel label1;
    private JTextArea textArea1;
    private JButton addTransaction;
    private JLabel transactionsLabel;
    private JLabel timeLimitsLabel;
    private JLabel fromDateLabel;
    private JLabel tillDateLabel;
    private JLabel fromYearLabel;
    private JTextField fromYearField;
    private JTextField fromMonthField;
    private JTextField fromDayField;
    private JLabel fromMonthLabel;
    private JLabel fromDayLabel;
    private JTextField tillYearField;
    private JTextField tillMonthField;
    private JTextField tillDayField;
    private JLabel tillYearLabel;
    private JLabel tillMonthLabel;
    private JLabel tillDayLabel;
    private JButton updateTrasactionsList;

    private ArrayList<Category> categories;
    private ArrayList<Transaction> transactions;
    private int funds;
    private long transactionLineCount;

    public Karbovanets() throws FileNotFoundException {
        //Components
        this.label1.setText(Integer.toString(this.funds));
        this.addTransaction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddTransaction addT = new AddTransaction(Karbovanets.this.categories, Karbovanets.this);
            }
        });
        this.updateTrasactionsList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Karbovanets.this.updateTransactions();
            }
        });
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        this.tillYearField.setText(Integer.toString(year));
        this.tillMonthField.setText(Integer.toString(month));
        this.tillDayField.setText(Integer.toString(day));

        LocalDate monthAgo = today.minusMonths(1);
        int yearFrom = monthAgo.getYear();
        int monthFrom = monthAgo.getMonthValue();
        int dayFrom = monthAgo.getDayOfMonth();
        this.fromYearField.setText(Integer.toString(yearFrom));
        this.fromMonthField.setText(Integer.toString(monthFrom));
        this.fromDayField.setText(Integer.toString(dayFrom));

        //attributes
        this.categories = new ArrayList<>();
        this.transactions = new ArrayList<>();
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
        this.setVisible(true);

    }

    public void initCategories() throws FileNotFoundException {
        Scanner s = new Scanner(new File("cache/categories.outgo"));
        while (s.hasNext()) {
            String word = s.nextLine();
            this.categories.add(new Category(word, true));
        }

        Scanner s1 = new Scanner(new File("cache/categories.income"));
        while (s1.hasNext()) {
            String word = s1.nextLine();
            this.categories.add(new Category(word, false));
        }
    }

    public void initTransactions() throws FileNotFoundException {
        Scanner s1 = new Scanner(new File("cache/transactions"));
        while (s1.hasNext()) {
            int sum = Integer.parseInt(s1.next());
            String cat = s1.next();
            LocalDate date = LocalDate.parse(s1.next());
            for (Category  category : this.categories) {
                if (category.toString().equals(cat)) {
                    this.transactions.add(new Transaction(sum, category, date));
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
        try (Scanner s2 = new Scanner(new File("cache/transactions"))) {
            int current = 1;
            while (current <= this.transactionLineCount && s2.hasNextLine()) {
                s2.nextLine();
                current++;
            }
            while (s2.hasNext()) {
                int sum = Integer.parseInt(s2.next());
                String cat = s2.next();
                LocalDate date = LocalDate.parse(s2.next());
                for (Category  category : this.categories) {
                    if (category.toString().equals(cat)) {
                        this.transactions.add(new Transaction(sum, category, date));
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            this.textArea1.setText("Problem while reading file, please contact technical support.");
            throw new RuntimeException(e);
        }

        this.textArea1.setText("");
        LocalDate upperBorder = LocalDate.of(Integer.parseInt(Karbovanets.this.tillYearField.getText()),
                                       Integer.parseInt(Karbovanets.this.tillMonthField.getText()),
                                       Integer.parseInt(Karbovanets.this.tillDayField.getText()));
        LocalDate lowerBorder = LocalDate.of(Integer.parseInt(Karbovanets.this.fromYearField.getText()),
                                             Integer.parseInt(Karbovanets.this.fromMonthField.getText()),
                                             Integer.parseInt(Karbovanets.this.fromDayField.getText()));
        for (Transaction tr : this.transactions) {
            if (tr.getDate().isAfter(lowerBorder.minusDays(1)) && tr.getDate().isBefore(upperBorder.plusDays(1))) {
                String space = String.format("%20s", "");
                this.textArea1.append("\n" + tr.getDate() + "   " + tr.getCategory());
                if (tr.getCategory().isOutgo()) {
                    this.textArea1.append("\n" + space + "-" + tr.getSum());
                } else {
                    this.textArea1.append("\n" + space + "+" + tr.getSum());
                }
            }
        }

        try (java.util.stream.Stream<String> lines = Files.lines(Path.of("cache/transactions"))) {
            this.transactionLineCount = lines.count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.calculateFunds();
    }
}
