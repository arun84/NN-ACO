
package org.arun.neuralnet;

import java.util.Random;
import org.apache.commons.math.random.RandomDataImpl;

/**
 *
 * @author arun
 */
public class AcoFramework {
    double[][] archive; /*Archive of NN weights*/
    int numLayers = 3; /*number of layers in the Neural Network*/
    int[] nodesPerLayer; /*array containing the number of nodes in each layer*/
    int numIns; /*number of inputs*/
    int numOuts; /* number of outputs*/
    int archiveSize; /*number of sets of NN weigthts in the archive*/
    int numWeights = 0; /*total number of weights in the neural network*/
    double[] fitness; /*array to store the fitness value of each NN weight set*/
    double[] solWeights; /*weight for each solution */
    double sumSolWeights = 0; /*sum of all solution weights*/
    double[] solution;
    NeuralNetwork nn; /*Neural network to be trained*/
    double epsilon = 0.3; /*affects pheromone evaporation rate*/
    double q = 2.0;
    Random rand;
    RandomDataImpl grand;
    double test_error = -1;

    /*
     *Constructor that creates the ACO framework.
     *Takes a neural network and the size of the archive as parameters.
     */
    AcoFramework(NeuralNetwork neuralNet, int archive_Size) {

        nn = neuralNet;
        archiveSize = archive_Size;
        nodesPerLayer = nn.nodesPerLayer;
        numIns = nodesPerLayer[0];
        numOuts = nodesPerLayer[nn.columns-1];
        numWeights = nn.numWeights;
        initialize();
    }

    /*
     * Method to create a initialize the archive with random weights.
     */
    protected void initialize() {
        int i,j;
        archive = new double[archiveSize*2][numWeights];
        fitness = new double[archiveSize*2];
        solWeights =  new double[archiveSize];
        rand = new Random();
        grand = new RandomDataImpl();

        /*
         * fill archive with random values for weights
         */
        for (i=0;i<archiveSize*2;i++)
            for (j=0;j<(numWeights);j++)
                archive[i][j] = (rand.nextDouble()*2)-1;
    }

    /*
     * Method to compute the fitness of all the weight sets in the archive.
     */
    public void computeFitness(int type) {
        if (type == 0) {
            for (int i=0;i<archiveSize*2;i++) {
                nn.setWeights(archive[i]);
                fitness[i] = nn.computeError(true);
            }
        }
        if (type == 1) {
            nn.setWeights(solution);
            test_error = nn.computeError(false);
            System.out.println("Test error: " + test_error);
        }
    }

    /*
     *Implementation of bubble sort algorithm to sort the archive according to the
     *fitness of each solution. This method has a boolean parameter, which is set
     *to true if the method is called to sort the just initialized array.
     */

     public void sortArchive(boolean init) {
        int i,j;
        int n = archive.length;

        /*System.out.println("Archive size = " + n);

        System.out.println("fitness before sort:");

        for (int x=0;x<n;x++)
            System.out.println(fitness[x]);*/

        for (i=0;i<n;i++)
            for (j=0;j<n-1;j++) {
                try {
                if (fitness[j] > fitness[j+1]) {

                    double temp = fitness[j];
                    fitness[j] = fitness[j+1];
                    fitness[j+1] = temp;

                    double[] tempTrail = archive[j];
                    archive[j] = archive[j+1];
                    archive[j+1] = tempTrail;
                }
                }
                catch(Exception e) {
                    System.out.println("error: " + j+" n "+n);
                    System.exit(0);
                }
            }
        
        /*System.out.println("fitness after sort:");
        for (int x=0;x<n;x++)
            System.out.println(fitness[x]);*/
    }

     /*
      * Method to compute the weights for each set of weights in the archive
      */
     public void computeSolutionWeights() {
         sumSolWeights = 0;
         for (int i=0;i<archiveSize;i++) {
             /*double exponent = (i*i)/(2*q*q*archiveSize*archiveSize);
             solWeights[i] =
             (1/(q*archiveSize*Math.sqrt(2*Math.PI)))*Math.pow(Math.E, -exponent);
             
             sumSolWeights += solWeights[i];*/

             //System.out.println("sumsol "+ sumSolWeights);
             //System.out.println("solweights "+ solWeights[i]);
             if (i==0) {
                 solWeights[i] = 1;
                 sumSolWeights += 1;
             }
             else {
                 solWeights[i] = solWeights[i-1]*0.5;
                 sumSolWeights += solWeights[i];

             }
         }
         //System.out.println(sumSolWeights);
     }


     /*
      * Method to calculate the standard deviation of a particular weight of a
      * particular weight set
      */
     protected double computeSD(int x,int l) {
         double sum=0, mean = 0.0;
         for (int i=0;i<archiveSize;i++) {
             //sum += (Math.abs(archive[i][x] - archive[l][x])/(archiveSize-1));
             sum += Math.abs(archive[i][x] - archive[l][x]);
         }
         mean = sum/(archiveSize-1);
         //if(sum ==0)
           //  System.out.println("sum = 0 "+ l);
         //System.out.println(mean);
         return (epsilon*mean + 0.1);
     }

     /*
      * select a Gaussian function that compose the Gaussian Kernel PDF
      */
     protected int selectPDF() {
         int i, l=0;
         double temp=0;
         double r = rand.nextDouble();
         
         for (i=0;i<archiveSize;i++) {
             //temp = .9*((solWeights[i]/sumSolWeights) - 10)/(-10 - 10) + .05;
             temp = solWeights[i]/sumSolWeights;
             //System.out.println("solweights " + solWeights[i] + " sumsolw "+sumSolWeights + "div "+ solWeights[i]/sumSolWeights);
             if (r<temp) {
                 l=i;
                 break;
             }
       
         }
         
         //System.out.println("pdfrand "+ i + "randval "+ r + " fraction= "+ temp +  " l= " + l);
         return l;
     }

     protected void generateBiasedWeights() {
         int i,j,pdf;
         double sigma; /*standard deviation*/
         double mu; /*mean*/

         for (i=archiveSize;i<archiveSize*2;i++) {
             pdf = selectPDF();
             for (j=0;j<numWeights;j++) {
                 sigma = computeSD(j,pdf);
                 //System.out.println("sigma "+sigma + " pdf "+ pdf);
                 mu = archive[pdf][j];
                 archive[i][j] = grand.nextGaussian(mu, sigma);
             }
         /*System.out.println("");
         for (j=0;j<numWeights;j++)
          System.out.print(" " + archive[i][j]);*/
         }

     }

     public boolean trainNeuralNet() {
         computeFitness(0);
         sortArchive(true);
         computeSolutionWeights();
         generateBiasedWeights();
         sortArchive(false);

         for (int j=0;j<100000;j++) {
             computeFitness(0);
             sortArchive(false);
             System.out.println(fitness[0]);
             if (fitness[0] < 0.9) {
                 System.out.println("Solution found in iteration" + (j+1));
                 solution = archive[0];
                 for (int i=0;i<numWeights;i++)
                     System.out.print(archive[0][i]);
                 return true;
             }
             computeSolutionWeights();
             generateBiasedWeights();
         }
         System.out.println("Network did not converge!");
         return false;
     }

     public void testNeuralNet() {
         computeFitness(1);
     }
}
