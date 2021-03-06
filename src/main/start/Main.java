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
import patients.PatientList;
import results.AddResults;
import results.Results;
import results.ShowResults;
import staff.StaffList;
import visits.Appointment;

import java.sql.*;

public class Main extends Application {
    //public  static final String HOST = "jdbc:mysql://cucumber02.myqnapcloud.com:3306/projekt_sql";
    public  static final String HOST = "jdbc:mysql://localhost:3306/projekt_sql";
    public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static String login = null;
    public static String pass = null;
    private String user = "admin";
    private TabPane tabs;
    private GridPane grid;
    private Text loginT;
    private Text passT;
    private TextField loginTF;
    private TextField passTF;
    private Button loginB;
    private Appointment app;
    private ShowResults res;
    private AddResults add;
    private LogoutTab logoutTab;
    private Statement stmt = null;
    private Connection conn;
    private int user_id;
    private Button logoutB;


    @Override
    public void start(Stage stage) {
        stage.setTitle("Projekt SQL");
        loginT = new Text("Login");
        passT = new Text("Hasło");
        loginTF = new TextField();
        passTF = new TextField();
        loginB = new Button("Login");
        loginB.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> tryLogin());
        loginB.setStyle("-fx-background-color: mediumturquoise; -fx-textfill: white;");
        grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setAlignment(Pos.CENTER);
        grid.add(loginT, 0, 0);
        grid.add(passT, 0, 1);
        grid.add(loginTF, 1, 0);
        grid.add(passTF, 1, 1);
        grid.add(loginB, 1, 2);
        Scene scene = new Scene(grid, 1200, 800);
        stage.setScene(scene);
        stage.show();

        logoutB = new Button("Wyloguj");
        logoutB.setOnAction(event -> logout());

        logoutTab = new LogoutTab(logoutB);
    }


    public static void main(String[] args) throws SQLException {

        launch(args);
    }

    public void tryLogin() {
        login = loginTF.getText();
        pass = passTF.getText();
        checkUser();
    }

    private void login() {
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
        try {
            conn = DriverManager.getConnection(HOST, "admin", "a3d6m9i2n");
        } catch (Exception e) {
            System.out.println(e);
        }
        Backup b = new Backup();
        app = new Appointment(conn, user, user_id);
        res = new ShowResults(user, user_id, conn);
        add = new AddResults(conn, user, user_id);
        StaffList stf = new StaffList(conn);
        PatientList plist = new PatientList(conn, user);
        tabs.getTabs().addAll(app,res, add, plist, stf, b, logoutTab);
    }

    public void userLogin() {
        try {
            conn = DriverManager.getConnection(HOST, "pacjent", "p4a8c2j6e0n1t");

        } catch (Exception e) {
            System.out.println(e);
        }
        app = new Appointment(conn, user, user_id);
        res = new ShowResults(user, user_id, conn);
        tabs.getTabs().addAll(res, app, logoutTab);
    }

    public void secretaryLogin() {
        try {
            conn = DriverManager.getConnection(HOST, "sekretarka", "s5e3k1r2e4t");

        } catch (Exception e) {
            System.out.println(e);
        }
        app = new Appointment(conn, user,user_id);
        res = new ShowResults(user, user_id, conn);
        tabs.getTabs().addAll(res, app, logoutTab);
    }

    public void doctorLogin() {
        try {
            conn = DriverManager.getConnection(HOST, "lekarz", "l2e6k0a4r8z");

        } catch (Exception e) {
            System.out.println(e);
        }

        res = new ShowResults(user, user_id, conn);
        add = new AddResults(conn, user, user_id);
        app = new Appointment(conn, user, user_id);
        tabs.getTabs().addAll(res,add,app, logoutTab);
    }

    private void checkUser() {
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(HOST, "admin", "a3d6m9i2n");

            Statement stmt1 = con.prepareStatement("SELECT u_id FROM uzytkownicy WHERE login=? AND haslo=?;");
            Statement stmt2 = con.prepareStatement("SELECT typ FROM pracownicy WHERE staff_id=?;");
            ((PreparedStatement) stmt1).setString(1, login);
            ((PreparedStatement) stmt1).setString(2, pass);

            ResultSet rs = ((PreparedStatement) stmt1).executeQuery();
            if(rs.next()!=false) {
                //TODO print test
                System.out.println("Znaleziono uzytkownika");
                user_id = rs.getInt(1);
                System.out.println("id= "+user_id);
                if(user_id<1000) { //pracownik
                    ((PreparedStatement) stmt2).setInt(1,user_id);
                    ResultSet rs2 = ((PreparedStatement) stmt2).executeQuery();
                    if(rs2.next()==false) System.out.println("brak wyniku");
                    String type = rs2.getString(1);
                    //todo print
                    System.out.println(type);
                    if(type.equals("lekarz")) {
                        user = "doctor";
                        //TODO print test
                        System.out.println("Zalogowano jako lekarz o i=" + user_id);
                    } else if(type.equals("admin")) {
                        user = "admin";

                    } else if(type.equals("sekretarka")) {
                        user = "secretary";
                    }
                } else {
                    user="user";
                }
                con.close();
                login();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Nie udało się zalogować!");
                alert.setHeaderText(null);
                alert.setContentText("Nieprawidłowy login lub hasło;");
                alert.showAndWait();
                loginTF.clear();
                passTF.clear();
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        try {
            conn.close();
            grid.getChildren().clear();
            loginTF.clear();
            passTF.clear();
            tabs.getTabs().clear();
            grid.setAlignment(Pos.CENTER);
            grid.add(loginT, 0, 0);
            grid.add(passT, 0, 1);
            grid.add(loginTF, 1, 0);
            grid.add(passTF, 1, 1);
            grid.add(loginB, 1, 2);

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}

