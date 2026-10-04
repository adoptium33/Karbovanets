import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
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
    private JLabel placeForIncomeChart;
    private JLabel placeForOutogoChart;
    private JLabel chartsLabel;

    private ArrayList<Category> categories;
    private ArrayList<Transaction> transactions;
    private List<Transaction> transactionsForCart;
    private double funds;
    private long transactionLineCount;
    private AddTransaction addT;

    public Karbovanets() throws FileNotFoundException {
        this.initCache();
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

        this.addTransaction.setContentAreaFilled(false);
        this.addTransaction.setOpaque(true);
        this.addTransaction.setFocusPainted(false);

        //attributes
        this.categories = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.transactionsForCart = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Path.of(System.getProperty("user.dir"), "cache", "transactions"))) {
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
        Scanner s = new Scanner(Path.of(System.getProperty("user.dir"), "cache", "categories.outgo").toFile());
        while (s.hasNext()) {
            String word = s.nextLine();
            this.categories.add(new Category(word, true));
        }
        s.close();

        Scanner s1 = new Scanner(Path.of(System.getProperty("user.dir"), "cache", "categories.income").toFile());
        while (s1.hasNext()) {
            String word = s1.nextLine();
            this.categories.add(new Category(word, false));
        }
        s1.close();
    }

    private void initCache() {
        Path dir = Path.of(System.getProperty("user.dir"), "cache");
        try {
            Files.createDirectories(dir);
            for (String name : new String[]{"transactions", "categories.outgo", "categories.income"}) {
                Path target = dir.resolve(name);
                if (Files.notExists(target)) {
                    try (InputStream in = Karbovanets.class.getResourceAsStream("/cache/" + name)) {
                        if (in != null) {
                            Files.copy(in, target);
                        } else {
                            Files.createFile(target);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createPieChartIncome() {
        PieChart chart = new PieChartBuilder().width(550).height(400).title("Income").build();
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
        PieChart chart = new PieChartBuilder().width(550).height(400).title("Outgo").build();
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
        try (Scanner s2 = new Scanner(Path.of(System.getProperty("user.dir"), "cache", "transactions").toFile())) {
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
                for (Category category : this.categories) {
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

        try (Stream<String> lines = Files.lines(Path.of(System.getProperty("user.dir"), "cache", "transactions"))) {
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
        try (Scanner s2 = new Scanner(Path.of(System.getProperty("user.dir"), "cache", "transactions").toFile())) {
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
                for (Category category : this.categories) {
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

        try (Stream<String> lines = Files.lines(Path.of(System.getProperty("user.dir"), "cache", "transactions"))) {
            this.transactionLineCount = lines.count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.calculateFunds();
        this.createPieChartIncome();
        this.createPieChartOutgo();
        this.addT = null;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(19, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel.setBackground(new Color(-7088210));
        walletLabel = new JLabel();
        Font walletLabelFont = this.$$$getFont$$$("Consolas", -1, 18, walletLabel.getFont());
        if (walletLabelFont != null) walletLabel.setFont(walletLabelFont);
        walletLabel.setText("Wallet");
        panel.add(walletLabel, new GridConstraints(0, 0, 3, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$("Consolas", Font.BOLD, 16, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Label");
        panel.add(label1, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textArea1 = new JTextArea();
        textArea1.setBackground(new Color(-8535656));
        textArea1.setDisabledTextColor(new Color(-15658473));
        textArea1.setEnabled(false);
        textArea1.setForeground(new Color(-15658473));
        panel.add(textArea1, new GridConstraints(3, 2, 16, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        addTransaction = new JButton();
        addTransaction.setBackground(new Color(-10312324));
        addTransaction.setText("Add Transaction");
        panel.add(addTransaction, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transactionsLabel = new JLabel();
        Font transactionsLabelFont = this.$$$getFont$$$("Consolas", -1, 18, transactionsLabel.getFont());
        if (transactionsLabelFont != null) transactionsLabel.setFont(transactionsLabelFont);
        transactionsLabel.setText("Transactions:");
        panel.add(transactionsLabel, new GridConstraints(0, 2, 3, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeLimitsLabel = new JLabel();
        Font timeLimitsLabelFont = this.$$$getFont$$$("Consolas", -1, 16, timeLimitsLabel.getFont());
        if (timeLimitsLabelFont != null) timeLimitsLabel.setFont(timeLimitsLabelFont);
        timeLimitsLabel.setText("Set time limits");
        panel.add(timeLimitsLabel, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromDateLabel = new JLabel();
        Font fromDateLabelFont = this.$$$getFont$$$("Consolas", -1, 16, fromDateLabel.getFont());
        if (fromDateLabelFont != null) fromDateLabel.setFont(fromDateLabelFont);
        fromDateLabel.setText("From");
        panel.add(fromDateLabel, new GridConstraints(6, 0, 6, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tillDateLabel = new JLabel();
        Font tillDateLabelFont = this.$$$getFont$$$("Consolas", -1, 16, tillDateLabel.getFont());
        if (tillDateLabelFont != null) tillDateLabel.setFont(tillDateLabelFont);
        tillDateLabel.setText("Till");
        panel.add(tillDateLabel, new GridConstraints(12, 0, 6, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromYearLabel = new JLabel();
        Font fromYearLabelFont = this.$$$getFont$$$("Consolas", -1, 14, fromYearLabel.getFont());
        if (fromYearLabelFont != null) fromYearLabel.setFont(fromYearLabelFont);
        fromYearLabel.setText("Year");
        panel.add(fromYearLabel, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromYearField = new JTextField();
        fromYearField.setBackground(new Color(-8535656));
        panel.add(fromYearField, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fromMonthLabel = new JLabel();
        Font fromMonthLabelFont = this.$$$getFont$$$("Consolas", -1, 14, fromMonthLabel.getFont());
        if (fromMonthLabelFont != null) fromMonthLabel.setFont(fromMonthLabelFont);
        fromMonthLabel.setText("Month");
        panel.add(fromMonthLabel, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromMonthField = new JTextField();
        fromMonthField.setBackground(new Color(-8535656));
        panel.add(fromMonthField, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fromDayLabel = new JLabel();
        Font fromDayLabelFont = this.$$$getFont$$$("Consolas", -1, 14, fromDayLabel.getFont());
        if (fromDayLabelFont != null) fromDayLabel.setFont(fromDayLabelFont);
        fromDayLabel.setText("Day");
        panel.add(fromDayLabel, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fromDayField = new JTextField();
        fromDayField.setBackground(new Color(-8535656));
        panel.add(fromDayField, new GridConstraints(11, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tillYearLabel = new JLabel();
        Font tillYearLabelFont = this.$$$getFont$$$("Consolas", -1, 14, tillYearLabel.getFont());
        if (tillYearLabelFont != null) tillYearLabel.setFont(tillYearLabelFont);
        tillYearLabel.setText("Year");
        panel.add(tillYearLabel, new GridConstraints(12, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tillYearField = new JTextField();
        tillYearField.setBackground(new Color(-8535656));
        panel.add(tillYearField, new GridConstraints(13, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tillMonthLabel = new JLabel();
        Font tillMonthLabelFont = this.$$$getFont$$$("Consolas", -1, 14, tillMonthLabel.getFont());
        if (tillMonthLabelFont != null) tillMonthLabel.setFont(tillMonthLabelFont);
        tillMonthLabel.setText("Month");
        panel.add(tillMonthLabel, new GridConstraints(14, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tillMonthField = new JTextField();
        tillMonthField.setBackground(new Color(-8535656));
        panel.add(tillMonthField, new GridConstraints(15, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tillDayLabel = new JLabel();
        Font tillDayLabelFont = this.$$$getFont$$$("Consolas", -1, 14, tillDayLabel.getFont());
        if (tillDayLabelFont != null) tillDayLabel.setFont(tillDayLabelFont);
        tillDayLabel.setText("Day");
        panel.add(tillDayLabel, new GridConstraints(16, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tillDayField = new JTextField();
        tillDayField.setBackground(new Color(-8535656));
        panel.add(tillDayField, new GridConstraints(17, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        placeForIncomeChart = new JLabel();
        placeForIncomeChart.setText("");
        panel.add(placeForIncomeChart, new GridConstraints(2, 3, 17, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        placeForOutogoChart = new JLabel();
        placeForOutogoChart.setText("");
        panel.add(placeForOutogoChart, new GridConstraints(1, 4, 18, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chartsLabel = new JLabel();
        Font chartsLabelFont = this.$$$getFont$$$("Consolas", -1, 18, chartsLabel.getFont());
        if (chartsLabelFont != null) chartsLabel.setFont(chartsLabelFont);
        chartsLabel.setText("Charts");
        panel.add(chartsLabel, new GridConstraints(0, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
