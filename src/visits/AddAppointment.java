package visits;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
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

public class AddAppointment extends Tab implements SearchDoctor, SearchPatient {
    BorderPane borderPane;
    VBox vBoxCenter;
    VBox vBoxTop;
    String userType;
    SelectDoctorPane selectDoctorPane;
    SelectPatientPane selectPatientPane;
    Connection con;
    int patientId;
    int doctorId;
    int day, month, year;
    private String date;



    AddAppointment(String userType, Connection con, int id) {
       super("Dodaj wizytę");
       this.userType = userType;
       this.con = con;


       this.setClosable(false);

       borderPane = new BorderPane();
       vBoxCenter = new VBox();
       vBoxCenter.setAlignment(Pos.CENTER);
       vBoxCenter.setSpacing(10);

       vBoxTop = new VBox();
       vBoxTop.setAlignment(Pos.TOP_LEFT);
       vBoxTop.setSpacing(10);
       //todo ????
        vBoxTop.setPadding(new Insets(7,7,7,7));

        borderPane.setCenter(vBoxCenter);
        borderPane.setTop(vBoxTop);
        this.setContent(borderPane);

        if(userType.equals("secretary") || userType.equals("admin")) {
            selectPatientPane = new SelectPatientPane(con,this);
            this.setContent(selectPatientPane);
            //findUser(vBoxCenter, vBox);
        } else if(userType.equals("user")) {
            selectDoctorPane = new SelectDoctorPane(con, this);
            patientId = id;
            this.setContent(selectDoctorPane);
        }
    }

    private void showTerms() {
        vBoxTop.getChildren().clear();
        borderPane.setCenter(null);

        Button backButton = new Button("Powrót");
        backButton.setOnAction(event -> {
            setContent(selectDoctorPane);
        });
        vBoxTop.getChildren().add(backButton);

        Text textChoose = new Text("Wybierz datę");
        DatePicker datePicker = new DatePicker();
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });

        Button nextButton = new Button("Szukaj");
        nextButton.setOnAction(event -> {

            if(datePicker.getValue()==null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uwaga!");
                alert.setHeaderText(null);
                alert.setContentText("Nie wybrano daty!");
                alert.showAndWait();
            } else {
                LocalDate localDate = datePicker.getValue();
                day = localDate.getDayOfMonth();
                month = localDate.getMonthValue();
                year = localDate.getYear();
                date = localDate.toString();
                createVisitTable();
            }
        });

        vBoxTop.getChildren().addAll(textChoose,datePicker,nextButton);
        setContent(borderPane);



    }

    private void createVisitTable() {

        TableView table = new TableView();
        table.setPrefWidth(400);
        table.setPrefHeight(500);
        table.setEditable(false);
        ObservableList<VisitTime> data = FXCollections.observableArrayList();

        TableColumn dateColumn = new TableColumn("Data");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn timeColumn = new TableColumn("Godzina");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn actionColumn = new TableColumn("Wybierz");
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("chooseB"));

        try{
            Statement statement = con.prepareStatement("SELECT poczatek FROM godziny WHERE g_id NOT IN(SELECT godzina_wizyty FROM wizyty WHERE id_lekarza=? AND data_wizyty=\'"+year+"-"+month+"-"+day+"\');");
            ((PreparedStatement) statement).setInt(1, doctorId);
            ResultSet rs = ((PreparedStatement) statement).executeQuery();
            while (rs.next()) {
                String beg = rs.getString(1);
                data.add(new VisitTime(date, beg, doctorId,patientId, con, null, null, null, 0));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        table.setItems(data);
        table.getColumns().addAll(dateColumn,timeColumn,actionColumn);


        borderPane.setCenter(table);


    }


    @Override
    public void setDoctorId(int id) {
        this.doctorId = id;
        showTerms();
    }

    @Override
    public void setPatientId(int id) {
        this.patientId = id;
        Button button = new Button("Powrot");
        button.setOnAction(event -> {
            selectPatientPane = new SelectPatientPane(con, this);
            setContent(selectPatientPane);
        });
        selectDoctorPane = new SelectDoctorPane(con, this, button);
        setContent(selectDoctorPane);
    }
}
