package neural;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DataContext {

    private File dataFile;
    private DataSet trainData;
    private DataSet testData;

    public DataSet getTrainData() {
        return trainData;
    }

    public DataSet getTestData() {
        return testData;
    }

    public DataContext(File dataFile, Map<String, Integer> params) throws IOException, InterruptedException {

        this.dataFile = dataFile;

        SplitTestAndTrain splitSet = readCSVDataset ( params.get ( "batchSize" ), params.get ( "labelIndex" ), params.get ( "numClasses" ) )
                .splitTestAndTrain ( 0.65 );
        this.trainData = splitSet.getTrain ( );
        this.testData = splitSet.getTest ( );

    }

    private DataSet readCSVDataset(int batchSize, int labelIndex, int numClasses) throws IOException, InterruptedException {

        try (RecordReader reader = new CSVRecordReader ( )) {
            reader.initialize ( new FileSplit ( dataFile ) );
            DataSet allData = new RecordReaderDataSetIterator ( reader, batchSize, labelIndex, numClasses ).next ( );
            allData.shuffle ( );
            return allData;
        }


    }

    public void normalizeData() {
        DataNormalization normalizer = new NormalizerStandardize ( );
        normalizer.fit ( trainData );           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
        normalizer.transform ( trainData );     //Apply normalization to the training data
        normalizer.transform ( testData );
    }


}
