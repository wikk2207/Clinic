package visits;


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
import staff.SearchDoctor;
import patients.SearchPatient;
import patients.SelectPatientPane;
import staff.SelectDoctorPane;
import staff.Staff;

import java.sql.*;
import java.time.LocalDate;

import java.util.ArrayList;

public class Appointment extends Tab {
    private Connection con;
    private TabPane tabPane;
    private Tab addAppointmentT;
    private Tab showAppointmentsT;
    private String userType;
    private int id;
    private Statement stmt;
    private String specialization;
    private BorderPane addBorderPane;
    private BorderPane showBorderPane;
    private String date;
    String imieNazwiskoPacjenta;
    int day, month, year;
    private int idDoctor;
    SelectPatientPane selectPatientPane;
    SelectDoctorPane selectDoctorPane;
    AddAppointment addAppointment;
    ShowAppointment plannedVisits;
    ShowAppointment previousVisits;

    public Appointment(Connection con, String userType, int id) {
        this.setText("Wizyty");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
        this.con=con;
        this.userType = userType;
        this.id = id;


        tabPane = new TabPane();

        if(!userType.equals("doctor")) {
            addAppointment = new AddAppointment(userType,con, id);
            tabPane.getTabs().add(addAppointment);
        }
        plannedVisits = new ShowAppointment("Zaplanowane", true, con, userType, id);
        previousVisits = new ShowAppointment("Zrealizowane", false, con, userType, id);

        tabPane.getTabs().addAll(plannedVisits, previousVisits);

        this.setContent(tabPane);
    }

}
