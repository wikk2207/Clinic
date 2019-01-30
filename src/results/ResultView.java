package results;


import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;


import java.util.Optional;

public class ResultView {
    private final SimpleStringProperty patientName;
    private final SimpleStringProperty patientLName;
    private final SimpleStringProperty doctorName;
    private final SimpleStringProperty doctorLName;
    private final SimpleStringProperty test;
    private final SimpleStringProperty result;
    private final SimpleStringProperty comment;
    private Button deleteB;

    public ResultView(String pName, String pLName, String dName, String dLName, String test, String result, String comment) {
        this.patientName = new SimpleStringProperty(pName);
        this.patientLName = new SimpleStringProperty(pLName);
        this.doctorName = new SimpleStringProperty(dName);
        this.doctorLName = new SimpleStringProperty(dLName);
        this.test = new SimpleStringProperty(test);
        this.result = new SimpleStringProperty(result);
        this.comment = new SimpleStringProperty(comment);
        this.deleteB = new Button("Usun");
        this.deleteB.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> deleteB());
    }

    public String getPatientName() {
        return patientName.get();
    }

    public void setPatientName(String pName) {
        patientName.set(pName);
    }

    public String getPatientLName() {
        return patientLName.get();
    }

    public void setPatientLName(String pLName) { patientLName.set(pLName); }

    public String getDoctorName() {
        return doctorName.get();
    }

    public void setDoctorName(String dName) {
        doctorName.set(dName);
    }

    public String getDoctorLName() {
        return doctorLName.get();
    }

    public void setDoctorLName(String dLName) {
        doctorLName.set(dLName);
    }

    public String getTest() {
        return test.get();
    }

    public void setTest(String testS) {
        test.set(testS);
    }

    public String getResult() { return result.get(); }

    public void setResult(String resultS) {
        result.set(resultS);
    }

    public String getComment() {
        return comment.get();
    }

    public void setComment(String commentS) {
        comment.set(commentS);
    }

    public Button getDeleteB() { return deleteB; }

    public void setDeleteB(Button button) { deleteB = button; }

    public void deleteB() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Czy chcesz usunac wynik?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            AddResults.deleteResult(this);
            this.deleteB.setText("Usunieto");
            this.deleteB.setDisable(true);
        } else {}
    }
}
