package patients;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import staff.Staff;

import java.sql.*;


public class SelectPatientPane extends BorderPane {
    private VBox vBoxTop;
    private TableView<Patient> table;
    private Connection con;
    private Statement searchPatient;
    private TextField firstNameSearchTF;
    private TextField nameSearchTF;
    private TextField peselSearchTF;
    private GridPane gridPane;
    private ObservableList<Patient> data;
    private SearchPatient parentTab;
    private FlowPane buttonPane;
    private Button returnButton;


    public SelectPatientPane(Connection con, SearchPatient parentTab, Button button) {
        this.con = con;
        this.parentTab = parentTab;
        this.returnButton = button;
        init();
        buttonPane.getChildren().add(returnButton);
    }

    public SelectPatientPane(Connection con, SearchPatient parentTab) {
        this.con = con;
        this.parentTab = parentTab;
        init();
    }

    private void init() {

        vBoxTop = new VBox();
        this.setTop(vBoxTop);

        buttonPane = new FlowPane();

        table = new TableView<>();
        try {
            searchPatient = con.prepareStatement(
                    "SELECT p_id, imie, nazwisko, pesel, data_urodzenia FROM pacjenci " +
                            "WHERE UPPER(imie) LIKE UPPER(?) AND UPPER(nazwisko) LIKE UPPER(?) AND UPPER(pesel) LIKE UPPER(?)");
        }
        catch (SQLException e) {
            System.out.println(e);
        }

        data = FXCollections.observableArrayList();

        Label searchPatientL = new Label("Szukanie Pacjenta");
        searchPatientL.setFont(new Font("Arial", 20));
        Label nameSL = new Label("Imie:");
        Label lnameSL = new Label("Nazwisko:");
        Label peselSL = new Label("Pesel:");
        firstNameSearchTF = new TextField();
        firstNameSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refresh());
        nameSearchTF = new TextField();
        nameSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refresh());
        peselSearchTF = new TextField();
        peselSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refresh());


        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(4);
        gridPane.setHgap(10);
        gridPane.add(searchPatientL, 0, 3, 3,1);
        gridPane.add(nameSL, 0, 4);
        gridPane.add(lnameSL, 1, 4);
        gridPane.add(peselSL, 2, 4);
        gridPane.add(firstNameSearchTF, 0, 5);
        gridPane.add(nameSearchTF, 1, 5);
        gridPane.add(peselSearchTF, 2, 5);

        vBoxTop.getChildren().addAll(buttonPane,gridPane);
        createTable();
        refresh();
        this.setCenter(table);
    }


    private void refresh() {
        String nameSearch = firstNameSearchTF.getText();
        String lnameSearch = nameSearchTF.getText();
        String peselSearch = peselSearchTF.getText();
        try {
            data.clear();
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
                patient.setParentTab(parentTab);
                data.add(patient);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void createTable() {
        table.setEditable(false);

        table.setMinSize(1200,735);
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

        table.setItems(data);
        table.getColumns().addAll(idCol, nameCol, lnameCol, peselCol, dateCol, selectCol);

    }

}
