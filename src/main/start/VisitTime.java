package main.start;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;


public class VisitTime {
    private final SimpleStringProperty date;
    private final SimpleStringProperty time;
    private Button chooseB;


    VisitTime(String date, String time) {
        this.date = new SimpleStringProperty(date);
        this.time = new SimpleStringProperty(time);
        chooseB = new Button("X");
        chooseB.setOnAction(event -> {

        });
    }

    public String getDate() {
        return date.get();
    }

    public void setDate (String date) {
        this.date.set(date);
    }

    public String getTime() {
        return time.get();
    }

    public void setTime (String time) {
        this.time.set(time);
    }

    public Button getChooseB() {
        return chooseB;
    }

    public void setChooseB(Button chooseB) {
        this.chooseB = chooseB;
    }
}
