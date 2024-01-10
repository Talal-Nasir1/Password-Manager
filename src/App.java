import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class App extends JFrame {
    private HashMap<String, String> passwordMap;
    private static final String DATA_FILE_PATH = System.getenv("APPDATA") + "\\PasswordManagerData.txt";

    public App() {
        passwordMap = new HashMap<>();

        setTitle("Password Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loadData();

        UI();
    }

    private String generatedRandomPassword(int length) {
        String charaters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+`{};:<>,.?/";
        StringBuilder password = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charaters.length());
            password.append(charaters.charAt(index));
        }
        return password.toString();
    }

    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE_PATH))) {
            for (String site : passwordMap.keySet()) {
                String password = passwordMap.get(site);
                writer.println(site + "," + password);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving password data.");
        }
    }

    private void loadData() {
        File dataFile = new File(DATA_FILE_PATH);

        if (dataFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String site = parts[0];
                        String password = parts[1];
                        passwordMap.put(site, password);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading password data.");
            }
        }
    }

    private void UI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField field = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton saveButton = new JButton("Save Passoword");
        JButton retrieveButton = new JButton("retrieve Password");
        JButton generaeButton = new JButton("Generate Random Password");
        JToggleButton showPasswordButton = new JToggleButton("Show Password");
        JTextField lengthField = new JTextField();
        lengthField.setColumns(3);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String site = field.getText();
                String password = new String(passwordField.getPassword());

                if (!site.isEmpty() && !password.isEmpty()) {
                    passwordMap.put(site, password);
                    saveData();
                    JOptionPane.showMessageDialog(null, "password saved for " + site);
                } else {
                    JOptionPane.showMessageDialog(null, "Site and Password cannon be empty");
                }

                field.setText("");
                passwordField.setText("");
            }
        });

        retrieveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String site = field.getText();
                String storedPassword = passwordMap.get(site);

                if (storedPassword != null) {
                    JOptionPane.showMessageDialog(null, "Password for " + site + ": " + storedPassword);
                } else {
                    JOptionPane.showMessageDialog(null, "Pssword not found for " + site);
                }

                field.setText("");
                passwordField.setText("");
            }
        });

        generaeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int passwordLength = Integer.parseInt(lengthField.getText());
                    String generatedPassword = generatedRandomPassword(passwordLength);
                    passwordField.setText(generatedPassword);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid password Length. ");
                }
            }
        });

        showPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (showPasswordButton.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('*');
                }
            }
        });

        panel.add(new JLabel("Site: "));
        panel.add(field);
        panel.add(new JLabel("Password: "));
        panel.add(passwordField);
        panel.add(saveButton);
        panel.add(retrieveButton);

        JPanel generatePanel = new JPanel();
        generatePanel.add(new JLabel("PasswordLength: "));
        generatePanel.add(lengthField);
        generatePanel.add(generaeButton);
        generatePanel.add(showPasswordButton);

        panel.add(generatePanel);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            FlatMacDarkLaf.setup();
        } catch (Exception e) {
            System.out.println("[class:Window][Exception]: There was a problem Loading'UIManager'");
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new App();
            }
        });
    }
}