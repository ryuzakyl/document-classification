package classifier.NeuralNetwork;

import category.CategoriesSet;
import category.ICategory;
import classifier.IClassifier;
import document.IDocument;
import feature.IFeatureSelector;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 1/13/13
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class NeuralNetClassifier implements IClassifier
{
    public double Bias;
    public double LearningRate;
    public ArrayList<NeuronLayer> Layers;
    public CategoriesSet cSet;

    public NeuralNetClassifier(double bias, double learningRate)
    {
        Bias = bias;
        LearningRate = learningRate;
        Layers = new ArrayList<NeuronLayer>();
    }


    @Override
    public void train(CategoriesSet set, IFeatureSelector selector) throws Exception {

        cSet = set;
        buildLayers(3);

        ArrayList<IDocument> documents = GetDocuments();
        ArrayList<double[]> realOutputs = GetRealOutputVectors();

        double[] input, output;
        for (int i = 0; i < documents.size(); i++)
        {
            input = makeInputVector(documents.get(i));
            output = GetValue(input);
            BackPropagate(output, realOutputs.get(i));
        }
    }

    private void buildLayers(int k)
    {
        //inputLayer
        int inputLayerSize = cSet.getVocabulary().size();
        buildOneLayer(null, inputLayerSize);

        //hiddenLayer
        for (int i = 0; i < k - 2; i++)
            buildOneLayer(Layers.get(Layers.size() - 1), 10);

        //outputLayer
        int outputLayerSize = cSet.getCategories().length;
        buildOneLayer(Layers.get(Layers.size() - 1), outputLayerSize);
    }

    private void buildOneLayer(NeuronLayer previousLayer, int currentLayerSize)
    {
        if(previousLayer == null) previousLayer = new NeuronLayer(null, new Neuron[0]);

        Neuron[] layerNeurons = new Neuron[currentLayerSize];
        Neuron neuron;
        for (int i = 0; i < currentLayerSize; i++)
        {
            layerNeurons[i] = new Neuron();
            for (int j = 0; j < previousLayer.Neurons.size(); j++)
            {
                neuron = previousLayer.Neurons.get(j);
                layerNeurons[i].Parents.add(new NeuronConnection(neuron, nextDouble(-0.05, 0.05)));
            }
        }
        Layers.add(new NeuronLayer(previousLayer, layerNeurons));
    }

    private double nextDouble(double lowerBound, double upperBound)
    {
        Random random = new Random();
        return random.nextDouble() * (upperBound - lowerBound) + upperBound;
    }

    private ArrayList<IDocument> GetDocuments() {
        ArrayList<IDocument> result = new ArrayList<IDocument>();

        for (int i = 0; i < cSet.getCategories().length; i++)
        {
            ICategory category = cSet.getCategories()[i];
            for (int j = 0; j < category.GetDocuments().length; j++)
            {
                result.add(category.GetDocuments()[j]);
            }
        }
        return result;
    }

    private ArrayList<double[]> GetRealOutputVectors() {
        ArrayList<double[]> result = new ArrayList<double[]>();

        double[] partVector;
        ICategory category;
        for (int i = 0; i < cSet.getCategories().length; i++)
        {
            category = cSet.getCategories()[i];
            for (int j = 0; j < category.GetDocuments().length; j++)
            {
                partVector = new double[cSet.getCategories().length];
                partVector[i] = 1.0;
                result.add(partVector);
            }
        }
        return result;
    }

    private void BackPropagate(double[] output, double[] real) {
        int lastLayer = Layers.size() - 1;
        int neuronCount = Layers.get(lastLayer).Neurons.size();

        double[] errors = new double[neuronCount];
        for (int i = 0; i < neuronCount; i++)
            errors[i] = output[i] * (1 - output[i]) * (real[i] - output[i]);

        for (int i = lastLayer; i > 0; i--)
            errors = Layers.get(i).Backpropagate(errors, LearningRate);
    }

    @Override
    public ICategory predict(IDocument document) {
        double[] input = makeInputVector(document);
        double[] aux = GetValue(input);

        int index = -1;
        double dMax = Double.MIN_VALUE;
        for (int i = 0; i < aux.length; i++)
        {
            if (aux[i] > dMax)
            {
                dMax = aux[i];
                index = i;
            }
        }

        return cSet.getCategories()[index];
    }

    private double[] makeInputVector(IDocument document) {

        double[] result = new double[cSet.getVocabulary().size()];
        for (int i = 0; i < cSet.getVocabulary().size(); i++)
        {
            result[i] = getFrequencyInDocument(document, cSet.getVocabulary().get(i));
        }
        return result;
    }

    private double getFrequencyInDocument(IDocument document, String term) {
        return cSet.getDocumentManager().GetTermFrequencyInDocument(document, term);
    }

    private double[] GetValue(double[] input) {
        for (int i = 0; i < Layers.get(0).Neurons.size(); i++)
        {
            Neuron n = Layers.get(0).Neurons.get(i);
            n.Output = input[i];
        }

        for (int l = 1; l < Layers.size(); l++)
            Layers.get(l).Calculate(Bias);

        int lLayer = Layers.size() - 1;
        double[] output = new double[Layers.get(lLayer).Neurons.size()];
        for (int i = 0; i < Layers.get(lLayer).Neurons.size(); i++)
            output[i] = Layers.get(lLayer).Neurons.get(i).Output;

        return output;
    }
}





