package classifier.NeuralNetwork;

import classifier.NeuralNetwork.Neuron;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 1/13/13
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class NeuronConnection
{
    public double Weight;
    public classifier.NeuralNetwork.Neuron Neuron;

    public NeuronConnection(Neuron n, double weight)
    {
        Weight = weight;
        Neuron = n;
    }
}
