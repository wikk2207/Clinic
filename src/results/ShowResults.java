package results;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import staff.SearchDoctor;
import patients.SearchPatient;
import patients.Patient;
import patients.SelectPatientPane;
import staff.SelectDoctorPane;
import staff.Staff;

import java.sql.*;

public class ShowResults extends Tab implements SearchPatient, SearchDoctor {
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
    private SelectPatientPane selectPatientPane;
    private SelectDoctorPane selectDoctorPane;
    Button returnToPatientOrDoctor1;
    Button returnToPatientOrDoctor2;



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
            showResults(false);
        } else if (userType.equals("doctor")){
            editable = true;
            doctorId=userId;
            showResults(true);
        } else if (userType.equals("secretary")) {
            editable = false;
            selectPatientPane = new SelectPatientPane(con, this);
            this.setContent(selectPatientPane);
        } else if(userType.equals("admin")) {
            editable = true;
            returnToPatientOrDoctor1 =  new Button("Powrot");
            returnToPatientOrDoctor2 = new Button("Powrot");
            returnToPatientOrDoctor1.setOnAction(event -> patientOrDoctor());
            returnToPatientOrDoctor2.setOnAction(event -> patientOrDoctor());
            selectPatientPane = new SelectPatientPane(con, this, returnToPatientOrDoctor1);
            selectDoctorPane = new SelectDoctorPane(con, this, returnToPatientOrDoctor2);
            patientOrDoctor();
        }



    }


    private void showResults(boolean ifdoctor) {
        borderPane.setTop(null);
        borderPane.setCenter(null);

        Button backButton = new Button("Powrot");
        backButton.setOnAction(event -> {
            showResults(ifdoctor);
        });

        if(userType.equals("secretary")) {
            Button back = new Button("Powrot");
            back.setOnAction(event -> {
                this.setContent(selectPatientPane);
            });
            borderPane.setTop(back);
        } else if( userType.equals("admin"))  {
            Button back = new Button("Powrot");
            back.setOnAction(event -> {
                patientOrDoctor();
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

        TableColumn deleteColumn = new TableColumn("Usun");
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("deleteB"));
        try {
            Statement statement;
            if(userType.equals("user") || userType.equals("secretary") || userType.equals("admin") && !ifdoctor) {
                statement = con.prepareStatement("SELECT wb_id, data_wystawienia, id_lekarza, imie, nazwisko FROM wyniki_badan A JOIN pracownicy B ON A.id_lekarza = B.staff_id WHERE id_pacjenta=?");
                ((PreparedStatement) statement).setInt(1, patientId);
            } else if(userType.equals("doctor")  || userType.equals("admin") && ifdoctor) {
                statement = con.prepareStatement("SELECT wb_id, data_wystawienia, id_pacjenta, imie, nazwisko FROM wyniki_badan A JOIN pacjenci B ON A.id_pacjenta = B.p_id WHERE id_lekarza=?");
                ((PreparedStatement) statement).setInt(1, doctorId);
            } else {
                statement = con.prepareStatement("SELECT wb_id, data_wystawienia, id_lekarza, id_pacjenta, B.imie, B.nazwisko, C.imie, C.nazwisko " +
                        "FROM wyniki_badan A JOIN pacjenci B ON A.id_pacjenta = B.p_id JOIN pracownicy C ON A.id_lekarza = C.staff_id WHERE A.id_pacjenta=? AND A.id_lekarza=?");
                ((PreparedStatement) statement).setInt(1, patientId);
                ((PreparedStatement) statement).setInt(2, doctorId);
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

                if(userType.equals("user") || userType.equals("secretary") || !ifdoctor) {
                    doctorId = rs.getInt(3);
                    firstnameD = rs.getString(4);
                    nameD=rs.getString(5);
                } else if(userType.equals("doctor") || ifdoctor) {
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
            } else if (userType.equals("admin") && ifdoctor){
                table.getColumns().addAll(dateColumn, firstNameColumnP, nameColumnP, selectColumn, deleteColumn);
            } else if(userType.equals("admin") && !ifdoctor) {
                table.getColumns().addAll(dateColumn, firstNameColumnD, nameColumnD, selectColumn, deleteColumn);
            }
            vbox.getChildren().add(table);


        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private void patientOrDoctor() {
        this.setContent(borderPane);

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
                this.setContent(selectPatientPane);
            }
            if(rb2.isSelected()) {
                this.setContent(selectDoctorPane);
            }

        });

        vBox.getChildren().addAll(text,rb1, rb2,button);

    }

    @Override
    public void setPatientId (int id) {
        this.patientId=id;
        this.setContent(borderPane);
        showResults(false);
    }

    @Override
    public void setDoctorId (int id) {
        this.doctorId=id;
        this.setContent(borderPane);
        showResults(true);
    }


}

