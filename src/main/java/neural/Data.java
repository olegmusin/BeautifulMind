package neural;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;

public class Data {

    private File dataFile;
    private SplitTestAndTrain testAndTrain;
    private DataSet trainingData;
    private DataSet testData;
    private MultiLayerNetwork model;


    public Data(File dataFile) {
        this.dataFile = dataFile;
    }

    private DataSet readCSVDataset(int batchSize, int labelIndex, int numClasses)
            throws IOException, InterruptedException {

        DataSetIterator iterator;
        DataSet allData;
        try (RecordReader rr = new CSVRecordReader ( )) {
            rr.initialize ( new FileSplit ( dataFile ) );
            iterator = new RecordReaderDataSetIterator ( rr, batchSize, labelIndex, numClasses );
        }
        allData = iterator.next ( );
        allData.shuffle ( );
        return allData;
    }


    public void process() {

        SplitAndNormalize ( );
        MultiLayerConfiguration config = BuildModel ( );
        RunModelTraining ( config );//TODO: training process could take long time here...

    }

    public void classify(int numClasses) {
        //evaluate the model on the test set
        Evaluation eval = new Evaluation ( numClasses );

        INDArray output = model.output ( testData.getFeatureMatrix ( ) );
        eval.eval ( testData.getLabels ( ), output );
        // log.info(eval.stats());
    }

    private void RunModelTraining(MultiLayerConfiguration config) {

        model = new MultiLayerNetwork ( config );
        model.init ( );
        model.setListeners ( new ScoreIterationListener ( 100 ) );
        model.fit ( trainingData );

    }

    private MultiLayerConfiguration BuildModel() {

        final int numInputs = 13;
        int outputNum = 13;
        int iterations = 1000;
        long seed = 6;

        return new NeuralNetConfiguration.Builder ( )
                .seed ( seed )
                .iterations ( iterations )
                .activation ( Activation.TANH )
                .weightInit ( WeightInit.XAVIER )
                .learningRate ( 0.1 )
                .regularization ( true ).l2 ( 1e-4 )
                .list ( )
                .layer ( 0, new DenseLayer.Builder ( ).nIn ( numInputs ).nOut ( 13 )
                        .build ( ) )
                .layer ( 1, new DenseLayer.Builder ( ).nIn ( 13 ).nOut ( 13 )
                        .build ( ) )
                .layer ( 2, new OutputLayer.Builder ( LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD )
                        .activation ( Activation.SOFTMAX )
                        .nIn ( 13 ).nOut ( outputNum ).build ( ) )
                .backprop ( true ).pretrain ( false )
                .build ( );
    }

    private void SplitAndNormalize() {

        trainingData = testAndTrain.getTrain ( );
        testData = testAndTrain.getTest ( );

        DataNormalization normalizer = new NormalizerStandardize ( );
        normalizer.fit ( trainingData );           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
        normalizer.transform ( trainingData );     //Apply normalization to the training data
        normalizer.transform ( testData );
    }

    public void prepare(int batchSize, int labelIndex, int numClasses) {
        try {
            testAndTrain = readCSVDataset ( batchSize, labelIndex, numClasses )
                    .splitTestAndTrain ( 0.65 );  //Use 65% of data for training
        } catch (IOException | InterruptedException e) {
            e.printStackTrace ( );
        }
    }
}
