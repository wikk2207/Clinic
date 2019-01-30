package staff;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import patients.Patient;

import java.sql.*;

public class SelectDoctorPane extends BorderPane {
    private VBox vBoxTop;
    private TableView<Staff> table;
    private Connection con;
    private Statement searchDoctor;
    private TextField firstNameSearchTF;
    private TextField nameSearchTF;
    private TextField typeSearchTF;
    private GridPane gridPane;
    private ObservableList<Staff> data;
    private SearchDoctor parentTab;
    private FlowPane buttonPane;
    private Button returnButton;


    public SelectDoctorPane(Connection con, SearchDoctor parentTab, Button button) {
        this.con = con;
        this.parentTab = parentTab;
        this.returnButton = button;
        init();
        buttonPane.getChildren().add(returnButton);
    }

    public SelectDoctorPane(Connection con, SearchDoctor parentTab) {
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
            searchDoctor = con.prepareStatement(
                    "SELECT staff_id, imie, nazwisko, specjalizacja FROM pracownicy " +
                            "WHERE UPPER(imie) LIKE UPPER(?) AND UPPER(nazwisko) LIKE UPPER(?) " +
                            "AND typ=\"lekarz\" " +
                            "AND (UPPER(specjalizacja) LIKE UPPER(?))");
        }
        catch (SQLException e) {
            System.out.println(e);
        }

        data = FXCollections.observableArrayList();

        Label searchDoctorL = new Label("Szukanie Lekarza");
        searchDoctorL.setFont(new Font("Arial", 20));
        Label nameSL = new Label("Imie:");
        Label lnameSL = new Label("Nazwisko:");
        Label typeSL = new Label("Specjalizacja:");
        firstNameSearchTF = new TextField();
        firstNameSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refresh());
        nameSearchTF = new TextField();
        nameSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refresh());
        typeSearchTF = new TextField();
        typeSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refresh());


        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(4);
        gridPane.setHgap(10);
        gridPane.add(searchDoctorL, 0, 3, 3,1);
        gridPane.add(nameSL, 0, 4);
        gridPane.add(lnameSL, 1, 4);
        gridPane.add(typeSL, 2, 4);
        gridPane.add(firstNameSearchTF, 0, 5);
        gridPane.add(nameSearchTF, 1, 5);
        gridPane.add(typeSearchTF, 2, 5);

        vBoxTop.getChildren().addAll(buttonPane,gridPane);
        createTable();
        refresh();
        this.setCenter(table);
    }


    private void refresh() {
        String nameSearch = firstNameSearchTF.getText();
        String lnameSearch = nameSearchTF.getText();
        String typeSearch = typeSearchTF.getText();
        try {
            data.clear();
            ((PreparedStatement) searchDoctor).setString(1, "%" + nameSearch + "%");
            ((PreparedStatement) searchDoctor).setString(2, "%" + lnameSearch + "%");
            ((PreparedStatement) searchDoctor).setString(3, "%" + typeSearch + "%");
            ((PreparedStatement) searchDoctor).executeQuery();
            ResultSet res = ((PreparedStatement) searchDoctor).executeQuery();
            while(res.next()) {
                int idS = res.getInt(1);
                String nameS = res.getString(2);
                String lnameS = res.getString(3);
                String specS = res.getString(4);
                Staff doc = new Staff(idS, nameS, lnameS, "lekarz", specS);
                doc.setParentTab(parentTab);
                data.add(doc);
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

        TableColumn specCol = new TableColumn("Specjalizacja");
        specCol.setPrefWidth(120);
        specCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("spec"));

        TableColumn selectCol = new TableColumn("Wybierz");
        selectCol.setPrefWidth(90);
        selectCol.setCellValueFactory(new PropertyValueFactory<Staff, String>("selectB"));

        table.setItems(data);
        table.getColumns().addAll(idCol, nameCol, lnameCol, specCol, selectCol);

    }
}
