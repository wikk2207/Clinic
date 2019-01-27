package patients;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import staff.Staff;

import java.sql.*;

public class PatientList extends Tab {
    private TableView<Patient> table = new TableView<Patient>();
    private ObservableList<Patient> data = FXCollections.observableArrayList();
    private Statement stmt = null;
    private Button addPatientB;
    private Button refreshB;
    private BorderPane borderPane;
    private GridPane gridPane;
    private Connection conn;
    private Statement selectPatient;
    private static Statement insertPatient;
    private Statement updatePatientName;
    private Statement updatePatientLName;
    private Statement updatePatientPesel;
    private Statement updatePatientDate;
    private static Statement deletePatient;
    private String user = null;
    private TextField nameTF;
    private TextField lnameTF;
    private TextField peselTF;
    private TextField dateTF;
    public PatientList(Connection conn, String user) {
        this.conn = conn;
        this.user = user;
        try {
            selectPatient = conn.prepareStatement("SELECT p_id, imie, nazwisko, pesel, data_urodzenia FROM pacjenci");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            deletePatient = conn.prepareStatement("DELETE FROM pacjenci WHERE p_id = ?");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            insertPatient = conn.prepareStatement("INSERT INTO pacjenci(imie, nazwisko, pesel, data_urodzenia) VALUES (?,?,?,?)");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            updatePatientName = conn.prepareStatement("UPDATE pacjenci SET imie = ? WHERE p_id = ?");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            updatePatientLName = conn.prepareStatement("UPDATE pacjenci SET nazwisko = ? WHERE p_id = ?");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            updatePatientPesel = conn.prepareStatement("UPDATE pacjenci SET pesel = ? WHERE p_id = ?");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            updatePatientDate = conn.prepareStatement("UPDATE pacjenci SET data_urodzenia = ? WHERE p_id = ?");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        this.setText("Pacjenci");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
        createTable();
        Label addPatientL = new Label("Dodawanie Pacjenta");
        addPatientL.setFont(new Font("Arial", 17));
        Label nameL = new Label("Imie:");
        Label lnameL = new Label("Nazwisko:");
        Label peselL = new Label("Pesel:");
        Label dateL = new Label("Data Urodzenia:");
        nameTF = new TextField();
        lnameTF = new TextField();
        peselTF = new TextField();
        dateTF = new TextField();
        addPatientB = new Button("Dodaj pacjenta");
        addPatientB.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> insert(nameTF.getText(), lnameTF.getText(), peselTF.getText(), dateTF.getText()));
        refreshB = new Button("Odswiez");
        refreshB.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> refresh());
        borderPane = new BorderPane();
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 0, 5, 0));
        gridPane.setVgap(4);
        gridPane.setHgap(4);
        gridPane.add(addPatientL, 0, 0);
        gridPane.add(nameL, 0, 1);
        gridPane.add(lnameL, 1, 1);
        gridPane.add(peselL, 2, 1);
        gridPane.add(dateL, 3, 1);
        gridPane.add(nameTF, 0, 2);
        gridPane.add(lnameTF, 1, 2);
        gridPane.add(peselTF, 2, 2);
        gridPane.add(dateTF, 3, 2);
        gridPane.add(addPatientB, 4, 2);
        borderPane.setTop(gridPane);
        borderPane.setCenter(table);
        this.setContent(borderPane);
        refresh();
    }

    public void createTable() {
        table.setEditable(true);
        table.setMinSize(1200,735);
        TableColumn idCol = new TableColumn("ID");
        idCol.setPrefWidth(70);
        idCol.setCellValueFactory(new PropertyValueFactory<Patient, Integer>("id"));
        TableColumn nameCol = new TableColumn("Imie");
        nameCol.setPrefWidth(120);
        nameCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("firstName"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Patient, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Patient, String> t) {
                        ((Patient) t.getTableView().getItems().get(t.getTablePosition().getRow())).setFirstName(t.getNewValue());
                        try {
                            ((PreparedStatement) updatePatientName).setInt(2, ((Patient) t.getTableView().getItems().get(t.getTablePosition().getRow())).getId());
                            ((PreparedStatement) updatePatientName).setString(1, t.getNewValue());
                            ((PreparedStatement) updatePatientName).executeUpdate();
                        }
                        catch(SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
        TableColumn lnameCol = new TableColumn("Nazwisko");
        lnameCol.setPrefWidth(120);
        lnameCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("lastName"));
        lnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lnameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Patient, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Patient, String> t) {
                        ((Patient) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLastName(t.getNewValue());
                        try {
                            ((PreparedStatement) updatePatientLName).setInt(2, ((Patient) t.getTableView().getItems().get(t.getTablePosition().getRow())).getId());
                            ((PreparedStatement) updatePatientLName).setString(1, t.getNewValue());
                            ((PreparedStatement) updatePatientLName).executeUpdate();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
        TableColumn typeCol = new TableColumn("Pesel");
        typeCol.setPrefWidth(120);
        typeCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("pesel"));
        typeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        typeCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Patient, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Patient, String> t) {
                        ((Patient) t.getTableView().getItems().get(t.getTablePosition().getRow())).setPesel(t.getNewValue());
                        try {
                            ((PreparedStatement) updatePatientPesel).setInt(2, ((Patient) t.getTableView().getItems().get(t.getTablePosition().getRow())).getId());
                            ((PreparedStatement) updatePatientPesel).setString(1, t.getNewValue());
                            ((PreparedStatement) updatePatientPesel).executeUpdate();
                        }
                        catch(SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
        TableColumn specCol = new TableColumn("Data Urodzenia");
        specCol.setPrefWidth(120);
        specCol.setCellValueFactory(new PropertyValueFactory<Patient, String>("date"));
        specCol.setCellFactory(TextFieldTableCell.forTableColumn());
        specCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Patient, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Patient, String> t) {
                        ((Patient) t.getTableView().getItems().get(t.getTablePosition().getRow())).setDate(t.getNewValue());
                        try {
                            ((PreparedStatement) updatePatientDate).setInt(2, ((Patient) t.getTableView().getItems().get(t.getTablePosition().getRow())).getId());
                            ((PreparedStatement) updatePatientDate).setString(1, t.getNewValue());
                            ((PreparedStatement) updatePatientDate).executeUpdate();
                        }
                        catch(SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
        if(user == "admin") {
            TableColumn delCol = new TableColumn("Akcja");
            delCol.setPrefWidth(90);
            delCol.setCellValueFactory(new PropertyValueFactory<Staff, String>("deleteB"));
            table.setItems(data);
            table.getColumns().addAll(idCol, nameCol, lnameCol, typeCol, specCol, delCol);
        } else {
            table.setItems(data);
            table.getColumns().addAll(idCol, nameCol, lnameCol, typeCol, specCol);
        }
    }

    public void refresh() {
        try {
            data.clear();
            ResultSet res = ((PreparedStatement) selectPatient).executeQuery();
            while(res.next()) {
                int idS = res.getInt("p_id");
                String nameS = res.getString("imie");
                String lnameS = res.getString("nazwisko");
                String peselS = res.getString("pesel");
                String dateS = res.getString("data_urodzenia");
                data.add(new Patient(idS, nameS, lnameS, peselS, dateS));
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insert(String name, String lname, String pesel, String date) {
        if(nameTF.getText().trim().isEmpty() || lnameTF.getText().trim().isEmpty() || peselTF.getText().trim().isEmpty() || dateTF.getText().trim().isEmpty()) {
            Text errorL = new Text("Pola nie moga byc puste!");
            errorL.setFill(Color.RED);
            gridPane.add(errorL,5,2);
        }
        else {
            try {
                ((PreparedStatement) insertPatient).setString(1, name);
                ((PreparedStatement) insertPatient).setString(2, lname);
                ((PreparedStatement) insertPatient).setString(3, pesel);
                ((PreparedStatement) insertPatient).setString(4, date);
                ((PreparedStatement) insertPatient).executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void deletePatient(int id) {
        try {
            ((PreparedStatement) deletePatient).setInt(1, id);
            ((PreparedStatement) deletePatient).executeUpdate();
        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

}
