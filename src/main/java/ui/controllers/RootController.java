package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import neural.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.PredictorApp;

import java.io.File;


public class RootController {

    @FXML
    private TextField textFieldDataFile;
    @FXML
    private Button btnTrainDataFile;
    @FXML
    private Button btnOutputFile;
    @FXML
    private Button btnStart;
    @FXML
    private Slider slrBatchSize;
    @FXML
    private TextField txtBatchSize;
    @FXML
    private Slider slrNumOfClasses;
    @FXML
    private TextField txtNumOfClasses;
    @FXML
    private TextField txtClassIndex;
    @FXML
    private Slider slrIterations;
    @FXML
    private TextField txtIterations;

    private PredictorApp app;

    private static Logger log = LoggerFactory.getLogger ( PredictorApp.class );

    public void setApp(PredictorApp app) {
        this.app = app;
    }


    public void initialize() {
        initSliders ( );
        initTextFieldsInput ( );
    }

    private void initSliders() {
        slrBatchSize.valueProperty ( ).addListener ( (observable, oldValue, newValue) ->
        {
            txtBatchSize.clear ( );
            txtBatchSize.appendText ( "" + newValue.intValue ( ) );
        } );
        slrNumOfClasses.valueProperty ( ).addListener ( (observable, oldValue, newValue) ->
        {
            txtNumOfClasses.clear ( );
            txtNumOfClasses.appendText ( "" + newValue.intValue ( ) );

        } );
        slrIterations.valueProperty ( ).addListener ( (observable, oldValue, newValue) ->
        {
            txtIterations.clear ( );
            txtIterations.appendText ( "" + newValue.intValue ( ) );

        } );

    }

    public void initTextFieldsInput() {
        txtBatchSize.textProperty ( ).addListener ( (observable, oldValue, newValue) -> {
            if (newValue.isEmpty ( )) return;
            slrBatchSize.setValue ( Double.parseDouble ( newValue ) );
        } );
        txtNumOfClasses.textProperty ( ).addListener ( (observable, oldValue, newValue) -> {
            if (newValue.isEmpty ( )) return;
            slrNumOfClasses.setValue ( Double.parseDouble ( newValue ) );
        } );
        txtIterations.textProperty ( ).addListener ( (observable, oldValue, newValue) -> {
            if (newValue.isEmpty ( )) return;
            slrIterations.setValue ( Double.parseDouble ( newValue ) );
        } );
    }

    public void saveOutputFileHandler(ActionEvent actionEvent) {
    }

    public void openDataFileHandler(ActionEvent actionEvent) {

        selectDataFile ( );
    }

    private void selectDataFile() {

        FileChooser fileChooser = new FileChooser ( );
        fileChooser.setTitle ( "Open Data File" );
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter ( "CSV files (*.CSV)", "*.csv" );
        fileChooser.getExtensionFilters ( ).add ( extFilter );
        File file = fileChooser.showOpenDialog ( app.getPrimaryStage ( ) );
        if (file != null) {
            textFieldDataFile.setText ( file.getAbsolutePath ( ) );
            btnStart.setVisible ( true );
        }
    }

    public void startBtnHandler(ActionEvent actionEvent) {

        Data data = new Data ( new File ( textFieldDataFile.getText ( ) ) );

        log.info ( "Preparing data..." );
        final int batchSize = slrBatchSize.valueProperty ( ).intValue ( );
        final int labelIndex = Integer.parseInt ( txtClassIndex.getText ( ) );
        final int numClasses = slrNumOfClasses.valueProperty ( ).intValue ( );

        data.prepare ( batchSize, labelIndex, numClasses );

        log.info ( "Training network..." );
        data.process ( );

        log.info ( "Classifying test data..." );
        data.classify ( numClasses );

    }


}
