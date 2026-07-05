import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public AddTransaction(ArrayList<Category> categories, ArrayList<Transaction> transactions) {
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

        //TODO fix create transaction logic
        //ActionListeners
        this.create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int int1 = Integer.parseInt(sumField.getText());
                    if (outgoCategories.isSelectionEmpty()) {
                        transactions.add(new Transaction(int1, incomeCategories.getSelectedValue()));
                    } else {
                        transactions.add(new Transaction(int1, outgoCategories.getSelectedValue()));
                    }
                    AddTransaction.this.dispose();
                } catch (NumberFormatException e1) {
                    AddTransaction.this.sumField.setText("Please fill with numbers");
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
