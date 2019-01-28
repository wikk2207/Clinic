package visits;

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
    private final SimpleStringProperty nameDoc;
    private final SimpleStringProperty namePat;
    private final SimpleStringProperty specialization;
    private Button chooseB;
    private Button deleteB;
    private int idDoctor;
    private int idPatient;
    private Connection con;
    private int idVisit;

    VisitTime(String date, String time, int idDoctor, int idPatient, Connection con, String nameDoc, String specialization, String namePat, int idVisit) {
        this.date = new SimpleStringProperty(date);
        this.time = new SimpleStringProperty(time);
        this.nameDoc = new SimpleStringProperty(nameDoc);
        this.specialization = new SimpleStringProperty(specialization);
        this.idDoctor=idDoctor;
        this.idPatient=idPatient;
        this.con=con;
        this.idVisit=idVisit;
        this.namePat= new SimpleStringProperty(namePat);
        chooseB = new Button("X");
        chooseB.setOnAction(event -> {
            addVisit();
        });

        deleteB = new Button("X");
        deleteB.setOnAction(event -> {
            deleleVisit();
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

    @SuppressWarnings("Duplicates")
    private void deleleVisit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Odwołanie wizyty");
        alert.setHeaderText(null);
        alert.setContentText("Czy na pewno chcesz odwolac wizyte "+date.get()+" o godzinie " +time.get()+"?");
        alert.setHeight(300);
        alert.setWidth(450);

        try {
            Statement stmt = con.prepareStatement("DELETE FROM wizyty WHERE w_id=?");
            ((PreparedStatement) stmt).setInt(1, idVisit);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                ((PreparedStatement) stmt).executeUpdate();
                deleteB.setDisable(true);
            } else {}
        } catch (Exception e) {
            System.out.println(e);
        }
    };

    public String getNameDoc() {
        return nameDoc.get();
    }


    public String getSpecialization() {
        return specialization.get();
    }
    public Button getDeleteB() {
        return deleteB;
    }

    public String getNamePat() {
        return namePat.get();
    }
}
