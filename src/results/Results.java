package results;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;


public class Results {
    private final SimpleStringProperty date;
    private final SimpleStringProperty firstNameD;
    private final SimpleStringProperty nameD;
    private final SimpleStringProperty firstNameP;
    private final SimpleStringProperty nameP;
    private Button selectB;
    int id;


    public Results (String date, String firstNameD, String nameD, String firstNameP, String nameP, int id)  {
        this.date = new SimpleStringProperty(date);
        this.firstNameD = new SimpleStringProperty(firstNameD);
        this.nameD = new SimpleStringProperty(nameD);
        this.firstNameP = new SimpleStringProperty(firstNameP);
        this.nameP = new SimpleStringProperty(nameP);
        this.id=id;
        selectB = new Button("Wybierz");
        selectB.setOnAction(event -> {select();});
    }

    private void select() {
        //todo
    }

    public String getDate() {
        return date.get();
    }


    public String getFirstNameD() {
        return firstNameD.get();
    }


    public String getNameD() {
        return nameD.get();
    }

    public String getFirstNameP() {
        return firstNameP.get();
    }

    public String getNameP() {
        return nameP.get();
    }

    public Button getSelectB() {
        return selectB;
    }
}
