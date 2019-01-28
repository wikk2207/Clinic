
package patients;

        import javafx.beans.property.SimpleIntegerProperty;
        import javafx.beans.property.SimpleStringProperty;
        import javafx.scene.control.Alert;
        import javafx.scene.control.Button;
        import javafx.scene.control.ButtonType;
        import javafx.scene.input.MouseEvent;
        import patients.PatientList;
        import results.ShowResults;

        import java.util.Optional;

public class Patient {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty pesel;
    private final SimpleStringProperty date;
    private Button deleteB;
    private Button selectB;
    private ShowResults showResults;

    public Patient(int id, String fName, String lName, String pesel, String date) {
        this.id = new SimpleIntegerProperty(id);
        this.firstName = new SimpleStringProperty(fName);
        this.lastName = new SimpleStringProperty(lName);
        this.pesel = new SimpleStringProperty(pesel);
        this.date = new SimpleStringProperty(date);
        this.deleteB = new Button("Usun");
        this.deleteB.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> deleteB());
        selectB = new Button("Wybierz");
        selectB.setOnAction(event -> {select();});
    }


    public int getId() {
        return id.get();
    }

    public void setId(int idl) {
        id.set(idl);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String fName) {
        firstName.set(fName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String fName) {
        lastName.set(fName);
    }

    public String getPesel() {
        return pesel.get();
    }

    public void setPesel(String pesel) {
        this.pesel.set(pesel);
    }

    public String getDate() { return date.get(); }

    public void setDate(String date) { this.date.set(date); }

    public Button getDeleteB() { return deleteB; }

    public void setDeleteB(Button button) { deleteB = button; }

    public void deleteB() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Usuwasz pacjenta");
        alert.setContentText("Czy jestes tego pewien?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            PatientList.deletePatient(getId());
            this.deleteB.setText("Usunieto");
            this.deleteB.setDisable(true);
        } else {}
    }

    public Button getSelectB() {
        return selectB;
    }

    public void setShowResults(ShowResults showResults) {
        this.showResults = showResults;
    }

    private void select() {
        showResults.setPatientId(id.get());
    }
}
