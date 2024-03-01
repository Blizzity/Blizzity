package wildepizza.com.github.blizzity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {
    private JFrame frame;
    private JPanel panel;
    private JLabel usernameLabel, passwordLabel, languageLabel, lengthLabel, usagesLabel, creditsLabel;
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
        // Create a JPanel with a more visually appealing layout manager
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Use descriptive variable names for better readability
        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Set appropriate font

        userText = new JTextField(20); // Set a reasonable width for the text field

        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(usernameLabel.getFont()); // Maintain consistency

        passText = new JPasswordField(20); // Set width for password field

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

        // Add spacing around components for better organization
        panel.add(usernameLabel);
        panel.add(Box.createHorizontalStrut(10)); // Add horizontal spacing
        panel.add(userText);

        panel.add(Box.createVerticalStrut(10)); // Add vertical spacing

        panel.add(passwordLabel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(passText);

        panel.add(Box.createVerticalStrut(15)); // Add more space before button

        panel.add(loginButton);

        // Set preferred size for the panel to avoid potential layout issues
        panel.setPreferredSize(new Dimension(300, 180));

        frame.add(panel, BorderLayout.CENTER); // Add panel to the center of the frame
        frame.pack(); // Adjust frame size to fit components
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
