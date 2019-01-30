package main.start;


import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;

public class Backup extends Tab {
    GridPane grid;
    public Backup() {
        super("Backup");
        this.setClosable(false);
        this.setStyle("-fx-pref-width: 100");
        grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(4);
        grid.setHgap(10);
        Button back = new Button("Wykonaj kopie zapasowa");
        back.setOnAction(event ->createBackup());
        grid.add(back,0,0);
        setContent(grid);
    }

    public void createBackup() {
        try {
            Process runtimeProcess = Runtime.getRuntime().exec("/C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump -uroot -pppnepp7007se projekt_sql -r C:\\backup.sql");
            int processComplete = runtimeProcess.waitFor();
            if (processComplete == 0) {
                System.out.println("Backup Successfull");
            } else {
                System.out.println("Backup Failure!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
    }
}