package main.start;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;

import javafx.scene.input.MouseEvent;
import java.sql.*;

public class StaffList extends Tab {
    private TableView<Staff> table = new TableView<Staff>();
    private ObservableList<Staff> data = FXCollections.observableArrayList();
    private Statement stmt = null;
    private Button refreshB;
    private FlowPane flow;
    public StaffList() {
        this.setText("Pracownicy");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
        createTable();
        refreshB = new Button("Odswiez");
        refreshB.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> refresh());
        flow = new FlowPane();
        flow.setPadding(new Insets(5, 0, 5, 0));
        flow.setVgap(4);
        flow.setHgap(4);
        flow.setPrefWrapLength(1200);
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
        table.setItems(data);
        table.getColumns().addAll(idCol, nameCol, lnameCol, typeCol, specCol);
    }

    public void refresh() {
        try {
            data.clear();
            Class.forName(Main.JDBC_DRIVER);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/projekt_sql", "user", "standardsqlpass");
            stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT staff_id, imie, nazwisko, typ, specjalizacja FROM Pracownicy");
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("closed");
            }

        }
    }
}
