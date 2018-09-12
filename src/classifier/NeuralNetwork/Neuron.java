package classifier.NeuralNetwork;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 1/13/13
 * Time: 10:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class Neuron
{
    public ArrayList<NeuronConnection> Parents;
    public double Output;

    public Neuron()
    {
        Parents = new ArrayList<NeuronConnection>();
    }
}
