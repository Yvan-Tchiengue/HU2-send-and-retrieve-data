package pis.hue2.common;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pis.hue2.client.LaunchClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

public class Gui extends Application {
    public static void main(String[] args) {
        //Programm starten
        launch(args);
    }

    private LaunchClient client = null;

    public void disconnect() {
        if (client != null && client.isIsloggedin()) {
            client.disconnect();
        }
        client = null;
    }

    private Text actiontarget1;

    public boolean tryConnect() {
        try {
            client = new LaunchClient(new Socket(InetAddress.getLocalHost(), 2000));
            return true;
        } catch (IllegalStateException e) {
            actiontarget1.setText("Too many clients on the server!");
            actiontarget1.setFill(Color.FIREBRICK);
            return false;
        } catch (IOException e) {
            actiontarget1.setText("An error occurd!");
            actiontarget1.setFill(Color.FIREBRICK);
            return false;
        }
    }

    /**
     * Die Methode wird aufgerufen, wenn JavaFX die Application startet.
     *
     * @param primaryStage die primäre Stage auf der alles in der Application angezeigt werden soll.
     */
    @Override
    public void start(Stage primaryStage) {

        //Create a GridPane Layout
        GridPane grid = new GridPane();

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(100, 100, 100, 100));

        Scene scene = new Scene(grid, 1100, 660);
        primaryStage.setScene(scene);

        //Add Text, Labels, and Text Fields
        Text scenetitle = new Text("Welcome");
        scenetitle.setId("welcome-text");
        //scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 1, 0, 2, 1);
        scenetitle.setCaretPosition(-2);

        Text tabSpace = new Text("Welcomeeeeeeeeeeeeeeeeeeeeeeeeee");
        tabSpace.setFill(Color.rgb(169,169,169)) ;
        grid.add(tabSpace, 0, 0);

        // Kodiere Methode

        /*Text kodiere = new Text("Kodierung");
        kodiere.setId("welcome-text");
        grid.add(kodiere, 0, 3, 2, 1);*/
        Label userName = new Label("Save als:");
        grid.add(userName, 0, 6);

        TextField putDatei = new TextField();
        grid.add(putDatei, 1, 6);

       // TextArea t1 = new TextArea();
        // grid.add(t1, 1, 10);
        //VBox v1 = new VBox(t1);
        //Scene scene1 = new Scene(v1, 1, 10);
        //primaryStage.setScene(scene1);
        //primaryStage.show();


       // Label l1 = new Label("hhuf");
        //grid.add(l1, 1, 11);

        /*Label pw = new Label("Lösungswort :");
        grid.add(pw, 0, 4);

        TextField lsw = new TextField();
        grid.add(lsw, 1, 6);*/

        // Group
        ToggleGroup group = new ToggleGroup();

// Radio 1: Caesar Kodierung
        Button button1 = new Button("File Hochladen");


// Radio 2: Wuerfel Kodierung
        //Button button2 = new Button("Wuerfel Kodierung");


        // Add a Button and Text
        Button btn = new Button("Done");
        HBox hbBtn = new HBox(20);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(button1);
        //hbBtn.getChildren().add(button2);

        grid.add(hbBtn, 1, 8);


        Text actiontarget = new Text();
        actiontarget.setId("actiontarget");
        grid.add(actiontarget, 0, 12);

        actiontarget1 = new Text();
        actiontarget1.setId("actiontarget");
        grid.add(actiontarget1, 0, 10);


        // Dekodiere Methode
        /*Text dekodiere = new Text("Dekodierung");
        dekodiere.setId("welcome-text");
        grid.add(dekodiere, 0, 18, 2, 1);*/
        Label klt = new Label("File on Server");
        grid.add(klt, 0, 14);

        TextArea t1 = new TextArea();
        grid.add(t1, 1, 11);

        //TextField klC = new TextField();
        //grid.add(klC, 1, 14);

        /*Label lw = new Label("Lösungswort :");
        grid.add(lw, 0, 20);

        TextField loesW = new TextField();
        grid.add(loesW, 1, 22);*/

        // Group
        //ToggleGroup grp = new ToggleGroup();

// Radio 1: Caesar Dekodierung

        //Radio 3: Wuerfel Dekodierung
        Button button1D = new Button("File Downloaden");
        Button button2D = new Button("Delete File");
        Button button3D = new Button("refresh List of File");

        //ListView<String> view = new ListView<>();
        ComboBox<String> view = new ComboBox<>();
        HBox hbBtnV = new HBox(20);
        hbBtnV.getChildren().add(view);
        grid.add(hbBtnV, 1, 14);
        // Add a Button and Text

        HBox hbBtn2 = new HBox(20);
        hbBtn2.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn2.getChildren().add(button1D);
        hbBtn2.getChildren().add(button2D);
        hbBtn2.getChildren().add(button3D);
        //hbBtn2.getChildren().add(view);

        grid.add(hbBtn2, 1, 16);

        Button button2 = new Button("Start");
        Button button3 = new Button("Disconnect");
        //Button bt = new Button("Done");
        HBox hbBtn1 = new HBox(22);
        hbBtn1.setAlignment(Pos.BOTTOM_LEFT);
        hbBtn1.getChildren().add(button2);

        HBox hbBtn3 = new HBox(22);
        hbBtn3.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn3.getChildren().add(button3);

