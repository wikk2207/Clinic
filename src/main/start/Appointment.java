package main.start;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Appointment extends Tab {
    private Connection con;
    private Button addAppointmentB;
    private Button showAppointmentsB;
    private TabPane tabPane;
    private Tab addAppointmentT;
    private Tab showAppointmentsT;
    private String userType;
    private int id;
    Statement findUserByPeselStmt;

    public Appointment(Connection con, String userType, int id) {
        this.setText("Wizyty");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
        this.con=con;
        this.userType = userType;
        this.id = id;


        tabPane = new TabPane();
        try {
            findUserByPeselStmt = con.prepareStatement("SELECT p_id FROM Pacjenci WHERE pesel=?;");
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

        FlowPane flowPane = new FlowPane();

        addAppointmentT.setContent(flowPane);

        Text t = new Text("Podaj pesel pacjenta");
        TextField textField = new TextField();
        Button b = new Button("Dalej");

        //TODO
        System.out.println(userType);

        if(userType.equals("sekretarka") || userType.equals("admin")) {
            System.out.println("Trzeba wybrac pacjenta");
            flowPane.getChildren().addAll(t, textField,b);
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                   findUserByPesel(textField.getText());
                    flowPane.getChildren().removeAll(t,textField,b);
                    //TODO wybieranie specjalizacji
                }
            });
        } else {
            //TODO wybieranie specjalizacji
        }
        //flowPane.getChildren().add(t);
        //t.setText("Wybierz dzień");




    }


    private void findUserByPesel(String pesel) {
        try {
            System.out.println(pesel);
            //int peselInt = Integer.parseInt(pesel);
            //System.out.println(peselInt);
            ((PreparedStatement)findUserByPeselStmt).setString(1, pesel);
            ResultSet rs = ((PreparedStatement) findUserByPeselStmt).executeQuery();
            //TODO dodac sprawdzenie czy znalazl i zachowanie jesli nie znalazl
            rs.next();
            id = rs.getInt(1);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
