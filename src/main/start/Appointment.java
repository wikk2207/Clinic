package main.start;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;

public class Appointment extends Tab {
    private Connection con;
    private TabPane tabPane;
    private Tab addAppointmentT;
    private Tab showAppointmentsT;
    private String userType;
    private int id;
    private Statement findUserByPeselStmt;
    private Statement stmt;
    private String specialization;
    private BorderPane addBorderPane;
    private BorderPane showBorderPane;
    private String date;
    String imieNazwiskoPacjenta;
    int day, month, year;

    public Appointment(Connection con, String userType, int id) {
        this.setText("Wizyty");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
        this.con=con;
        this.userType = userType;
        this.id = id;


        tabPane = new TabPane();
        try {
            findUserByPeselStmt = con.prepareStatement("SELECT p_id, pesel FROM pacjenci WHERE pesel=?;");
            stmt = con.createStatement();
        } catch (Exception e) {
            //
        }

        buildAddAppointmentTab();

        showAppointmentsT = new Tab("Pokaż wizyty");
        showAppointmentsT.setClosable(false);
        tabPane.getTabs().addAll( showAppointmentsT);

        this.setContent(tabPane);

       /* try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT id_lekarza, id_pacjenta FROM Wizyty WHERE godzina_wizyty>15;");
            while (rs.next()) {
                System.out.println(rs.getInt(1)+ " " + rs.getInt(2));
            }

        } catch (Exception e) {
            //
        }*/

    }

    private void buildAddAppointmentTab() {
        addAppointmentT = new Tab("Dodaj wizytę");
        addAppointmentT.setClosable(false);
        tabPane.getTabs().add(addAppointmentT);

        addBorderPane = new BorderPane();
        FlowPane flowPane = new FlowPane();
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setHgap(20);
        flowPane.setVgap(20);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setSpacing(10);
        //todo ????
        vBox.setPadding(new Insets(7,7,7,7));

        addBorderPane.setCenter(flowPane);
        addBorderPane.setTop(vBox);
        addAppointmentT.setContent(addBorderPane);

        if(userType.equals("sekretarka") || userType.equals("admin")) {
            findUser(flowPane, vBox);
        } else if(userType.equals("user")) {
            chooseSpecialization(flowPane, vBox);
        }



    }

