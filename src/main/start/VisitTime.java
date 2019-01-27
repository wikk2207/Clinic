package main.start;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;


public class VisitTime {
    private final SimpleStringProperty date;
    private final SimpleStringProperty time;
    private Button chooseB;
    private int idDoctor;
    private int idPatient;
    private Connection con;


    VisitTime(String date, String time, int idDoctor, int idPatient, Connection con) {
        this.date = new SimpleStringProperty(date);
        this.time = new SimpleStringProperty(time);
        this.idDoctor=idDoctor;
        this.idPatient=idPatient;
        this.con=con;
        chooseB = new Button("X");
        chooseB.setOnAction(event -> {
            addVisit();
        });
    }

    public String getDate() {
        return date.get();
    }

    public void setDate (String date) {
        this.date.set(date);
    }

    public String getTime() {
        return time.get();
    }

    public void setTime (String time) {
        this.time.set(time);
    }

    public Button getChooseB() {
        return chooseB;
    }

    public void setChooseB(Button chooseB) {
        this.chooseB = chooseB;
    }

    private void addVisit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Nowa wizyta");
        alert.setHeaderText(null);
        alert.setContentText("Czy na pewno chcesz umówić się na wizytę "+date.get()+" o godzinie " +time.get()+"?");
        alert.setHeight(300);
        alert.setWidth(450);

        int idTime;
        try {
            Statement stmt = con.prepareStatement("SELECT g_id FROM godziny WHERE poczatek=?");
            ((PreparedStatement) stmt).setString(1, time.get());
            ResultSet resultSet = ((PreparedStatement) stmt).executeQuery();
            if(resultSet.next()) {
                idTime = resultSet.getInt(1);
                Statement statement = con.prepareStatement("INSERT INTO wizyty(id_lekarza, id_pacjenta, data_wizyty, godzina_wizyty) VALUES(?,?,?,?);");
                ((PreparedStatement) statement).setInt(1, idDoctor);
                ((PreparedStatement) statement).setInt(2,idPatient);
                ((PreparedStatement) statement).setString(3, date.get());
                ((PreparedStatement) statement).setInt(4,idTime);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    ((PreparedStatement) statement).executeUpdate();
                    chooseB.setDisable(true);
                } else {}
            } else {
                System.out.println("Error");
            }


        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
