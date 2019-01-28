package staff;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.sql.*;

public class StaffList extends Tab {
    private TableView<Staff> table = new TableView<Staff>();
    private ObservableList<Staff> data = FXCollections.observableArrayList();
    private Statement stmt = null;
    private Button addStaffB;
    private Button refreshB;
    private BorderPane borderPane;
    private GridPane gridPane;
    private Connection conn;
    private Statement selectStaff;
    private static Statement insertStaff;
    private Statement updateStaffName;
    private Statement updateStaffLName;
    private Statement updateStaffType;
    private Statement updateStaffSpec;
    private static Statement deleteStaff;
    private TextField nameSearchTF;
    private TextField lnameSearchTF;
    private ChoiceBox typeCB;
    private ChoiceBox specCB;
    public StaffList(Connection conn) {
        this.conn = conn;
        try {
            selectStaff = conn.prepareStatement("SELECT staff_id, imie, nazwisko, typ, specjalizacja FROM pracownicy " +
                    "WHERE UPPER(imie) LIKE UPPER(?) AND UPPER(nazwisko) LIKE UPPER(?) " +
                    "AND (UPPER(typ) LIKE UPPER(?) OR typ IS NULL) " +
                    "AND (UPPER(specjalizacja) LIKE UPPER(?) OR specjalizacja IS NULL)");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            deleteStaff = conn.prepareStatement("DELETE FROM pracownicy WHERE staff_id = ?");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            insertStaff = conn.prepareStatement("INSERT INTO pracownicy(imie, nazwisko, typ, specjalizacja) VALUES (?,?,?,?)");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            updateStaffName = conn.prepareStatement("UPDATE Pracownicy SET imie = ? WHERE staff_id = ?");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            updateStaffLName = conn.prepareStatement("UPDATE Pracownicy SET nazwisko = ? WHERE staff_id = ?");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            updateStaffType = conn.prepareStatement("UPDATE Pracownicy SET typ = ? WHERE staff_id = ?");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        try {
            updateStaffSpec = conn.prepareStatement("UPDATE Pracownicy SET specjalizacja = ? WHERE staff_id = ?");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        this.setText("Pracownicy");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
        createTable();
        addStaffB = new Button("Dodaj pracownika");
        addStaffB.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> popup());
        refreshB = new Button("Odswiez");
        refreshB.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> refresh());
        Label searchStaffL = new Label("Szukanie Pracownika");
        searchStaffL.setFont(new Font("Arial", 20));
        Label nameL = new Label("Imie:");
        Label lnameL = new Label("Nazwisko:");
        Label typeL = new Label("Typ:");
        Label specL = new Label("Specjalizacja:");
        nameSearchTF = new TextField();
        nameSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refresh());
        lnameSearchTF = new TextField();
        lnameSearchTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> refresh());
        typeCB = new ChoiceBox(FXCollections.observableArrayList("", "Admin", "Lekarz", "Sekretarka"));
        typeCB.getSelectionModel().selectFirst();
        typeCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue observableValue, Object o, Object t1) -> refresh());
        specCB = new ChoiceBox(FXCollections.observableArrayList("", "endokrynolog", "pediatra", "dermatolog", "lekarz rodzinny", "ginekolog", "okulista", "alergolog"));
        specCB.getSelectionModel().selectFirst();
        specCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue observableValue, Object o, Object t1) -> refresh());
        gridPane = new GridPane();
        borderPane = new BorderPane();
        gridPane.setPadding(new Insets(15));
        gridPane.setVgap(4);
        gridPane.setHgap(10);
        gridPane.add(addStaffB,0,0);
        gridPane.add(refreshB,3,0);
        gridPane.add(searchStaffL,0,1,3,1);
        gridPane.add(nameL,0,2);
        gridPane.add(lnameL,1,2);
        gridPane.add(typeL,2,2);
        gridPane.add(specL,3,2);
        gridPane.add(nameSearchTF,0,3);
        gridPane.add(lnameSearchTF,1,3);
        gridPane.add(typeCB,2,3);
        gridPane.add(specCB,3,3);
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
        idCol.setCellValueFactory(new PropertyValueFactory<Staff, Integer>("id"));
        TableColumn nameCol = new TableColumn("Imie");
        nameCol.setPrefWidth(120);
        nameCol.setCellValueFactory(new PropertyValueFactory<Staff, String>("firstName"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Staff, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Staff, String> t) {
                        ((Staff) t.getTableView().getItems().get(t.getTablePosition().getRow())).setFirstName(t.getNewValue());
                        try {
                            ((PreparedStatement) updateStaffName).setInt(2, ((Staff) t.getTableView().getItems().get(t.getTablePosition().getRow())).getId());
                            ((PreparedStatement) updateStaffName).setString(1, t.getNewValue());
                            ((PreparedStatement) updateStaffName).executeUpdate();
                        }
                        catch(SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
        TableColumn lnameCol = new TableColumn("Nazwisko");
        lnameCol.setPrefWidth(120);
        lnameCol.setCellValueFactory(new PropertyValueFactory<Staff, String>("lastName"));
        lnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lnameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Staff, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Staff, String> t) {
                        ((Staff) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLastName(t.getNewValue());
                        try {
                            ((PreparedStatement) updateStaffLName).setInt(2, ((Staff) t.getTableView().getItems().get(t.getTablePosition().getRow())).getId());
                            ((PreparedStatement) updateStaffLName).setString(1, t.getNewValue());
                            ((PreparedStatement) updateStaffLName).executeUpdate();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
        TableColumn typeCol = new TableColumn("Typ");
        typeCol.setPrefWidth(120);
        typeCol.setCellValueFactory(new PropertyValueFactory<Staff, String>("type"));
        typeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        typeCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Staff, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Staff, String> t) {
                        ((Staff) t.getTableView().getItems().get(t.getTablePosition().getRow())).setType(t.getNewValue());
                        try {
                            ((PreparedStatement) updateStaffType).setInt(2, ((Staff) t.getTableView().getItems().get(t.getTablePosition().getRow())).getId());
                            ((PreparedStatement) updateStaffType).setString(1, t.getNewValue());
                            ((PreparedStatement) updateStaffType).executeUpdate();
                        }
                        catch(SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
        TableColumn specCol = new TableColumn("Specjalizacja");
        specCol.setPrefWidth(120);
        specCol.setCellValueFactory(new PropertyValueFactory<Staff, String>("spec"));
        specCol.setCellFactory(TextFieldTableCell.forTableColumn());
        specCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Staff, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Staff, String> t) {
                        ((Staff) t.getTableView().getItems().get(t.getTablePosition().getRow())).setSpec(t.getNewValue());
                        try {
                            ((PreparedStatement) updateStaffSpec).setInt(2, ((Staff) t.getTableView().getItems().get(t.getTablePosition().getRow())).getId());
                            ((PreparedStatement) updateStaffSpec).setString(1, t.getNewValue());
                            ((PreparedStatement) updateStaffSpec).executeUpdate();
                        }
                        catch(SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
        TableColumn delCol = new TableColumn("Akcja");
        delCol.setPrefWidth(90);
        delCol.setCellValueFactory(new PropertyValueFactory<Staff, String>("deleteB"));
        table.setItems(data);
        table.getColumns().addAll(idCol, nameCol, lnameCol, typeCol, specCol, delCol);
    }

    public void refresh() {
        String nameSearch = nameSearchTF.getText();
        String lnameSearch = lnameSearchTF.getText();
        String typeSearch = (String)typeCB.getSelectionModel().getSelectedItem();
        String specSearch = (String)specCB.getSelectionModel().getSelectedItem();
        try {
            data.clear();
            ((PreparedStatement) selectStaff).setString(1, "%" + nameSearch + "%");
            ((PreparedStatement) selectStaff).setString(2, "%" + lnameSearch + "%");
            ((PreparedStatement) selectStaff).setString(3, "%" + typeSearch + "%");
            ((PreparedStatement) selectStaff).setString(4, "%" + specSearch + "%");
            ((PreparedStatement) selectStaff).executeQuery();
            ResultSet res = ((PreparedStatement) selectStaff).executeQuery();
            while(res.next()) {
                int idS = res.getInt("staff_id");
                String nameS = res.getString("imie");
                String lnameS = res.getString("nazwisko");
                String typeS = res.getString("typ");
                String specS = res.getString("specjalizacja");
                data.add(new Staff(idS, nameS, lnameS, typeS, specS));
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void popup() {
        StaffPopup p = new StaffPopup();
        p.display();
    }

    public static void insert(String name, String lname, String type, String spec) {
        try {
            ((PreparedStatement) insertStaff).setString(1, name);
            ((PreparedStatement) insertStaff).setString(2, lname);
            ((PreparedStatement) insertStaff).setString(3, type);
            ((PreparedStatement) insertStaff).setString(4, spec);
            ((PreparedStatement) insertStaff).executeUpdate();
        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteStaff(int id) {
        try {
            ((PreparedStatement) deleteStaff).setInt(1, id);
            ((PreparedStatement) deleteStaff).executeUpdate();
        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

}
