package main.start;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import staff.Staff;

import java.sql.*;
import java.time.LocalDate;
<<<<<<< HEAD
import java.util.*;
=======
import java.util.ArrayList;
>>>>>>> f3e9a4b266af2a456b2c0790799d2e855d5172c7

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
    private int idDoctor;

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

        if(!userType.equals("doctor")) {
            buildAddAppointmentTab();
        }
        buildShowAppointmentTab();



        this.setContent(tabPane);
    }



    // ********** POKAZ WIZYTY ***********

    private void buildShowAppointmentTab() {
        showAppointmentsT = new Tab("Pokaż wizyty");
        showAppointmentsT.setClosable(false);
        tabPane.getTabs().add( showAppointmentsT);


        showBorderPane =  new BorderPane();
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        showBorderPane.setTop(flowPane);

        Button plannedButton = new Button("Zaplanowane");
        plannedButton.setOnAction(event -> {
            show(true);

        });
        Button previousButton = new Button("Zrealizowane");
        previousButton.setOnAction(event -> {
           show(false);

        });

        flowPane.getChildren().addAll(plannedButton, previousButton);



        showAppointmentsT.setContent(showBorderPane);

    }

    @SuppressWarnings("Duplicates")
    private void patientOrDoctor(boolean planned) {
        showBorderPane.setCenter(null);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        showBorderPane.setCenter(vBox);

        Text text = new Text("Czyje wizyty chcesz wyswietlic?");

        ToggleGroup group = new ToggleGroup();
        RadioButton rb1 = new RadioButton("pacjent");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);
        RadioButton rb2 = new RadioButton("lekarz");
        rb2.setToggleGroup(group);

        Button button = new Button("Dalej");
        button.setOnAction(event -> {
            if(rb1.isSelected()) {
                 findUserShow(planned);
            }
            if(rb2.isSelected()) {
                findDoctor(planned);
            }

        });

        vBox.getChildren().addAll(text,rb1, rb2,button);

    }

    private void show(boolean planned) {
        if(userType.equals("secretary")) {
            findUserShow(planned);
        } else if(userType.equals("admin")){
            patientOrDoctor(planned);
        } else if (userType.equals("doctor")){
            idDoctor=id;
            buildShowTabForDoctor(planned);

        } else if (userType.equals("user")){
            buildShowTabForPatient(planned);
        }
    }

    @SuppressWarnings("Duplicates")
    private void findDoctor(boolean planned) {
        showBorderPane.setCenter(null);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        showBorderPane.setCenter(vBox);

        Text t1 = new Text("Imie lekarza");
        Text t2 = new Text("Nazwisko lekarza");
        TextField textField1 = new TextField();
        TextField textField2 = new TextField();
        textField1.setMaxWidth(150);
        textField2.setMaxWidth(150);
        Button b = new Button("Dalej");
        b.setOnAction(event -> {
            if(findDoctorByName(textField1.getText(), textField2.getText())) {
                buildShowTabForDoctor(planned);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uwaga!");
                alert.setHeaderText(null);
                alert.setContentText("Lekarz o podanym imieniu i nazwisku nie istnieje!");
                alert.showAndWait();
            }
        });

        vBox.getChildren().addAll(t1,textField1,t2, textField2,b);



    }

    private boolean findDoctorByName(String firstname, String surname) {
        boolean result = false;

        try {
            Statement stmt = con.prepareStatement("SELECT staff_id FROM pracownicy WHERE imie=? AND nazwisko=? AND typ=\"lekarz\";");
            ((PreparedStatement) stmt).setString(1, firstname);
            ((PreparedStatement) stmt).setString(2, surname);
            ResultSet rs = ((PreparedStatement) stmt).executeQuery();

            if(rs.next()) {
                idDoctor=rs.getInt(1);
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e){
            System.out.println(e);
        }

        return result;
    }

    @SuppressWarnings("Duplicates")
    private void buildShowTabForPatient(boolean planned) {
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        showBorderPane.setCenter(flowPane);

        TableView table = new TableView();
        table.setPrefWidth(700);
        table.setEditable(false);

        ObservableList<VisitTime> data = FXCollections.observableArrayList();

        TableColumn dateColumn = new TableColumn("Data");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn timeColumn = new TableColumn("Godzina");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn nameColumn = new TableColumn("Imie i nazwisko lekarza");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nameDoc"));
        nameColumn.setPrefWidth(200);

        TableColumn specColumn = new TableColumn("Specjalizacja");
        specColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));

        TableColumn deleteColumn = new TableColumn("Odwolaj");
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("deleteB"));
        try {
            Statement statement;
            if(planned) {
                statement = con.prepareStatement("SELECT data_wizyty, poczatek, imie, nazwisko, specjalizacja FROM wizyty A JOIN godziny B ON A.godzina_wizyty=B.g_id JOIN pracownicy C ON A.id_lekarza=C.staff_id " +
                        "WHERE id_pacjenta=? AND data_wizyty>=curdate() ORDER BY data_wizyty, poczatek;");
            } else {
                statement = con.prepareStatement("SELECT data_wizyty, poczatek, imie, nazwisko, specjalizacja FROM wizyty A JOIN godziny B ON A.godzina_wizyty=B.g_id JOIN pracownicy C ON A.id_lekarza=C.staff_id " +
                            "WHERE id_pacjenta=? AND data_wizyty<curdate() ORDER BY data_wizyty, poczatek DESC;");
            }
            ((PreparedStatement) statement).setInt(1, id);
            ResultSet rs = ((PreparedStatement) statement).executeQuery();
            while(rs.next()) {
                String name = rs.getString(3) + " " + rs.getString(4);
                data.add(new VisitTime(rs.getString(1), rs.getString(2),0,id, con, name, rs.getString(5), null));
            }
            table.setItems(data);
            if(planned) {
                table.getColumns().addAll(dateColumn, timeColumn, nameColumn, specColumn, deleteColumn);
            } else {
                table.getColumns().addAll(dateColumn, timeColumn, nameColumn, specColumn);
            }
            flowPane.getChildren().add(table);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @SuppressWarnings("Duplicates")
    private void buildShowTabForDoctor(boolean planned) {

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        showBorderPane.setCenter(flowPane);

        TableView table = new TableView();
        table.setPrefWidth(700);
        table.setEditable(false);

        ObservableList<VisitTime> data = FXCollections.observableArrayList();

        TableColumn dateColumn = new TableColumn("Data");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn timeColumn = new TableColumn("Godzina");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn nameColumn = new TableColumn("Imie i nazwisko pacjenta");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("namePat"));
        nameColumn.setPrefWidth(200);


        TableColumn deleteColumn = new TableColumn("Odwolaj");
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("deleteB"));

        try {
            Statement statement;
            if(planned) {
                statement = con.prepareStatement("SELECT data_wizyty, poczatek, imie, nazwisko FROM wizyty A JOIN godziny B ON A.godzina_wizyty=B.g_id JOIN pacjenci C ON A.id_pacjenta=C.p_id " +
                        "WHERE id_lekarza=? AND data_wizyty>=curdate() ORDER BY data_wizyty, poczatek;");
            } else {
                statement = con.prepareStatement("SELECT data_wizyty, poczatek, imie, nazwisko FROM wizyty A JOIN godziny B ON A.godzina_wizyty=B.g_id JOIN pacjenci C ON A.id_pacjenta=C.p_id " +
                        "WHERE id_lekarza=? AND data_wizyty<curdate() ORDER BY data_wizyty DESC, poczatek DESC;");
            }
            ((PreparedStatement) statement).setInt(1, idDoctor);
            ResultSet rs = ((PreparedStatement) statement).executeQuery();
            while(rs.next()) {
                String name = rs.getString(3) + " " + rs.getString(4);
                data.add(new VisitTime(rs.getString(1), rs.getString(2),idDoctor,0, con, null, null, name));
            }
            table.setItems(data);
            if(planned && userType.equals("admin")) {
                table.getColumns().addAll(dateColumn, timeColumn, nameColumn,  deleteColumn);
            } else {
                table.getColumns().addAll(dateColumn, timeColumn, nameColumn);
            }
            flowPane.getChildren().add(table);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @SuppressWarnings("Duplicates")
    private void findUserShow(boolean planned) {
        //todo
        System.out.println("halooo");
        showBorderPane.setCenter(null);

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setAlignment(Pos.CENTER);
        showBorderPane.setCenter(flowPane);

        Text t = new Text("Podaj numer pesel pacjenta");
        TextField textField = new TextField();
        textField.setMaxWidth(150);
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
                    Statement statement = con.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT imie, nazwisko FROM pacjenci WHERE p_id="+id+";");
                    if(rs.next()) {
                        imieNazwiskoPacjenta = rs.getString(1)+" "+rs.getString(2);
                    }
                    buildShowTabForPatient(planned);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }


    // ********** DODAJ WIZYTE ***********

    private void buildAddAppointmentTab() {
        addAppointmentT = new Tab("Dodaj wizytę");
        addAppointmentT.setClosable(false);
        tabPane.getTabs().add(addAppointmentT);

        addBorderPane = new BorderPane();
        VBox vBoxCenter = new VBox();
        vBoxCenter.setAlignment(Pos.CENTER);
        vBoxCenter.setSpacing(10);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setSpacing(10);
        //todo ????
        vBox.setPadding(new Insets(7,7,7,7));

        addBorderPane.setCenter(vBoxCenter);
        addBorderPane.setTop(vBox);
        addAppointmentT.setContent(addBorderPane);

        if(userType.equals("secretary") || userType.equals("admin")) {
            findUser(vBoxCenter, vBox);
        } else if(userType.equals("user")) {
            chooseSpecializationAndDate(vBoxCenter, vBox);
        }



    }

    //zabezpieczone
    private void findUser(VBox vBoxCenter, VBox vBox) {
        addBorderPane.setBottom(null);
        vBoxCenter.getChildren().clear();
        vBox.getChildren().clear();

        Text t = new Text("Podaj numer pesel pacjenta");
        TextField textField = new TextField();
        textField.setMaxWidth(150);
        Button b = new Button("Dalej");

        vBoxCenter.getChildren().addAll(t, textField,b);
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

                    vBoxCenter.getChildren().removeAll(t,textField,b);
                    chooseSpecializationAndDate(vBoxCenter, vBox);
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        });
    }

    //zabezpieczone
    private String findUserByPesel(String pesel)    {
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

    //zabezpieczone
    private void chooseSpecializationAndDate(VBox vBoxCenter, VBox vBox) {
        vBoxCenter.getChildren().clear();
        addBorderPane.setBottom(null);
        vBox.getChildren().clear();


        Text patient = new Text(null);
        Text text = new Text("Wybierz specjalizację");
        Text textChoose = new Text("Wybierz datę");
        Text text1 = patient; //XD zeby sie nie rzucal o final w lamda expr


        HBox chooseSpec = new HBox();
        chooseSpec.setAlignment(Pos.CENTER);
        chooseSpec.setSpacing(10);

        HBox chooseDate = new HBox();
        chooseDate.setAlignment(Pos.CENTER);
        chooseDate.setSpacing(10);

        MenuButton menuButton = new MenuButton("specjalizacja");
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        String s;

        DatePicker datePicker = new DatePicker();
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });

        Button nextButton = new Button("Dalej");
        nextButton.setOnAction(event -> {

            if(menuButton.getText().equals("specjalizacja") || datePicker.getValue()==null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uwaga!");
                alert.setHeaderText(null);
                alert.setContentText("Nie wybrano daty lub specjalizacji.");
                alert.showAndWait();
            } else {
                specialization = menuButton.getText();
                Text spec = new Text("Szukany lekarz: "+specialization);

                LocalDate localDate = datePicker.getValue();
                day = localDate.getDayOfMonth();
                month = localDate.getMonthValue();
                year = localDate.getYear();
                date = localDate.toString();
                showAppointments(vBoxCenter, vBox, text1, spec);
            }
        });


        if(userType.equals("sekretarka") || userType.equals("admin")) {
            patient= new Text("Wybrany pacjent: " +imieNazwiskoPacjenta);
            vBox.getChildren().add(patient);
            Button backButton = new Button("Powrót");
            backButton.setOnAction(event -> {
                findUser(vBoxCenter, vBox);
            });
            addBorderPane.setBottom(backButton);
        }

        chooseSpec.getChildren().addAll(text,menuButton);
        chooseDate.getChildren().addAll(textChoose,datePicker);

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
        } catch (Exception e) {
            System.out.println(e);
        }

        vBoxCenter.getChildren().addAll(chooseSpec, chooseDate,nextButton);

    }

    //dost do pacjenta i zabezpieczone
    private void showAppointments(VBox vBoxCenter, VBox vBox, Text patient, Text spec) {
        vBox.getChildren().clear();

        if(userType.equals("sekretarka") || userType.equals("admin")) {
            vBox.getChildren().addAll(patient, spec);
        } else {
            vBox.getChildren().addAll(spec);
        }



        Text chooseText = new Text("Wybierz lekarza: ");

        vBoxCenter.getChildren().clear();
        addBorderPane.setBottom(null);

        ArrayList<Staff> arrayList = new ArrayList<>();
        Staff newDoctor;
        MenuButton menuButton = new MenuButton();
        menuButton.setPrefWidth(200);

        Button backButton = new Button("Powrót");
        backButton.setOnAction(event -> {
            chooseSpecializationAndDate(vBoxCenter, vBox);
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
                                createTableWithTerms(vBoxCenter, staff.getId());
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
    @SuppressWarnings("Duplicates")
    public void createTableWithTerms(VBox vBoxCenter, int idDoctor) {
        vBoxCenter.getChildren().clear();

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
                data.add(new VisitTime(date, beg, idDoctor,id, con, null, null, null));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        table.setItems(data);
        table.getColumns().addAll(dateColumn,timeColumn,actionColumn);





        vBoxCenter.getChildren().addAll(table);


    }
}
