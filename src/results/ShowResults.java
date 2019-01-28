package results;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import patients.Patient;
import staff.Staff;

import java.sql.*;

public class ShowResults extends Tab {
    private BorderPane borderPane;
    private String userType;
    private int userId;
    private Connection con;
    private Statement searchPatient;
    private Statement searchDoctor;
    private TextField nameSearchTF;
    private TextField lnameSearchTF;
    private TextField peselSearchTF;
    private TextField typeSearchTF;
    private GridPane gridPane;
    private TableView<Patient> patientTable;
    private ObservableList<Patient> patientData = FXCollections.observableArrayList();
    private TableView<Staff> doctorTable;
    private ObservableList<Staff> doctorData = FXCollections.observableArrayList();
    private int patientId;
    private int doctorId;
    private boolean editable;



    public ShowResults (String userType, int userId, Connection con) {
        this.setText("Wyniki Badań");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");

        this.userType = userType;
        this.userId = userId;
        this.con = con;
        this.borderPane = new BorderPane();

        this.setContent(borderPane);


        if(userType.equals("user")){
            patientId=userId;
            editable = false;
            showResults();
        } else if (userType.equals("doctor")){
            editable = true;
            doctorId=userId;
            showResults();
        } else if (userType.equals("secretary")) {
            editable = false;
            selectPatient();
        } else if(userType.equals("admin")) {
            editable = true;
            patientOrDoctor();
        }



    }


    private void showResults() {
        borderPane.setTop(null);
        borderPane.setCenter(null);

        Button backButton = new Button("Powrot");
        backButton.setOnAction(event -> {
            showResults();
        });

        if(userType.equals("secretary") || userType.equals("admin")) {
            Button back = new Button("Powrot");
            back.setOnAction(event -> {
                selectPatient();
            });
            borderPane.setTop(back);
        }

        VBox vbox = new VBox();
        borderPane.setCenter(vbox);

        TableView table = new TableView();
        table.setEditable(editable);

        ObservableList<Results> data = FXCollections.observableArrayList();

        TableColumn dateColumn = new TableColumn("Data i godzina wystawienia");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn firstNameColumnD = new TableColumn("Imię lekarza");
        firstNameColumnD.setCellValueFactory(new PropertyValueFactory<>("firstNameD"));

        TableColumn nameColumnD = new TableColumn("Nazwisko lekarza");
        nameColumnD.setCellValueFactory(new PropertyValueFactory<>("nameD"));

        TableColumn firstNameColumnP = new TableColumn("Imię pacjenta");
        firstNameColumnP.setCellValueFactory(new PropertyValueFactory<>("firstNameP"));

        TableColumn nameColumnP = new TableColumn("Nazwisko pacjenta");
        nameColumnP.setCellValueFactory(new PropertyValueFactory<>("nameP"));

        TableColumn selectColumn = new TableColumn("Szczegoly");
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("selectB"));

        try {
            Statement statement;
            if(userType.equals("user") || userType.equals("secretary")) {
                statement = con.prepareStatement("SELECT wb_id, data_wystawienia, id_lekarza, imie, nazwisko FROM wyniki_badan A JOIN pracownicy B ON A.id_lekarza = B.staff_id WHERE id_pacjenta=?");
                ((PreparedStatement) statement).setInt(1, patientId);
            } else if(userType.equals("doctor")) {
                statement = con.prepareStatement("SELECT wb_id, data_wystawienia, id_pacjenta, imie, nazwisko FROM wyniki_badan A JOIN pacjenci B ON A.id_pacjenta = B.p_id WHERE id_lekarza=?");
                ((PreparedStatement) statement).setInt(1, doctorId);
            } else {
                statement = con.prepareStatement("SELECT wb_id, data_wystawienia, id_lekarza, id_pacjenta, B.imie, B.nazwisko, C.imie, C.nazwisko FROM wyniki_badan A JOIN pacjenci B ON A.id_pacjenta = B.p_id JOIN pracownicy C ON A.id_lekarza = C.staff_id");
            }

            ResultSet rs = ((PreparedStatement) statement).executeQuery();

            while(rs.next()){
                int wbId = rs.getInt(1);
                String date = rs.getString(2);
                int userId=0;
                int doctorId =0;
                String nameP = null;
                String firstnameP = null;
                String nameD = null;
                String firstnameD = null;

                if(userType.equals("user") || userType.equals("secretary")) {
                    doctorId = rs.getInt(3);
                    firstnameD = rs.getString(4);
                    nameD=rs.getString(5);
                } else if(userType.equals("doctor")) {
                    userId = rs.getInt(3);
                    firstnameP = rs.getString(4);
                    nameP = rs.getString(5);
                } else { //admin
                    doctorId = rs.getInt(3);
                    firstnameD = rs.getString(7);
                    nameD=rs.getString(8);
                    userId = rs.getInt(4);
                    firstnameP = rs.getString(5);
                    nameP = rs.getString(6);
                }
                Results results = new Results(date,firstnameD,nameD,firstnameP,nameP,wbId,userId, doctorId);
                results.setBackButton(backButton);
                results.setBorderPane(borderPane);
                results.setCon(con);
                results.setEditable(editable);
                results.setUserType(userType);
                data.add(results);

            }
            table.setItems(data);

            if(userType.equals("user") || userType.equals("secretary")) {
                table.getColumns().addAll(dateColumn,firstNameColumnD,nameColumnD,selectColumn);
            } else if(userType.equals("doctor")) {
                table.getColumns().addAll(dateColumn,firstNameColumnP, nameColumnP,selectColumn);
            } else {
                table.getColumns().addAll(dateColumn,firstNameColumnD,nameColumnD, firstNameColumnP, nameColumnP, selectColumn);
            }
            vbox.getChildren().add(table);


        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private void patientOrDoctor() {
        borderPane.setCenter(null);
        borderPane.setTop(null);
        borderPane.setBottom(null);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        borderPane.setCenter(vBox);

        Text text = new Text("Szukaj badań");

        ToggleGroup group = new ToggleGroup();
        RadioButton rb1 = new RadioButton("pacjenta");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);
        RadioButton rb2 = new RadioButton("lekarza");
        rb2.setToggleGroup(group);

        Button button = new Button("Dalej");
        button.setOnAction(event -> {
            if(rb1.isSelected()) {
                selectPatient();
            }
            if(rb2.isSelected()) {
                selectDoctor();
            }

        });

        vBox.getChildren().addAll(text,rb1, rb2,button);

    }


