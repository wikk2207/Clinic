package results;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

public class ResultDetails {
    private final SimpleStringProperty testName;
    private final SimpleStringProperty value;
    private final SimpleFloatProperty previousValue;
    private final SimpleFloatProperty valueMin;
    private final SimpleFloatProperty valueMax;
    private final SimpleStringProperty unit;
    private final SimpleStringProperty comment;
    private Button deleteB;
    private Button updateB;
    int id;
    int testId;
    private Connection con;
    private float originalVal;



    public ResultDetails (String testName, String value, float previousValue, float valueMin, float valueMax, String unit, String comment, int id, int testId)  {
        this.testName = new SimpleStringProperty(testName);
        this.value = new SimpleStringProperty(value);
        this.previousValue = new SimpleFloatProperty(previousValue);
        this.valueMin = new SimpleFloatProperty(valueMin);
        this.valueMax = new SimpleFloatProperty(valueMax);
        this.unit = new SimpleStringProperty(unit);
        this.comment = new SimpleStringProperty(comment);
        this.id=id;
        this.testId=testId;
        this.originalVal=Float.parseFloat(this.value.get());
        deleteB = new Button("X");
        deleteB.setOnAction(event -> {delete();});
        updateB = new Button("X");
        updateB.setOnAction(event -> {update();});
    }

    private void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuniecie wynikow.");
        alert.setHeaderText(null);
        alert.setContentText("Czy na pewno chcesz usunac wyniki badania "+testName.get()+"?");
        alert.setHeight(300);
        alert.setWidth(450);

        try {
            Statement stmt = con.prepareStatement("DELETE FROM wyniki_zawartosc WHERE id_wynikow=? AND id_badania=?");
            ((PreparedStatement) stmt).setInt(1, id);
            ((PreparedStatement) stmt).setInt(2,testId);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                ((PreparedStatement) stmt).executeUpdate();
                deleteB.setDisable(true);
                updateB.setDisable(true);
            } else {}
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void update() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuniecie wynikow.");
        alert.setHeaderText(null);
        alert.setContentText("Czy na pewno chcesz zmienic wartosc badania "+testName.get()+" z "+originalVal+" na "+value.get()+"?");
        alert.setHeight(300);
        alert.setWidth(450);

        try {
            Statement stmt = con.prepareStatement("UPDATE wyniki_zawartosc SET wartosc=? WHERE id_wynikow=? AND id_badania=?");
            ((PreparedStatement) stmt).setFloat(1, Float.parseFloat(value.get()));
            ((PreparedStatement) stmt).setInt(2, id);
            ((PreparedStatement) stmt).setInt(3,testId);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                ((PreparedStatement) stmt).executeUpdate();
            } else {}
        } catch (Exception e) {
            System.out.println(e);
        }
        originalVal = Float.parseFloat(value.get());
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getTestName() {
        return testName.get();
    }

    public String getValue() {
        return value.get();
    }

    public float getPreviousValue() {
        return previousValue.get();
    }


    public float getValueMin() {
        return valueMin.get();
    }

    public float getValueMax() {
        return valueMax.get();
    }

    public String getUnit() {
        return unit.get();
    }

    public String getComment() {
        return comment.get();
    }

    public Button getDeleteB() {
        return deleteB;
    }

    public Button getUpdateB() {
        return updateB;
    }
}
