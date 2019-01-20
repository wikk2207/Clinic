package main.start;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.sql.*;

public class Main extends Application {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    String user = "admin";
    private TabPane tabs;
    private GridPane grid;
    private Text login;
    private Text pass;
    private TextField loginTF;
    private TextField passTF;
    private Button loginB;
    private Appointment app;
    private Results res;
    private AddResults add;
    @Override
    public void start(Stage stage) {
        stage.setTitle("Projekt SQL");
        login = new Text("Login");
        pass = new Text("Hasło");
        loginTF = new TextField();
        passTF = new TextField();
        loginB = new Button("Login");
        loginB.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> login());
        loginB.setStyle("-fx-background-color: mediumturquoise; -fx-textfill: white;");
        grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setAlignment(Pos.CENTER);
        grid.add(login, 0, 0);
        grid.add(pass, 0, 1);
        grid.add(loginTF, 1, 0);
        grid.add(passTF, 1, 1);
        grid.add(loginB, 1, 2);
        Scene scene = new Scene(grid, 1200, 800);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) throws SQLException {
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/projekt_sql", "user", "standardsqlpass");
            stmt = conn.createStatement();
            String testquery = "Select imie, nazwisko, typ From Pracownicy";
            ResultSet res = stmt.executeQuery(testquery);
            while(res.next()) {
                String imie = res.getString("imie");
                String nazwisko = res.getString("nazwisko");
                String typ = res.getString("typ");
                System.out.println(imie + "\t" + nazwisko + "\t" + typ);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(stmt != null) {
                stmt.close();
                System.out.println("closed");
            }

        }
        launch(args);
    }

    public void login() {
        grid.getChildren().clear();
        tabs = new TabPane();
        tabs.setMinSize(1200,800);
        if(user == "admin") {
            adminLogin();
        }
        else if(user == "user") {
            userLogin();
        }
        else if(user == "secretary") {
            secretaryLogin();
        }
        else if(user == "doctor") {
            doctorLogin();
        }
        grid.add(tabs,0,0);
    }

    public void adminLogin() {
        app = new Appointment();
        res = new Results();
        add = new AddResults();
        tabs.getTabs().add(res);
        tabs.getTabs().add(add);
        tabs.getTabs().add(app);
    }

    public void userLogin() {
        app = new Appointment();
        res = new Results();
        tabs.getTabs().add(res);
        tabs.getTabs().add(app);
    }

    public void secretaryLogin() {
        app = new Appointment();
        res = new Results();
        tabs.getTabs().add(res);
        tabs.getTabs().add(app);
    }

    public void doctorLogin() {
        res = new Results();
        add = new AddResults();
        tabs.getTabs().add(res);
        tabs.getTabs().add(add);
    }
}
