package main.start;

import javafx.scene.control.Tab;

public class Appointment extends Tab {
    public Appointment() {
        this.setText("Wizyty");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
    }
}
