package results;

import javafx.scene.control.Tab;

public class Results extends Tab {
    public Results () {
        this.setText("Wyniki Badań");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
    }
}
