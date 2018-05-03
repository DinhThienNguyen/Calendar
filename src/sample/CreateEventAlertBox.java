package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;


public class CreateEventAlertBox {
    private String oldStartTime;
    private String oldEndTime;
    private boolean isChanged = false;
    private boolean isInColorPicker = false;
    private boolean isNewEventAdded = false;

    public boolean display(int day, Calendar c, double x, double y, boolean update) {
        Stage window = new Stage();
        if(update){
            window.setTitle("Cập nhật sự kiện");
        }else {
            window.setTitle("Thêm sự kiện mới");
        }

        window.setMinWidth(610);
        window.setMinHeight(500);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        window.initModality(Modality.WINDOW_MODAL);
        window.setResizable(true);
        window.initStyle(StageStyle.UTILITY);

        TextField titleTextField = new TextField();
        titleTextField.setFont(new Font("Calibri", 32));
        titleTextField.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(titleTextField, Priority.ALWAYS);
        VBox.setMargin(titleTextField, new Insets(20, 20, 10, 20));
        titleTextField.setPromptText("Thêm tiêu đề");
        titleTextField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused)
                isInColorPicker = false;
        });
        titleTextField.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                isChanged = true;
            }
        });

        ImageView eventTimeImageView = makeImageView("eventtimeicon", "Thời gian", 30, 10, 0, 8);

        Label start = new Label("Từ");
        HBox.setMargin(start, new Insets(8, 20, 0, 5));
        start.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        start.setStyle("-fx-font: 20px \"System\";");

        Calendar temp = c;
        temp.set(Calendar.DATE, day);
        DatePicker startDatePicker = makeDatePicker(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DAY_OF_MONTH));
        startDatePicker.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused)
                isInColorPicker = false;
        });


        ComboBox<Time> startTimeComboBox = makeTimeComboBox();

        TextField startTimeTextField = new TextField();
        startTimeTextField.setMaxWidth(100);
        startTimeTextField.setFont(new Font("System", 20));
        Calendar current = Calendar.getInstance();
        oldStartTime = (current.get(Calendar.HOUR_OF_DAY) >= 10 ? "" + current.get(Calendar.HOUR_OF_DAY) : "0" + current.get(Calendar.HOUR_OF_DAY))
                + ":" +
                (current.get(Calendar.MINUTE) >= 10 ? "" + current.get(Calendar.MINUTE) : "0" + current.get(Calendar.MINUTE));
        startTimeTextField.setText(oldStartTime);
        startTimeTextField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                if (!startTimeTextField.getText().matches("[0-23]\\:[0-59]")) {
                    startTimeTextField.setText(oldStartTime);
                    if (!startTimeComboBox.isFocused())
                        startTimeComboBox.setVisible(false);
                }
            } else if (newValue) {
                isInColorPicker = false;
                startTimeComboBox.setVisible(true);
            }
        });
        ;
        startTimeComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                oldStartTime = startTimeComboBox.getSelectionModel().getSelectedItem().getTime();
                startTimeTextField.setText(oldStartTime);
            }
        });

        DatePicker endDatePicker = makeDatePicker(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DAY_OF_MONTH));

        Label end = new Label("Đến");
        HBox.setMargin(end, new Insets(8, 7, 0, 5));
        end.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        end.setStyle("-fx-font: 20px \"System\";");

        ComboBox<Time> endTimeComboBox = makeTimeComboBox();

        TextField endTimeTextField = new TextField();
        endTimeTextField.setMaxWidth(100);
        endTimeTextField.setFont(new Font("System", 20));
        current.add(Calendar.MINUTE, 30);
        oldEndTime = (current.get(Calendar.HOUR_OF_DAY) >= 10 ? "" + current.get(Calendar.HOUR_OF_DAY) : "0" + current.get(Calendar.HOUR_OF_DAY))
                + ":" +
                (current.get(Calendar.MINUTE) >= 10 ? "" + current.get(Calendar.MINUTE) : "0" + current.get(Calendar.MINUTE));
        endTimeTextField.setText(oldEndTime);
        endTimeTextField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                if (!endTimeTextField.getText().matches("[0-23]\\:[0-59]")) {
                    endTimeTextField.setText(oldEndTime);
                    if (!endTimeComboBox.isFocused())
                        endTimeComboBox.setVisible(false);
                }
            } else if (newValue) {
                isInColorPicker = false;
                endTimeComboBox.setVisible(true);
            }
        });
        endTimeComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                oldEndTime = endTimeComboBox.getSelectionModel().getSelectedItem().getTime();
                endTimeTextField.setText(oldEndTime);
            }
        });

        HBox eventStartTimeHBox = new HBox();
        eventStartTimeHBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        eventStartTimeHBox.setPadding(new Insets(0, 0, 10, 0));
        eventStartTimeHBox.getChildren().addAll(start, startDatePicker, startTimeTextField, startTimeComboBox);

        HBox eventEndTimeHBox = new HBox();
        eventEndTimeHBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        eventEndTimeHBox.setPadding(new Insets(0, 0, 10, 0));
        eventEndTimeHBox.getChildren().addAll(end, endDatePicker, endTimeTextField, endTimeComboBox);

        VBox eventTimeVBox = new VBox();
        eventTimeVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        eventTimeVBox.setPadding(new Insets(0, 0, 10, 0));
        eventTimeVBox.getChildren().addAll(eventStartTimeHBox, eventEndTimeHBox);

        HBox timeHBox = new HBox();
        timeHBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        timeHBox.setPadding(new Insets(0, 0, 10, 0));
        timeHBox.getChildren().addAll(eventTimeImageView, eventTimeVBox);

        ImageView eventNotifyTimeImageView = makeImageView("notifyicon", "Thời gian thông báo", 5, 10, 0, 8);

        ComboBox notifyTimeComboBox = new ComboBox<>();
        notifyTimeComboBox.getItems().addAll(
                "Tại thời điểm sự kiện",
                "Trước 10 phút",
                "Trước 30 phút",
                "Tùy chọn",
                "Không thông báo"
        );
        notifyTimeComboBox.setStyle("-fx-font: 20px \"System\";");
        notifyTimeComboBox.getSelectionModel().selectFirst();
        notifyTimeComboBox.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused)
                isInColorPicker = false;
        });

        TextField notifyTimeTextField = new TextField();
        notifyTimeTextField.setFont(new Font("System", 20));
        notifyTimeTextField.setPromptText("Trước");
        notifyTimeTextField.setMaxWidth(110);
        notifyTimeTextField.setVisible(false);
        notifyTimeTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    notifyTimeTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        ComboBox notifyTimeUnitComboBox = new ComboBox<>();
        notifyTimeUnitComboBox.getItems().addAll(
                "phút",
                "giờ",
                "ngày"
        );
        notifyTimeUnitComboBox.setStyle("-fx-font: 20px \"System\";");
        notifyTimeUnitComboBox.setMaxWidth(120);
        notifyTimeUnitComboBox.getSelectionModel().selectFirst();
        notifyTimeUnitComboBox.setVisible(false);
        notifyTimeComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (notifyTimeComboBox.getSelectionModel().getSelectedItem().toString().equals("Tùy chọn")) {
                    notifyTimeTextField.setVisible(true);
                    notifyTimeUnitComboBox.setVisible(true);
                } else {
                    notifyTimeTextField.setVisible(false);
                    notifyTimeUnitComboBox.setVisible(false);
                }
            }
        });

        HBox notifyTimeHBox = new HBox();
        notifyTimeHBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        notifyTimeHBox.setPadding(new Insets(0, 0, 10, 0));
        notifyTimeHBox.getChildren().addAll(eventNotifyTimeImageView, notifyTimeComboBox, notifyTimeTextField, notifyTimeUnitComboBox);

        ImageView eventDescriptionImageView = makeImageView("description", "Mô tả sự kiện", 5, 10, 0, 8);

        TextArea eventDescriptionTextArea = new TextArea();
        eventDescriptionTextArea.setPromptText("Thêm mô tả sự kiện");
        eventDescriptionTextArea.setWrapText(true);
        eventDescriptionTextArea.setPrefRowCount(5);
        eventDescriptionTextArea.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                isChanged = true;
            }
        });
        eventDescriptionTextArea.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused)
                isInColorPicker = false;
        });
        HBox.setMargin(eventDescriptionTextArea, new Insets(0, 10, 0, 0));
        eventDescriptionTextArea.setFont(new Font("System", 20));
        HBox.setHgrow(eventDescriptionTextArea, Priority.ALWAYS);

        HBox eventDescriptionHBox = new HBox();
        eventDescriptionHBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        eventDescriptionHBox.setPadding(new Insets(0, 0, 10, 0));
        VBox.setVgrow(eventDescriptionHBox, Priority.ALWAYS);
        eventDescriptionHBox.getChildren().addAll(eventDescriptionImageView, eventDescriptionTextArea);

        ImageView colorChooser = makeImageView("colorchooser", "Chọn màu sự kiện", 5, 10, 0, 8);
        final ColorPicker colorPicker = new ColorPicker();
        DbConnection dbConnection = new DbConnection();
        colorPicker.setValue(Color.web(dbConnection.getDefaultColor("event")));
        colorPicker.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                isInColorPicker = true;
            }
        });

        HBox colorChooserHBox = new HBox();
        colorChooserHBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        colorChooserHBox.setPadding(new Insets(0, 0, 10, 0));
        colorChooserHBox.getChildren().addAll(colorChooser, colorPicker);

        Button acceptButton = new Button();
        if(update){
            acceptButton.setText("Cập nhật");
        }else{
            acceptButton.setText("Thêm");
        }
        acceptButton.setFont(new Font("System", 20));
        acceptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Event newEvent = new Event();
                if (!titleTextField.getText().isEmpty()) {
                    newEvent.setTitle(titleTextField.getText());
                } else {
                    newEvent.setTitle("");
                }

                if (!eventDescriptionTextArea.getText().isEmpty()) {
                    newEvent.setDescription(eventDescriptionTextArea.getText());
                } else {
                    newEvent.setDescription("");
                }

                LocalDate localDate = startDatePicker.getValue();
                Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                Date date = Date.from(instant);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                String[] hourAndMinute = startTimeTextField.getText().split(":");
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourAndMinute[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(hourAndMinute[1]));
                newEvent.setStartTime(calendar.getTimeInMillis() / 1000);
                System.out.println(calendar.getTimeInMillis() / 1000);

                localDate = endDatePicker.getValue();
                instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                date = Date.from(instant);
                calendar = Calendar.getInstance();
                calendar.setTime(date);
                hourAndMinute = endTimeTextField.getText().split(":");
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourAndMinute[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(hourAndMinute[1]));
                newEvent.setEndTime(calendar.getTimeInMillis() / 1000);
                System.out.println(calendar.getTimeInMillis() / 1000);

                String selection = notifyTimeComboBox.getSelectionModel().getSelectedItem().toString();
                if (selection.equals("Không thông báo")) {
                    newEvent.setNotifyTime(-1L);
                } else if (selection.equals("Tại thời điểm sự kiện")) {
                    newEvent.setNotifyTime(0L);
                } else if (selection.equals("Trước 10 phút")) {
                    newEvent.setNotifyTime(10 * 60L);
                } else if (selection.equals("Trước 30 phút")) {
                    newEvent.setNotifyTime(30 * 60L);
                } else if (selection.equals("Tùy chọn")) {
                    if (notifyTimeTextField.getText().isEmpty()) {
                        Alert noinput = new Alert(Alert.AlertType.WARNING);
                        noinput.setTitle("Cảnh báo");
                        noinput.setContentText("Bạn chưa nhập thời gian thông báo tùy chọn");
                        return;
                    } else {
                        String unitSelection = notifyTimeUnitComboBox.getSelectionModel().getSelectedItem().toString();
                        if (unitSelection.equals("phút")) {
                            newEvent.setNotifyTime(Long.parseLong(notifyTimeTextField.getText()) * 60);
                        } else if (unitSelection.equals("giờ")) {
                            newEvent.setNotifyTime(Long.parseLong(notifyTimeTextField.getText()) * 60 * 60);
                        } else if (unitSelection.equals("ngày")) {
                            newEvent.setNotifyTime(Long.parseLong(notifyTimeTextField.getText()) * 24 * 60 * 60);
                        }
                    }
                }

                newEvent.setColor(Utils.toRGBCode(colorPicker.getValue()));
                DbConnection dbConnection = new DbConnection();
                dbConnection.addEventToMonthEventTable(newEvent, calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));

                isChanged = false;
                isNewEventAdded = true;
                window.close();
            }
        });

        HBox controlHBox = new HBox();
        controlHBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        controlHBox.setPadding(new Insets(0, 0, 10, 10));
        controlHBox.getChildren().addAll(acceptButton);

        VBox layout = new VBox(0);
        layout.setPadding(new Insets(0, 0, 20, 0));
        layout.setMaxWidth(600);
        layout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        layout.getChildren().addAll(titleTextField, timeHBox, notifyTimeHBox, eventDescriptionHBox, colorChooserHBox, controlHBox);

        if (primaryScreenBounds.getWidth() - x < window.getMinWidth()) {
            if (primaryScreenBounds.getHeight() - y < window.getMinHeight()) {
                window.setY(y - window.getMinHeight());
                window.setX(x - window.getMinWidth());
            } else {
                window.setY(y);
                window.setX(x - window.getMinWidth());
            }
        } else {
            if (primaryScreenBounds.getHeight() - y < window.getMinHeight()) {
                window.setY(y - window.getMinHeight());
                window.setX(x);
            } else {
                window.setY(y);
                window.setX(x);
            }
        }
        window.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                if (!isInColorPicker) {
                    if (!isChanged) {
                        if (!isNewEventAdded)
                            window.close();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Xác nhận");
                        alert.setContentText("Sự kiện chưa được lưu, bạn có muốn hủy sự kện?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            isNewEventAdded = false;
                            window.close();
                        } else {
                            alert.close();
                        }
                    }
                }
            }
        });

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        return isNewEventAdded;
    }

    private ComboBox<Time> makeTimeComboBox() {
        ComboBox<Time> comboBox = new ComboBox<Time>();
        comboBox.setItems(FXCollections.observableArrayList(
                new Time(0, 0),
                new Time(0, 30),
                new Time(1, 0),
                new Time(1, 30),
                new Time(2, 0),
                new Time(2, 30),
                new Time(3, 0),
                new Time(3, 30),
                new Time(4, 0),
                new Time(4, 30),
                new Time(5, 0),
                new Time(5, 30),
                new Time(6, 0),
                new Time(6, 30),
                new Time(7, 0),
                new Time(7, 30),
                new Time(8, 0),
                new Time(8, 30),
                new Time(9, 0),
                new Time(9, 30),
                new Time(10, 30),
                new Time(11, 0),
                new Time(11, 30),
                new Time(12, 0),
                new Time(12, 30),
                new Time(13, 0),
                new Time(13, 30),
                new Time(14, 0),
                new Time(14, 30),
                new Time(15, 0),
                new Time(15, 30),
                new Time(16, 0),
                new Time(16, 30),
                new Time(17, 0),
                new Time(17, 30),
                new Time(18, 0),
                new Time(18, 30),
                new Time(19, 0),
                new Time(19, 30),
                new Time(20, 0),
                new Time(20, 30),
                new Time(21, 0),
                new Time(21, 30),
                new Time(22, 0),
                new Time(22, 3),
                new Time(23, 0),
                new Time(23, 30)
        ));
        comboBox.setConverter(new StringConverter<Time>() {
            @Override
            public String toString(Time object) {
                return object.getTime();
            }

            @Override
            public Time fromString(String string) {
                return null;
            }
        });
        comboBox.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue)  //when focus lost
                comboBox.setVisible(false);
        });
        ;
        comboBox.setStyle("-fx-font: 20px \"System\";");
        comboBox.setVisible(false);
        return comboBox;
    }

    private DatePicker makeDatePicker(int defaultYear, int defaultMonth, int defaultDay) {
        DatePicker datePicker = new DatePicker();
        datePicker.setMaxWidth(200);
        datePicker.setStyle("-fx-font: 20px \"System\";");
        datePicker.setConverter(new StringConverter<LocalDate>() {
            private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null)
                    return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        });
        datePicker.setValue(LocalDate.of(defaultYear, defaultMonth + 1, defaultDay));
        return datePicker;
    }

    private ImageView makeImageView(String fileName, String toolTip, double topPadding, double rightPadding, double bottomPadding, double leftPadding) {
        File imageFile = new File("resources/" + fileName + ".png");
        Image image = new Image(imageFile.toURI().toString());
        ImageView imageView = new ImageView();
        HBox.setMargin(imageView, new Insets(topPadding, rightPadding, bottomPadding, leftPadding));
        imageView.setImage(image);
        Tooltip.install(imageView, new Tooltip(toolTip));

        return imageView;
    }


}
