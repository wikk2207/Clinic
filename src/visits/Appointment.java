package visits;

import javafx.scene.control.*;

import java.sql.*;


public class Appointment extends Tab {
    private Connection con;
    private TabPane tabPane;

    private String userType;
    private int id;

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
