import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

public class AddTransaction extends JFrame {
    private JLabel category;
    private JLabel sum;
    private JPanel addTransaction;
    private JLabel outgo;
    private JLabel income;
    private JList<Category> outgoCategories;
    private JList<Category> incomeCategories;
    private JTextField sumField;
    private JButton create;
    private JButton cancel;
    private JLabel newCategoryLabel;
    private JTextField newCategoryField;
    private JButton newCategoryButton;
    private JComboBox categoriesType;

    private DefaultListModel<Category> outmodel;
    private DefaultListModel<Category> inmodel;

    public AddTransaction(ArrayList<Category> categories, Karbovanets karbovanets) {
        //Components
        this.outmodel = new DefaultListModel<>();
        this.inmodel = new DefaultListModel<>();
        for (Category c : categories) {
            if (c.isOutgo()) {
                this.outmodel.addElement(c);
            } else {
                this.inmodel.addElement(c);
            }
        }
        this.outgoCategories.setModel(this.outmodel);
        this.incomeCategories.setModel(this.inmodel);

        this.categoriesType.addItem("outgo");
        this.categoriesType.addItem("income");

        this.create.setContentAreaFilled(false);
        this.create.setOpaque(true);
        this.create.setFocusPainted(false);
        this.cancel.setContentAreaFilled(false);
        this.cancel.setOpaque(true);
        this.cancel.setFocusPainted(false);
        this.newCategoryButton.setContentAreaFilled(false);
        this.newCategoryButton.setOpaque(true);
        this.newCategoryButton.setFocusPainted(false);

        //ListSelectionListeners
        this.outgoCategories.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !outgoCategories.isSelectionEmpty()) {
                    incomeCategories.clearSelection();
                }
            }
        });
        this.incomeCategories.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !incomeCategories.isSelectionEmpty()) {
                    outgoCategories.clearSelection();
                }
            }
        });

        //ActionListeners
        this.create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double.parseDouble(sumField.getText());
                    try {
                        BufferedWriter w = new BufferedWriter(new FileWriter(Path.of(System.getProperty("user.dir"), "cache", "transactions").toFile(), true));
                        File file = Path.of(System.getProperty("user.dir"), "cache", "transactions").toFile();
                        if (file.length() != 0) {
                            w.newLine();
                        }
                        if (outgoCategories.isSelectionEmpty()) {
                            w.write(sumField.getText() + " " + incomeCategories.getSelectedValue() + " " + LocalDate.now());
                        } else {
                            w.write(sumField.getText() + " " + outgoCategories.getSelectedValue() + " " + LocalDate.now());
                        }
                        w.close();
                        karbovanets.updateTransactions();
                        AddTransaction.this.dispose();
                    } catch (IOException e1) {
                        throw new RuntimeException();
                    }
                } catch (NumberFormatException e2) {
                    sumField.setText("Write only decimal number");
                }
            }
        });
        this.cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddTransaction.this.dispose();
                karbovanets.updateTransactions();
            }
        });
        this.newCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    BufferedWriter w = new BufferedWriter(new FileWriter(Path.of(System.getProperty("user.dir"), "cache", "categories.outgo").toFile(), true));

                    if (AddTransaction.this.newCategoryField.getText().isBlank() && AddTransaction.this.newCategoryField.getText().isEmpty()) {
                        AddTransaction.this.newCategoryField.setText("Name of new category");
                    } else {
                        String selectedType = (String) AddTransaction.this.categoriesType.getSelectedItem();
                        if (selectedType.equals("outgo")) {
                            w.newLine();
                            w.write(newCategoryField.getText());
                            AddTransaction.this.outmodel.addElement(new Category(newCategoryField.getText(), true));
                        } else {
                            w = new BufferedWriter(new FileWriter(Path.of(System.getProperty("user.dir"), "cache", "categories.income").toFile(), true));
                            w.newLine();
                            w.write(newCategoryField.getText());
                            AddTransaction.this.inmodel.addElement(new Category(newCategoryField.getText(), false));
                        }

                    }
                    w.close();
                } catch (IOException e1) {
                    AddTransaction.this.newCategoryField.setText("Name of new category");
                    throw new RuntimeException();
                }
            }
        });


        this.setContentPane(addTransaction);

        this.setTitle("Add transaction");
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
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
        addTransaction = new JPanel();
        addTransaction.setLayout(new GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), -1, -1));
        addTransaction.setBackground(new Color(-7088210));
        category = new JLabel();
        Font categoryFont = this.$$$getFont$$$("Consolas", -1, 18, category.getFont());
        if (categoryFont != null) category.setFont(categoryFont);
        category.setText("Category:");
        addTransaction.add(category, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sum = new JLabel();
        Font sumFont = this.$$$getFont$$$("Consolas", -1, 18, sum.getFont());
        if (sumFont != null) sum.setFont(sumFont);
        sum.setText("Sum");
        addTransaction.add(sum, new GridConstraints(0, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outgo = new JLabel();
        Font outgoFont = this.$$$getFont$$$("Consolas", -1, 16, outgo.getFont());
        if (outgoFont != null) outgo.setFont(outgoFont);
        outgo.setText("Outgo");
        addTransaction.add(outgo, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        income = new JLabel();
        Font incomeFont = this.$$$getFont$$$("Consolas", -1, 16, income.getFont());
        if (incomeFont != null) income.setFont(incomeFont);
        income.setText("Income");
        addTransaction.add(income, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outgoCategories = new JList();
        outgoCategories.setBackground(new Color(-8535656));
        outgoCategories.setDropMode(DropMode.USE_SELECTION);
        addTransaction.add(outgoCategories, new GridConstraints(2, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        incomeCategories = new JList();
        incomeCategories.setBackground(new Color(-8535656));
        addTransaction.add(incomeCategories, new GridConstraints(2, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        create = new JButton();
        create.setBackground(new Color(-10312324));
        create.setText("Create transaction");
        addTransaction.add(create, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newCategoryLabel = new JLabel();
        Font newCategoryLabelFont = this.$$$getFont$$$("Consolas", -1, 14, newCategoryLabel.getFont());
        if (newCategoryLabelFont != null) newCategoryLabel.setFont(newCategoryLabelFont);
        newCategoryLabel.setText("New category:");
        addTransaction.add(newCategoryLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newCategoryField = new JTextField();
        newCategoryField.setBackground(new Color(-8535656));
        addTransaction.add(newCategoryField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        newCategoryButton = new JButton();
        newCategoryButton.setBackground(new Color(-10312324));
        newCategoryButton.setText("Create category");
        addTransaction.add(newCategoryButton, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        categoriesType = new JComboBox();
        categoriesType.setBackground(new Color(-10312324));
        addTransaction.add(categoriesType, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancel = new JButton();
        cancel.setBackground(new Color(-10312324));
        cancel.setText("Cancel");
        addTransaction.add(cancel, new GridConstraints(3, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sumField = new JTextField();
        sumField.setBackground(new Color(-8535656));
        sumField.setText("");
        addTransaction.add(sumField, new GridConstraints(1, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
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
        return addTransaction;
    }

}
