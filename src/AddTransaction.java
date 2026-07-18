import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class AddTransaction extends  JFrame {
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

    public AddTransaction(ArrayList<Category> categories) {
        //Components
        DefaultListModel<Category> outmodel = new DefaultListModel<>();
        DefaultListModel<Category> inmodel = new DefaultListModel<>();
        for (Category c : categories) {
            if (c.isOutgo()) {
                outmodel.addElement(c);
            } else {
                inmodel.addElement(c);
            }
        }
        this.outgoCategories.setModel(outmodel);
        this.incomeCategories.setModel(inmodel);

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
                    BufferedWriter w = new BufferedWriter(new FileWriter("cache/transactions", true));

                    if (outgoCategories.isSelectionEmpty()) {
                        w.write(sumField.getText() + " " + incomeCategories.getSelectedValue());
                    } else {
                        w.write(sumField.getText() + " " + outgoCategories.getSelectedValue());
                    }
                    w.newLine();
                    w.close();
                    AddTransaction.this.dispose();
                } catch (IOException e1) {
                    AddTransaction.this.sumField.setText("Please fill with numbers");
                    throw new RuntimeException();
                }
            }
        });
        this.cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddTransaction.this.dispose();
            }
        });


        this.setContentPane(addTransaction);

        this.setTitle("Add transaction");
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }
}
