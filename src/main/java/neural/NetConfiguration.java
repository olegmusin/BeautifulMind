package neural;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Map;

public class NetConfiguration extends MultiLayerConfiguration {

    private int inputsNum;
    private int outputsNum;
    private int iterations;
    private int seed;
    private MultiLayerConfiguration config;

    public NetConfiguration(Map<String, Integer> hyperparams) {

        this.inputsNum = hyperparams.get ( "inputsNum" );
        this.outputsNum = hyperparams.get ( "outputsNum" );
        this.iterations = hyperparams.getOrDefault ( "iterations", 1000 );
        this.seed = hyperparams.getOrDefault ( "seed", 12345 );

        this.config = new NeuralNetConfiguration.Builder ( )
                .seed ( seed )
                .iterations ( iterations )
                .activation ( Activation.TANH )
                .weightInit ( WeightInit.XAVIER )
                .learningRate ( 0.1 )
                .regularization ( true ).l2 ( 1e-4 )
                .list ( )
                .layer ( 0, new DenseLayer.Builder ( ).nIn ( inputsNum ).nOut ( 13 )
                        .build ( ) )
                .layer ( 1, new DenseLayer.Builder ( ).nIn ( 13 ).nOut ( 13 )
                        .build ( ) )
                .layer ( 2, new OutputLayer.Builder ( LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD )
                        .activation ( Activation.SOFTMAX )
                        .nIn ( 13 ).nOut ( outputsNum ).build ( ) )
                .backprop ( true ).pretrain ( false )
                .build ( );
    }
}
