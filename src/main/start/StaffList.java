package main.start;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;

import javafx.scene.input.MouseEvent;
import java.sql.*;

public class StaffList extends Tab {
    private TableView<Staff> table = new TableView<Staff>();
    private ObservableList<Staff> data = FXCollections.observableArrayList();
    private Statement stmt = null;
    private Button addStaffB;
    private Button refreshB;
    private FlowPane flow;
    private Connection conn;
    private Statement selectStaff;
    private Statement insertStaff;
    private static Statement deleteStaff;
    public StaffList(Connection conn) {
        this.conn = conn;
        try {
            selectStaff = conn.prepareStatement("SELECT staff_id, imie, nazwisko, typ, specjalizacja FROM pracownicy");
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
        this.setText("Pracownicy");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
        createTable();
        addStaffB = new Button("Dodaj pracownika");
        addStaffB.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> insert());
        refreshB = new Button("Odswiez");
        refreshB.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> refresh());
        flow = new FlowPane();
        flow.setPadding(new Insets(5, 0, 5, 0));
        flow.setVgap(4);
        flow.setHgap(4);
        flow.setPrefWrapLength(1200);
        flow.getChildren().add(addStaffB);
        flow.getChildren().add(refreshB);
        flow.getChildren().add(table);
        this.setContent(flow);
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
        try {
            data.clear();
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

    public void insert() {
        /*
        try {
            ((PreparedStatement) insertStaff).setString(1, Integer.toString(id));
            ((PreparedStatement) insertStaff).executeUpdate();
        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }
        */
    }

    public static void deleteStaff(int id) {
        try {
            ((PreparedStatement) deleteStaff).setString(1, Integer.toString(id));
            ((PreparedStatement) deleteStaff).executeUpdate();
        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
}