    private void selectPatient() {
        borderPane.setTop(null);
        borderPane.setCenter(null);
        borderPane.setBottom(null);

        VBox vBox = new VBox();
        borderPane.setTop(vBox);

        if(userType.equals("admin")) {

            Button back = new Button("Powrot");
            back.setOnAction(event -> patientOrDoctor());
            vBox.getChildren().add(back);
        }

        patientTable = new TableView<Patient>();
        try {
            searchPatient = con.prepareStatement(
                    "SELECT p_id, imie, nazwisko, pesel, data_urodzenia FROM pacjenci " +
                            "WHERE UPPER(imie) LIKE UPPER(?) AND UPPER(nazwisko) LIKE UPPER(?) AND UPPER(pesel) LIKE UPPER(?)");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        Label searchPatientL = new Label("Szukanie Pacjenta");
        searchPatientL.setFont(new Font("Arial", 20));
        Label nameSL = new Label("Imie:");
        Label lnameSL = new Label("Nazwisko:");
        Label peselSL = new Label("Pesel:");
        nameSearchTF = new TextField();
        nameSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refreshP());
        lnameSearchTF = new TextField();
        lnameSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refreshP());
        peselSearchTF = new TextField();
        peselSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refreshP());
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(4);
        gridPane.setHgap(10);
        gridPane.add(searchPatientL, 0, 3, 3,1);
        gridPane.add(nameSL, 0, 4);
        gridPane.add(lnameSL, 1, 4);
        gridPane.add(peselSL, 2, 4);
        gridPane.add(nameSearchTF, 0, 5);
        gridPane.add(lnameSearchTF, 1, 5);
        gridPane.add(peselSearchTF, 2, 5);
        vBox.getChildren().add(gridPane);
        createPatientTable();
        refreshP();
        borderPane.setCenter(patientTable);


    }

    private void createPatientTable() {
        patientTable.setEditable(false);
        patientTable.setMinSize(1200,735);
        TableColumn idCol = new TableColumn("ID");
        idCol.setPrefWidth(70);
        idCol.setCellValueFactory(new PropertyValueFactory<Patient, Integer>("id"));
        TableColumn nameCol = new TableColumn("Imie");
        nameCol.setPrefWidth(120);
        nameCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("firstName"));
        TableColumn lnameCol = new TableColumn("Nazwisko");
        lnameCol.setPrefWidth(120);
        lnameCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("lastName"));
        TableColumn peselCol = new TableColumn("Pesel");
        peselCol.setPrefWidth(120);
        peselCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("pesel"));
        TableColumn dateCol = new TableColumn("Data Urodzenia");
        dateCol.setPrefWidth(120);
        dateCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("date"));
        TableColumn selectCol = new TableColumn("Wybierz");
        selectCol.setPrefWidth(90);
        selectCol.setCellValueFactory(new PropertyValueFactory<Staff, String>("selectB"));
        patientTable.setItems(patientData);
        patientTable.getColumns().addAll(idCol, nameCol, lnameCol, peselCol, dateCol, selectCol);

    }

    public void refreshP() {
        String nameSearch = nameSearchTF.getText();
        String lnameSearch = lnameSearchTF.getText();
        String peselSearch = peselSearchTF.getText();
        try {
            patientData.clear();
            ((PreparedStatement) searchPatient).setString(1, "%" + nameSearch + "%");
            ((PreparedStatement) searchPatient).setString(2, "%" + lnameSearch + "%");
            ((PreparedStatement) searchPatient).setString(3, "%" + peselSearch + "%");
            ((PreparedStatement) searchPatient).executeQuery();
            ResultSet res = ((PreparedStatement) searchPatient).executeQuery();
            while(res.next()) {
                int idS = res.getInt("p_id");
                String nameS = res.getString("imie");
                String lnameS = res.getString("nazwisko");
                String peselS = res.getString("pesel");
                String dateS = res.getString("data_urodzenia");
                Patient patient = new Patient(idS, nameS, lnameS, peselS, dateS);
                patient.setShowResults(this);
                patientData.add(patient);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setPatientId (int id) {
        this.patientId=id;
        showResults();
    }


    private void selectDoctor(){
        borderPane.setTop(null);
        borderPane.setCenter(null);
        borderPane.setBottom(null);

        VBox vBox = new VBox();
        borderPane.setTop(vBox);

        if(userType.equals("admin")) {
            Button back = new Button("Powrot");
            back.setOnAction(event -> patientOrDoctor());
            vBox.getChildren().add(back);
        }

        doctorTable = new TableView<Staff>();
        try {
            searchDoctor = con.prepareStatement(
                    "SELECT staff_id, imie, nazwisko, specjalizacja FROM pracownicy " +
                            "WHERE UPPER(imie) LIKE UPPER(?) AND UPPER(nazwisko) LIKE UPPER(?) " +
                            "AND typ=\"lekarz\" " +
                            "AND (UPPER(specjalizacja) LIKE UPPER(?))");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        Label searchDoctorL = new Label("Szukanie Lekarza");
        searchDoctorL.setFont(new Font("Arial", 20));
        Label nameSL = new Label("Imie:");
        Label lnameSL = new Label("Nazwisko:");
        Label typeSL = new Label("Specjalizacja:");
        nameSearchTF = new TextField();
        nameSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refreshD());
        lnameSearchTF = new TextField();
        lnameSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refreshD());
        typeSearchTF = new TextField();
        typeSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refreshD());
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(4);
        gridPane.setHgap(10);
        gridPane.add(searchDoctorL, 0, 3, 3,1);
        gridPane.add(nameSL, 0, 4);
        gridPane.add(lnameSL, 1, 4);
        gridPane.add(typeSL, 2, 4);
        gridPane.add(nameSearchTF, 0, 5);
        gridPane.add(lnameSearchTF, 1, 5);
        gridPane.add(typeSearchTF, 2, 5);
        vBox.getChildren().add(gridPane);
        createDoctorTable();
        refreshD();
        borderPane.setCenter(doctorTable);
    }

    private void refreshD () {
        String nameSearch = nameSearchTF.getText();
        String lnameSearch = lnameSearchTF.getText();
        String typeSearch = typeSearchTF.getText();
        try {
            doctorData.clear();
            ((PreparedStatement) searchDoctor).setString(1, "%" + nameSearch + "%");
            ((PreparedStatement) searchDoctor).setString(2, "%" + lnameSearch + "%");
            ((PreparedStatement) searchDoctor).setString(3, "%" + typeSearch + "%");
            ((PreparedStatement) searchDoctor).executeQuery();
            ResultSet res = ((PreparedStatement) searchDoctor).executeQuery();
            while(res.next()) {
                int idS = res.getInt(1);
                String nameS = res.getString(2);
                String lnameS = res.getString(3);
                String specS = res.getString(4);
                Staff doc = new Staff(idS, nameS, lnameS, "lekarz", specS);
                doc.setShowResults(this);
                doctorData.add(doc);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    private  void createDoctorTable() {
        doctorTable.setEditable(false);
        doctorTable.setMinSize(1200,735);
        TableColumn idCol = new TableColumn("ID");
        idCol.setPrefWidth(70);
        idCol.setCellValueFactory(new PropertyValueFactory<Patient, Integer>("id"));

        TableColumn nameCol = new TableColumn("Imie");
        nameCol.setPrefWidth(120);
        nameCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("firstName"));

        TableColumn lnameCol = new TableColumn("Nazwisko");
        lnameCol.setPrefWidth(120);
        lnameCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("lastName"));

        TableColumn specCol = new TableColumn("Specjalizacja");
        specCol.setPrefWidth(120);
        specCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("spec"));

        TableColumn selectCol = new TableColumn("Wybierz");
        selectCol.setPrefWidth(90);
        selectCol.setCellValueFactory(new PropertyValueFactory<Staff, String>("selectB"));

        doctorTable.setItems(doctorData);
        doctorTable.getColumns().addAll(idCol, nameCol, lnameCol, specCol, selectCol);
    }

    public void setDoctorId (int id) {
        this.doctorId=id;
        showResults();
    }


}

