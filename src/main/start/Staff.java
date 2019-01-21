package main.start;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Staff {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty type;
    private final SimpleStringProperty spec;

    public Staff(int id, String fName, String lName, String type, String spec) {
        this.id = new SimpleIntegerProperty(id);
        this.firstName = new SimpleStringProperty(fName);
        this.lastName = new SimpleStringProperty(lName);
        this.type = new SimpleStringProperty(type);
        this.spec = new SimpleStringProperty(spec);
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

    public String getType() {
        return type.get();
    }

    public void setType(String typ) {
        type.set(typ);
    }

    public String getSpec() {
        return spec.get();
    }

    public void setSpec(String specS) {
        spec.set(specS);
    }
}
