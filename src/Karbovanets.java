import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Scanner;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;

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
    private JLabel placeForIncomeChart;
    private JLabel placeForOutogoChart;

    private ArrayList<Category> categories;
    private ArrayList<Transaction> transactions;
    private List<Transaction> transactionsForCart;
    private double funds;
    private long transactionLineCount;
    private AddTransaction addT;

    public Karbovanets() throws FileNotFoundException {
        //Components
        this.label1.setText(Double.toString(this.funds));
        this.addTransaction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addT == null || !addT.isDisplayable()) {
                    Karbovanets.this.addT = new AddTransaction(Karbovanets.this.categories, Karbovanets.this);
                }
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
        this.transactionsForCart = new ArrayList<>();
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
        s.close();

        Scanner s1 = new Scanner(new File("cache/categories.income"));
        while (s1.hasNext()) {
            String word = s1.nextLine();
            this.categories.add(new Category(word, false));
        }
        s1.close();
    }

    public void createPieChartIncome() {
        PieChart chart = new PieChartBuilder().width(500).height(400).title("Income").build();
        Map<Category, Double> groupedTransactions = this.transactionsForCart.stream().collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getSum)));
        for (Map.Entry<Category, Double> entry : groupedTransactions.entrySet()) {
            if (!entry.getKey().isOutgo()) {
                chart.addSeries(entry.getKey().toString(), entry.getValue());
            }
        }

        BufferedImage chartImage = BitmapEncoder.getBufferedImage(chart);
        this.placeForIncomeChart.setIcon(new ImageIcon(chartImage));
        this.placeForIncomeChart.revalidate();
        this.placeForIncomeChart.repaint();
    }

    public void createPieChartOutgo() {
        PieChart chart = new PieChartBuilder().width(500).height(400).title("Outgo").build();
        Map<Category, Double> groupedTransactions = this.transactionsForCart.stream().collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getSum)));
        for (Map.Entry<Category, Double> entry : groupedTransactions.entrySet()) {
            if (entry.getKey().isOutgo()) {
                chart.addSeries(entry.getKey().toString(), entry.getValue());
            }
        }

        BufferedImage chartImage = BitmapEncoder.getBufferedImage(chart);
        this.placeForOutogoChart.setIcon(new ImageIcon(chartImage));
        this.placeForOutogoChart.revalidate();
        this.placeForOutogoChart.repaint();
    }

    public void initTransactions() {
        try (Scanner s2 = new Scanner(new File("cache/transactions"))) {
            while (s2.hasNext()) {
                String[] line = s2.nextLine().trim().split("\\s+");
                double sum = Double.parseDouble(line[0]);
                StringBuilder catB = new StringBuilder();
                for (int i = 1; i < line.length - 1; i++) {
                    catB.append(line[i]);
                    if (line.length > 3 && i != line.length - 2) {
                        catB.append(" ");
                    }
                }
                String cat = catB.toString();
                LocalDate date = LocalDate.parse(line[line.length - 1]);
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

        try (java.util.stream.Stream<String> lines = Files.lines(Path.of("cache/transactions"))) {
            this.transactionLineCount = lines.count();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        this.label1.setText(Double.toString(this.funds));
    }

    public void updateTransactions() {
        try (Scanner s2 = new Scanner(new File("cache/transactions"))) {
            int current = 1;
            while (current <= this.transactionLineCount && s2.hasNextLine()) {
                s2.nextLine();
                current++;
            }
            while (s2.hasNext()) {
                String[] line = s2.nextLine().trim().split("\\s+");
                double sum = Double.parseDouble(line[0]);
                StringBuilder catB = new StringBuilder();
                for (int i = 1; i < line.length - 1; i++) {
                    catB.append(line[i]);
                    if (line.length > 3 && i != line.length - 2) {
                        catB.append(" ");
                    }
                }
                String cat = catB.toString();
                LocalDate date = LocalDate.parse(line[line.length - 1]);
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
        this.transactionsForCart.clear();
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
                this.transactionsForCart.add(tr);
            }
        }

        try (java.util.stream.Stream<String> lines = Files.lines(Path.of("cache/transactions"))) {
            this.transactionLineCount = lines.count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.calculateFunds();
        this.createPieChartIncome();
        this.createPieChartOutgo();
        this.addT = null;
    }
}
