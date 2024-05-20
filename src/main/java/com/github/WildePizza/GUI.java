package com.github.WildePizza;

import com.github.WildePizza.gui.javafx.*;
import com.github.WildePizza.gui.swing.*;
import com.github.WildePizza.utils.StringUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.util.Duration;
import com.github.WildePizza.gui.listeners.LoginListener;
import com.github.WildePizza.gui.listeners.ScreenListener;

import javafx.scene.media.Media;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"SameParameterValue", "deprecation"})
public class GUI {
    public static boolean autoScale = true;
    public static double sizeMultiplier = 0.9;
    public static JFrame frame;
    private SVGButton selectedSectionButton;
    private JPanel panel;
    static public JRoundedTextField userText;
    static public JRoundedPasswordField passText;
    private JRoundedButton nextButton;
    private JDarkenedPanel titleBarPanel;
    private AccountComboBox accountComboBox;
    private JLabel signInLabel, descriptionJLabel;
    private final API api;
    private Point lastClick; // Used for dragging
    public static JSimpleButton closeButton;
    public static JSimpleButton minimizeButton;
    public static SimpleVariables variables;
    Color color1 = new Color(19, 19, 20);
    public static Color color2 = new Color(30, 31, 32);
    Color color3 = new Color(191, 191, 191);
    Color color4 = new Color(63, 61, 62);
    Color color5 = new Color(26, 115, 233);
    Color color6 = new Color(227, 227, 227);
    Color color7 = new Color(210, 210, 210);
    Color color8 = new Color(46, 135, 253);
    Color color9 = new Color(22, 67, 128);
    private Rectangle
            optionsBackground,
            optionsBackground2,
            resultBackground,
            resultBackground2,
            detailsBackground,
            detailsBackground2,
            timelineBackground,
            timelineBackground2,
            darkenBackground,
            loadingBackground,
            exportBackground,
            exportTitle,
            shareBackground,
            shareTitle;
    private Switch discloseSwitch;
    private Label
            discloseLabel,
            discloseDescribtionLabel,
            languageLabel,
            lengthLabel,
            resultLabel,
            detailsLabel,
            timelineLabel,
            nameLabel,
            captionNameLabel,
            exportLabel,
            exportFieldLabel,
            exportTitleLabel,
            spaceLabel,
            shareTitleLabel,
            titleLabel,
            descriptionLabel,
            nameLabel2,
            pathLabel,
            pathLabel2,
            nameDetailLabel,
            ratioLabel,
            ratioLabel2,
            resolutionLabel,
            resolutionLabel2,
            usagesLabel,
            creditsLabel,
            tiktokLabel,
            snapchatLabel,
            adminLabel,
            youtubeLabel,
            fpsLabel,
            fpsLabel2,
            settingsLabel;
    private SimpleComboBox<String>
            languageComboBox,
            spaceComboBox,
            privacyComboBox,
            lengthComboBox,
            youtubePrivacyComboBox,
            youtubeComboBox,
            tiktokComboBox,
            snapchatComboBox;
    private Label
            privacyLabel,
            stitchLabel,
            duetLabel,
            commentLabel,
            youtubePrivacyLabel;
    private CheckBox
            commentCheckBox,
            stitchCheckBox,
            duetCheckBox;
    private Double<File, String> file;
    private Button
            jfxCloseButton,
            folderButton;
    private SimpleButton
            logoutButton,
            exportCompleteButton,
            generateButton,
            resetButton,
            shareCompleteButton;
    private TextField nameTextField;
    private TextArea
            captionNameTextField,
            titleTextField,
            descriptionTextField;
    private MediaView
            mediaView,
            mediaViewClone;
    public static JFXPanel jfxPanel;
    private JIconButton exportButton;
    private JIconButton shareButton;
    ImageView imageView;
    int screenWidth = 1530;
    int screenHeight = 1000;
    String[] languages = {"English", "French", "German", "Italian", "Portuguese", "Spanish"};
    double x = (double) screenWidth /2- (double) 640 /2;
    double y = (double) screenHeight / 2 - (double) 670 / 2;
    GUI (API api) {
        this.api = api;
    }
    void init() {
        variables = new VariablesBuilder().withProjectName("Blizzity").build();
        String signInText = "Sign in";
        signInLabel = new JLabel(signInText);
        signInLabel.setForeground(color6);
        signInLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        int width = signInLabel.getFontMetrics(signInLabel.getFont()).stringWidth(signInText)+5;
        signInLabel.setBounds(frame.getWidth()/2-width/2, 90, width, 25);

        String descriptionText = "Use your Blizzity Account";
        descriptionJLabel = new JLabel(descriptionText);
        descriptionJLabel.setForeground(color7);
        descriptionJLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        width = descriptionJLabel.getFontMetrics(descriptionJLabel.getFont()).stringWidth(descriptionText);
        descriptionJLabel.setBounds(frame.getWidth()/2-width/2, 130, width, 15);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if ((screenWidth > screenSize.getWidth() || screenHeight > screenSize.getHeight()) && autoScale)
            sizeMultiplier = Math.min(screenSize.getHeight()/screenHeight, screenSize.getWidth()/screenWidth);
        jfxPanel = new JFXPanel();
        ScreenListener.addMouseListener(jfxPanel, 0, 40);
        optionsBackground = new Rectangle(670*sizeMultiplier, 490*sizeMultiplier);
        optionsBackground.setArcWidth(30*sizeMultiplier);
        optionsBackground.setArcHeight(30*sizeMultiplier);
        optionsBackground.setLayoutX(10*sizeMultiplier);
        optionsBackground.setLayoutY(10*sizeMultiplier);
        optionsBackground.setFill(javafx.scene.paint.Color.rgb(45, 45, 45));

        optionsBackground2 = new Rectangle(670*sizeMultiplier, 410*sizeMultiplier);
        optionsBackground2.setLayoutX(10*sizeMultiplier);
        optionsBackground2.setLayoutY(50*sizeMultiplier);
        optionsBackground2.setFill(javafx.scene.paint.Color.rgb(30, 31, 32));

        languageLabel = new Label("Language:");
        languageLabel.setFont(new javafx.scene.text.Font(languageLabel.getFont().getFamily(), languageLabel.getFont().getSize()*sizeMultiplier));
        languageLabel.setLayoutX(22*sizeMultiplier);
        languageLabel.setLayoutY(65*sizeMultiplier);
        languageLabel.setStyle("-fx-text-fill: rgb(210, 210, 210)"); //TODO make it look better
        languageLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        languageComboBox = new SimpleComboBox<>(100*sizeMultiplier, 25*sizeMultiplier, 10*sizeMultiplier);
        languageComboBox.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
        languageComboBox.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));
        languageComboBox.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
        languageComboBox.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
        languageComboBox.setSelectedBackgroundColor(javafx.scene.paint.Color.rgb(46, 67, 110));
        languageComboBox.getItems().addAll(languages);
        languageComboBox.getSelectionModel().select(0);
        languageComboBox.setLayoutX(87*sizeMultiplier);
        languageComboBox.setLayoutY(62*sizeMultiplier);

        lengthLabel = new Label("Length:");
        lengthLabel.setFont(new javafx.scene.text.Font(lengthLabel.getFont().getFamily(), lengthLabel.getFont().getSize()*sizeMultiplier));
        lengthLabel.setLayoutX(22*sizeMultiplier);
        lengthLabel.setLayoutY(105*sizeMultiplier);
        lengthLabel.setStyle("-fx-text-fill: rgb(210, 210, 210)"); //TODO make it look better
        lengthLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        String[] lengths = {"< 1 min", "> 1 min 30 sec"};
        lengthComboBox = new SimpleComboBox<>(117*sizeMultiplier, 25*sizeMultiplier, 10*sizeMultiplier);
        lengthComboBox.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
        lengthComboBox.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));
        lengthComboBox.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
        lengthComboBox.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
        lengthComboBox.setSelectedBackgroundColor(javafx.scene.paint.Color.rgb(46, 67, 110));
        lengthComboBox.getItems().addAll(lengths);
        lengthComboBox.getSelectionModel().select(0);
        lengthComboBox.setLayoutX(87*sizeMultiplier);
        lengthComboBox.setLayoutY(102*sizeMultiplier);

        logoutButton = new SimpleButton("Logout", 72*sizeMultiplier, 22*sizeMultiplier, 10*sizeMultiplier);
        logoutButton.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
        logoutButton.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
        logoutButton.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
        logoutButton.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));
        logoutButton.setLayoutX(590*sizeMultiplier);
        logoutButton.setLayoutY(470*sizeMultiplier);
        logoutButton.setOnAction(actionEvent -> {
            variables.deleteVariable("key");
            frame.remove(jfxPanel);
            showLoginPanel();
        });

        resultBackground = new Rectangle(430*sizeMultiplier, 490*sizeMultiplier);
        resultBackground.setArcWidth(30*sizeMultiplier);
        resultBackground.setArcHeight(30*sizeMultiplier);
        resultBackground.setLayoutX(690*sizeMultiplier);
        resultBackground.setLayoutY(10*sizeMultiplier);
        resultBackground.setFill(javafx.scene.paint.Color.rgb(45, 45, 45));

        resultBackground2 = new Rectangle(430*sizeMultiplier, 410*sizeMultiplier);
        resultBackground2.setLayoutX(690*sizeMultiplier);
        resultBackground2.setLayoutY(50*sizeMultiplier);
        resultBackground2.setFill(javafx.scene.paint.Color.rgb(30, 31, 32));

        resultLabel = new Label("Player");
        resultLabel.setFont(new javafx.scene.text.Font(resultLabel.getFont().getFamily(), resultLabel.getFont().getSize()*sizeMultiplier));
        resultLabel.setLayoutX(702*sizeMultiplier);
        resultLabel.setLayoutY(22*sizeMultiplier);
        resultLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        detailsBackground = new Rectangle(390*sizeMultiplier, 490*sizeMultiplier);
        detailsBackground.setArcWidth(30*sizeMultiplier);
        detailsBackground.setArcHeight(30*sizeMultiplier);
        detailsBackground.setLayoutX(1130*sizeMultiplier);
        detailsBackground.setLayoutY(10*sizeMultiplier);
        detailsBackground.setFill(javafx.scene.paint.Color.rgb(45, 45, 45));

        detailsBackground2 = new Rectangle(390*sizeMultiplier, 410*sizeMultiplier);
        detailsBackground2.setLayoutX(1130*sizeMultiplier);
        detailsBackground2.setLayoutY(50*sizeMultiplier);
        detailsBackground2.setFill(javafx.scene.paint.Color.rgb(30, 31, 32));

        detailsLabel = new Label("Details");
        detailsLabel.setFont(new javafx.scene.text.Font(detailsLabel.getFont().getFamily(), detailsLabel.getFont().getSize()*sizeMultiplier));
        detailsLabel.setLayoutX(1142*sizeMultiplier);
        detailsLabel.setLayoutY(22*sizeMultiplier);
        detailsLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        timelineBackground = new Rectangle(1510*sizeMultiplier, 400*sizeMultiplier);
        timelineBackground.setArcWidth(30*sizeMultiplier);
        timelineBackground.setArcHeight(30*sizeMultiplier);
        timelineBackground.setLayoutX(10*sizeMultiplier);
        timelineBackground.setLayoutY(510*sizeMultiplier);
        timelineBackground.setFill(javafx.scene.paint.Color.rgb(45, 45, 45));

        timelineBackground2 = new Rectangle(1510*sizeMultiplier, 320*sizeMultiplier);
        timelineBackground2.setLayoutX(10*sizeMultiplier);
        timelineBackground2.setLayoutY(550*sizeMultiplier);
        timelineBackground2.setFill(javafx.scene.paint.Color.rgb(30, 31, 32));

        timelineLabel = new Label("Timeline");
        timelineLabel.setFont(new javafx.scene.text.Font(timelineLabel.getFont().getFamily(), timelineLabel.getFont().getSize()*sizeMultiplier));
        timelineLabel.setLayoutX(22*sizeMultiplier);
        timelineLabel.setLayoutY(522*sizeMultiplier);
        timelineLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        darkenBackground = new Rectangle(screenWidth*sizeMultiplier, screenHeight*sizeMultiplier);
        darkenBackground.setLayoutX(0*sizeMultiplier);
        darkenBackground.setLayoutY(0*sizeMultiplier);
        darkenBackground.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.5));

        loadingBackground = new Rectangle(screenWidth*sizeMultiplier, screenHeight*sizeMultiplier);
        loadingBackground.setLayoutX(0*sizeMultiplier);
        loadingBackground.setLayoutY(0*sizeMultiplier);
        loadingBackground.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.5));

        exportBackground = new Rectangle(640*sizeMultiplier, 540*sizeMultiplier);
        exportBackground.setLayoutX(x*sizeMultiplier);
        exportBackground.setLayoutY(y - 40 + 65*sizeMultiplier);
        exportBackground.setFill(javafx.scene.paint.Color.rgb(19, 19, 20));

        shareBackground = new Rectangle(640*sizeMultiplier, 540*sizeMultiplier);
        shareBackground.setLayoutX(x*sizeMultiplier);
        shareBackground.setLayoutY(y - 40 + 65*sizeMultiplier);
        shareBackground.setFill(javafx.scene.paint.Color.rgb(19, 19, 20));

        exportTitle = new Rectangle(640*sizeMultiplier, 40*sizeMultiplier);
        exportTitle.setLayoutX(x*sizeMultiplier);
        exportTitle.setLayoutY(y - 80 + 65*sizeMultiplier);
        exportTitle.setFill(javafx.scene.paint.Color.rgb(30, 31, 32));

        shareTitle = new Rectangle(640*sizeMultiplier, 40*sizeMultiplier);
        shareTitle.setLayoutX(x*sizeMultiplier);
        shareTitle.setLayoutY(y - 80 + 65*sizeMultiplier);
        shareTitle.setFill(javafx.scene.paint.Color.rgb(30, 31, 32));

        exportTitleLabel = new Label("Export");
        exportTitleLabel.setFont(new javafx.scene.text.Font(exportTitleLabel.getFont().getFamily(), exportTitleLabel.getFont().getSize()*sizeMultiplier));
        exportTitleLabel.setFont(javafx.scene.text.Font.font("Arial", FontPosture.REGULAR, 16));
        exportTitleLabel.setLayoutX(x + 15*sizeMultiplier);
        exportTitleLabel.setLayoutY(y - 70 + 65*sizeMultiplier);
        exportTitleLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        shareTitleLabel = new Label("Share");
        shareTitleLabel.setFont(new javafx.scene.text.Font(shareTitleLabel.getFont().getFamily(), shareTitleLabel.getFont().getSize()*sizeMultiplier));
        shareTitleLabel.setFont(javafx.scene.text.Font.font("Arial", FontPosture.REGULAR, 16));
        shareTitleLabel.setLayoutX(x + 15*sizeMultiplier);
        shareTitleLabel.setLayoutY(y - 70 + 65*sizeMultiplier);
        shareTitleLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        generateButton = new SimpleButton("Generate", 72*sizeMultiplier, 22*sizeMultiplier, 10*sizeMultiplier);
        generateButton.setBackgroundColor(javafx.scene.paint.Color.rgb(53,116,240));
        generateButton.setStrokeColor(javafx.scene.paint.Color.rgb(53,116,240));
        generateButton.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53,116,240));
        generateButton.setTextFill(javafx.scene.paint.Color.WHITE);
        generateButton.setLayoutX(590*sizeMultiplier);
        generateButton.setLayoutY(470*sizeMultiplier);

        resetButton = new SimpleButton("Reset", 72*sizeMultiplier, 22*sizeMultiplier, 10*sizeMultiplier);
        resetButton.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
        resetButton.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
        resetButton.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
        resetButton.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));
        resetButton.setLayoutX(510*sizeMultiplier);
        resetButton.setLayoutY(470*sizeMultiplier);
        resetButton.setOnAction(actionEvent -> {
            lengthComboBox.getSelectionModel().select(0);
            languageComboBox.getSelectionModel().select(0);
        });

        // export

        {
            nameLabel = new Label("Name");
            nameLabel.setFont(new javafx.scene.text.Font(nameLabel.getFont().getFamily(), nameLabel.getFont().getSize()*sizeMultiplier));
            nameLabel.setPrefSize(85*sizeMultiplier, 20*sizeMultiplier);
            nameLabel.setLayoutX(x + 360*sizeMultiplier);
            nameLabel.setLayoutY(y - 25 + 65*sizeMultiplier);
            nameLabel.setTextFill(javafx.scene.paint.Color.WHITE);

            nameTextField = new TextField("output");
            nameTextField.setPrefSize(200*sizeMultiplier, 20*sizeMultiplier);
            nameTextField.setStyle("-fx-background-color: rgb(45, 45, 45); -fx-background-radius: 5; -fx-border-radius: 5; -fx-text-fill: white;");
            nameTextField.setLayoutX(x + 425*sizeMultiplier);
            nameTextField.setLayoutY(y - 25 + 65*sizeMultiplier);

            exportLabel = new Label("Export to");
            exportLabel.setFont(new javafx.scene.text.Font(    exportLabel.getFont().getFamily(),     exportLabel.getFont().getSize()*sizeMultiplier));
            exportLabel.setPrefSize(85*sizeMultiplier, 20*sizeMultiplier);
            exportLabel.setLayoutX(x + 360*sizeMultiplier);
            exportLabel.setLayoutY(y + 20 + 65*sizeMultiplier);
            exportLabel.setTextFill(javafx.scene.paint.Color.WHITE);

            File defaultDir = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getParent() + "\\Videos");
            exportFieldLabel = new Label("  " + defaultDir);
            exportFieldLabel.setFont(new javafx.scene.text.Font(    exportFieldLabel.getFont().getFamily(),     exportFieldLabel.getFont().getSize()*sizeMultiplier));
            exportFieldLabel.setPrefSize(159*sizeMultiplier, 26*sizeMultiplier);
            exportFieldLabel.setStyle("-fx-background-color: rgb(45, 45, 45); -fx-background-radius: 5; -fx-border-radius: 5; -fx-text-fill: white;");
            exportFieldLabel.setLayoutX(x + 425*sizeMultiplier);
            exportFieldLabel.setLayoutY(y + 17 + 65*sizeMultiplier);

            folderButton = new Button();
            SVGPath path = new SVGPath();
            path.setContent("M3 8.2C3 7.07989 3 6.51984 3.21799 6.09202C3.40973 5.71569 3.71569 5.40973 4.09202 5.21799C4.51984 5 5.0799 5 6.2 5H9.67452C10.1637 5 10.4083 5 10.6385 5.05526C10.8425 5.10425 11.0376 5.18506 11.2166 5.29472C11.4184 5.4184 11.5914 5.59135 11.9373 5.93726L12.0627 6.06274C12.4086 6.40865 12.5816 6.5816 12.7834 6.70528C12.9624 6.81494 13.1575 6.89575 13.3615 6.94474C13.5917 7 13.8363 7 14.3255 7H17.8C18.9201 7 19.4802 7 19.908 7.21799C20.2843 7.40973 20.5903 7.71569 20.782 8.09202C21 8.51984 21 9.0799 21 10.2V15.8C21 16.9201 21 17.4802 20.782 17.908C20.5903 18.2843 20.2843 18.5903 19.908 18.782C19.4802 19 18.9201 19 17.8 19H6.2C5.07989 19 4.51984 19 4.09202 18.782C3.71569 18.5903 3.40973 18.2843 3.21799 17.908C3 17.4802 3 16.9201 3 15.8V8.2Z");
            path.setStrokeWidth(1.3);
            path.setStroke(javafx.scene.paint.Color.WHITE);
            path.setFill(javafx.scene.paint.Color.rgb(45, 45, 45));
            folderButton.setGraphic(path);
            folderButton.setPrefSize(36*sizeMultiplier, 26*sizeMultiplier);
            folderButton.setStyle("-fx-background-color: rgb(45, 45, 45); -fx-background-radius: 5; -fx-border-radius: 5; -fx-text-fill: white;");
            folderButton.setLayoutX(x + 425 + 159 + 5*sizeMultiplier);
            folderButton.setLayoutY(y + 17 + 65*sizeMultiplier);
            folderButton.setOnAction(event -> {
                JFileChooser fileChooser = new JFileChooser(defaultDir);
                // Set to directories only for folder selection
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(frame);

                if (result == JFileChooser.APPROVE_OPTION) {
                    // Get the selected folder path
                    String selectedFolder = fileChooser.getSelectedFile().getAbsolutePath();
                    exportFieldLabel.setText("  " + selectedFolder);
                }
            });

            exportCompleteButton = new SimpleButton("Generate", 10*sizeMultiplier);
            exportCompleteButton.setPrefSize(100*sizeMultiplier, 26*sizeMultiplier);
            exportCompleteButton.setLayoutX(x + 500*sizeMultiplier);
            exportCompleteButton.setLayoutY(y + 455 + 65*sizeMultiplier);
            exportCompleteButton.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
            exportCompleteButton.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
            exportCompleteButton.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
            exportCompleteButton.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));
            exportCompleteButton.setOnAction(event -> {
                Scene scene = jfxPanel.getScene();
                Group root = ((Group) scene.getRoot());
                Platform.runLater(() -> {
                    try {
                        Files.copy(Paths.get(mediaViewClone.getMediaPlayer().getMedia().getSource().replace("file:/", "")), Paths.get(exportFieldLabel.getText().replace("  ", "") + "\\" + nameTextField.getText() + ".mp4"), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    root.getChildren().removeAll(getExportParts());
                    titleBarPanel.setDarkened(false);
                    exportButton.setDarkened(false);
                    minimizeButton.setDarkened(false);
                    closeButton.setDarkened(false);
                    shareButton.setDarkened(false);
                    frame.add(jfxPanel);
                    frame.pack();
                });
            });

        }

        // share

        {
            spaceLabel = new Label("Space");
            spaceLabel.setFont(new javafx.scene.text.Font(spaceLabel.getFont().getFamily(), spaceLabel.getFont().getSize()*sizeMultiplier));
            spaceLabel.setPrefSize(85*sizeMultiplier, 20*sizeMultiplier);
            spaceLabel.setLayoutX(x + 360*sizeMultiplier);
            spaceLabel.setLayoutY(y - 25 + 65*sizeMultiplier);
            spaceLabel.setTextFill(javafx.scene.paint.Color.WHITE);

            String[] spaces = {"TikTok", "Youtube", "Snapchat"/*, "X", "Instagram", "Facebook"*/};
            spaceComboBox = new SimpleComboBox<>(117*sizeMultiplier, 25*sizeMultiplier, 10*sizeMultiplier);
            spaceComboBox.getItems().addAll(spaces);
            spaceComboBox.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
            spaceComboBox.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));
            spaceComboBox.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
            spaceComboBox.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
            spaceComboBox.setSelectedBackgroundColor(javafx.scene.paint.Color.rgb(46, 67, 110));
            spaceComboBox.setLayoutX(x + 425*sizeMultiplier);
            spaceComboBox.setLayoutY(y - 28 + 65*sizeMultiplier);

            // youtube

            {
                titleLabel = new Label("Title");
                titleLabel.setFont(new javafx.scene.text.Font(titleLabel.getFont().getFamily(), titleLabel.getFont().getSize()*sizeMultiplier));

                Text font = new Text(titleLabel.getText());
                font.setFont(titleLabel.getFont());
                double textSize = font.getBoundsInLocal().getHeight()/2;

                titleTextField = new TextArea();
                titleTextField.setPromptText("Add a title that describes your video");
                titleTextField.setPrefSize(200*sizeMultiplier, 40*sizeMultiplier);
                titleTextField.setStyle("-fx-control-inner-background: rgb(45, 45, 45); -fx-background-radius: 5; -fx-border-radius: 0; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
                titleTextField.setLayoutX(x + 425*sizeMultiplier);
                titleTextField.setLayoutY(y + 167*sizeMultiplier);

                titleLabel.setPrefSize(85*sizeMultiplier, 20*sizeMultiplier);
                titleLabel.setLayoutX(x + 360*sizeMultiplier);
                titleLabel.setLayoutY(y + 167 + titleTextField.getPrefHeight()/2 - textSize);
                titleLabel.setTextFill(javafx.scene.paint.Color.WHITE);

                descriptionTextField = new TextArea();
                descriptionTextField.setPromptText("You can add an detailed description of your video here");
                descriptionTextField.setPrefSize(200*sizeMultiplier, 60*sizeMultiplier);
                descriptionTextField.setStyle("-fx-control-inner-background: rgb(45, 45, 45); -fx-background-radius: 5; -fx-border-radius: 0; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
                descriptionTextField.setLayoutX(x + 425*sizeMultiplier);
                descriptionTextField.setLayoutY(y + 232*sizeMultiplier);

                descriptionLabel = new Label("Description");
                descriptionLabel.setFont(new javafx.scene.text.Font(descriptionLabel.getFont().getFamily(), descriptionLabel.getFont().getSize()*sizeMultiplier));
                descriptionLabel.setPrefSize(85*sizeMultiplier, 20*sizeMultiplier);
                descriptionLabel.setLayoutX(x + 360*sizeMultiplier);
                descriptionLabel.setLayoutY(y + 232 + descriptionTextField.getPrefHeight()/2 - textSize);
                descriptionLabel.setTextFill(javafx.scene.paint.Color.WHITE);
                descriptionLabel.setStyle("-fx-font-size: 11px;");

                String[] privacy = {"Public", "Unlisted", "Private"};
                youtubePrivacyComboBox = new SimpleComboBox<>(100*sizeMultiplier, 25*sizeMultiplier, 10*sizeMultiplier);
                youtubePrivacyComboBox.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
                youtubePrivacyComboBox.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));
                youtubePrivacyComboBox.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
                youtubePrivacyComboBox.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
                youtubePrivacyComboBox.setSelectedBackgroundColor(javafx.scene.paint.Color.rgb(46, 67, 110));
                youtubePrivacyComboBox.getItems().addAll(privacy);
                youtubePrivacyComboBox.setPrefSize(265*sizeMultiplier, 20*sizeMultiplier);
                youtubePrivacyComboBox.setLayoutX(x + 425*sizeMultiplier);
                youtubePrivacyComboBox.setLayoutY(y + 317*sizeMultiplier);

                youtubePrivacyLabel = new Label("Visibility");
                youtubePrivacyLabel.setFont(new javafx.scene.text.Font(youtubePrivacyLabel.getFont().getFamily(),         youtubePrivacyLabel.getFont().getSize()*sizeMultiplier));
                youtubePrivacyLabel.setPrefSize(200*sizeMultiplier, 20*sizeMultiplier);
                youtubePrivacyLabel.setLayoutX(x + 360*sizeMultiplier);
                youtubePrivacyLabel.setLayoutY(y + 317 + youtubePrivacyComboBox.getNewHeight()/2 - textSize);
                youtubePrivacyLabel.setTextFill(javafx.scene.paint.Color.WHITE);
            }

            // tiktok

            {
                captionNameTextField = new TextArea();
                captionNameTextField.setPromptText("Add a title that describes your video");
                captionNameTextField.setPrefSize(200*sizeMultiplier, 60*sizeMultiplier);
                captionNameTextField.setStyle("-fx-control-inner-background: rgb(45, 45, 45); " +
                        "-fx-background-radius: 5; -fx-border-radius: 0; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
                captionNameTextField.setLayoutX(x + 425*sizeMultiplier);
                captionNameTextField.setLayoutY(y + 167*sizeMultiplier);

                captionNameLabel = new Label("Caption");
                captionNameLabel.setFont(new javafx.scene.text.Font(captionNameLabel.getFont().getFamily(), captionNameLabel.getFont().getSize()*sizeMultiplier));

                Text font = new Text(captionNameLabel.getText());
                font.setFont(captionNameLabel.getFont());
                double textSize = font.getBoundsInLocal().getHeight()/2;

                captionNameLabel.setPrefSize(85*sizeMultiplier, 20*sizeMultiplier);
                captionNameLabel.setLayoutX(x + 360*sizeMultiplier);
                captionNameLabel.setLayoutY(y + 167 + captionNameTextField.getPrefHeight()/2 - textSize);
                captionNameLabel.setTextFill(javafx.scene.paint.Color.WHITE);

                privacyLabel = new Label("Who can watch this video");
                privacyLabel.setFont(new javafx.scene.text.Font(privacyLabel.getFont().getFamily(), privacyLabel.getFont().getSize()*sizeMultiplier));
                privacyLabel.setPrefSize(200*sizeMultiplier, 20*sizeMultiplier);
                privacyLabel.setLayoutX(x + 360*sizeMultiplier);
                privacyLabel.setLayoutY(y + 252*sizeMultiplier);
                privacyLabel.setTextFill(javafx.scene.paint.Color.WHITE);

                String[] privacy = {"Public", "Friends", "Private"};
                privacyComboBox = new SimpleComboBox<>(100*sizeMultiplier, 25*sizeMultiplier, 10*sizeMultiplier);
                privacyComboBox.setStyle("-fx-base: rgb(57, 59, 64); -fx-text-fill: white; -fx-background-radius: 5;"); //TODO make it look better
                privacyComboBox.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
                privacyComboBox.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
                privacyComboBox.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
                privacyComboBox.setSelectedBackgroundColor(javafx.scene.paint.Color.rgb(46, 67, 110));
                privacyComboBox.getItems().addAll(privacy);
                privacyComboBox.setPrefSize(265*sizeMultiplier, 20*sizeMultiplier);
                privacyComboBox.setLayoutX(x + 360*sizeMultiplier);
                privacyComboBox.setLayoutY(y + 262 + privacyLabel.getPrefHeight());

                settingsLabel = new Label("Allow users to");
                settingsLabel.setFont(new javafx.scene.text.Font(settingsLabel.getFont().getFamily(), settingsLabel.getFont().getSize()*sizeMultiplier));
                settingsLabel.setPrefSize(85*sizeMultiplier, 20*sizeMultiplier);
                settingsLabel.setLayoutX(x + 360*sizeMultiplier);
                settingsLabel.setLayoutY(y  + 307 + privacyLabel.getPrefHeight());
                settingsLabel.setTextFill(javafx.scene.paint.Color.WHITE);

                commentCheckBox = new CheckBox();
                commentCheckBox.setStyle("-fx-background-color: blue");
                commentCheckBox.setLayoutX(x + 360*sizeMultiplier);
                commentCheckBox.setLayoutY(y  + 330 + privacyLabel.getPrefHeight());

                commentLabel = new Label("Comment");
                commentLabel.setFont(new javafx.scene.text.Font(commentLabel.getFont().getFamily(),         commentLabel.getFont().getSize()*sizeMultiplier));
                commentLabel.setPrefSize(60*sizeMultiplier, 20*sizeMultiplier);
                commentLabel.setLayoutX(x + 390*sizeMultiplier);
                commentLabel.setLayoutY(y  + 330 + privacyLabel.getPrefHeight());
                commentLabel.setTextFill(javafx.scene.paint.Color.WHITE);

                duetCheckBox = new CheckBox();
                duetCheckBox.setStyle("-fx-background-color: blue");
                duetCheckBox.setLayoutX(x + 470*sizeMultiplier);
                duetCheckBox.setLayoutY(y  + 330 + privacyLabel.getPrefHeight());

                duetLabel = new Label("Duet");
                duetLabel.setFont(new javafx.scene.text.Font(duetLabel.getFont().getFamily(),         duetLabel.getFont().getSize()*sizeMultiplier));
                duetLabel.setPrefSize(50*sizeMultiplier, 20*sizeMultiplier);
                duetLabel.setLayoutX(x + 500*sizeMultiplier);
                duetLabel.setLayoutY(y  + 330 + privacyLabel.getPrefHeight());
                duetLabel.setTextFill(javafx.scene.paint.Color.WHITE);

                stitchCheckBox = new CheckBox();
                stitchCheckBox.setStyle("-fx-background-color: blue");
                stitchCheckBox.setLayoutX(x + 560*sizeMultiplier);
                stitchCheckBox.setLayoutY(y  + 330 + privacyLabel.getPrefHeight());

                stitchLabel = new Label("Stitch");
                stitchLabel.setFont(new javafx.scene.text.Font(stitchLabel.getFont().getFamily(),         stitchLabel.getFont().getSize()*sizeMultiplier));
                stitchLabel.setPrefSize(50*sizeMultiplier, 20*sizeMultiplier);
                stitchLabel.setLayoutX(x + 590*sizeMultiplier);
                stitchLabel.setLayoutY(y  + 330 + privacyLabel.getPrefHeight());
                stitchLabel.setTextFill(javafx.scene.paint.Color.WHITE);

                discloseSwitch = new Switch(36*sizeMultiplier, 20*sizeMultiplier);
                discloseSwitch.setBackground(javafx.scene.paint.Color.rgb(45, 45, 45));
                discloseSwitch.setForeground(javafx.scene.paint.Color.rgb(220, 220, 220));
                discloseSwitch.setLayoutX(x + 585*sizeMultiplier);
                discloseSwitch.setActivated(javafx.scene.paint.Color.rgb(3, 181, 193));
                discloseSwitch.setLayoutY(this.y + 375 + privacyLabel.getPrefHeight());

                discloseLabel = new Label("Disclose video content");
                discloseLabel.setFont(new javafx.scene.text.Font(discloseLabel.getFont().getFamily(),         discloseLabel.getFont().getSize()*sizeMultiplier));
                discloseLabel.setPrefSize(150*sizeMultiplier, 20*sizeMultiplier);
                discloseLabel.setLayoutX(x + 360*sizeMultiplier);
                discloseLabel.setLayoutY(this.y + 375 + privacyLabel.getPrefHeight() + discloseSwitch.getPrefHeight()/2 - textSize);
                discloseLabel.setTextFill(javafx.scene.paint.Color.WHITE);

                discloseDescribtionLabel = new Label("Turn on to disclose that this video promotes goods or\nservices in exchange for something of value. Your video\ncould promote yourself, a third party, or both.");
                discloseDescribtionLabel.setFont(new javafx.scene.text.Font(discloseDescribtionLabel.getFont().getFamily(),         discloseDescribtionLabel.getFont().getSize()*sizeMultiplier));
                discloseDescribtionLabel.setPrefSize(250*sizeMultiplier, 60*sizeMultiplier);
                discloseDescribtionLabel.setLayoutX(x + 360*sizeMultiplier);
                discloseDescribtionLabel.setLayoutY(this.y + 398 + privacyLabel.getPrefHeight() + discloseSwitch.getPrefHeight()/2 - textSize);
                discloseDescribtionLabel.setTextFill(javafx.scene.paint.Color.rgb(200, 200, 200));
                discloseDescribtionLabel.setStyle("-fx-font-size: 10px;");
            }

            shareCompleteButton = new SimpleButton("Share", 10*sizeMultiplier);
            shareCompleteButton.setPrefSize(100*sizeMultiplier, 26*sizeMultiplier);
            shareCompleteButton.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
            shareCompleteButton.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
            shareCompleteButton.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
            shareCompleteButton.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));
            shareCompleteButton.setLayoutX(x + 500*sizeMultiplier);
            shareCompleteButton.setLayoutY(this.y + 455 + 65*sizeMultiplier);
        }

        jfxCloseButton = new Button();
        // First SVG path
        SVGPath path1 = new SVGPath();
        path1.setContent("M7 17L16.8995 7.10051");
        path1.setStrokeWidth(0.7);
        path1.setStroke(javafx.scene.paint.Color.WHITE);

        // Second SVG path
        SVGPath path2 = new SVGPath();
        path2.setContent("M7 7.00001L16.8995 16.8995");
        path2.setStrokeWidth(0.7);
        path2.setStroke(javafx.scene.paint.Color.WHITE);

        // Combine the SVGPaths into a Group to use as button graphic
        Group svgGroup = new Group(path1, path2);
        svgGroup.setScaleX(1.2);
        svgGroup.setScaleY(1.2);
        jfxCloseButton.setGraphic(svgGroup); //TODO make thinner (or the other one thicker)
        jfxCloseButton.setStyle("-fx-background-color: #1E1F20; -fx-padding: 10; -fx-background-radius: 0;");
        jfxCloseButton.setOnMouseEntered(e -> jfxCloseButton.setStyle("-fx-background-color: #C94F4F; -fx-padding: 10; -fx-background-radius: 0;"));
        jfxCloseButton.setOnMouseExited(e -> jfxCloseButton.setStyle("-fx-background-color: #1E1F20; -fx-padding: 10; -fx-background-radius: 0;"));
        jfxCloseButton.setPrefSize(50*sizeMultiplier, 39*sizeMultiplier);
        jfxCloseButton.setLayoutX(x + 590*sizeMultiplier);
        jfxCloseButton.setLayoutY(this.y - 80 + 65*sizeMultiplier);
        jfxCloseButton.setOnMousePressed(e -> jfxCloseButton.setPrefHeight(40));
        jfxCloseButton.setOnMouseClicked(e -> jfxCloseButton.setPrefHeight(39));
        jfxCloseButton.setOnAction(actionEvent -> {
            Scene scene = jfxPanel.getScene();
            Group root = ((Group)scene.getRoot());
            Platform.runLater(() -> {
                if (root.getChildren().contains(exportBackground))
                    root.getChildren().removeAll(getExportParts());
                else {
                    root.getChildren().removeAll(getShareParts());
                    root.getChildren().remove(accountComboBox);
                    root.getChildren().removeAll(getSpaceParts(spaceComboBox.getValue()));
                }
                titleBarPanel.setDarkened(false);
                exportButton.setDarkened(false);
                minimizeButton.setDarkened(false);
                closeButton.setDarkened(false);
                shareButton.setDarkened(false);
                frame.add(jfxPanel);
                frame.pack();
            });
        });
    }
    public void open() {
        frame = new JFrame("BlizzityAI");
        frame.setSize(450, 520);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setUndecorated(true);
        init();
        if (variables.getVariable("key") != null) {
            showContentPanel((String) variables.getVariable("key"));
            addTitleBarPanel(false);
        } else {
            addTitleBarPanel(false);
            showLoginPanel();
        }
    }
    private void addTitleBarPanel(boolean advanced) {
        if (titleBarPanel != null) {
            frame.remove(titleBarPanel);
        }
        titleBarPanel = new JDarkenedPanel();
        titleBarPanel.setBackground(color2); // Set background color
        titleBarPanel.setLayout(new GridBagLayout());
        ScreenListener.addMouseListener(titleBarPanel, 0, 0);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 10);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.anchor = GridBagConstraints.EAST;
        gbc2.weightx = 2000.0;
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.anchor = GridBagConstraints.WEST;
        gbc3.weightx = 2000.0;
        gbc3.insets = new Insets(0, 10, 0, 0);

        JLabel label = new JLabel("Blizzity");
        titleBarPanel.add(label, gbc3);

        if (advanced) {
            try {
                ExportIcon exportIcon = new ExportIcon(25, 25);
                exportButton = new JIconButton(10*sizeMultiplier, 10*sizeMultiplier, exportIcon);
                MovableIcon shareIcon = new MovableIcon(new ShareIcon(30, 30), (int) (2.5*sizeMultiplier), (int) (3.5*sizeMultiplier));
                shareButton = new JIconButton(10*sizeMultiplier, 10*sizeMultiplier, shareIcon);
                exportButton.setBackground(color2);
                exportButton.setHoverForeground(color6);
                exportButton.setForeground(color3);
                exportButton.setHoverBackground(color4);
                exportButton.addActionListener((ConditionalEventListener) e -> {
                    Scene scene = jfxPanel.getScene();
                    Group root = ((Group)scene.getRoot());
                    Platform.runLater(() -> {
                        Rectangle clipRect = new Rectangle(1080*sizeMultiplier, 1920*sizeMultiplier);
                        clipRect.setArcWidth(80*sizeMultiplier); // Adjust corner radius
                        clipRect.setArcHeight(80*sizeMultiplier); // Adjust corner radius
                        mediaViewClone.setClip(clipRect);
                        root.getChildren().addAll(getExportParts());
                        frame.add(jfxPanel);
                        frame.pack();
                        titleBarPanel.setDarkened(true);
                        exportButton.setDarkened(true);
                        minimizeButton.setDarkened(true);
                        closeButton.setDarkened(true);
                        shareButton.setDarkened(true);
                    });
                });
                exportButton.setPreferredSize(new Dimension((int) (40*sizeMultiplier), (int) (40*sizeMultiplier)));
                exportButton.setMinimumSize(new Dimension((int) (40*sizeMultiplier), (int) (40*sizeMultiplier)));
                titleBarPanel.add(exportButton, gbc2);
                shareButton.setBackground(color2);
                shareButton.setHoverForeground(color6);
                shareButton.setForeground(color3);
                shareButton.setHoverBackground(color4);
                shareButton.addActionListener((ConditionalEventListener) e -> {
                    Scene scene = jfxPanel.getScene();
                    Group root = ((Group)scene.getRoot());
                    Platform.runLater(() -> {
                        Rectangle clipRect = new Rectangle(1080*sizeMultiplier, 1920*sizeMultiplier);
                        clipRect.setArcWidth(80*sizeMultiplier); // Adjust corner radius
                        clipRect.setArcHeight(80*sizeMultiplier); // Adjust corner radius
                        mediaViewClone.setClip(clipRect);
                        root.getChildren().addAll(getShareParts());
                        frame.add(jfxPanel);
                        if (spaceComboBox.getValue() != null) {
                            root.getChildren().addAll(getSpaceParts(spaceComboBox.getValue()));
                            spaceComboBox.toFront();
                        }
                        if (accountComboBox != null)
                            root.getChildren().add(accountComboBox);
                        spaceComboBox.toFront();
                        frame.add(jfxPanel);
                        frame.pack();
                        titleBarPanel.setDarkened(true); //TODO fix interactible
                        exportButton.setDarkened(true);
                        minimizeButton.setDarkened(true);
                        closeButton.setDarkened(true);
                        shareButton.setDarkened(true);
                    });
                });
                shareButton.setPreferredSize(new Dimension((int) (40*sizeMultiplier), (int) (40*sizeMultiplier)));
                shareButton.setMinimumSize(new Dimension((int) (40*sizeMultiplier), (int) (40*sizeMultiplier)));
                titleBarPanel.add(shareButton, gbc);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        minimizeButton = new JSimpleButton("-");
        minimizeButton.setBackground(color2);
        minimizeButton.setHoverForeground(color6);
        minimizeButton.setForeground(color7);
        minimizeButton.setHoverBackground(color4);
        minimizeButton.addActionListener((ConditionalEventListener) e -> {
            frame.setState(JFrame.ICONIFIED); // Close the window
        });
        minimizeButton.setPreferredSize(new Dimension((int) (50*sizeMultiplier), (int) (40*sizeMultiplier)));
        minimizeButton.setMinimumSize(new Dimension((int) (50*sizeMultiplier), (int) (40*sizeMultiplier)));
        if (advanced)
            titleBarPanel.add(minimizeButton);
        else
            titleBarPanel.add(minimizeButton, gbc2);
        closeButton = new JSimpleButton("X");
        closeButton.setBackground(color2);
        closeButton.setForeground(color7);
        closeButton.setHoverForeground(color6);
        closeButton.setHoverBackground(new Color(201, 79, 79));
        closeButton.addActionListener((ConditionalEventListener) e -> {
            frame.dispose(); // Close the window
        });
        closeButton.setPreferredSize(new Dimension((int) (50*sizeMultiplier), (int) (40*sizeMultiplier)));
        closeButton.setMinimumSize(new Dimension((int) (50*sizeMultiplier), (int) (40*sizeMultiplier)));
        titleBarPanel.add(closeButton);

        // Add custom title bar panel to the frame
        titleBarPanel.setPreferredSize(new Dimension((int) (450*sizeMultiplier), (int) (40*sizeMultiplier)));
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
    private Node[] getSpaceParts(String space) {
        List<Node> parts = new ArrayList<>();
        if (space != null) {
            switch (space) {
                case "TikTok":
                        parts.addAll(List.of(captionNameLabel, captionNameTextField, privacyLabel, commentCheckBox, settingsLabel, stitchCheckBox, duetCheckBox, stitchLabel, duetLabel, commentLabel, discloseSwitch, discloseLabel, discloseDescribtionLabel, privacyComboBox));
                case "Youtube":
                        parts.addAll(List.of(titleLabel, descriptionLabel, titleTextField, descriptionTextField, youtubePrivacyLabel, youtubePrivacyComboBox));
                case "Snapchat":
                    parts.addAll(List.of(titleTextField));
            }
        }
        return convert(parts);
    }
    private Node[] getSectionParts(int section) {
        switch (section) {
            case 1: return getGenerateParts();
            case 2: return getAccountParts();
            case 3: return getAdminParts();
            default: return null;
        }
    }
    private Node[] convert(List<Node> parts) {
        Node[] result = new Node[parts.size()];
        for (Node n : parts) {
            result[parts.indexOf(n)] = n;
        }
        return result;
    }
    private Node[] getExportParts() {
        return new Node[] {darkenBackground, exportBackground, nameTextField, exportTitle, jfxCloseButton, nameLabel, exportLabel, exportFieldLabel, folderButton, mediaViewClone, exportCompleteButton, exportTitleLabel};
    }
    private Node[] getShareParts() {
        return new Node[] {darkenBackground, shareBackground, shareTitle, jfxCloseButton, mediaViewClone, shareCompleteButton, shareTitleLabel, spaceComboBox, spaceLabel};
    }
    private void showLoginPanel() {
        panel = new JPanel();
        panel.setLayout(null);
        ScreenListener.addMouseListener(panel, 0, 40);
        panel.addMouseListener(new LoginListener());
        panel.setBackground(color1);

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
        createAccountButton.addActionListener(e -> {
            frame.remove(panel);
            showRegisterPanel();
        });

        nextButton = new JRoundedButton(10,10, "Next");
        nextButton.setForeground(color1); // Slightly lighter gray
        nextButton.setBackground(color5); // Slightly lighter gray
        nextButton.setPressedColor(color9, color8);
        nextButton.setBounds(330, 400, 80, 35);
        nextButton.addActionListener(e -> {
            if (!userText.getText().isEmpty()) {
                if (!api.connect(userText.getText())) {
                    frame.remove(panel);
                    showLoginVerifyPanel(userText.getText());
                } else
                    JOptionPane.showMessageDialog(frame, "This User doesn't exist", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add spacing around components for better organization
        panel.add(createAccountButton);
        panel.add(descriptionJLabel);
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
        ScreenListener.addMouseListener(panel, 0, 40);
        panel.setBackground(color1);

        String signInText = "Welcome";
        JLabel signInLabel = new JLabel(signInText);
        signInLabel.setForeground(color6);
        signInLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        width = signInLabel.getFontMetrics(signInLabel.getFont()).stringWidth(signInText)+5;
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
        width = descriptionLabel.getFontMetrics(descriptionLabel.getFont()).stringWidth(descriptionText)+10;
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
        nextButton.addActionListener(e -> {
            if (!userText.getText().isEmpty()) {
                if (api.login(username, passText.getText())) {
                    frame.remove(panel);
                    variables.setVariable("key", StringUtils.generateUniqueString(StringUtils.encrypt(userText.getText(), passText.getText())));
                    frame.pack();
                    showContentPanel(StringUtils.generateUniqueString(StringUtils.encrypt(userText.getText(), passText.getText())));
                } else
                    JOptionPane.showMessageDialog(frame, "Wrong password", "Error", JOptionPane.ERROR_MESSAGE);
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
        ScreenListener.addMouseListener(panel, 0, 40);
        panel.setBackground(color1);

        String createText = "Create a Blizzity Account";
        JLabel createLabel = new JLabel(createText);
        createLabel.setForeground(color6);
        createLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        width = createLabel.getFontMetrics(createLabel.getFont()).stringWidth(createText)+10;
        createLabel.setBounds(frame.getWidth()/2-width/2, 90, width, 25);

        String descriptionText = "Enter your credentials";
        JLabel descriptionLabel = new JLabel(descriptionText);
        descriptionLabel.setForeground(color7);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        width = descriptionLabel.getFontMetrics(descriptionLabel.getFont()).stringWidth(descriptionText)+5;
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
        logInButton.addActionListener(e -> {
            frame.remove(panel);
            showLoginPanel();
        });

        JRoundedButton createButton = new JRoundedButton(10, 10, "Create");
        createButton.setForeground(color1); // Slightly lighter gray
        createButton.setBackground(color5); // Slightly lighter gray
        createButton.setPressedColor(color9, color8);
        createButton.setBounds(330, 400, 80, 35);
        createButton.addActionListener(e -> {
            if (api.register(userText.getText(), passText.getText())) {
                frame.remove(panel);
                variables.setVariable("key", StringUtils.generateUniqueString(StringUtils.encrypt(userText.getText(), passText.getText())));
                showContentPanel(StringUtils.generateUniqueString(StringUtils.encrypt(userText.getText(), passText.getText())));
            } else
                JOptionPane.showMessageDialog(frame, "This username is already taken", "Error", JOptionPane.ERROR_MESSAGE);
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
    boolean init = true;
    Scene scene;
    private void showContentPanel(String key) {
        if (init) {
            init = false;
            int frameWidth = (int) (screenWidth * sizeMultiplier);
            int frameHeight = (int) ((screenHeight-40) * sizeMultiplier);
            int x = (Toolkit.getDefaultToolkit().getScreenSize().width - frameWidth) / 2;
            int y = (Toolkit.getDefaultToolkit().getScreenSize().height - frameHeight) / 2;

            int videoWidth = 1080;
            int videoHeight = 1920;
            int videoPreviewWidth = 230;
            int videoPreviewHeight = 410;

            usagesLabel = new Label("Usages: " + api.usages(key));
            usagesLabel.setLayoutX(22 * sizeMultiplier);
            usagesLabel.setLayoutY(65 * sizeMultiplier);
            usagesLabel.setFont(new javafx.scene.text.Font(usagesLabel.getFont().getFamily(), usagesLabel.getFont().getSize() * sizeMultiplier));
            usagesLabel.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));

            creditsLabel = new Label("Credits: " + api.credits(key));
            creditsLabel.setLayoutX(22 * sizeMultiplier);
            creditsLabel.setLayoutY(90 * sizeMultiplier);
            creditsLabel.setFont(new javafx.scene.text.Font(creditsLabel.getFont().getFamily(), creditsLabel.getFont().getSize() * sizeMultiplier));
            creditsLabel.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));

            youtubeLabel = new Label("Youtube:");
            youtubeLabel.setLayoutX(22 * sizeMultiplier);
            youtubeLabel.setLayoutY(65 * sizeMultiplier);
            youtubeLabel.setFont(new javafx.scene.text.Font(youtubeLabel.getFont().getFamily(), youtubeLabel.getFont().getSize() * sizeMultiplier));
            youtubeLabel.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));

            youtubeComboBox = new SimpleComboBox<>(100*sizeMultiplier, 25*sizeMultiplier, 10*sizeMultiplier);
            youtubeComboBox.setLayoutX(22 * sizeMultiplier);
            youtubeComboBox.setLayoutY(85 * sizeMultiplier);
            youtubeComboBox.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
            youtubeComboBox.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));
            youtubeComboBox.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
            youtubeComboBox.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
            youtubeComboBox.setSelectedBackgroundColor(javafx.scene.paint.Color.rgb(46, 67, 110));
            AtomicReference<AccountComboBox> adminAccountComboBox = new AtomicReference<>();

            adminLabel = new Label("");
            adminLabel.setLayoutX(400 * sizeMultiplier);
            adminLabel.setLayoutY(140 * sizeMultiplier);
            adminLabel.setFont(new javafx.scene.text.Font(adminLabel.getFont().getFamily(), adminLabel.getFont().getSize() * sizeMultiplier));
            adminLabel.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));

            youtubeComboBox.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                    adminLabel.setText("Youtube - " + newValue);
                        admin(adminAccountComboBox, "youtube", key, newValue);
                    });
            youtubeComboBox.getItems().addAll(languages);

            tiktokLabel = new Label("Tiktok:");
            tiktokLabel.setLayoutX(22 * sizeMultiplier);
            tiktokLabel.setLayoutY(115 * sizeMultiplier);
            tiktokLabel.setFont(new javafx.scene.text.Font(tiktokLabel.getFont().getFamily(), tiktokLabel.getFont().getSize() * sizeMultiplier));
            tiktokLabel.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));

            tiktokComboBox = new SimpleComboBox<>(100*sizeMultiplier, 25*sizeMultiplier, 10*sizeMultiplier);
            tiktokComboBox.setLayoutX(22 * sizeMultiplier);
            tiktokComboBox.setLayoutY(135 * sizeMultiplier);
            tiktokComboBox.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
            tiktokComboBox.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));
            tiktokComboBox.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
            tiktokComboBox.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
            tiktokComboBox.setSelectedBackgroundColor(javafx.scene.paint.Color.rgb(46, 67, 110));
            tiktokComboBox.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        adminLabel.setText("TikTok - " + newValue);
                        admin(adminAccountComboBox, "tiktok", key, newValue);
                    });
            tiktokComboBox.getItems().addAll(languages);

            snapchatLabel = new Label("Snapchat:");
            snapchatLabel.setLayoutX(22 * sizeMultiplier);
            snapchatLabel.setLayoutY(165 * sizeMultiplier);
            snapchatLabel.setFont(new javafx.scene.text.Font(snapchatLabel.getFont().getFamily(), snapchatLabel.getFont().getSize() * sizeMultiplier));
            snapchatLabel.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));

            snapchatComboBox = new SimpleComboBox<>(100*sizeMultiplier, 25*sizeMultiplier, 10*sizeMultiplier);
            snapchatComboBox.setLayoutX(22 * sizeMultiplier);
            snapchatComboBox.setLayoutY(185 * sizeMultiplier);
            snapchatComboBox.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
            snapchatComboBox.setTextFill(javafx.scene.paint.Color.rgb(210, 210, 210));
            snapchatComboBox.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
            snapchatComboBox.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
            snapchatComboBox.setSelectedBackgroundColor(javafx.scene.paint.Color.rgb(46, 67, 110));
            snapchatComboBox.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        adminLabel.setText("Snapchat - " + newValue);
                        admin(adminAccountComboBox, "snapchat", key, newValue);
                    });
            snapchatComboBox.getItems().addAll(languages);

            AtomicReference<MediaPlayer> mediaPlayer = new AtomicReference<>();
            generateButton.setOnAction(actionEvent -> {
                Scene scene = jfxPanel.getScene();
                startLoadingScreen(scene);
                AtomicReference<Double<File, String>> tempFile = new AtomicReference<>();
                Thread thread = new Thread(() -> {
                    tempFile.set(api.video(key, languageComboBox.getValue().toLowerCase(), lengthComboBox.getSelectionModel().getSelectedIndex() == 0 ? 6 : 10));
                    if (tempFile.get() != null)
                        file = tempFile.get();
                    Platform.runLater(() -> {
                        if (file != null) {
                            Media media = new Media(file.getKey().toURI().toString());
                            Group root = ((Group) scene.getRoot());
                            if (nameLabel2 != null)
                                root.getChildren().removeAll(
                                        nameLabel2,
                                        pathLabel,
                                        nameDetailLabel,
                                        mediaView,
                                        ratioLabel,
                                        ratioLabel2,
                                        resolutionLabel,
                                        resolutionLabel2,
                                        fpsLabel,
                                        fpsLabel2,
                                        pathLabel2
                                );

                            nameLabel2 = new Label(file.getKey().getName());
                            nameLabel2.setFont(new javafx.scene.text.Font(nameLabel2.getFont().getFamily(), nameLabel2.getFont().getSize() * sizeMultiplier));
                            nameLabel2.setLayoutX(1250 * sizeMultiplier);
                            nameLabel2.setLayoutY(60 * sizeMultiplier);
                            nameLabel2.setTextFill(javafx.scene.paint.Color.rgb(178, 178, 178));

                            pathLabel = new Label("Path:");
                            pathLabel.setFont(new javafx.scene.text.Font(pathLabel.getFont().getFamily(), pathLabel.getFont().getSize() * sizeMultiplier));
                            pathLabel.setLayoutX(1142 * sizeMultiplier);
                            pathLabel.setLayoutY(83 * sizeMultiplier);
                            pathLabel.setTextFill(javafx.scene.paint.Color.rgb(128, 128, 128));

                            pathLabel2 = new Label(file.getKey().getAbsolutePath().replace("\\" + file.getKey().getName(), ""));
                            pathLabel2.setFont(new javafx.scene.text.Font(pathLabel2.getFont().getFamily(), pathLabel2.getFont().getSize() * sizeMultiplier));
                            pathLabel2.setLayoutX(1250 * sizeMultiplier);
                            pathLabel2.setLayoutY(83 * sizeMultiplier);
                            pathLabel2.setTextFill(javafx.scene.paint.Color.rgb(178, 178, 178));

                            double moveY = 205;
                            double moveX = 130;
                            mediaPlayer.set(new MediaPlayer(media));
                            mediaView = new MediaView(mediaPlayer.get());
                            mediaView.setScaleX((double) videoPreviewWidth / videoWidth * sizeMultiplier);
                            mediaView.setScaleY((double) videoPreviewHeight / videoHeight * sizeMultiplier);
                            mediaView.setLayoutY((-755-moveY) + (50+moveY) * sizeMultiplier);
                            mediaView.setLayoutX((-414.5-moveX) + (780+moveX) * sizeMultiplier);

                            mediaViewClone = new MediaView(mediaPlayer.get());
                            mediaViewClone.setScaleX((double) videoPreviewWidth / videoWidth * sizeMultiplier);
                            mediaViewClone.setScaleY((double) videoPreviewHeight / videoHeight * sizeMultiplier);
                            mediaViewClone.setLayoutX(80 * sizeMultiplier);
                            mediaViewClone.setLayoutY(-610 + 65 * sizeMultiplier);

                            nameDetailLabel = new Label("Name:");
                            nameDetailLabel.setFont(new javafx.scene.text.Font(nameDetailLabel.getFont().getFamily(), nameDetailLabel.getFont().getSize() * sizeMultiplier));
                            nameDetailLabel.setLayoutX(1142 * sizeMultiplier);
                            nameDetailLabel.setLayoutY(60 * sizeMultiplier);
                            nameDetailLabel.setTextFill(javafx.scene.paint.Color.rgb(128, 128, 128));

                            ratioLabel = new Label("Aspect ratio:");
                            ratioLabel.setFont(new javafx.scene.text.Font(ratioLabel.getFont().getFamily(), ratioLabel.getFont().getSize() * sizeMultiplier));
                            ratioLabel.setLayoutX(1142 * sizeMultiplier);
                            ratioLabel.setLayoutY(106 * sizeMultiplier);
                            ratioLabel.setTextFill(javafx.scene.paint.Color.rgb(128, 128, 128));

                            ratioLabel2 = new Label("9:16");
                            ratioLabel2.setFont(new javafx.scene.text.Font(ratioLabel2.getFont().getFamily(), ratioLabel2.getFont().getSize() * sizeMultiplier));
                            ratioLabel2.setLayoutX(1250 * sizeMultiplier);
                            ratioLabel2.setLayoutY(106 * sizeMultiplier);
                            ratioLabel2.setTextFill(javafx.scene.paint.Color.rgb(178, 178, 178));

                            resolutionLabel = new Label("Resolution:");
                            resolutionLabel.setFont(new javafx.scene.text.Font(resolutionLabel.getFont().getFamily(), resolutionLabel.getFont().getSize() * sizeMultiplier));
                            resolutionLabel.setLayoutX(1142 * sizeMultiplier);
                            resolutionLabel.setLayoutY(129 * sizeMultiplier);
                            resolutionLabel.setTextFill(javafx.scene.paint.Color.rgb(128, 128, 128));

                            resolutionLabel2 = new Label(videoWidth + "x" + videoHeight);
                            resolutionLabel2.setFont(new javafx.scene.text.Font(resolutionLabel2.getFont().getFamily(), resolutionLabel2.getFont().getSize() * sizeMultiplier));
                            resolutionLabel2.setLayoutX(1250 * sizeMultiplier);
                            resolutionLabel2.setLayoutY(129 * sizeMultiplier);
                            resolutionLabel2.setTextFill(javafx.scene.paint.Color.rgb(178, 178, 178));

                            fpsLabel = new Label("Frame rate:");
                            fpsLabel.setFont(new javafx.scene.text.Font(fpsLabel.getFont().getFamily(), fpsLabel.getFont().getSize() * sizeMultiplier));
                            fpsLabel.setLayoutX(1142 * sizeMultiplier);
                            fpsLabel.setLayoutY(152 * sizeMultiplier);
                            fpsLabel.setTextFill(javafx.scene.paint.Color.rgb(128, 128, 128));

                            fpsLabel2 = new Label("25.00fps");
                            fpsLabel2.setFont(new javafx.scene.text.Font(fpsLabel2.getFont().getFamily(), fpsLabel2.getFont().getSize() * sizeMultiplier));
                            fpsLabel2.setLayoutX(1250 * sizeMultiplier);
                            fpsLabel2.setLayoutY(152 * sizeMultiplier);
                            fpsLabel2.setTextFill(javafx.scene.paint.Color.rgb(178, 178, 178));

                            root.getChildren().addAll(
                                    nameLabel2,
                                    pathLabel,
                                    nameDetailLabel,
                                    mediaView,
                                    ratioLabel,
                                    ratioLabel2,
                                    resolutionLabel,
                                    resolutionLabel2,
                                    fpsLabel,
                                    fpsLabel2,
                                    pathLabel2
                            );
                        } else
                            JOptionPane.showMessageDialog(frame, "No video available", "Error", JOptionPane.ERROR_MESSAGE);
                        stopLoadingScreen(scene);
                    });
                });
                thread.start();
            });

            Button playButton = new Button();
            playButton.setGraphic(createPlayShape(11 * sizeMultiplier, 12 * sizeMultiplier, javafx.scene.paint.Color.WHITE));
            playButton.setBackground(null);
            playButton.setLayoutX(890 * sizeMultiplier - 11 * sizeMultiplier / 2);
            playButton.setLayoutY(468 * sizeMultiplier - 12 * sizeMultiplier / 2);
            final boolean[] playing = {false};
            playButton.setOnAction(event -> {
                if (playing[0]) {
                    mediaPlayer.get().pause();
                    playButton.setGraphic(createPlayShape(11 * sizeMultiplier, 12 * sizeMultiplier, javafx.scene.paint.Color.WHITE));
                } else {
                    mediaPlayer.get().play();
                    playButton.setGraphic(createPauseShape(11 * sizeMultiplier, 12 * sizeMultiplier, javafx.scene.paint.Color.WHITE));
                }
                playing[0] = !playing[0];
            });
            spaceComboBox.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        Scene scene = jfxPanel.getScene();
                        Group root = ((Group) scene.getRoot());
                        startLoadingScreen(scene);
                        Thread thread = new Thread(() -> {
                            if (imageView != null)
                                Platform.runLater(() -> root.getChildren().removeAll(imageView));
                            if (api.check(newValue.toLowerCase(), key) || api.connect(newValue.toLowerCase(), key)) {
                                accountComboBox = new AccountComboBox(api.info(newValue.toLowerCase(), key), 265*sizeMultiplier, 70*sizeMultiplier, 10*sizeMultiplier, true);
                                accountComboBox.setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
                                accountComboBox.setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
                                accountComboBox.setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
                                accountComboBox.setSelectedBackgroundColor(javafx.scene.paint.Color.rgb(46, 67, 110));
                                accountComboBox.setLayoutX(this.x + 360 * sizeMultiplier);
                                accountComboBox.setLayoutY(this.y + 20 + 55 * sizeMultiplier);
                                accountComboBox.setOnSelect(actionEvent -> api.select(newValue.toLowerCase(), key, accountComboBox.getSelectionModel().getSelectedItem().get("id")));
                                accountComboBox.setOnAction(actionEvent -> {
                                    if (api.connect(newValue.toLowerCase(), key)) {
                                        List<Map<String, String>> data = api.info(newValue.toLowerCase(), key, true);
                                        accountComboBox.getItems().setAll(data);
                                        for (Map<String, String> entry : data) {
                                            if (entry.get("selected").equalsIgnoreCase("true")) {
                                                accountComboBox.getSelectionModel().select(entry);
                                                break;
                                            }
                                        }
                                    }
                                });
                                Platform.runLater(() -> {
                                    stopLoadingScreen(scene);
                                    root.getChildren().addAll(getSpaceParts(newValue));
                                    root.getChildren().removeAll(getSpaceParts(oldValue));
                                    root.getChildren().add(accountComboBox);
                                    spaceComboBox.toFront();
                                });
                            }
                        });
                        thread.start();
                    }
            );
            shareCompleteButton.setOnAction(event -> {
                if (
                        !(spaceComboBox.getValue().equals("TikTok") && privacyComboBox.getValue() == null) &&
                                !(spaceComboBox.getValue().equals("Youtube") && (youtubePrivacyComboBox.getValue() == null || titleTextField.getText().isEmpty() || descriptionTextField.getText().isEmpty()))
                ) {
                    Scene scene = jfxPanel.getScene();
                    Group root = ((Group) scene.getRoot());
                    startLoadingScreen(scene);
                    Thread thread = new Thread(() -> {
                        if (api.check(spaceComboBox.getValue().toLowerCase(), key)) {
                            if (spaceComboBox.getValue().equals("TikTok")) {
                                String privacy;
                                switch (privacyComboBox.getValue()) {
                                    case "Private":
                                        privacy = "SELF_ONLY";
                                        break;
                                    case "Friends":
                                        privacy = "MUTUAL_FOLLOW_FRIENDS";
                                        break;
                                    case "Public":
                                        privacy = "PUBLIC_TO_EVERYONE";
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + privacyComboBox.getValue());
                                }
                                api.tiktokPost(key, URLEncoder.encode(file.getValue()), URLEncoder.encode(nameTextField.getText()), privacy, duetCheckBox.isSelected(), commentCheckBox.isSelected(), stitchCheckBox.isSelected(), 1000);
                            } else if (spaceComboBox.getValue().equals("Youtube")) {
                                api.youtubePost(key, URLEncoder.encode(file.getValue()), URLEncoder.encode(titleTextField.getText()), URLEncoder.encode(descriptionTextField.getText()), youtubePrivacyComboBox.getValue());
                            } else if (spaceComboBox.getValue().equals("Snapchat")) {
                                api.snapchatPost(key, URLEncoder.encode(file.getValue()), URLEncoder.encode(titleTextField.getText()));
                            }
                            Platform.runLater(() -> {
                                stopLoadingScreen(scene);
                                root.getChildren().removeAll(getShareParts());
                                root.getChildren().remove(accountComboBox);
                                root.getChildren().removeAll(getSpaceParts(spaceComboBox.getValue()));
                                titleBarPanel.setDarkened(false);
                                exportButton.setDarkened(false);
                                minimizeButton.setDarkened(false);
                                closeButton.setDarkened(false);
                                shareButton.setDarkened(false);
                                frame.add(jfxPanel);
                                frame.pack();
                            });

                        }
                    });
                    thread.start();
                } else {
                    // TODO mark
                }
            });
            Platform.runLater(() -> {
                Group root = new Group();
                scene = new Scene(root, 0, 0);
                scene.setFill(javafx.scene.paint.Color.rgb(19, 19, 20));

                SVGPath accountSvg = getAccountSVG(javafx.scene.paint.Color.WHITE);
                SVGButton accountPane = new SVGButton(accountSvg, 40 * sizeMultiplier, 40 * sizeMultiplier, 10 * sizeMultiplier);
                Group generateSvgGroup = getGenerateSVG(javafx.scene.paint.Color.WHITE);
                SVGButton generatePane = new SVGButton(generateSvgGroup, 40 * sizeMultiplier, 40 * sizeMultiplier, 10 * sizeMultiplier);
                SVGPath adminSvgGroup = getAdminSVG(javafx.scene.paint.Color.WHITE);
                SVGButton adminPane = new SVGButton(adminSvgGroup, 40 * sizeMultiplier, 40 * sizeMultiplier, 10 * sizeMultiplier);
                selectedSectionButton = generatePane;
                AtomicInteger selected = new AtomicInteger(1);
                {
                    generatePane.setBackgroundColor(javafx.scene.paint.Color.rgb(80, 83, 84));
                    generatePane.setSelected(true);
                    generatePane.setOnAction(actionEvent -> {
                        if (!generatePane.selected) {
                            root.getChildren().addAll(getGenerateParts());
                            root.getChildren().removeAll(getSectionParts(selected.get()));
                            selectedSectionButton.setSelected(false);
                            generatePane.setSelected(true);
                            selectedSectionButton = generatePane;
                            selected.set(1);
                        }
                    });
                    generatePane.setLayoutX(20 * sizeMultiplier);
                    generatePane.setLayoutY(10 * sizeMultiplier);
                }

                {
                    accountPane.setBackgroundColor(javafx.scene.paint.Color.rgb(80, 83, 84));
                    accountPane.setSelected(false);
                    accountPane.setOnAction(actionEvent -> {
                        if (!accountPane.selected) {
                            root.getChildren().addAll(getAccountParts());
                            root.getChildren().removeAll(getSectionParts(selected.get()));
                            selectedSectionButton.setSelected(false);
                            accountPane.setSelected(true);
                            selectedSectionButton = accountPane;
                            selected.set(2);
                        }
                    });
                    accountPane.setLayoutX(60 * sizeMultiplier);
                    accountPane.setLayoutY(10 * sizeMultiplier);
                }

                {
                    adminPane.setBackgroundColor(javafx.scene.paint.Color.rgb(80, 83, 84));
                    adminPane.setSelected(false);
                    adminPane.setOnAction(actionEvent -> {
                        if (!adminPane.selected) {
                            root.getChildren().addAll(getAdminParts());
                            root.getChildren().removeAll(getSectionParts(selected.get()));
                            selectedSectionButton.setSelected(false);
                            adminPane.setSelected(true);
                            selectedSectionButton = adminPane;
                            selected.set(3);
                        }
                    });
                    adminPane.setLayoutX(100 * sizeMultiplier);
                    adminPane.setLayoutY(10 * sizeMultiplier);
                }

                root.getChildren().addAll(
                        optionsBackground,
                        optionsBackground2,
                        generatePane,
                        accountPane,
                        resultBackground,
                        resultBackground2,
                        resultLabel,
                        playButton,
                        detailsBackground,
                        detailsBackground2,
                        detailsLabel,
                        timelineBackground,
                        timelineBackground2,
                        timelineLabel
                );
                if (api.admin(key))
                    root.getChildren().add(adminPane);
                root.getChildren().addAll(getGenerateParts());
                scene.setRoot(root);
                jfxPanel.setScene(scene);
                jfxPanel.setPreferredSize(new Dimension((int) (screenWidth * sizeMultiplier), (int) (920 * sizeMultiplier)));
                addTitleBarPanel(true);
                frame.add(jfxPanel);
                frame.pack();
            });
            // Set the location of the JFrame
            frame.setLocation(x, y);
        } else {
            frame.add(jfxPanel); //TODO fix white screen
        }
        frame.pack(); // Adjust frame size to fit components
        frame.setVisible(true);
    }

    Rectangle loading;
    private void startLoadingScreen(Scene scene) {
        Group root = ((Group) scene.getRoot());
        loading = new Rectangle(50*sizeMultiplier, 20*sizeMultiplier, javafx.scene.paint.Color.BLACK);
        loading.setArcWidth(10*sizeMultiplier);
        loading.setArcHeight(10*sizeMultiplier);
        loading.setX(765*sizeMultiplier);
        loading.setY(400*sizeMultiplier);

        // Create a timeline for rotation animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(loading.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(loading.rotateProperty(), 360))
        );
        timeline.setCycleCount(Animation.INDEFINITE); // Repeat indefinitely

        // Start the animation
        timeline.play();

        root.getChildren().addAll(loadingBackground, loading);
        scene.setRoot(root);
    }
    private void admin(AtomicReference<AccountComboBox> adminAccountComboBox, String space, String key, String language) {
        if (adminAccountComboBox.get() == null)
            Platform.runLater(() -> {
                Group root = ((Group) scene.getRoot());
                root.getChildren().remove(adminAccountComboBox.get());
            });
        adminAccountComboBox.set(new AccountComboBox(api.info(space, key), 265 * sizeMultiplier, 70 * sizeMultiplier, 10 * sizeMultiplier, false));
        adminAccountComboBox.get().setBackgroundColor(javafx.scene.paint.Color.rgb(57, 59, 64));
        adminAccountComboBox.get().setStrokeColor(javafx.scene.paint.Color.rgb(78, 81, 87));
        adminAccountComboBox.get().setSelectedStrokeColor(javafx.scene.paint.Color.rgb(53, 116, 240));
        adminAccountComboBox.get().setSelectedBackgroundColor(javafx.scene.paint.Color.rgb(46, 67, 110));
        adminAccountComboBox.get().setLayoutX(400 * sizeMultiplier);
        adminAccountComboBox.get().setLayoutY(65 * sizeMultiplier);
        adminAccountComboBox.get().setOnSelect(actionEvent -> api.admin(space, language, key, adminAccountComboBox.get().getSelectionModel().getSelectedItem().get("id")));
        adminAccountComboBox.get().setOnAction(actionEvent -> {
            if (api.connect(space, key)) {
                List<Map<String, String>> data = api.info(space, key, true);
                adminAccountComboBox.get().getItems().setAll(data);
                for (Map<String, String> entry : data) {
                    if (entry.get("selected").equalsIgnoreCase("true")) {
                        adminAccountComboBox.get().getSelectionModel().select(entry);
                        api.admin(space, language, key, entry.get("id"));
                        break;
                    }
                }
            }
        });
        Map<String, String> admin = api.adminCheck(space, language, key);
        if (admin != null)
            adminAccountComboBox.get().getSelectionModel().select(admin);
        Platform.runLater(() -> {
            Group root = ((Group) scene.getRoot());
            root.getChildren().add(adminAccountComboBox.get());
        });
    }
    private void stopLoadingScreen(Scene scene) {
        Group root = ((Group) scene.getRoot());
        root.getChildren().removeAll(loadingBackground, loading);
        scene.setRoot(root);
    }
    private Group getGenerateSVG(javafx.scene.paint.Color color) {
        Group generateSvgGroup = new Group(svgPath(0, 0, color, "M11.5805 4.77604C12.2752 3.00516 12.6226 2.11971 13.349 2.01056C14.0755 1.90141 14.6999 2.64083 15.9488 4.11967L16.2719 4.50226C16.6268 4.9225 16.8042 5.13263 17.0455 5.25261C17.2868 5.37259 17.5645 5.38884 18.1201 5.42135L18.6258 5.45095C20.5808 5.56537 21.5583 5.62258 21.8975 6.26168C22.2367 6.90079 21.713 7.69853 20.6656 9.29403L20.3946 9.7068C20.097 10.1602 19.9482 10.3869 19.908 10.6457C19.8678 10.9045 19.9407 11.1662 20.0866 11.6895L20.2195 12.166C20.733 14.0076 20.9898 14.9284 20.473 15.4325C19.9562 15.9367 19.0081 15.6903 17.1118 15.1975L16.6213 15.07C16.0824 14.93 15.813 14.86 15.5469 14.8999C15.2808 14.9399 15.0481 15.0854 14.5828 15.3763L14.1591 15.6412C12.5215 16.6649 11.7027 17.1768 11.0441 16.8493C10.3854 16.5217 10.3232 15.5717 10.1987 13.6717L10.1665 13.1801C10.1311 12.6402 10.1134 12.3702 9.98914 12.1361C9.86488 11.902 9.64812 11.7302 9.21459 11.3867L8.8199 11.0739C7.29429 9.86506 6.53149 9.26062 6.64124 8.55405C6.751 7.84748 7.66062 7.50672 9.47988 6.8252L9.95054 6.64888C10.4675 6.45522 10.726 6.35839 10.9153 6.17371C11.1046 5.98903 11.2033 5.73742 11.4008 5.23419L11.5805 4.77604Z"), svgPath(0, 0, color, "M5.31003 9.59277C2.87292 11.9213 1.27501 15.8058 2.33125 22.0002C3.27403 19.3966 5.85726 17.2407 8.91219 15.9528C8.80559 15.3601 8.7583 14.6364 8.70844 13.8733L8.66945 13.2782C8.66038 13.1397 8.65346 13.0347 8.64607 12.9443C8.643 12.9068 8.64012 12.8754 8.63743 12.8489C8.61421 12.829 8.58591 12.8053 8.55117 12.7769C8.47874 12.7177 8.39377 12.6503 8.28278 12.5623L7.80759 12.1858C7.11448 11.6368 6.46884 11.1254 6.02493 10.6538C5.77182 10.385 5.48876 10.0304 5.31003 9.59277Z"), svgPath(0, 0, color, "M10.3466 15.4231C10.3415 15.3857 10.3365 15.3475 10.3316 15.3086L10.3877 15.41C10.374 15.4144 10.3603 15.4187 10.3466 15.4231Z"));
        generateSvgGroup.setScaleX(0.7 * sizeMultiplier);
        generateSvgGroup.setScaleY(0.7 * sizeMultiplier);
        generateSvgGroup.setLayoutX(40 * sizeMultiplier / 2 - generateSvgGroup.getLayoutBounds().getWidth() / 2);
        generateSvgGroup.setLayoutY(40 * sizeMultiplier / 2 - generateSvgGroup.getLayoutBounds().getHeight() / 2);
        return generateSvgGroup;
    }
    private SVGPath getAdminSVG(javafx.scene.paint.Color color) {
        SVGPath path = new SVGPath();
        path.setContent("M 25 3 C 18.363281 3 13 8.363281 13 15 L 13 20 L 9 20 C 7.355469 20 6 21.355469 6 23 L 6 47 C 6 48.644531 7.355469 50 9 50 L 41 50 C 42.644531 50 44 48.644531 44 47 L 44 23 C 44 21.355469 42.644531 20 41 20 L 37 20 L 37 15 C 37 8.363281 31.636719 3 25 3 Z M 25 5 C 30.566406 5 35 9.433594 35 15 L 35 20 L 15 20 L 15 15 C 15 9.433594 19.433594 5 25 5 Z M 9 22 L 41 22 C 41.554688 22 42 22.445313 42 23 L 42 47 C 42 47.554688 41.554688 48 41 48 L 9 48 C 8.445313 48 8 47.554688 8 47 L 8 23 C 8 22.445313 8.445313 22 9 22 Z M 25 30 C 23.300781 30 22 31.300781 22 33 C 22 33.898438 22.398438 34.6875 23 35.1875 L 23 38 C 23 39.101563 23.898438 40 25 40 C 26.101563 40 27 39.101563 27 38 L 27 35.1875 C 27.601563 34.6875 28 33.898438 28 33 C 28 31.300781 26.699219 30 25 30 Z");
        path.setFill(color);
        path.setStroke(color);
        path.setStrokeWidth(2);
        path.setScaleX(0.34 * sizeMultiplier);
        path.setScaleY(0.34 * sizeMultiplier);
        path.setLayoutX(31 * sizeMultiplier / 2 - path.getLayoutBounds().getWidth() / 2);
        path.setLayoutY(38 * sizeMultiplier / 2 - path.getLayoutBounds().getHeight() / 2);
        return path;
    }
    private SVGPath getAccountSVG(javafx.scene.paint.Color color) {
        SVGPath path = new SVGPath();
        path.setContent("M16.03,18.616l5.294-4.853a1,1,0,0,1,1.352,1.474l-6,5.5a1,1,0,0,1-1.383-.03l-3-3a1,1,0,0,1,1.414-1.414ZM1,20a9.01,9.01,0,0,1,5.623-8.337A4.981,4.981,0,1,1,10,13a7.011,7.011,0,0,0-6.929,6H10a1,1,0,0,1,0,2H2A1,1,0,0,1,1,20ZM7,8a3,3,0,1,0,3-3A3,3,0,0,0,7,8Z");
        path.setFill(color);
        path.setScaleX(0.9 * sizeMultiplier);
        path.setScaleY(0.9 * sizeMultiplier);
        path.setLayoutX(40 * sizeMultiplier / 2 - path.getLayoutBounds().getWidth() / 2);
        path.setLayoutY(38 * sizeMultiplier / 2 - path.getLayoutBounds().getHeight() / 2 - 1.5 / sizeMultiplier);
        return path;
    }
    private Node[] getGenerateParts() {
        return new Node[] {languageLabel, lengthComboBox, languageComboBox, lengthLabel, generateButton, resetButton};
    }
    private Node[] getAccountParts() {
        return new Node[] {usagesLabel, creditsLabel, logoutButton};
    }
    private Node[] getAdminParts() {
        return new Node[] {snapchatComboBox, snapchatLabel, tiktokLabel, youtubeLabel, tiktokComboBox, youtubeComboBox, adminLabel};
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

        Rectangle pauseShape1 = new Rectangle(rectangleWidth*sizeMultiplier, height*sizeMultiplier);
        pauseShape1.setFill(fillColor);
        pauseShape1.setTranslateX(0);

        Rectangle pauseShape2 = new Rectangle(rectangleWidth*sizeMultiplier, height*sizeMultiplier);
        pauseShape2.setFill(fillColor);
        pauseShape2.setTranslateX(rectangleWidth * 2);

        StackPane pauseShape = new StackPane(pauseShape1, pauseShape2);
        pauseShape.setAlignment(Pos.CENTER);

        return pauseShape;
    }
    private SVGPath svgPath(int x, int y, float scale, javafx.scene.paint.Color color, String path) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(path);
        svgPath.setLayoutX(x*sizeMultiplier);
        svgPath.setLayoutY(y*sizeMultiplier);
        svgPath.setScaleX(scale);
        svgPath.setScaleY(scale);
        svgPath.setFill(javafx.scene.paint.Color.TRANSPARENT);
        svgPath.setStroke(color);
        svgPath.setStrokeWidth(2.3);
        return svgPath;
    }
}
