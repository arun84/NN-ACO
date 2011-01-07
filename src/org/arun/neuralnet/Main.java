
package org.arun.neuralnet;

/**
 *
 * @author arun
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        double[][] trainingData, testData;
        int numInputs = 10;
        int numOutputs = 1;
        int numTrainCases = 25010;
        int numTestCases = 10;
        int repoSize = 20; //size of the repository
        /*String trainingFilePath = "E:\\course\\csc 215\\project\\training_data\\wine_training.dat";
        String testFilePath = "E:\\course\\csc 215\\project\\training_data\\wine_test.dat";*/

        String trainingFilePath = "E:\\course\\csc 215\\project\\training_data\\poker-hand-training.dat";
        String testFilePath = "E:\\course\\csc 215\\project\\training_data\\poker-hand-test.dat";

        /*
         * read training and test data.
         */
        InputParser training_cases = 
                new InputParser(trainingFilePath, numInputs, numOutputs, numTrainCases);
        InputParser test_cases = 
                new InputParser(testFilePath, numInputs, numOutputs, numTestCases);
        training_cases.readInput();
        test_cases.readInput();

        /*
         * Scale down training and test data.
         */
        trainingData = training_cases.scaleDown();
        testData = test_cases.scaleDown();

        System.out.println("Scaled down test data:\n");
        for (int i=0;i<numTrainCases;i++) {
            for (int j=0;j<11;j++)
                System.out.print(trainingData[i][j] + " ");
            System.out.println("");
        }
        System.out.println("");
        System.out.println("End: Test data:");

        int[] numPerLayer = new int[3];
        numPerLayer[0] = numInputs; numPerLayer[1] = numInputs; numPerLayer[2] = numOutputs;
        NeuralNetwork nn = new NeuralNetwork(3, numInputs, numPerLayer);
        nn.assignDataSet(trainingData);
        AcoFramework AF = new AcoFramework(nn, repoSize);
        
        if (AF.trainNeuralNet()) {
            System.out.println("******Test Output*****");
            nn.assignDataSet(testData);
            AF.testNeuralNet();
        }
        else {
            System.exit(0);
        }
    }
}
