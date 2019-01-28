package results;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;

public class ResultDetails {
    private final SimpleStringProperty testName;
    private final SimpleFloatProperty value;
    private final SimpleFloatProperty previousValue;
    private final SimpleFloatProperty valueMin;
    private final SimpleFloatProperty valueMax;
    private final SimpleStringProperty unit;
    private final SimpleStringProperty comment;
    private Button deleteB;
    int id;



    public ResultDetails (String testName, float value, float previousValue, float valueMin, float valueMax, String unit, String comment, int id)  {
        this.testName = new SimpleStringProperty(testName);
        this.value = new SimpleFloatProperty(value);
        this.previousValue = new SimpleFloatProperty(previousValue);
        this.valueMin = new SimpleFloatProperty(valueMin);
        this.valueMax = new SimpleFloatProperty(valueMax);
        this.unit = new SimpleStringProperty(unit);
        this.comment = new SimpleStringProperty(comment);
        this.id=id;
        deleteB = new Button("Usun");
        deleteB.setOnAction(event -> {delete();});
    }

    private void delete() {
        //todo
    }

    public String getTestName() {
        return testName.get();
    }

    public float getValue() {
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

}
