/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import gui.BellsSimpleGUIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Quinten Holmes
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        BellsSimpleGUIController controller = new BellsSimpleGUIController(primaryStage);
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/BellsSimpleGUI.fxml"));
        fxmlLoader.setController(controller);
      
        Parent root = (Parent) fxmlLoader.load();
        
        Scene scene = new Scene(root);        
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(e -> {
            controller.shutDown();
            System.exit(1);
        });
        
        //show main program
        primaryStage.show();
        /*
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
                new test();
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
        */
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
