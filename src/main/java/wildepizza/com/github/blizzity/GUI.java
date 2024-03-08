package wildepizza.com.github.blizzity;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import wildepizza.com.github.blizzity.gui.*;
import wildepizza.com.github.blizzity.utils.StringUtils;

import javafx.scene.media.Media;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URLEncoder;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class GUI {
    private JFrame frame;
    private JPanel panel;
    static public JRoundedTextField userText;
    static public JRoundedPasswordField passText;
    private JRoundedButton nextButton, createButton;
    private JPanel contentPanel;
    private API api;
    private Point lastClick; // Used for dragging
    public static JSimpleButton closeButton;
    public static JSimpleButton minimizeButton;
    Color color1 = new Color(19, 19, 20);
    Color color2 = new Color(30, 31, 32);
    Color color3 = new Color(191, 191, 191);
    Color color4 = new Color(63, 61, 62);
    Color color5 = new Color(26, 115, 233);
    Color color6 = new Color(227, 227, 227);
    Color color7 = new Color(210, 210, 210);
    Color color8 = new Color(46, 135, 253);
    Color color9 = new Color(22, 67, 128);
    GUI (API api) {
        this.api = api;
    }
    public void open() {
        frame = new JFrame("BlizzityAI");
        frame.setSize(450, 520);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setUndecorated(true);
        addTitleBarPanel();
        showContentPanel2();
//        showLoginPanel();
    }
    private void addTitleBarPanel() {
        JPanel titleBarPanel = new JPanel();
        titleBarPanel.setBackground(color2); // Set background color
        titleBarPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        minimizeButton = new JSimpleButton("-");
        minimizeButton.setBackground(color2);
        minimizeButton.setForeground(color7);
        minimizeButton.setPressedColor(color6, color4);
        minimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setState(JFrame.ICONIFIED); // Close the window
            }
        });
        minimizeButton.setPreferredSize(new Dimension(50, 40));
        titleBarPanel.add(minimizeButton);
        closeButton = new JSimpleButton("X");
        closeButton.setBackground(color2);
        closeButton.setForeground(color7);
        closeButton.setPressedColor(color6, Color.RED);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the window
            }
        });
        closeButton.setPreferredSize(new Dimension(50, 40));
        titleBarPanel.add(closeButton);

        // Add custom title bar panel to the frame
        titleBarPanel.setPreferredSize(new Dimension(450, 40));
        // Add mouse listener for dragging
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                frame.setLocation(frame.getX()+(e.getPoint().x-lastClick.x), frame.getY()+(e.getPoint().y-lastClick.y));
            }
            @Override
            public void mousePressed(MouseEvent e) {
                lastClick = e.getPoint();
            }
        };
        frame.addMouseListener(mouseHandler);
        frame.addMouseMotionListener(mouseHandler);
        frame.getContentPane().add(titleBarPanel, BorderLayout.NORTH);
    }
    private void showLoginPanel() {
        int width;
        panel = new JPanel();
        panel.setLayout(null);
        panel.addMouseListener(new MouseListeners());
        panel.setBackground(color1);

        String signInText = "Sign in";
        JLabel signInLabel = new JLabel(signInText);
        signInLabel.setForeground(color6);
        signInLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        width = signInLabel.getFontMetrics(signInLabel.getFont()).stringWidth(signInText);
        signInLabel.setBounds(frame.getWidth()/2-width/2, 90, width, 25);

        String descriptionText = "Use your Blizzity Account";
        JLabel descriptionLabel = new JLabel(descriptionText);
        descriptionLabel.setForeground(color7);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        width = descriptionLabel.getFontMetrics(descriptionLabel.getFont()).stringWidth(descriptionText);
        descriptionLabel.setBounds(frame.getWidth()/2-width/2, 130, width, 15);

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

        JTransparentButton createAccountButton = new JTransparentButton("Create account");
        createAccountButton.setBackground(color1);
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 13));
        createAccountButton.setBounds(45, 405, 120, 15);
        createAccountButton.setPressedColor(color8, color9);
        createAccountButton.setBorder(BorderFactory.createEmptyBorder());
        createAccountButton.setOpaque(false);
        createAccountButton.setForeground(color5);
        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.remove(panel);
                showRegisterPanel();
            }
        });

        nextButton = new JRoundedButton(10,10, "Next");
        nextButton.setForeground(color1); // Slightly lighter gray
        nextButton.setBackground(color5); // Slightly lighter gray
        nextButton.setPressedColor(color9, color8);
        nextButton.setBounds(330, 400, 80, 35);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!userText.getText().isEmpty()) {
                    if (!api.verify(userText.getText())) {
                        frame.remove(panel);
                        showLoginVerifyPanel(userText.getText());
                    } else
                        JOptionPane.showMessageDialog(frame, "This User doesn't exist", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add spacing around components for better organization
        panel.add(createAccountButton);
        panel.add(descriptionLabel);
        panel.add(forgotUsernameButton);
        panel.add(signInLabel);
        panel.add(userText);
        panel.add(nextButton);

        // Set preferred size for the panel to avoid potential layout issues
        panel.setPreferredSize(new Dimension(450, 520));

        frame.add(panel, BorderLayout.CENTER); // Add panel to the center of the frame
        frame.pack(); // Adjust frame size to fit components
        frame.setVisible(true);
    }
    private void showLoginVerifyPanel(String username) {
        int width;
        panel = new JPanel();
        panel.setLayout(null);
        panel.addMouseListener(new MouseListeners());
        panel.setBackground(color1);

        String signInText = "Welcome";
        JLabel signInLabel = new JLabel(signInText);
        signInLabel.setForeground(color6);
        signInLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        width = signInLabel.getFontMetrics(signInLabel.getFont()).stringWidth(signInText);
        signInLabel.setBounds(frame.getWidth()/2-width/2, 90, width, 25);

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setForeground(color7);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        width = usernameLabel.getFontMetrics(usernameLabel.getFont()).stringWidth(username);
        usernameLabel.setBounds(frame.getWidth()/2-width/2, 130, width, 15);

        String descriptionText = "To continue, first verify it's you";
        JLabel descriptionLabel = new JLabel(descriptionText);
        descriptionLabel.setForeground(color7);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        width = descriptionLabel.getFontMetrics(descriptionLabel.getFont()).stringWidth(descriptionText);
        descriptionLabel.setBounds(45, 190, width, 15);

        passText = new JRoundedPasswordField(10,10, 20);
        passText.setAlignmentOffset(20);
        passText.setOpaque(false);
        passText.setBackground(color2);
        passText.setForeground(color3);
        passText.setPreviewText("Enter your password", color4);
        passText.setFont(new Font("Arial", Font.PLAIN, 16));
        passText.setBorder(BorderFactory.createEmptyBorder());
        passText.setBounds(45, 225, 360, 55);

        JTransparentButton forgotUsernameButton = new JTransparentButton("Forgot password?");
        forgotUsernameButton.setBackground(color1);
        forgotUsernameButton.setFont(new Font("Arial", Font.BOLD, 13));
        forgotUsernameButton.setBounds(45, 405, 120, 15);
        forgotUsernameButton.setPressedColor(color8, color9);
        forgotUsernameButton.setBorder(BorderFactory.createEmptyBorder());
        forgotUsernameButton.setOpaque(false);
        forgotUsernameButton.setForeground(color5);

        nextButton = new JRoundedButton(10,10, "Log In");
        nextButton.setForeground(color1); // Slightly lighter gray
        nextButton.setBackground(color5); // Slightly lighter gray
        nextButton.setPressedColor(color9, color8);
        nextButton.setBounds(330, 400, 80, 35);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!userText.getText().isEmpty()) {
                    if (api.login(username, passText.getText())) {
                        frame.remove(panel);
                        showContentPanel(StringUtils.encrypt(userText.getText(), passText.getText()));
                    } else
                        JOptionPane.showMessageDialog(frame, "Wrong password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add spacing around components for better organization
        panel.add(descriptionLabel);
        panel.add(forgotUsernameButton);
        panel.add(signInLabel);
        panel.add(usernameLabel);
        panel.add(passText);
        panel.add(nextButton);

        // Set preferred size for the panel to avoid potential layout issues
        panel.setPreferredSize(new Dimension(450, 520));

        frame.add(panel, BorderLayout.CENTER); // Add panel to the center of the frame
        frame.pack(); // Adjust frame size to fit components
        frame.setVisible(true);
    }
    private void showRegisterPanel() {
        int width;
        panel = new JPanel();
        panel.setLayout(null);
        panel.addMouseListener(new MouseListeners());
        panel.setBackground(color1);

        String createText = "Create a Blizzity Account";
        JLabel createLabel = new JLabel(createText);
        createLabel.setForeground(color6);
        createLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        width = createLabel.getFontMetrics(createLabel.getFont()).stringWidth(createText);
        createLabel.setBounds(frame.getWidth()/2-width/2, 90, width, 25);

        String descriptionText = "Enter your credentials";
        JLabel descriptionLabel = new JLabel(descriptionText);
        descriptionLabel.setForeground(color7);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        width = descriptionLabel.getFontMetrics(descriptionLabel.getFont()).stringWidth(descriptionText);
        descriptionLabel.setBounds(frame.getWidth()/2-width/2, 130, width, 15);

        userText = new JRoundedTextField(10,10, 20);
        userText.setAlignmentOffset(20);
        userText.setOpaque(false);
        userText.setBackground(color2);
        userText.setForeground(color3);
        userText.setPreviewText("Username", color4);
        userText.setFont(new Font("Arial", Font.PLAIN, 16));
        userText.setBorder(BorderFactory.createEmptyBorder());
        userText.setBounds(45, 185, 360, 55);

        passText = new JRoundedPasswordField(10,10, 20);
        passText.setAlignmentOffset(20);
        passText.setOpaque(false);
        passText.setBackground(color2);
        passText.setForeground(color3);
        passText.setPreviewText("Enter your password", color4);
        passText.setFont(new Font("Arial", Font.PLAIN, 16));
        passText.setBorder(BorderFactory.createEmptyBorder());
        passText.setBounds(45, 265, 360, 55);

        JTransparentButton logInButton = new JTransparentButton("Already have an account?");
        logInButton.setBackground(color1);
        logInButton.setFont(new Font("Arial", Font.BOLD, 13));
        logInButton.setBounds(45, 405, 190, 15);
        logInButton.setPressedColor(color8, color9);
        logInButton.setBorder(BorderFactory.createEmptyBorder());
        logInButton.setOpaque(false);
        logInButton.setForeground(color5);
        logInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.remove(panel);
                showLoginPanel();
            }
        });

        createButton = new JRoundedButton(10,10, "Create");
        createButton.setForeground(color1); // Slightly lighter gray
        createButton.setBackground(color5); // Slightly lighter gray
        createButton.setPressedColor(color9, color8);
        createButton.setBounds(330, 400, 80, 35);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (api.register(userText.getText(), passText.getText())) {
                    frame.remove(panel);
                    showContentPanel(StringUtils.encrypt(userText.getText(), passText.getText()));
                } else
                    JOptionPane.showMessageDialog(frame, "This username is already taken", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add spacing around components for better organization
        panel.add(createLabel);
        panel.add(logInButton);
        panel.add(descriptionLabel);
        panel.add(passText);
        panel.add(userText);
        panel.add(createButton);

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

        String[] languages = {"English", "French", "German", "Italian", "Portuguese", "Spanish"};
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

        JRoundedButton logoutButton = new JRoundedButton(5,5, "Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.remove(contentPanel);
                showLoginPanel();
            }
        });
        contentPanel.add(logoutButton);
        JRoundedButton generateButton = new JRoundedButton(5,5, "Generate");
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (api.video(URLEncoder.encode(key), languageComboBox.getSelectedItem().toString().toLowerCase(), amountSlider.getValue())) {
                } else
                    JOptionPane.showMessageDialog(frame, "No video available", "Error", JOptionPane.ERROR_MESSAGE);
                }
        });
        contentPanel.add(generateButton);

        frame.add(contentPanel);
        frame.revalidate();
        frame.repaint();
    }
    private void showContentPanel2() {
        int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        int frameWidth = 1530;
        int frameHeight = 960;
        int x = (screenWidth - frameWidth) / 2;
        int y = (screenHeight - frameHeight) / 2;

        /*panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(color2);
        // Set preferred size for the panel to avoid potential layout issues
        panel.setPreferredSize(new Dimension(1530, 920));

        frame.add(panel, BorderLayout.CENTER); // Add panel to the center of the frame*/
        c();


        // Set the location of the JFrame
        frame.setLocation(x, y);
        frame.pack(); // Adjust frame size to fit components
        frame.setVisible(true);
    }

    private void c() {
        File file = new File("C:/Users/Finn/Videos/output.mp4");
        System.out.println(file.exists());
        JFXPanel videoPanel = new JFXPanel();
        videoPanel.setPreferredSize( new Dimension(1530, 1530));
        Platform.runLater(() -> {
            Media media = new Media(file.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);
            MediaView mediaView = new MediaView(player);

            // Create a layout container as the parent of the MediaView
            StackPane root = new StackPane();
            root.getChildren().add(mediaView);

            // Set the layout container as the root node of the scene
            Scene scene = new Scene(root);
            videoPanel.setScene(scene);

            // Play the media when it's ready
            player.setOnReady(() -> player.play());

            player.setOnError(() -> {
                System.out.println("Media error occurred: " + player.getError());
            });

            // Set the preferred size of the JFXPanel
            videoPanel.setPreferredSize(new Dimension(800, 600));

            player.play(); // Start playback automatically
        });
        frame.add(videoPanel);
    }
}
