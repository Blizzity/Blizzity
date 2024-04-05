package wildepizza.com.github.blizzity.gui.javafx;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import wildepizza.com.github.blizzity.gui.listeners.ScreenListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "unused"})
public class AccountComboBox extends Pane {
    private ComboBox<Map<String, String>> internalComboBox;
    private Rectangle rectangle;
    private Node selection;
    private final List<Label> items = new ArrayList<>();
    private Polygon arrow;
    double height;
    double width;
    private Map<String, String> selected;
    private Pane selectedPane;
    private double sizeMultiplier = 1;
    private Color strokeColor = Color.WHITE;
    private Color selectedStrokeColor = Color.BLUE;
    private Color textFillColor = Color.BLACK;
    private Color selectedBackground = Color.BLACK;
    public void setBackgroundColor(Color color) {
        rectangle.setFill(color);
    }
    public void setSelectedBackgroundColor(Color color) {
        selectedBackground = color;
    }
    public void setStrokeColor(Color color) {
        strokeColor = color;
        rectangle.setStroke(color);
    }
    public void setSelectedStrokeColor(Color color) {
        selectedStrokeColor = color;
    }
    private Pane generateAccountPane(Map<String, String> info) {
        Pane pane = new Pane();
        Image image = new Image(info.get("avatar"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        imageView.setLayoutX(360 * sizeMultiplier);
        imageView.setLayoutY(20 + 65 * sizeMultiplier);
        Circle clip = new Circle(imageView.getFitHeight() / 2, imageView.getFitHeight() / 2, imageView.getFitHeight() / 2);
        imageView.setClip(clip);
        Label displayName = new Label(info.get("display_name"));
        displayName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        displayName.setPrefSize(85 * sizeMultiplier, 20 * sizeMultiplier);
        displayName.setLayoutX(425 * sizeMultiplier);
        displayName.setLayoutY(20 + 55 + imageView.getFitHeight() / 2);
        displayName.setTextFill(javafx.scene.paint.Color.WHITE);
        Label username = new Label("@" + info.get("username"));
        username.setStyle("-fx-font-weight: bold; -fx-font-size: 9px;");
        username.setPrefSize(85 * sizeMultiplier, 20 * sizeMultiplier);
        username.setLayoutX(425 * sizeMultiplier);
        username.setLayoutY(20 + 55 + 10 + imageView.getFitHeight() / 2);
        username.setTextFill(javafx.scene.paint.Color.rgb(128, 128, 128));
        pane.getChildren().addAll(imageView, displayName, username);
        return pane;
    }
    public AccountComboBox(List<Map<String, String>> data, double width, double height, double arc) {
        internalComboBox = new ComboBox<>();
        this.height = height;
        this.width = width;
        double multiplier = Math.min(height, width)/25;
        rectangle = new Rectangle(width, height);
        rectangle.setFill(Color.WHITE);
        rectangle.setArcWidth(arc);
        rectangle.setArcHeight(arc);
        rectangle.setStroke(strokeColor);
        rectangle.setStrokeWidth(1);

        selectedPane = generateAccountPane(data.get(0));

        arrow = new Polygon();
        arrow.getPoints().addAll(
                width-10.0*multiplier, height/2-2.5*multiplier,
                width-15.0*multiplier, height/2+2.5*multiplier,
                width-20.0*multiplier, height/2-2.5*multiplier,
                width-21.0*multiplier, height/2-1.5*multiplier,
                width-15.0*multiplier, height/2+4.5*multiplier,
                width-9.0*multiplier, height/2-1.5*multiplier
        );
        arrow.setFill(Color.rgb(147,163,170));

        getChildren().addAll(rectangle, arrow, selectedPane);
        ScreenListener.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!isHover()) {
                    Platform.runLater(() -> hide());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        setOnMouseMoved(event -> {
            if (internalComboBox.isShowing()) {
                mouseEvent(event);
            }
        });
        setOnMouseClicked(event -> {
            if (internalComboBox.isShowing()) {
                int index = (int) (event.getY() / height);
                if (index < internalComboBox.getItems().size()) {
                    if (!(index == 0)) {
                        ObservableList<Map<String, String>> items = internalComboBox.getItems();
                        int i = 0;
                        for (Map<String, String> item : items)
                            if (!item.equals(selected)) {
                                if (i == index-1) {
                                    selected = item;
                                    getSelectionModel().select(item);
                                }
                                i++;
                            }
                    }
                }
                hide();
            } else {
                internalComboBox.show();
                getChildren().remove(arrow);
                rectangle.setStroke(selectedStrokeColor);
                rectangle.setStrokeWidth(3);
                rectangle.setHeight(height*(internalComboBox.getItems().size()));
                int index = 0;
                for (Map<String, String> item : internalComboBox.getItems()) {
                    if (!item.equals(selected)) {
                        /*index++;
                        Label itemLabel = new Label((String) item);
                        itemLabel.setTextFill(textFillColor);
                        itemLabel.setFont(font);
                        Text newFont = new Text((String) item);
                        newFont.setFont(font);
                        itemLabel.setLayoutY((height - newFont.getBoundsInLocal().getHeight()) /2 + index * height);
                        itemLabel.setLayoutX(selected.getLayoutX());
                        getChildren().add(itemLabel);
                        items.add(itemLabel);*/
                    }
                }
                mouseEvent(event);
            }
        });
        getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    /*Text newFont = new Text((String) newValue);
                    newFont.setFont(selected.getFont());
                    selected.setLayoutY((height - newFont.getBoundsInLocal().getHeight()) /2);
                    selected.setText((String) newValue);*/
                });
    }
    public double getNewHeight() {
        return height;
    }
    public double getNewWidth() {
        return width;
    }
    public void mouseEvent(javafx.scene.input.MouseEvent event) {
        if (selection != null)
            getChildren().remove(selection);
        int index = (int) (event.getY() / height);
        if (index < internalComboBox.getItems().size()) {
            int indent = 3;
            double arc = 2.5;
            Path path = new Path();
            path.setFill(selectedBackground);
            path.setStroke(selectedBackground);
            if (index == 0) {
                path.getElements().add(new MoveTo(indent, indent + arc));
                path.getElements().add(new ArcTo(arc, arc, 45, indent + arc, indent, false, true));
                path.getElements().add(new LineTo(width - indent - arc, indent));
                path.getElements().add(new ArcTo(arc, arc, 45, width - indent, indent + arc, false, true));
                path.getElements().add(new LineTo(width - indent, height));
                path.getElements().add(new LineTo(indent, height));
                path.getElements().add(new ClosePath());
                selection = path;
                getChildren().add(selection);
                selectedPane.toFront();
            } else {
                if (index+1 == internalComboBox.getItems().size()) {
                    path.getElements().add(new MoveTo(indent, index * height));
                    path.getElements().add(new LineTo(width - indent, index * height));
                    path.getElements().add(new LineTo(width - indent, (index+1) * height - arc - indent));
                    path.getElements().add(new ArcTo(arc, arc, 45, width - arc - indent, (index+1) * height - indent, false, true));
                    path.getElements().add(new LineTo(arc + indent, (index+1) * height - indent));
                    path.getElements().add(new ArcTo(arc, arc, 45, indent, (index+1) * height - arc - indent, false, true));
                    path.getElements().add(new ClosePath());
                    selection = path;
                    getChildren().add(selection);
                } else {
                    Rectangle rectangle = new Rectangle(width - indent*2, height);
                    rectangle.setLayoutY(index * height + indent);
                    rectangle.setLayoutX(indent);
                    rectangle.setFill(selectedBackground);
                    selection = rectangle;
                    getChildren().add(selection);
                }
                items.get(index - 1).toFront();
            }
        }
    }
    public void hide() {
        if (selection != null)
            getChildren().remove(selection);
        internalComboBox.hide();
        rectangle.setStroke(strokeColor);
        rectangle.setStrokeWidth(1);
        rectangle.setHeight(height);
        for (Label label : items)
            getChildren().remove(label);
        if (!getChildren().contains(arrow))
            getChildren().add(arrow);
        items.clear();
    }
    public ObservableList<Map<String, String>> getItems() {
        return internalComboBox.getItems();
    }
    public Map<String, String> getValue() {
        return internalComboBox.getSelectionModel().getSelectedItem();
    }
    public  SingleSelectionModel<Map<String, String>> getSelectionModel() {
        return internalComboBox.getSelectionModel();
    }
}