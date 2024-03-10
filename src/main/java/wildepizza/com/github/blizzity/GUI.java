package wildepizza.com.github.blizzity;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.SVGPath;
import wildepizza.com.github.blizzity.gui.*;
import wildepizza.com.github.blizzity.utils.StringUtils;

import javafx.scene.media.Media;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URLEncoder;

public class GUI {
    private JFrame frame;
    private JPanel panel;
    static public JRoundedTextField userText;
    static public JRoundedPasswordField passText;
    private JRoundedButton nextButton;
    private JPanel titleBarPanel;
    private final API api;
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
    private javafx.scene.shape.Rectangle optionsBackground, optionsBackground2, resultBackground, resultBackground2, detailsBackground, detailsBackground2, timelineBackground, timelineBackground2;
    private javafx.scene.control.Label languageLabel, lengthLabel, generateLabel, adjustmentsLabel, resultLabel, detailsLabel, timelineLabel;
    private ComboBox<String> languageComboBox;
    private javafx.scene.control.Button logoutButton;
    private Slider amountSlider;
    private JFXPanel jfxPanel;
    void init() {
        jfxPanel = new JFXPanel();
        MouseListeners.addMouseClickListener(jfxPanel, 0, 40);
        optionsBackground = new javafx.scene.shape.Rectangle(670, 490);
        optionsBackground.setArcWidth(30);
        optionsBackground.setArcHeight(30);
        optionsBackground.setLayoutX(10);
        optionsBackground.setLayoutY(10);
        optionsBackground.setFill(javafx.scene.paint.Color.rgb(45, 45, 45));

        optionsBackground2 = new javafx.scene.shape.Rectangle(670, 410);
        optionsBackground2.setLayoutX(10);
        optionsBackground2.setLayoutY(50);
        optionsBackground2.setFill(javafx.scene.paint.Color.rgb(30, 31, 32));

        languageLabel = new javafx.scene.control.Label("Select Language:");
        languageLabel.setLayoutX(22);
        languageLabel.setLayoutY(55);
        languageLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        String[] languages = {"English", "French", "German", "Italian", "Portuguese", "Spanish"};
        languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll(languages);
        languageComboBox.getSelectionModel().select(0);
        languageComboBox.setLayoutX(22);
        languageComboBox.setLayoutY(82);

        amountSlider = new Slider(1, 20, 10);

        lengthLabel = new javafx.scene.control.Label("Select Length:"  + (int) amountSlider.getValue());
        lengthLabel.setLayoutX(22);
        lengthLabel.setLayoutY(195);
        lengthLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        amountSlider.setLayoutX(22);
        amountSlider.setLayoutY(235);
        amountSlider.setShowTickMarks(true);
        amountSlider.setMinorTickCount(20);
        amountSlider.setMinorTickCount(4);
        amountSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            lengthLabel.setText("Select Length:"  + newValue.intValue());
        });

        logoutButton = new javafx.scene.control.Button("Logout");
        logoutButton.setLayoutX(22);
        logoutButton.setLayoutY(275);
        logoutButton.setOnAction(actionEvent -> {
            frame.remove(panel);
            showLoginPanel();
        });

        generateLabel = new javafx.scene.control.Label("Generate");
        generateLabel.setLayoutX(22);
        generateLabel.setLayoutY(32);
        generateLabel.setTextFill(javafx.scene.paint.Color.rgb(3, 181, 193));


        adjustmentsLabel = new javafx.scene.control.Label("Adjustments");
        adjustmentsLabel.setLayoutX(90);
        adjustmentsLabel.setLayoutY(32);
        adjustmentsLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        resultBackground = new javafx.scene.shape.Rectangle(430, 490);
        resultBackground.setArcWidth(30);
        resultBackground.setArcHeight(30);
        resultBackground.setLayoutX(690);
        resultBackground.setLayoutY(10);
        resultBackground.setFill(javafx.scene.paint.Color.rgb(45, 45, 45));

        resultBackground2 = new javafx.scene.shape.Rectangle(430, 410);
        resultBackground2.setLayoutX(690);
        resultBackground2.setLayoutY(50);
        resultBackground2.setFill(javafx.scene.paint.Color.rgb(30, 31, 32));

        resultLabel = new javafx.scene.control.Label("Player");
        resultLabel.setLayoutX(702);
        resultLabel.setLayoutY(22);
        resultLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        detailsBackground = new javafx.scene.shape.Rectangle(390, 490);
        detailsBackground.setArcWidth(30);
        detailsBackground.setArcHeight(30);
        detailsBackground.setLayoutX(1130);
        detailsBackground.setLayoutY(10);
        detailsBackground.setFill(javafx.scene.paint.Color.rgb(45, 45, 45));

        detailsBackground2 = new javafx.scene.shape.Rectangle(390, 410);
        detailsBackground2.setLayoutX(1130);
        detailsBackground2.setLayoutY(50);
        detailsBackground2.setFill(javafx.scene.paint.Color.rgb(30, 31, 32));

        detailsLabel = new javafx.scene.control.Label("Details");
        detailsLabel.setLayoutX(1142);
        detailsLabel.setLayoutY(22);
        detailsLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        timelineBackground = new javafx.scene.shape.Rectangle(1510, 400);
        timelineBackground.setArcWidth(30);
        timelineBackground.setArcHeight(30);
        timelineBackground.setLayoutX(10);
        timelineBackground.setLayoutY(510);
        timelineBackground.setFill(javafx.scene.paint.Color.rgb(45, 45, 45));

        timelineBackground2 = new javafx.scene.shape.Rectangle(1510, 320);
        timelineBackground2.setLayoutX(10);
        timelineBackground2.setLayoutY(550);
        timelineBackground2.setFill(javafx.scene.paint.Color.rgb(30, 31, 32));

        timelineLabel = new javafx.scene.control.Label("Timeline");
        timelineLabel.setLayoutX(22);
        timelineLabel.setLayoutY(522);
        timelineLabel.setTextFill(javafx.scene.paint.Color.WHITE);
    }
    public void open() {
        frame = new JFrame("BlizzityAI");
        frame.setSize(450, 520);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setUndecorated(true);
        init();
        addTitleBarPanel(false);
        showContentPanel(StringUtils.encrypt("admin", "admin"));

//        showLoginPanel();
    }
    private void addTitleBarPanel(boolean advanced) {
        if (titleBarPanel != null) {
            frame.remove(titleBarPanel);
        }
        titleBarPanel = new JPanel();
        titleBarPanel.setBackground(color2); // Set background color
        titleBarPanel.setLayout(new GridBagLayout());
        MouseListeners.addMouseClickListener(titleBarPanel, 0, 0);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 10);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.anchor = GridBagConstraints.EAST;
        gbc2.weightx = 2000.0;

        if (advanced) {
            try {
                JImageButton exportButton = new JImageButton(10, 10, new ImageIcon(ImageIO.read(new File("export.png"))));
                exportButton.setBackground(color2);
                exportButton.setHoverForeground(color6);
                exportButton.setForeground(color7);
                exportButton.setHoverBackground(color4);
                exportButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Scene scene = jfxPanel.getScene();
                        Group root = ((Group)scene.getRoot());
                        Platform.runLater(() -> {
                            root.getChildren().remove(timelineLabel);
                            frame.add(jfxPanel);
                            frame.pack();
                        });
                    }
                });
                exportButton.setPreferredSize(new Dimension(40, 40));
                exportButton.setMinimumSize(new Dimension(40, 40));
                titleBarPanel.add(exportButton, gbc2);
                JImageButton shareButton = new JImageButton(10, 10, new ImageIcon(ImageIO.read(new File("share3.png"))));
                shareButton.setBackground(color2);
                shareButton.setHoverForeground(color6);
                shareButton.setForeground(color7);
                shareButton.setHoverBackground(color4);
                shareButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose(); // Close the window
                    }
                });
                shareButton.setPreferredSize(new Dimension(40, 40));
                shareButton.setMinimumSize(new Dimension(40, 40));
                titleBarPanel.add(shareButton, gbc);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }

        minimizeButton = new JSimpleButton("-");
        minimizeButton.setBackground(color2);
        minimizeButton.setHoverForeground(color6);
        minimizeButton.setForeground(color7);
        minimizeButton.setHoverBackground(color4);
        minimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setState(JFrame.ICONIFIED); // Close the window
            }
        });
        minimizeButton.setPreferredSize(new Dimension(50, 40));
        minimizeButton.setMinimumSize(new Dimension(50, 40));
        titleBarPanel.add(minimizeButton);
        closeButton = new JSimpleButton("X");
        closeButton.setBackground(color2);
        closeButton.setForeground(color7);
        closeButton.setHoverForeground(color6);
        closeButton.setHoverBackground(Color.RED);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the window
            }
        });
        closeButton.setPreferredSize(new Dimension(50, 40));
        closeButton.setMinimumSize(new Dimension(50, 40));
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
        MouseListeners.addMouseClickListener(panel, 0, 40);
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
        MouseListeners.addMouseClickListener(panel, 0, 40);
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
        MouseListeners.addMouseClickListener(panel, 0, 40);
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

        JRoundedButton createButton = new JRoundedButton(10, 10, "Create");
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
        int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        int frameWidth = 1530;
        int frameHeight = 960;
        int x = (screenWidth - frameWidth) / 2;
        int y = (screenHeight - frameHeight) / 2;

        int videoWidth = 1080;
        int videoHeight = 1920;
        int videoPreviewWidth = 230;
        int videoPreviewHeight = 410;


        File file = new File("received_video.mp4");
        Media media = new Media(file.toURI().toString());

        javafx.scene.control.Label usagesLabel = new javafx.scene.control.Label("Usages:" + api.usages(key));
        usagesLabel.setLayoutX(22);
        usagesLabel.setLayoutY(115);
        usagesLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        javafx.scene.control.Label creditsLabel = new javafx.scene.control.Label("Credits:" + api.credits(key));
        creditsLabel.setLayoutX(22);
        creditsLabel.setLayoutY(155);
        creditsLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        javafx.scene.control.Button generateButton = new javafx.scene.control.Button("Generate");
        generateButton.setLayoutX(22);
        generateButton.setLayoutY(315);
        generateButton.setOnAction(actionEvent -> {
            if (api.video(URLEncoder.encode(key), languageComboBox.getValue().toLowerCase(), (int) amountSlider.getValue())) {
                System.out.println("received video");
            } else
                JOptionPane.showMessageDialog(frame, "No video available", "Error", JOptionPane.ERROR_MESSAGE);
        });

        javafx.scene.control.Label nameLabel = new javafx.scene.control.Label("Name:");
        nameLabel.setLayoutX(1142);
        nameLabel.setLayoutY(60);
        nameLabel.setTextFill(javafx.scene.paint.Color.rgb(128, 128, 128));

        javafx.scene.control.Label nameLabel2 = new javafx.scene.control.Label(file.getName());
        nameLabel2.setLayoutX(1250);
        nameLabel2.setLayoutY(60);
        nameLabel2.setTextFill(javafx.scene.paint.Color.rgb(178, 178, 178));

        javafx.scene.control.Label pathLabel = new javafx.scene.control.Label("Path:");
        pathLabel.setLayoutX(1142);
        pathLabel.setLayoutY(83);
        pathLabel.setTextFill(javafx.scene.paint.Color.rgb(128, 128, 128));

        javafx.scene.control.Label pathLabel2 = new javafx.scene.control.Label(file.getAbsolutePath().replace("\\" + file.getName(), ""));
        pathLabel2.setLayoutX(1250);
        pathLabel2.setLayoutY(83);
        pathLabel2.setTextFill(javafx.scene.paint.Color.rgb(178, 178, 178));

        javafx.scene.control.Label ratioLabel = new javafx.scene.control.Label("Aspect ratio:");
        ratioLabel.setLayoutX(1142);
        ratioLabel.setLayoutY(106);
        ratioLabel.setTextFill(javafx.scene.paint.Color.rgb(128, 128, 128));

        javafx.scene.control.Label ratioLabel2 = new javafx.scene.control.Label("9:16");
        ratioLabel2.setLayoutX(1250);
        ratioLabel2.setLayoutY(106);
        ratioLabel2.setTextFill(javafx.scene.paint.Color.rgb(178, 178, 178));

        javafx.scene.control.Label resolutionLabel = new javafx.scene.control.Label("Resolution:");
        resolutionLabel.setLayoutX(1142);
        resolutionLabel.setLayoutY(129);
        resolutionLabel.setTextFill(javafx.scene.paint.Color.rgb(128, 128, 128));

        javafx.scene.control.Label resolutionLabel2 = new javafx.scene.control.Label(videoWidth + "x" + videoHeight);
        resolutionLabel2.setLayoutX(1250);
        resolutionLabel2.setLayoutY(129);
        resolutionLabel2.setTextFill(javafx.scene.paint.Color.rgb(178, 178, 178));

        javafx.scene.control.Label fpsLabel = new javafx.scene.control.Label("Frame rate:");
        fpsLabel.setLayoutX(1142);
        fpsLabel.setLayoutY(152);
        fpsLabel.setTextFill(javafx.scene.paint.Color.rgb(128, 128, 128));

        javafx.scene.control.Label fpsLabel2 = new javafx.scene.control.Label("25.00fps");
        fpsLabel2.setLayoutX(1250);
        fpsLabel2.setLayoutY(152);
        fpsLabel2.setTextFill(javafx.scene.paint.Color.rgb(178, 178, 178));

        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setScaleX((double) videoPreviewWidth / videoWidth);
        mediaView.setScaleY((double) videoPreviewHeight / videoHeight);
        mediaView.setLayoutY(-705);
        mediaView.setLayoutX(363.5);

        javafx.scene.control.Button playButton = new Button();
        playButton.setGraphic(createPlayShape(11, 12, javafx.scene.paint.Color.WHITE));
        playButton.setBackground(null);
        playButton.setLayoutX(890);
        playButton.setLayoutY(468);
        final boolean[] playing = {false};
        playButton.setOnAction(event -> {
            if (playing[0]) {
                mediaPlayer.pause();
                playButton.setGraphic(createPlayShape(11, 12, javafx.scene.paint.Color.WHITE));
            } else {
                mediaPlayer.play();
                playButton.setGraphic(createPauseShape(11, 12, javafx.scene.paint.Color.WHITE));
            }
            playing[0] = !playing[0];
        });
        Platform.runLater(() -> {
            Group root = new Group();
            Scene scene = new Scene(root, 0, 0);
            scene.setFill(javafx.scene.paint.Color.rgb(19, 19, 20));
            root.getChildren().addAll(
                    optionsBackground,
                    optionsBackground2,
                    generateLabel,
                    adjustmentsLabel,
                    svgPath(29, 12, javafx.scene.paint.Color.rgb(3, 181, 193), "M11.5805 4.77604C12.2752 3.00516 12.6226 2.11971 13.349 2.01056C14.0755 1.90141 14.6999 2.64083 15.9488 4.11967L16.2719 4.50226C16.6268 4.9225 16.8042 5.13263 17.0455 5.25261C17.2868 5.37259 17.5645 5.38884 18.1201 5.42135L18.6258 5.45095C20.5808 5.56537 21.5583 5.62258 21.8975 6.26168C22.2367 6.90079 21.713 7.69853 20.6656 9.29403L20.3946 9.7068C20.097 10.1602 19.9482 10.3869 19.908 10.6457C19.8678 10.9045 19.9407 11.1662 20.0866 11.6895L20.2195 12.166C20.733 14.0076 20.9898 14.9284 20.473 15.4325C19.9562 15.9367 19.0081 15.6903 17.1118 15.1975L16.6213 15.07C16.0824 14.93 15.813 14.86 15.5469 14.8999C15.2808 14.9399 15.0481 15.0854 14.5828 15.3763L14.1591 15.6412C12.5215 16.6649 11.7027 17.1768 11.0441 16.8493C10.3854 16.5217 10.3232 15.5717 10.1987 13.6717L10.1665 13.1801C10.1311 12.6402 10.1134 12.3702 9.98914 12.1361C9.86488 11.902 9.64812 11.7302 9.21459 11.3867L8.8199 11.0739C7.29429 9.86506 6.53149 9.26062 6.64124 8.55405C6.751 7.84748 7.66062 7.50672 9.47988 6.8252L9.95054 6.64888C10.4675 6.45522 10.726 6.35839 10.9153 6.17371C11.1046 5.98903 11.2033 5.73742 11.4008 5.23419L11.5805 4.77604Z"),
                    svgPath(29, 12, javafx.scene.paint.Color.rgb(3, 181, 193), "M5.31003 9.59277C2.87292 11.9213 1.27501 15.8058 2.33125 22.0002C3.27403 19.3966 5.85726 17.2407 8.91219 15.9528C8.80559 15.3601 8.7583 14.6364 8.70844 13.8733L8.66945 13.2782C8.66038 13.1397 8.65346 13.0347 8.64607 12.9443C8.643 12.9068 8.64012 12.8754 8.63743 12.8489C8.61421 12.829 8.58591 12.8053 8.55117 12.7769C8.47874 12.7177 8.39377 12.6503 8.28278 12.5623L7.80759 12.1858C7.11448 11.6368 6.46884 11.1254 6.02493 10.6538C5.77182 10.385 5.48876 10.0304 5.31003 9.59277Z"),
                    svgPath(29, 12, javafx.scene.paint.Color.rgb(3, 181, 193), "M10.3466 15.4231C10.3415 15.3857 10.3365 15.3475 10.3316 15.3086L10.3877 15.41C10.374 15.4144 10.3603 15.4187 10.3466 15.4231Z"),
                    svgPath(107, 7, 0.8f, javafx.scene.paint.Color.WHITE, "M23,13.4l-4.9,4.9c-0.2,0.2-0.5,0.3-0.7,0.3s-0.5-0.1-0.7-0.3c-0.4-0.4-0.4-1,0-1.4l4.9-4.9c-1.5-1.2-3.5-2-5.6-2 c-5,0-9,4-9,9s4,9,9,9s9-4,9-9C25,16.9,24.3,14.9,23,13.4z"),
                    svgPath(107, 9, 0.8f, javafx.scene.paint.Color.WHITE, "M16,8c-0.6,0-1-0.4-1-1V5c0-0.6,0.4-1,1-1s1,0.4,1,1v2C17,7.6,16.6,8,16,8z"),
                    svgPath(107, 9, 0.8f, javafx.scene.paint.Color.WHITE, "M10,9.6c-0.3,0-0.7-0.2-0.9-0.5l-1-1.7C7.9,6.9,8,6.3,8.5,6C9,5.7,9.6,5.9,9.9,6.4l1,1.7c0.3,0.5,0.1,1.1-0.4,1.4 C10.3,9.6,10.2,9.6,10,9.6z"),
                    svgPath(108, 9, 0.8f, javafx.scene.paint.Color.WHITE, "M5.6,14c-0.2,0-0.3,0-0.5-0.1l-1.7-1C2.9,12.6,2.7,12,3,11.5c0.3-0.5,0.9-0.6,1.4-0.4l1.7,1c0.5,0.3,0.6,0.9,0.4,1.4 C6.3,13.8,6,14,5.6,14z"),
                    svgPath(109, 9, 0.8f, javafx.scene.paint.Color.WHITE, "M4,20H2c-0.6,0-1-0.4-1-1s0.4-1,1-1h2c0.6,0,1,0.4,1,1S4.6,20,4,20z"),
                    svgPath(105, 9, 0.8f, javafx.scene.paint.Color.WHITE, "M30,20h-2c-0.6,0-1-0.4-1-1s0.4-1,1-1h2c0.6,0,1,0.4,1,1S30.6,20,30,20z"),
                    svgPath(106, 9, 0.8f, javafx.scene.paint.Color.WHITE, "M26.4,14c-0.3,0-0.7-0.2-0.9-0.5c-0.3-0.5-0.1-1.1,0.4-1.4l1.7-1c0.5-0.3,1.1-0.1,1.4,0.4c0.3,0.5,0.1,1.1-0.4,1.4l-1.7,1 C26.7,14,26.6,14,26.4,14z"),
                    svgPath(107, 9, 0.8f, javafx.scene.paint.Color.WHITE, "M22,9.6c-0.2,0-0.3,0-0.5-0.1c-0.5-0.3-0.6-0.9-0.4-1.4l1-1.7C22.4,5.9,23,5.7,23.5,6c0.5,0.3,0.6,0.9,0.4,1.4l-1,1.7 C22.7,9.4,22.3,9.6,22,9.6z"),
                    languageLabel,
                    languageComboBox,
                    usagesLabel,
                    creditsLabel,
                    lengthLabel,
                    amountSlider,
                    logoutButton,
                    generateButton,
                    resultBackground,
                    resultBackground2,
                    resultLabel,
                    mediaView,
                    playButton,
                    detailsBackground,
                    detailsBackground2,
                    detailsLabel,
                    nameLabel,
                    nameLabel2,
                    pathLabel,
                    pathLabel2,
                    ratioLabel,
                    ratioLabel2,
                    resolutionLabel,
                    resolutionLabel2,
                    fpsLabel,
                    fpsLabel2,
                    timelineBackground,
                    timelineBackground2,
                    timelineLabel
            );

            scene.setRoot(root);
            jfxPanel.setScene(scene);
            jfxPanel.setPreferredSize(new Dimension(1530, 920));
            addTitleBarPanel(true);
            frame.add(jfxPanel);
            frame.pack();
        });

        // Set the location of the JFrame
        frame.setLocation(x, y);
        frame.pack(); // Adjust frame size to fit components
        frame.setVisible(true);
    }
    private SVGPath svgPath(int x, int y, javafx.scene.paint.Color color, String path) {
        return svgPath(x, y, 1, color, path);
    }
    private Polygon createPlayShape(double width, double height, javafx.scene.paint.Color fillColor) {
        double halfHeight = height / 2;

        Polygon playShape = new Polygon();
        playShape.getPoints().addAll(
                width, halfHeight,
                0.0 , 0.0,
                0.0 , height,
                width, halfHeight
        );
        playShape.setFill(fillColor);

        return playShape;
    }
    private StackPane createPauseShape(double width, double height, javafx.scene.paint.Color fillColor) {
        double rectangleWidth = width / 3;
        double rectangleHeight = height;

        javafx.scene.shape.Rectangle pauseShape1 = new javafx.scene.shape.Rectangle(rectangleWidth, rectangleHeight);
        pauseShape1.setFill(fillColor);
        pauseShape1.setTranslateX(0);

        javafx.scene.shape.Rectangle pauseShape2 = new javafx.scene.shape.Rectangle(rectangleWidth, rectangleHeight);
        pauseShape2.setFill(fillColor);
        pauseShape2.setTranslateX(rectangleWidth * 2);

        StackPane pauseShape = new StackPane(pauseShape1, pauseShape2);
        pauseShape.setAlignment(Pos.CENTER);

        return pauseShape;
    }
    private SVGPath svgPath(int x, int y, float scale, javafx.scene.paint.Color color, String path) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(path);
        svgPath.setLayoutX(x);
        svgPath.setLayoutY(y);
        svgPath.setScaleX(scale);
        svgPath.setScaleY(scale);
        svgPath.setFill(color);
        return svgPath;
    }
}
