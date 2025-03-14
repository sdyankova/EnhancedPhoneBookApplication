import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Objects;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class form extends JFrame{
    private JPanel panel;
    private JTable table1;
    private JButton addContactButton;
    private JButton deleteContactButton;

    public static DefaultTableModel model;
    private ArrayList<String[]> contacts;

    public form() {
        setSize(700, 400);
        setContentPane(panel);
        setVisible(true);


        model = new DefaultTableModel();
        table1.setModel(model);

        contacts = connect.executeQuery("SELECT * FROM PhonebookDB.Contacts");
        updateTable();

        table1.setColumnSelectionAllowed(true);

        deleteContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int selectedRow = table1.getSelectedRow();
                String Name = table1.getValueAt(selectedRow, 0).toString();
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to delete this row?",
                        "Delete Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    connect.deleteRow(Name);
                    refreshTable();
                }
            }
        });

        table1.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
            private String oldValue;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                oldValue = (value != null) ? value.toString() : "";
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }

            @Override
            public boolean stopCellEditing() {
                String newValue = getCellEditorValue().toString();
                int row = table1.getEditingRow();
                int column = table1.getEditingColumn();
                String columnName = table1.getColumnName(column);
                String Name = (String) table1.getValueAt(row, 0);

                if (!newValue.equals(oldValue)) {

                    boolean isValid = true;

                    if (Objects.equals(columnName, "Name")){
                        if (newValue.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Enter Name!", "Input Error", JOptionPane.ERROR_MESSAGE);
                            isValid = false;
                            refreshTable();
                        }
                    }

                    if (Objects.equals(columnName, "Phone")){
                        if (!newValue.matches("\\d{10,15}")) {
                            JOptionPane.showMessageDialog(null, "Invalid Phone Number!", "Input Error", JOptionPane.ERROR_MESSAGE);
                            isValid = false;
                            refreshTable();
                        }
                    }

                    if (Objects.equals(columnName, "Email")){
                        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
                        Matcher matcher = pattern.matcher(newValue);

                        if (newValue.isEmpty() || !matcher.matches()) {
                            JOptionPane.showMessageDialog(null, "Invalid Email!", "Input Error", JOptionPane.ERROR_MESSAGE);
                            isValid = false;
                            refreshTable();
                        }
                    }

                    if (Objects.equals(columnName, "Address")){
                        if (newValue.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Enter Address!", "Input Error", JOptionPane.ERROR_MESSAGE);
                            isValid = false;
                            refreshTable();
                        }
                    }

                    if (isValid) {
                        String[] columns = {columnName};
                        String[] newValues = {newValue};
                        connect.updateDatabase(Name, columns, newValues);
                    }
                }

                return super.stopCellEditing();
            }
        });

        addContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame child = new NewContact();
                child.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        refreshTable();
                    }
                });
            }
        });
    }

    private void updateTable() {
        model.setRowCount(0);
        for (String[] client : contacts) {
            model.addRow(client);
        }
    }

    private void refreshTable(){
        model = new DefaultTableModel();
        table1.setModel(model);
        contacts = connect.executeQuery("SELECT * FROM PhonebookDB.Contacts");
        updateTable();
    }
}
