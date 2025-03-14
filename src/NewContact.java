import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewContact extends JFrame{
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JButton addContactButton;
    private JPanel panel;
    private JLabel lblPhone;
    private JLabel lblEmail;

    private boolean isPhoneValid = false;
    private boolean isEmailValid = false;

    public NewContact() {
        setSize(600, 400);
        setContentPane(panel);
        setVisible(true);

        textField2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String phone = textField2.getText().trim();
                if (!phone.matches("\\d{10,15}")) {
                    isPhoneValid = false;
                    lblPhone.setVisible(true);
                    lblPhone.setForeground(Color.RED);
                    lblPhone.setPreferredSize(new Dimension(30,3));
                    lblPhone.setText("Invalid Phone Number!");
                    textField2.requestFocus();
                } else {
                    isPhoneValid = true;
                    lblPhone.setVisible(false);
                }
            }
        });

        textField3.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
                Matcher matcher = pattern.matcher(textField3.getText().trim());

                if (!textField3.getText().isEmpty() && !matcher.matches()) {
                    isEmailValid = false;
                    lblEmail.setVisible(true);
                    lblEmail.setForeground(Color.RED);
                    lblEmail.setPreferredSize(new Dimension(30,3));
                    lblEmail.setText("Invalid Email Format!");
                    textField3.requestFocus();
                } else {
                    isEmailValid = true;
                    lblEmail.setVisible(false);

                }
            }
        });

        addContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String Name = textField1.getText();
                String Phone = textField2.getText();
                String Email = textField3.getText();
                String Address = textField4.getText();

                if (Name.isEmpty() || Phone.isEmpty() || Email.isEmpty() || Address.isEmpty() || !isPhoneValid || !isEmailValid) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled correctly!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    connect.addContact(Name, Phone, Email, Address);
                    JOptionPane.showMessageDialog(null, "Successfully added contact: " + Name, "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error adding contact: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
