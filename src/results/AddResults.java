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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.sql.*;

public class AddResults extends Tab {
    private int user_id;
    private String user;
    private int patientID;
    private int doctorID;
    private int testID;
    private GridPane grid;
    private Connection conn;
    private TextField patientPesel;
    private Text patientName;
    private Text patientLName;
    private Statement searchPatient;
    private Statement searchPesel;
    private Statement searchDoctor;
    private Statement searchTests;
    private ContextMenu entriesPopup;
    private ChoiceBox specCB;
    private String doctorSpec;
    private boolean patientFound = false;
    private boolean doctorFound = false;
    private ChoiceBox<String> doctorCB = null;
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
            searchTests = conn.prepareStatement(
                    "SELECT b_id, nazwa FROM badania WHERE UPPER(nazwa) LIKE UPPER(?)");
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
        final Separator sepVer = new Separator();
        sepVer.setOrientation(Orientation.VERTICAL);
        sepVer.setValignment(VPos.CENTER);
        grid.add(sepVer, 3, 0, 1, 3);
        this.setContent(grid);
        entriesPopup = new ContextMenu();
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
        Label doctorInfo = new Label("Dane lekarza");
        doctorInfo.setFont(new Font("Arial", 20));
        Label doctorSpec = new Label("Specjalizacja");
        specCB = new ChoiceBox();
        specCB = new ChoiceBox(FXCollections.observableArrayList("", "endokrynolog", "pediatra", "dermatolog", "lekarz rodzinny", "ginekolog", "okulista", "alergolog"));
        specCB.getSelectionModel().selectFirst();
        specCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue observableValue, Object o, Object t1) -> searchDoctor());
        grid.add(doctorInfo, 4,0,2,1);
        grid.add(doctorSpec,4,1);
        grid.add(specCB,4,2);
    }

    public void searchDoctor() {
        if(user == "admin") {
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
                        addNewR();
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
                    int counter = 0;
                    ((PreparedStatement) searchDoctor).setString(1, doctorSpec);
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
        } else {
            addNewR();
        }
    }

    public void addNewR() {
        Button newResultB = new Button("Dodaj nowy wynik");
        newResultB.setOnAction(e -> createTable());
        final Separator sepHor = new Separator();
        sepHor.setValignment(VPos.CENTER);
        grid.add(sepHor, 0, 3, 9, 1);
        final Separator sepVer = new Separator();
        sepVer.setOrientation(Orientation.VERTICAL);
        sepVer.setValignment(VPos.CENTER);
        grid.add(sepVer, 6, 0, 1, 3);
        grid.add(newResultB,9,1, 2, 2);
    }

    public void createTable() {

    }
}
