package wildepizza.com.github.blizzity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {
    private JFrame frame;
    private JPanel panel;
    private JLabel userLabel, passLabel, languageLabel, lengthLabel, usagesLabel, creditsLabel;
    private JTextField userText;
    private JPasswordField passText;
    private JButton loginButton;
    private JComboBox<String> languageComboBox;
    private JSlider amountSlider;
    private JPanel contentPanel;
    private API api;
    GUI (API api) {
        this.api = api;
    }
    public void open() {
        frame = new JFrame("BlizzityAI");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showLoginPanel();
    }

    private void showLoginPanel() {
        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        userLabel = new JLabel("Username:");
        panel.add(userLabel);

        userText = new JTextField();
        panel.add(userText);

        passLabel = new JLabel("Password:");
        panel.add(passLabel);

        passText = new JPasswordField();
        panel.add(passText);

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passText.getPassword());
                if (api.login(username, password)) {
                    frame.remove(panel);
                    showContentPanel(StringUtils.encrypt(username, password));
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(loginButton);

        frame.add(panel);
        frame.setVisible(true);
    }
    private void showContentPanel(String key) {
        contentPanel = new JPanel();

        languageLabel = new JLabel("Select Language:");
        contentPanel.add(languageLabel);

        String[] languages = {"English", "Spanish", "French"};
        languageComboBox = new JComboBox<>(languages);
        contentPanel.add(languageComboBox);

        usagesLabel = new JLabel("Usages:" + api.usages(key));
        contentPanel.add(usagesLabel);

        creditsLabel = new JLabel("Credits:" + api.credits(key));
        contentPanel.add(creditsLabel);

        lengthLabel = new JLabel("Select Length:");
        contentPanel.add(lengthLabel);

        amountSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, 10);
        amountSlider.setMajorTickSpacing(1);
        amountSlider.setMinorTickSpacing(0);
        amountSlider.setPaintTicks(true);
        amountSlider.setPaintLabels(true);
        contentPanel.add(amountSlider);
        contentPanel.setLayout(new GridLayout(2, 1));

        loginButton = new JButton("Logout");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.remove(contentPanel);
                showLoginPanel();
            }
        });
        contentPanel.add(loginButton);

        frame.add(contentPanel);
        frame.revalidate();
        frame.repaint();
    }
}
