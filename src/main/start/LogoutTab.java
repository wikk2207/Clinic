package main.start;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;


public class LogoutTab extends Tab {

    LogoutTab (Button button) {
        super("Wyloguj");
        FlowPane flowPane = new FlowPane();
        flowPane.setAlignment(Pos.CENTER);
        setClosable(false);
        flowPane.getChildren().add(button);
        setContent(flowPane);
    }
}
