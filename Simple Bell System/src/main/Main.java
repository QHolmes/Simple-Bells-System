/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import gui.BellsSimpleGUIController;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import jfxtras.styles.jmetro8.JMetro;

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
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(e -> {
            controller.shutDown();
            System.exit(1);
        });
        
        primaryStage.setFullScreenExitHint("Press F11 to toggles full-screen mode");
        new JMetro(JMetro.Style.LIGHT).applyTheme(scene);
       
        //show main program
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
