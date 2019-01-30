package results;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;


public class ResultsPopup {
    private GridPane grid;
    private Stage popupwindow;
    private TextField nameTF;
    private TextField resultTF;
    private TextField commentTF;
    private Statement selectTestList;
    private Statement searchTestList;
    private ContextMenu entriesPopup;
    private int testID;
    private String testName;
    private String jed;
    private Label jedL;
    private AddResults results;
    public void display(Connection conn, AddResults results) {
        this.results = results;
        try {
            selectTestList = conn.prepareStatement(
                    "SELECT nazwa FROM badania WHERE UPPER(nazwa) LIKE UPPER(?)");
        } catch (SQLException e) {
            System.out.println(e);
        }
        try {
            searchTestList = conn.prepareStatement(
                    "SELECT b_id, nazwa, jednostka FROM badania WHERE UPPER(nazwa) LIKE UPPER(?)");
        } catch (SQLException e) {
            System.out.println(e);
        }
        entriesPopup = new ContextMenu();
        popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Dodawanie Badan");
        Label nameL= new Label("Rodzaj Badania");
        nameTF = new TextField();
        nameTF.addEventHandler(KeyEvent.KEY_RELEASED, event -> searchName(nameTF.getText()));
        Label resultL= new Label("Wynik");
        resultTF = new TextField();
        jedL = new Label("  ");
        Label commentsL= new Label("Uwagi");
        commentTF = new TextField();
        Button buttonL= new Button("Zatwierdz");
        buttonL.setOnAction(e -> confirm());
        grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setAlignment(Pos.CENTER);
        grid.add(nameL, 0, 0);
        grid.add(resultL, 1, 0);
        grid.add(commentsL, 0, 2);
        grid.add(jedL, 2, 1);
        grid.add(nameTF, 0, 1);
        grid.add(resultTF, 1, 1);
        grid.add(commentTF,0,3,2,2);
        grid.add(buttonL, 0, 5);
        Scene scene = new Scene(grid, 400, 300);
        popupwindow.setScene(scene);
        popupwindow.showAndWait();
    }

    public void confirm() {
        String resultS = resultTF.getText();
        String commentS = commentTF.getText();
        if(testName != null && resultS != null) {
            results.insert(testName, resultS, commentS, testID);
            popupwindow.close();
        }
        else {
            Text errorL = new Text("Pola nie moga byc puste!");
            errorL.setFill(Color.RED);
            grid.add(errorL, 1, 5);
        }
    }

    public void searchName(String testS) {
        int counter =0;
        entriesPopup.getItems().clear();
        if (testS.length() > 0) {
            try {
                ((PreparedStatement) selectTestList).setString(1, "%" + testS + "%");
                ResultSet res = ((PreparedStatement) selectTestList).executeQuery();
                while (res.next()) {
                    if(counter < 10) {
                        MenuItem item = new MenuItem(res.getString("nazwa"));
                        entriesPopup.getItems().add(item);
                        item.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try {
                                    res.close();
                                    ((PreparedStatement) searchTestList).setString(1, "%" + item.getText() + "%");
                                    ResultSet res2 = ((PreparedStatement) searchTestList).executeQuery();
                                    if(res2.next()) {
                                        testID = res2.getInt("b_id");
                                        testName = res2.getString("nazwa");
                                        jed = res2.getString("jednostka");
                                        nameTF.setText(testName);
                                        jedL.setText(jed);
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
                entriesPopup.show(nameTF, Side.BOTTOM, 0, 0);
            }
        }
    }
}