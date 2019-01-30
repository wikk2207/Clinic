package visits;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import patients.SearchPatient;
import patients.SelectPatientPane;
import staff.SearchDoctor;
import staff.SelectDoctorPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ShowAppointment extends Tab implements SearchDoctor, SearchPatient {
    BorderPane borderPane;
    boolean planned;
    Connection con;
    String userType;
    SelectPatientPane selectPatientPane;
    SelectDoctorPane selectDoctorPane;
    int patientId;
    int doctorId;
    Button returnToPatientOrDoctor1;
    Button returnToPatientOrDoctor2;
    Button refreshButton;


    ShowAppointment(String title, boolean planned, Connection con, String userType, int userId) {
       super(title);
       setClosable(false);

       this.planned = planned;
       this.con = con;
       this.userType = userType;

       borderPane = new BorderPane();


       if (userType.equals("admin")) {
           returnToPatientOrDoctor1 =  new Button("Powrot");
           returnToPatientOrDoctor2 = new Button("Powrot");
           returnToPatientOrDoctor1.setOnAction(event -> patientOrDoctor());
           returnToPatientOrDoctor2.setOnAction(event -> patientOrDoctor());
           selectPatientPane = new SelectPatientPane(con, this, returnToPatientOrDoctor1);
           selectDoctorPane = new SelectDoctorPane(con, this, returnToPatientOrDoctor2);
           patientOrDoctor();
       } else if (userType.equals("secretary")) {
           selectPatientPane = new SelectPatientPane(con, this);
           this.setContent(selectPatientPane);
       } else if (userType.equals("doctor")) {
           doctorId = userId;
           showDoctor();
       } else if (userType.equals("user")) {
           patientId = userId;
           showPatient();
       }
    }

    private void patientOrDoctor() {
        borderPane.setCenter(null);
        borderPane.setTop(null);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        borderPane.setCenter(vBox);

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
                this.setContent(selectPatientPane);
            }
            if(rb2.isSelected()) {
                this.setContent(selectDoctorPane);
            }

        });

        vBox.getChildren().addAll(text,rb1, rb2,button);
        this.setContent(borderPane);

    }

    private void showPatient() {
        TableView table = new TableView();
        table.setPrefWidth(700);
        table.setEditable(false);

        refreshButton = new Button("Odswiez");
        refreshButton.setOnAction(event -> showPatient());


        if(userType.equals("secretary") || userType.equals("admin")) {
            Button returnButton = new Button("Powrot");
            returnButton.setOnAction(event -> {
                setContent(selectPatientPane);
            });
            HBox hBox = new HBox();
            hBox.getChildren().addAll(returnButton, returnButton);
            borderPane.setTop(hBox);
        } else {
            borderPane.setTop(refreshButton);
        }

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
                statement = con.prepareStatement("SELECT data_wizyty, poczatek, imie, nazwisko, specjalizacja, w_id FROM wizyty A JOIN godziny B ON A.godzina_wizyty=B.g_id JOIN pracownicy C ON A.id_lekarza=C.staff_id " +
                        "WHERE id_pacjenta=? AND data_wizyty>=curdate() ORDER BY data_wizyty, poczatek;");
            } else {
                statement = con.prepareStatement("SELECT data_wizyty, poczatek, imie, nazwisko, specjalizacja, w_id FROM wizyty A JOIN godziny B ON A.godzina_wizyty=B.g_id JOIN pracownicy C ON A.id_lekarza=C.staff_id " +
                        "WHERE id_pacjenta=? AND data_wizyty<curdate() ORDER BY data_wizyty, poczatek DESC;");
            }
            ((PreparedStatement) statement).setInt(1, patientId);
            ResultSet rs = ((PreparedStatement) statement).executeQuery();
            while(rs.next()) {
                String name = rs.getString(3) + " " + rs.getString(4);
                data.add(new VisitTime(rs.getString(1), rs.getString(2),0,patientId, con, name, rs.getString(5), null, rs.getInt(6)));
            }
            table.setItems(data);
            if(planned) {
                table.getColumns().addAll(dateColumn, timeColumn, nameColumn, specColumn, deleteColumn);
            } else {
                table.getColumns().addAll(dateColumn, timeColumn, nameColumn, specColumn);
            }
            borderPane.setCenter(table);
            this.setContent(borderPane);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void showDoctor() {
        TableView table = new TableView();
        table.setPrefWidth(700);
        table.setEditable(false);

        refreshButton = new Button("Odswiez");
        refreshButton.setOnAction(event -> {
            showDoctor();
        });


        if(userType.equals("secretary") || userType.equals("admin")) {
            Button returnButton = new Button("Powrot");
            returnButton.setOnAction(event -> setContent(selectDoctorPane));
            HBox hBox = new HBox();
            hBox.getChildren().addAll(returnButton, returnButton);
            borderPane.setTop(hBox);
        } else {
            borderPane.setTop(refreshButton);
        }

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
                statement = con.prepareStatement("SELECT data_wizyty, poczatek, imie, nazwisko, w_id FROM wizyty A JOIN godziny B ON A.godzina_wizyty=B.g_id JOIN pacjenci C ON A.id_pacjenta=C.p_id " +
                        "WHERE id_lekarza=? AND data_wizyty>=curdate() ORDER BY data_wizyty, poczatek;");
            } else {
                statement = con.prepareStatement("SELECT data_wizyty, poczatek, imie, nazwisko, w_id FROM wizyty A JOIN godziny B ON A.godzina_wizyty=B.g_id JOIN pacjenci C ON A.id_pacjenta=C.p_id " +
                        "WHERE id_lekarza=? AND data_wizyty<curdate() ORDER BY data_wizyty DESC, poczatek DESC;");
            }
            ((PreparedStatement) statement).setInt(1, doctorId);
            ResultSet rs = ((PreparedStatement) statement).executeQuery();
            while(rs.next()) {
                String name = rs.getString(3) + " " + rs.getString(4);
                data.add(new VisitTime(rs.getString(1), rs.getString(2),doctorId,0, con, null, null, name, rs.getInt(5)));
            }
            table.setItems(data);
            if(planned && userType.equals("admin")) {
                table.getColumns().addAll(dateColumn, timeColumn, nameColumn,  deleteColumn);
            } else {
                table.getColumns().addAll(dateColumn, timeColumn, nameColumn);
            }
           borderPane.setCenter(table);
            setContent(borderPane);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void setPatientId(int id) {
        this.patientId = id;
        showPatient();
    }

    @Override
    public void setDoctorId(int id) {
        this.doctorId = id;
        showDoctor();
    }
}
