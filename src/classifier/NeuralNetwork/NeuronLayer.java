package classifier.NeuralNetwork;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 1/13/13
 * Time: 11:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class NeuronLayer
{
    public NeuronLayer PreviousLayer;
    public ArrayList<Neuron> Neurons;

    public NeuronLayer(NeuronLayer prevLayer, Neuron[] neurons)
    {
        PreviousLayer = prevLayer;
        Neurons = new ArrayList<Neuron>();
        Collections.addAll(Neurons, neurons);
    }

    public void Calculate(double bias)
    {
        for (int i = 0; i < Neurons.size(); i++)
        {
            Neuron n = Neurons.get(i);
            double dSum = -bias;

            for (int j = 0; j < n.Parents.size(); j++)
            {
                NeuronConnection c = n.Parents.get(j);
                dSum += c.Neuron.Output * c.Weight;
            }

            n.Output = 1.0 / (1.0 + Math.exp(-dSum));
        }
    }

    public double[] Backpropagate(double[] errors, double learningRate)
    {
        double[] nErrors = new double[PreviousLayer.Neurons.size()];
        for (int i = 0; i < Neurons.size(); i++)
        {
            Neuron n = Neurons.get(i);

            for (int j = 0; j < n.Parents.size(); j++)
            {
                NeuronConnection c = n.Parents.get(j);
                nErrors[j] += (c.Neuron.Output * (1 - c.Neuron.Output)) * (c.Weight * errors[i]);
            }
        }

        for (int i = 0; i < Neurons.size(); i++)
        {
            Neuron n = Neurons.get(i);
            for (int j = 0; j < n.Parents.size(); j++)
            {
                NeuronConnection c = n.Parents.get(j);
                c.Weight += learningRate * c.Neuron.Output * errors[i];
            }
        }
        return nErrors;
    }
}