    //zabezpieczone
    private void findUser(FlowPane flowPane, VBox vBox) {
        flowPane.getChildren().clear();
        vBox.getChildren().clear();

        Text t = new Text("Podaj numer pesel pacjenta");
        TextField textField = new TextField();
        Button b = new Button("Dalej");

        flowPane.getChildren().addAll(t, textField,b);
        b.setOnAction(event -> {

            if(findUserByPesel(textField.getText()) == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uwaga!");
                alert.setHeaderText(null);
                alert.setContentText("Wprowadzony numer pesel jest nieprawidłowy lub nie istnieje w bazie pacjentów!");

                alert.showAndWait();
            } else {
                try{
                    //Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    //alert.setTitle("Znaleziono pacjenta!");
                    //alert.setHeaderText(null);
                    Statement statement = con.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT imie, nazwisko FROM pacjenci WHERE p_id="+id+";");
                    if(rs.next()) {
                        imieNazwiskoPacjenta = rs.getString(1)+" "+rs.getString(2);
                        //alert.setContentText("Pacjent: "+imieNazwiskoPacjenta);
                        //alert.showAndWait();
                    }

                    flowPane.getChildren().removeAll(t,textField,b);
                    chooseSpecialization(flowPane, vBox);
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        });
    }

    //zabezpieczone
    private String findUserByPesel(String pesel) {
        String userPesel = null;
        try {
            System.out.println(pesel);
            ((PreparedStatement)findUserByPeselStmt).setString(1, pesel);
            ResultSet rs = ((PreparedStatement) findUserByPeselStmt).executeQuery();
            if(rs.next()){
                id = rs.getInt(1);
                userPesel = rs.getString(2);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return userPesel;
    }

    //TODO połączyć specjalizacje i date w jedno okno
    private void chooseSpecialization(FlowPane flowPane, VBox vBox) {
        vBox.getChildren().clear();

        Text textTop = new Text("Wybrany pacjent: " +imieNazwiskoPacjenta);
        vBox.getChildren().add(textTop);
        flowPane.getChildren().clear();
        addBorderPane.setBottom(null);
        Text text = new Text("Wybierz specjalizację");
        MenuButton menuButton = new MenuButton("specjalizacja");
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        String s;
        Button nextButton = new Button("dalej");
        nextButton.setOnAction(event -> {
            specialization = menuButton.getText();
            chooseDate(flowPane, vBox, textTop);
        });
        Button backButton = new Button("Powrót");
        backButton.setOnAction(event -> {
            findUser(flowPane, vBox);
        });

        try {
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT specjalizacja FROM pracownicy WHERE specjalizacja IS NOT NULL ORDER BY specjalizacja ;");
            while(rs.next()) {
                s = rs.getString(1);
                final MenuItem newMenuItem = new MenuItem(s);
                newMenuItem.setOnAction((event) -> {
                    menuButton.setText(newMenuItem.getText());
                });
                menuItems.add(newMenuItem);
            }
            menuButton.getItems().addAll(menuItems);
            flowPane.getChildren().addAll(text,menuButton,nextButton);
        } catch (Exception e) {
            System.out.println(e);
        }
        addBorderPane.setBottom(backButton);

    }

    private void chooseDate(FlowPane flowPane, VBox vBox, Text patient) {
        vBox.getChildren().clear();
        vBox.getChildren().add(patient);

        Text spec = new Text("Szukany lekarz: "+specialization);
        vBox.getChildren().add(spec);


        DatePicker datePicker = new DatePicker();
        Text text = new Text("Wybierz datę");
        Button button = new Button("Dalej");
        button.setOnAction(event -> {
            LocalDate localDate = datePicker.getValue();
            day = localDate.getDayOfMonth();
            month = localDate.getMonthValue();
            year = localDate.getYear();
            date = localDate.toString();
            showAppointments(flowPane, vBox, patient, spec);
        });
        Button backButton = new Button("Powrót");
        backButton.setOnAction(event -> {
            chooseSpecialization(flowPane, vBox);
        });

        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });

        flowPane.getChildren().clear();
        addBorderPane.setBottom(null);
        flowPane.getChildren().addAll(text,datePicker,button);
        addBorderPane.setBottom(backButton);

    }

    private void showAppointments(FlowPane flowPane, VBox vBox, Text patient, Text spec) {
        vBox.getChildren().clear();
        vBox.getChildren().addAll(patient, spec);

        Text chooseText = new Text("Wybierz lekarza: ");

        flowPane.getChildren().clear();
        addBorderPane.setBottom(null);

        ArrayList<Staff> arrayList = new ArrayList<>();
        Staff newDoctor;
        MenuButton menuButton = new MenuButton();
        menuButton.setPrefWidth(200);

        Button backButton = new Button("Powrót");
        backButton.setOnAction(event -> {
            chooseDate(flowPane, vBox,patient);
        });

        vBox.getChildren().addAll(chooseText,menuButton);


        try{
            Statement stmtDoctors = con.prepareStatement("SELECT staff_id, imie, nazwisko, typ, specjalizacja FROM pracownicy WHERE specjalizacja=?;");
            ((PreparedStatement) stmtDoctors).setString(1,specialization);
            ResultSet rs = ((PreparedStatement) stmtDoctors).executeQuery();
            while(rs.next()) {
                newDoctor = new Staff(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                newDoctor.setImieNazwisko(rs.getString(2)+ " "+rs.getString(3));
                arrayList.add(newDoctor);
                MenuItem newItemDoctor = new MenuItem(rs.getString(2)+" "+rs.getString(3));
                newItemDoctor.setOnAction(event -> {
                    menuButton.setText(newItemDoctor.getText());

                    try {
                        for (Staff staff: arrayList) {
                            if(staff.getImieNazwisko().equals(newItemDoctor.getText())) {
                                createTableWithTerms(flowPane, staff.getId());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }


                });
                menuButton.getItems().add(newItemDoctor);
            }

        } catch (Exception e){
            System.out.println(e);
        }



        //Statement doctors = con.prepareStatement("SELECT FROM Wizyty A JOIN Pracownicy B ON A.id_lekarza=B.staff_id WHERE data_wizyty=?)
        //stmt.executeQuery("SELECT FROM Wizyty A JOIN Pracownicy B ON A.id_lekarza=B.staff_id WHERE data_wizyty="+date+);
        addBorderPane.setBottom(backButton);
    }

    public void createTableWithTerms(FlowPane flowPane, int idDoctor) {
        flowPane.getChildren().clear();

        TableView table = new TableView();
        table.setPrefWidth(400);
        table.setPrefHeight(500);
        table.setEditable(false);
        ObservableList<VisitTime> data = FXCollections.observableArrayList();

        TableColumn dateColumn = new TableColumn("Data");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        //dateColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn timeColumn = new TableColumn("Godzina");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn actionColumn = new TableColumn("Wybierz");
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("chooseB"));

        try{
            Statement statement = con.prepareStatement("SELECT poczatek FROM godziny WHERE g_id NOT IN(SELECT godzina_wizyty FROM wizyty WHERE id_lekarza=? AND data_wizyty=\'"+year+"-"+month+"-"+day+"\');");
            ((PreparedStatement) statement).setInt(1, idDoctor);
            ResultSet rs = ((PreparedStatement) statement).executeQuery();
            while (rs.next()) {
                String beg = rs.getString(1);
                data.add(new VisitTime(date, beg, idDoctor,id, con));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        table.setItems(data);
        table.getColumns().addAll(dateColumn,timeColumn,actionColumn);





        flowPane.getChildren().addAll(table);


    }
}