        grid.add(hbBtn1, 0, 1);
        grid.add(hbBtn3, 3, 1);

        Text actiontarget2 = new Text();
        actiontarget2.setId("actiontarget2");
        actiontarget2.setWrappingWidth(400);
        grid.add(actiontarget2, 0, 18);
        // Add Code to Handle an Event
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                actiontarget1.setText("");
                actiontarget2.setText("");
                if (client == null) {
                    //Client ist nicht verbunden!
                    return;
                }
                FileChooser fileChooser = new FileChooser();
                t1.appendText("\nACK received!");
                fileChooser.setTitle("Open Upload File");
                File f = fileChooser.showOpenDialog(primaryStage);
                if (f == null) {
                    actiontarget1.setText("No file selected!");
                    actiontarget1.setFill(Color.FIREBRICK);
                    return;
                }
                String s = putDatei.getText();
                if (s == null || s.isEmpty()) {
                    actiontarget1.setText("No file name selected!");
                    actiontarget1.setFill(Color.FIREBRICK);
                    return;
                } else if (s.contains("/") || s.contains("\\") || s.contains("?") ||
                        s.contains("*") || s.contains("\"") || s.contains("<") ||
                        s.contains(">") || s.contains(":") || s.contains("|")) {
                    actiontarget1.setText("File name can not contain one of: / \\ ? : * \" < > |");
                    actiontarget1.setFill(Color.FIREBRICK);
                    return;
                }
                client.putFile(putDatei.getText());
                boolean b = false;
                try {
                    FileInputStream is = new FileInputStream(f);
                    b = client.writeData(f.length(), is);
                    is.close();
                } catch (IOException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                    t1.appendText("DND unsucessful operation");
                }
                actiontarget1.setText(b ? "File successfully uploaded" : "Error on file upload!");
                actiontarget1.setFill(Color.FIREBRICK);

                t1.appendText("\nACK received!");
            }
        });


        button3.setOnAction(e -> {
            disconnect();
            button1.setDisable(true);
            button1D.setDisable(true);
            button2D.setDisable(true);
            button3D.setDisable(true);
            button2.setDisable(false);
            button3.setDisable(true);
            t1.appendText("\nDSC Disconnected!");
        });

        button1D.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                actiontarget1.setText("");
                actiontarget2.setText("");
                if (client == null) {
                    //Client ist nicht verbunden!
                    return;
                }
                //actiontarget.setFill(Color.FIREBRICK);
                //kodiere Input
                // String kodiereKl = loesW.getText();
                String filename = view.getValue();
                if (filename == null) {
                    actiontarget2.setText("No File selected!");
                    actiontarget2.setFill(Color.FIREBRICK);
                    return;
                }
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save downloaded file");
                File f = fileChooser.showSaveDialog(primaryStage);
                if (f == null) {
                    actiontarget2.setText("No file specified to download!");
                    actiontarget2.setFill(Color.FIREBRICK);
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(f);
                    if (client.getFile(filename, fos)) {
                        actiontarget2.setText("File successfully downloaded to: " + f.getAbsolutePath());
                    } else {
                        actiontarget2.setText("Error by file download!");
                    }
                    fos.close();
                    actiontarget2.setFill(Color.FIREBRICK);
                } catch (IOException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                    t1.appendText("\nDND operation unsucessful!");
                }
                t1.appendText("\nACK received");
            }
        });

        button2D.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                actiontarget1.setText("");
                actiontarget2.setText("");
                if (client == null) {
                    //Client ist nicht verbunden!
                    return;
                }
                //String kodiereKl = loesW.getText();
                String filename = view.getValue();
                if (filename == null) {
                    actiontarget2.setText("No File selected!");
                    actiontarget2.setFill(Color.FIREBRICK);
                    return;
                }
                actiontarget2.setText("Result Delete: " + (client.deleteFile(filename) ? "Success" : "Failure"));
                actiontarget2.setFill(Color.FIREBRICK);
                t1.appendText("\nACK received!");
            }
        });

        button3D.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                actiontarget1.setText("");
                actiontarget2.setText("");
                t1.appendText("\nACK received!");
                if (client == null) {
                    //Client ist nicht verbunden!
                    return;
                }
                //String kodiereKl = loesW.getText();
                //String kodiereLs = klC.getText();
                view.setItems(FXCollections.observableArrayList(client.doList()));
            }
        });

        button1.setDisable(true);
        button1D.setDisable(true);
        button2D.setDisable(true);
        button3D.setDisable(true);
        button3.setDisable(true);

        button2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                actiontarget1.setText("");
                actiontarget2.setText("");
                if (client != null) {
                    //Schon verbunden
                    button2.setDisable(true);
                    return;
                }
                boolean b = tryConnect();
                button1.setDisable(!b);
                button1D.setDisable(!b);
                button2D.setDisable(!b);
                button3D.setDisable(!b);
                button2.setDisable(b);
                button3.setDisable(!b);
                t1.appendText("\nACK received");
            }

        });


        URL url = this.getClass().getClassLoader().getResource("pis/hue2/common/stylesheet.css");
        scene.getStylesheets().add(url.toExternalForm());
        primaryStage.show();
        Runtime.getRuntime().addShutdownHook(new Thread(this::disconnect));
    }
}