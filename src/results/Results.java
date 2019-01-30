package results;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import staff.Staff;

import java.sql.*;
import java.util.Optional;


public class Results {
    private final SimpleStringProperty date;
    private final SimpleStringProperty firstNameD;
    private final SimpleStringProperty nameD;
    private final SimpleStringProperty firstNameP;
    private final SimpleStringProperty nameP;
    private Button selectB;
    private Button deleteB;

    private int resultId;
    private int userId;
    private int doctorId;

    //needed to create resultDetails
    private Button backButton;
    private BorderPane borderPane;
    private boolean editable;
    private String userType;
    private Connection con;

    public Results (String date, String firstNameD, String nameD, String firstNameP, String nameP, int resultId, int userId, int doctorId)  {
        this.date = new SimpleStringProperty(date);
        this.firstNameD = new SimpleStringProperty(firstNameD);
        this.nameD = new SimpleStringProperty(nameD);
        this.firstNameP = new SimpleStringProperty(firstNameP);
        this.nameP = new SimpleStringProperty(nameP);
        this.resultId = resultId;
        this.userId = userId;
        this.doctorId = doctorId;
        selectB = new Button("Wybierz");
        selectB.setOnAction(event -> {select();});
        deleteB = new Button("X");
        deleteB.setOnAction(event -> {delete();});
    }

    private void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuniecie wynikow.");
        alert.setHeaderText(null);
        alert.setContentText("Czy na pewno chcesz usunac wyniki pacjenta "+firstNameP.get()+ " " +nameP.get()+" z dnia " +date.get()+"?");
        alert.setHeight(300);
        alert.setWidth(450);

        try {
            Statement stmt = con.prepareStatement("DELETE FROM wyniki WHERE wb_id=?");
            ((PreparedStatement) stmt).setInt(1, resultId);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                ((PreparedStatement) stmt).executeUpdate();
                deleteB.setDisable(true);
            } else {}
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void select() {
        VBox vBoxCenter = new VBox();
        borderPane.setCenter(vBoxCenter);

        VBox vBoxTop = new VBox();
        borderPane.setTop(vBoxTop);
        Text t1 = new Text("Data wystawienia: "+getDate());
        Text t2 = new Text("");
        if(userType.equals("user") || userType.equals("secretary")) {
            t2 = new Text("Lekarz: "+getFirstNameD() + " " +getNameD());
        } else if (userType.equals("doctor")) {
            t2 = new Text("Pacjent: "+getFirstNameP() + " " +getNameP());
        }

        vBoxTop.getChildren().addAll(backButton,t1,t2);


        TableView table = new TableView();
        table.setEditable(editable);

        ObservableList<ResultDetails> data = FXCollections.observableArrayList();

        TableColumn nameColumn = new TableColumn("Nazwa badania");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("testName"));

        TableColumn valueColumn = new TableColumn("Wartosc");
        valueColumn.setCellValueFactory(new PropertyValueFactory<ResultDetails, String>("value"));
        //valueColumn.setEditable(true); //todo editable
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<ResultDetails, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<ResultDetails, String> t) {
                        ((ResultDetails) t.getTableView().getItems().get(t.getTablePosition().getRow())).setValue(t.getNewValue());
                    }
                }
        );
        TableColumn prevValColumn = new TableColumn("PoprzedniaWartosc");
        prevValColumn.setCellValueFactory(new PropertyValueFactory<>("previousValue"));

        TableColumn minColumn = new TableColumn("Min");
        minColumn.setCellValueFactory(new PropertyValueFactory<>("valueMin"));

        TableColumn maxColumn = new TableColumn("Max");
        maxColumn.setCellValueFactory(new PropertyValueFactory<>("valueMax"));

        TableColumn unitColumn = new TableColumn("Jednostka");
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));

        TableColumn commentColumnt = new TableColumn("Uwagi");
        commentColumnt.setCellValueFactory(new PropertyValueFactory<>("comment"));

        TableColumn deleteColumn = new TableColumn("Usun");
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("deleteB"));

        TableColumn updateColumn = new TableColumn("Aktualizuj");
        updateColumn.setCellValueFactory(new PropertyValueFactory<>("updateB"));

        try {
            Statement statement;
            statement = con.prepareStatement("SELECT id_badania, nazwa, wartosc, min_wartosc, max_wartosc, jednostka, poprzednia_wartosc, A.uwagi FROM wyniki_zawartosc A JOIN  badania B ON A.id_badania=B.b_id WHERE id_wynikow =?");
            ((PreparedStatement) statement).setInt(1, resultId);

            ResultSet rs = ((PreparedStatement) statement).executeQuery();
            String name, unit, com, val;
            float min, max, prev;
            int id;
            while(rs.next()) {
                id = rs.getInt(1);
                name = rs.getString(2);
                val = rs.getString(3);
                min = rs.getFloat(4);
                max = rs.getFloat(5);
                unit = rs.getString(6);
                prev = rs.getFloat(7);
                com = rs.getString(8);
                ResultDetails rd = new ResultDetails(name, val, prev, min, max, unit, com, resultId, id);
                rd.setCon(con);
                data.add(rd);
            }

            table.setItems(data);

            table.getColumns().addAll(nameColumn, valueColumn, minColumn, maxColumn, unitColumn, prevValColumn, commentColumnt);
            if(userType.equals("admin")) {
                table.getColumns().addAll(deleteColumn, updateColumn);
            }
            if(userType.equals("doctor")){
                table.getColumns().add(updateColumn);
            }
            vBoxCenter.getChildren().add(table);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }

    public void setBackButton(Button backButton) {
        this.backButton = backButton;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public String getDate() {
        return date.get();
    }


    public String getFirstNameD() {
        return firstNameD.get();
    }


    public String getNameD() {
        return nameD.get();
    }

    public String getFirstNameP() {
        return firstNameP.get();
    }

    public String getNameP() {
        return nameP.get();
    }

    public Button getSelectB() {
        return selectB;
    }

    public Button getDeleteB() {
        return deleteB;
    }

}
