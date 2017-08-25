package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ui.controllers.RootController;

import java.io.IOException;

public class PredictorApp extends Application {


    private AnchorPane rootLayout;

    private static String rootLayoutFileUrl = "views/RootLayout.fxml";

    public static void main(String[] args) {
        launch ( args );
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle ( "Predictor" );
        showStartScene ( );

    }

    private void showStartScene() {

        try {
            FXMLLoader loader = new FXMLLoader ( );
            loader.setLocation ( PredictorApp.class.getResource ( rootLayoutFileUrl ) );
            rootLayout = loader.load ( );

            primaryStage.setScene ( new Scene ( rootLayout ) );
            primaryStage.show ( );
            RootController rootController = loader.getController ( );
            rootController.setApp ( this );

        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }


}
