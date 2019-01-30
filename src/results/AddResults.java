package results;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddResults extends Tab {
    private BorderPane borderPane;
    private static List<ResultView> resultList = new ArrayList<ResultView>();
    private TableView<ResultView> table = new TableView<ResultView>();
    private static ObservableList<ResultView> resultViews = FXCollections.observableArrayList();
    private int user_id;
    private String user;
    private int patientID;
    private int testID;
    private GridPane grid;
    private Connection conn;
    private static TextField patientPesel;
    private Text patientName;
    private Text patientLName;
    private Statement selectTestID;
    private Statement searchPatient;
    private Statement searchPesel;
    private Statement searchDoctor;
    private Statement insertTest;
    private Statement insertResult;
    private Statement selectID;
    private Statement searchDoctorID;
    private ContextMenu entriesPopup;
    private static ChoiceBox specCB;
    private boolean patientFound = false;
    private boolean doctorFound = false;
    private static ChoiceBox<String> doctorCB = null;
    private String resultS;
    private String commentS;
    private String dName;
    private String dLName;
    private String doctorSpec;
    public AddResults(Connection conn, String user, int user_id) {
        this.user_id = user_id;
        this.user = user;
        try {
            searchPatient = conn.prepareStatement(
                    "SELECT p_id, imie, nazwisko FROM pacjenci WHERE UPPER(pesel) LIKE UPPER(?)");
        } catch (SQLException e) {
            System.out.println(e);
        }
        try {
            searchPesel = conn.prepareStatement(
                    "SELECT pesel FROM pacjenci WHERE UPPER(pesel) LIKE UPPER(?)");
        } catch (SQLException e) {
            System.out.println(e);
        }
        try {
            searchDoctor = conn.prepareStatement(
                    "SELECT imie, nazwisko FROM pracownicy WHERE specjalizacja = ?");
        } catch (SQLException e) {
            System.out.println(e);
        }
        try {
            insertTest = conn.prepareStatement(
                    "INSERT INTO wyniki_badan(data_wystawienia, id_pacjenta, id_lekarza) VALUES (NOW(), ?, ?)");
        } catch (SQLException e) {
            System.out.println(e);
        }
        try {
            insertResult = conn.prepareStatement(
                    "INSERT INTO wyniki_zawartosc(id_wynikow, id_badania, wartosc, uwagi) VALUES (?, ?, ?, ?)");
        } catch (SQLException e) {
            System.out.println(e);
        }
        try {
            selectID = conn.prepareStatement(
                    "SELECT * FROM wyniki_badan ORDER BY wb_id DESC LIMIT 1");
        } catch (SQLException e) {
            System.out.println(e);
        }
        try {
            searchDoctorID = conn.prepareStatement(
                    "SELECT staff_id FROM pracownicy WHERE imie = ? AND nazwisko = ? AND specjalizacja = ?");
        } catch (SQLException e) {
            System.out.println(e);
        }
        try {
            selectTestID = conn.prepareStatement(
                    "SELECT * FROM badania WHERE nazwa = ? ORDER BY b_id DESC LIMIT 1");
        } catch (SQLException e) {
            System.out.println(e);
        }
        this.conn = conn;
        this.setText("Dodaj Wyniki");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
        Label patientInfo = new Label("Dane pacjenta");
        patientInfo.setFont(new Font("Arial", 20));
        Label patientNameL = new Label("Imie            ");
        Label patientLNameL = new Label("Nazwisko       ");
        Label patientPeselL = new Label("Pesel");
        patientName = new Text();
        patientName.setFont(new Font("Arial", 14));
        patientLName = new Text();
        patientLName.setFont(new Font("Arial", 14));
        patientPesel = new TextField();
        patientPesel.addEventHandler(KeyEvent.KEY_RELEASED, event -> searchByPesel(patientPesel.getText()));
        grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(4);
        grid.setHgap(10);
        grid.add(patientInfo,0,0,2,1);
        grid.add(patientPeselL,0,1);
        grid.add(patientNameL,1,1);
        grid.add(patientLNameL,2,1);
        grid.add(patientPesel,0,2);
        grid.add(patientName,1,2);
        grid.add(patientLName,2,2);
        createTable();
        final Separator sepVer = new Separator();
        sepVer.setOrientation(Orientation.VERTICAL);
        sepVer.setValignment(VPos.CENTER);
        grid.add(sepVer, 3, 0, 1, 3);
        entriesPopup = new ContextMenu();
        borderPane = new BorderPane();
        borderPane.setTop(grid);
        this.setContent(borderPane);
    }

    public void searchByPesel(String pesel) {
        patientName.setText("");
        patientLName.setText("");
        int counter =0;
        entriesPopup.getItems().clear();
        if (pesel.length() > 0) {
            try {
                ((PreparedStatement) searchPesel).setString(1, "%" + pesel + "%");
                ResultSet res = ((PreparedStatement) searchPesel).executeQuery();
                while (res.next()) {
                    if(counter < 10) {
                        MenuItem item = new MenuItem(res.getString("pesel"));
                        entriesPopup.getItems().add(item);
                        item.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try {
                                    res.close();
                                    ((PreparedStatement) searchPatient).setString(1, "%" + item.getText() + "%");
                                    ResultSet res2 = ((PreparedStatement) searchPatient).executeQuery();
                                    if(res2.next()) {
                                        patientID = res2.getInt("p_id");
                                        patientName.setText(res2.getString("imie"));
                                        patientLName.setText(res2.getString("nazwisko"));
                                        patientPesel.setText(item.getText());
                                        if(!patientFound) {
                                            searchBySpec();
                                            patientFound = true;
                                        }
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        counter++;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            if (!entriesPopup.isShowing()) {
                entriesPopup.show(patientPesel, Side.BOTTOM, 0, 0);
            }
        } else {
            patientName.setText("");
            patientLName.setText("");
        }
    }
    public void searchBySpec() {
        if(user == "admin") {
            Label doctorInfo = new Label("Dane lekarza");
            doctorInfo.setFont(new Font("Arial", 20));
            Label doctorSpec = new Label("Specjalizacja");
            specCB = new ChoiceBox();
            specCB = new ChoiceBox(FXCollections.observableArrayList("", "endokrynolog", "pediatra", "dermatolog", "lekarz rodzinny", "ginekolog", "okulista", "alergolog"));
            specCB.getSelectionModel().selectFirst();
            specCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue observableValue, Object o, Object t1) -> searchDoctor());
            grid.add(doctorInfo, 4, 0, 2, 1);
            grid.add(doctorSpec, 4, 1);
            grid.add(specCB, 4, 2);
        }
        else {
            addResultB();
        }
    }

    public void searchDoctor() {
        doctorSpec = (String) specCB.getSelectionModel().getSelectedItem();
        String doctorName[] = new String[100];
        String doctorLName[] = new String[100];
        if (doctorFound) {
            doctorCB.getItems().clear();
        } else {
            doctorCB = new ChoiceBox<String>();
            doctorCB.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    addResultB();
                }
            });
            Label doctorL = new Label("Lekarz");
            doctorFound = true;
            grid.add(doctorL, 5, 1);
            grid.add(doctorCB, 5, 2);
        }
        if (doctorSpec != "") {
            ObservableList<String> docList = FXCollections.observableArrayList();
            try {
                int counter = 0;((PreparedStatement) searchDoctor).setString(1, doctorSpec);
                ResultSet res = ((PreparedStatement) searchDoctor).executeQuery();
                while (res.next()) {
                    doctorName[counter] = res.getString("imie");
                    doctorLName[counter] = res.getString("nazwisko");
                    docList.add(doctorName[counter] + " " + doctorLName[counter]);
                    counter++;
                }
                doctorCB.setItems(docList);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addResultB() {
        Button newResultB = new Button("Dodaj nowy wynik");
        Button saveResultB = new Button("Zapisz wyniki");
        saveResultB.setOnAction(e -> saveResult());
        newResultB.setOnAction(e -> getResult());
        grid.add(newResultB,7,1, 2,2);
        grid.add(saveResultB,9,1,2,2);
    }
    public void getResult() {
        ResultsPopup p = new ResultsPopup();
        p.display(conn, this);
    }

    public void createTable() {
        TableColumn patientNameCol = new TableColumn("Imie pacjenta");
        patientNameCol.setPrefWidth(120);
        patientNameCol.setCellValueFactory(new PropertyValueFactory<ResultView, String>("patientName"));
        TableColumn patientLNameCol = new TableColumn("Nazwisko pacjenta");
        patientLNameCol.setPrefWidth(120);
        patientLNameCol.setCellValueFactory(new PropertyValueFactory<ResultView, String>("patientLName"));
        TableColumn doctorNameCol = new TableColumn("Imie lekarza");
        doctorNameCol.setPrefWidth(120);
        doctorNameCol.setCellValueFactory(new PropertyValueFactory<ResultView, String>("doctorName"));
        TableColumn doctorLNameCol = new TableColumn("Nazwisko lekarza");
        doctorLNameCol.setPrefWidth(120);
        doctorLNameCol.setCellValueFactory(new PropertyValueFactory<ResultView, String>("doctorLName"));
        TableColumn testCol = new TableColumn("Badanie");
        testCol.setPrefWidth(120);
        testCol.setCellValueFactory(new PropertyValueFactory<ResultView, String>("test"));
        TableColumn resultCol = new TableColumn("Wynik");
        resultCol.setPrefWidth(120);
        resultCol.setCellValueFactory(new PropertyValueFactory<ResultView, String>("result"));
        TableColumn commentCol = new TableColumn("Uwagi");
        commentCol.setPrefWidth(120);
        commentCol.setCellValueFactory(new PropertyValueFactory<ResultView, String>("comment"));
        TableColumn deleteCol = new TableColumn("Akcja");
        deleteCol.setPrefWidth(120);
        deleteCol.setCellValueFactory(new PropertyValueFactory<ResultView, String>("deleteB"));
        table.setEditable(true);
        table.setItems(resultViews);
        table.getColumns().addAll(patientNameCol, patientLNameCol, doctorNameCol, doctorLNameCol, testCol, resultCol, commentCol, deleteCol);
    }
    public void insert(String testS, String resultS, String commentS, int testID) {
        this.resultS = resultS;
        this. commentS = commentS;
        this.testID = testID;
        specCB.setDisable(true);
        doctorCB.setDisable(true);
        patientPesel.setDisable(true);
        borderPane.setCenter(null);
        String doctor = (String)doctorCB.getSelectionModel().getSelectedItem();
        String[] split = doctor.split("\\s+");
        String pName = patientName.getText();
        String pLName = patientLName.getText();
        dName = split[0];
        dLName = split[1];
        ResultView rv = new ResultView(pName, pLName , dName, dLName, testS, resultS, commentS);
        resultViews.add(rv);
        resultList.add(rv);
        resultViews.clear();
        for(ResultView r : resultList) {
            resultViews.add(r);
        }
        borderPane.setCenter(table);
    }

    public static void deleteResult(ResultView rv) {
        resultList.remove(rv);
        resultViews.remove(rv);
        if(resultList.isEmpty()) {
            patientPesel.setDisable(false);
            specCB.setDisable(false);
            doctorCB.setDisable(false);
        }
    }

    public void saveResult() {
        int doctorID = 0;
        if(user == "admin") {
            try {
                ((PreparedStatement) searchDoctorID).setString(1, dName);
                ((PreparedStatement) searchDoctorID).setString(2, dLName);
                ((PreparedStatement) searchDoctorID).setString(3, doctorSpec);
                ResultSet res1 = ((PreparedStatement) searchDoctorID).executeQuery();
                if (res1.next()) {
                    doctorID = res1.getInt("staff_id");
                }
            }
            catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
        else {
            doctorID = user_id;
        }
        try {
            ((PreparedStatement) insertTest).setInt(1, patientID);
            ((PreparedStatement) insertTest).setInt(2, doctorID);
            ((PreparedStatement) insertTest).executeUpdate();
            ResultSet res = ((PreparedStatement) selectID).executeQuery();
            int id = 0;
            if(res.next()) {
                id = res.getInt("wb_id");
            }
            for(int i=0; i < resultList.size(); i++) {
                ((PreparedStatement) selectTestID).setString(1, resultList.get(i).getTest());
                ResultSet res2 = ((PreparedStatement) selectTestID).executeQuery();
                int testId = 0;
                if(res2.next()) {
                    testId = res2.getInt("b_id");
                }
                ((PreparedStatement) insertResult).setInt(1, id);
                ((PreparedStatement) insertResult).setInt(2, testId);
                ((PreparedStatement) insertResult).setString(3, resultList.get(i).getResult());
                ((PreparedStatement) insertResult).setString(4, resultList.get(i).getComment());
                ((PreparedStatement) insertResult).executeUpdate();
            }
        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }
        borderPane.setCenter(null);
        patientPesel.setDisable(false);
        specCB.setDisable(false);
        doctorCB.setDisable(false);
    }
}
