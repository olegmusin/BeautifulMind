package neural;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

public class NeuralNet {

    private MultiLayerNetwork model;

    public NeuralNet(MultiLayerConfiguration config) {

        this.model = new MultiLayerNetwork ( config );

    }

    public void train(DataSet trainData) {

        model.init ( );
        model.setListeners ( new ScoreIterationListener ( 100 ) );
        model.fit ( trainData );
    }

    public void classify(DataSet testData, int numClasses) {

        Evaluation eval = new Evaluation ( numClasses );

        INDArray output = model.output ( testData.getFeatureMatrix ( ) );
        eval.eval ( testData.getLabels ( ), output );
    }
}
