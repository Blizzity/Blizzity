package wildepizza.com.github.blizzity;

import wildepizza.com.github.blizzity.gui.JRoundedTextField;
import wildepizza.com.github.blizzity.gui.JTransparentButton;
import wildepizza.com.github.blizzity.gui.MouseListeners;
import wildepizza.com.github.blizzity.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI {
    private JFrame frame;
    private JPanel panel;
    static public JRoundedTextField userText;
    private JPasswordField passText;
    private JButton loginButton;
    private JPanel contentPanel;
    private API api;
    GUI (API api) {
        this.api = api;
    }
    public void open() {
        frame = new JFrame("BlizzityAI");
        frame.setSize(450, 520);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        showLoginPanel();
    }

    private void showLoginPanel() {
        Color color1 = new Color(19, 19, 20);
        Color color2 = new Color(30, 31, 32);
        Color color3 = new Color(191, 191, 191);
        Color color4 = new Color(63, 61, 62);
        Color color5 = new Color(26, 115, 233);
        Color color6 = new Color(227, 227, 227);
        Color color7 = new Color(210, 210, 210);
        Color color8 = new Color(46, 135, 253);
        Color color9 = new Color(22, 67, 128);

        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setLayout(null);
        panel.addMouseListener(new MouseListeners());
        panel.setBackground(color1);

        JLabel signInLabel = new JLabel("Sign in");
        signInLabel.setForeground(color6);
        signInLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        signInLabel.setBounds(190, 90, 70, 25);

        JLabel textLabel = new JLabel("Use your Blizzity Account");
        textLabel.setForeground(color7);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        textLabel.setBounds(130, 130, 190, 15);

        userText = new JRoundedTextField(10,10, 20);
        userText.setAlignmentOffset(20);
        userText.setOpaque(false);
        userText.setBackground(color2);
        userText.setForeground(color3);
        userText.setPreviewText("Username", color4);
        userText.setFont(new Font("Arial", Font.PLAIN, 16));
        userText.setBorder(BorderFactory.createEmptyBorder());
        userText.setBounds(45, 185, 360, 55);

        JTransparentButton forgotUsernameButton = new JTransparentButton("Forgot username?");
        forgotUsernameButton.setBackground(color1);
        forgotUsernameButton.setFont(new Font("Arial", Font.BOLD, 13));
        forgotUsernameButton.setBounds(45, 250, 120, 15);
        forgotUsernameButton.setPressedColor(color8, color9);
        forgotUsernameButton.setBorder(BorderFactory.createEmptyBorder());
        forgotUsernameButton.setOpaque(false);
        forgotUsernameButton.setForeground(color5);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE); // White text
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));

        passText = new JPasswordField(20); // Set width for password field
        passText.setBackground(color2); // Slightly lighter gray
        passText.setForeground(Color.WHITE);
        passText.setBorder(BorderFactory.createEmptyBorder());

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
        panel.add(textLabel);
        panel.add(forgotUsernameButton);
        panel.add(signInLabel);
        panel.add(Box.createHorizontalStrut(10)); // Add horizontal spacing
        panel.add(userText);

        panel.add(Box.createVerticalStrut(10)); // Add vertical spacing

        panel.add(passwordLabel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(passText);

        panel.add(Box.createVerticalStrut(15)); // Add more space before button

        panel.add(loginButton);

        // Set preferred size for the panel to avoid potential layout issues
        panel.setPreferredSize(new Dimension(450, 520));

        frame.add(panel, BorderLayout.CENTER); // Add panel to the center of the frame
        frame.pack(); // Adjust frame size to fit components
        frame.setVisible(true);
    }
    private void showContentPanel(String key) {
        contentPanel = new JPanel();

        JLabel languageLabel = new JLabel("Select Language:");
        contentPanel.add(languageLabel);

        String[] languages = {"English", "Spanish", "French"};
        JComboBox<String> languageComboBox = new JComboBox<>(languages);
        contentPanel.add(languageComboBox);

        JLabel usagesLabel = new JLabel("Usages:" + api.usages(key));
        contentPanel.add(usagesLabel);

        JLabel creditsLabel = new JLabel("Credits:" + api.credits(key));
        contentPanel.add(creditsLabel);

        JLabel lengthLabel = new JLabel("Select Length:");
        contentPanel.add(lengthLabel);

        JSlider amountSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, 10);
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
