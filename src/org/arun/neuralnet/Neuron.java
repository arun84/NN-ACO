
package org.arun.neuralnet;

/**
 *
 * @author arun
 */
public class Neuron {
    public double output;
    public double[] weights;
    public int numWeights;

    Neuron(int numWeights) {
        this.numWeights = numWeights;
        this.weights = new double[this.numWeights];
    }
    
    public void calculateOutput(double[] input) {
        output=0;
        for (int i=0;i<numWeights;i++) {
            output+=weights[i]*input[i];
        }
    }
}
