package main.start;

import javafx.scene.control.Tab;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Appointment extends Tab {
    private Connection con;

    public Appointment(Connection con) {
        this.setText("Wizyty");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
        this.con=con;

        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT id_lekarza, id_pacjenta FROM Wizyty WHERE godzina_wizyty>15;");
            while (rs.next()) {
                System.out.println(rs.getInt(1)+ " " + rs.getInt(2));
            }

        } catch (Exception e) {
            //
        }

    }
}
