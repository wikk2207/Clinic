package staff;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import staff.StaffList;

public class StaffPopup {
    private String name;
    private String lname;
    private String type;
    private String spec;
    private GridPane grid;
    private Stage popupwindow;
    private ChoiceBox typeCB;
    private ChoiceBox specCB;
    private TextField nameTF;
    private TextField lnameTF;
    public void display() {
        popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Dodawanie pracownika");
        Label nameL= new Label("Imie");
        nameTF = new TextField();
        Label lnameL= new Label("Nazwisko");
        lnameTF = new TextField();
        Label typeL= new Label("Typ pracownika");
        typeCB = new ChoiceBox(FXCollections.observableArrayList("Admin", "Lekarz", "Sekretarka"));
        Label specL= new Label("Specjalizacja");
        specCB = new ChoiceBox(FXCollections.observableArrayList("brak", "endokrynolog", "pediatra", "dermatolog", "lekarz rodzinny", "ginekolog", "okulista", "alergolog"));
        Button buttonL= new Button("Zatwierdz");
        buttonL.setOnAction(e -> getNew());
        grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setAlignment(Pos.CENTER);
        grid.add(nameL, 0, 0);
        grid.add(lnameL, 0, 1);
        grid.add(typeL, 0, 2);
        grid.add(specL, 0, 3);
        grid.add(nameTF, 1, 0);
        grid.add(lnameTF, 1, 1);
        grid.add(typeCB, 1, 2);
        grid.add(specCB, 1, 3);
        grid.add(buttonL, 1, 4);
        Scene scene = new Scene(grid, 400, 300);
        popupwindow.setScene(scene);
        popupwindow.showAndWait();
    }
    public void getNew() {
        name = nameTF.getText();
        lname = lnameTF.getText();
        type = (String)typeCB.getSelectionModel().getSelectedItem();
        spec = (String)specCB.getSelectionModel().getSelectedItem();
        if(name != null && lname != null && type != null && spec != null) {
            StaffList.insert(name, lname, type, spec);
            popupwindow.close();
        }
        else {
            Text errorL = new Text("Pola nie moga byc puste!");
            errorL.setFill(Color.RED);
            grid.add(errorL, 1, 5);
        }

    }
}