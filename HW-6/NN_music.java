import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class NN_music {
	public static void main(String[] args) {
		// args[0] is the training data
		// args[1] is the training labels
		// args[2] is the development data
		// args[3] is the weights for inputs to hidden layer for stochastic gradient descent
		// args[4] is the weights for hidden layer to output for stochastic gradient descent
		ArrayList<double[]> trainingData = new ArrayList<double[]>();
		File trainFile = new File(args[0]);
		File trainLabelFile = new File(args[1]);
		double[][] gdWeights1 = new double[3][5];
		double[] gdWeights2 = new double[4];
		double[][] stWeights1 = new double[3][5];
		double[] stWeights2 = new double[4];
		double[][] errorWeights1 = new double[3][5];
		double[] errorWeights2 = new double[4];
		double gdLoss = Double.MAX_VALUE;
		ArrayList<Double> real_outputs = new ArrayList<Double>();
		// initialize some random weights, 5 for input layer, 4 for hidden layer
		for (int i = 0; i<3; i++) {
			for (int j = 0; j < 5; j++) {
				gdWeights1[i][j] = Math.random() - 0.5;
			}
		}
		for (int i = 0; i < 4; i++) {
			gdWeights2[i] = 1 - 2 * Math.random() - 0.5;
		}
		// get all training labels
		Scanner trainLsc;
		try {
			trainLsc = new Scanner(trainLabelFile);
			while(trainLsc.hasNextLine()) {
				if(trainLsc.nextLine().equals("yes")) {
					real_outputs.add((double) 1);
				} else {
					real_outputs.add((double) 0);
				}
			}
			trainLsc.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		// read all the input examples
		try {
			Scanner trainsc = new Scanner(trainFile);
			boolean columnNames = true;
			while(trainsc.hasNextLine()) {
				if(columnNames) {
					columnNames = false;
					trainsc.nextLine();
					continue;
				} else {
					String[] inputValues = trainsc.nextLine().split(",");
					double[] inputs = new double[inputValues.length+1];
					// inputs[0] will always be bias 1
					inputs[0] = 1;
					// Normalize and store the inputs
					for(int i = 1; i < inputs.length; i++) {
						if(i == 1){
							inputs[i] = (Double.parseDouble(inputValues[i-1]) - 1900) / 100;
						} else if (i == 2) {
							inputs[i] = Double.parseDouble(inputValues[i-1]) / 7;
						} else {
							if(inputValues[i-1].equals("yes")) {
								inputs[i] = 1;
							} else {
								inputs[i] = 0;
							}
						}
					}
					trainingData.add(inputs);
				}
			}
			trainsc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// begin training using gradient descent
		// stopping criteria: when the loss is less than 1
		double minLoss = gdLoss;
		double gdlearningRate = 0.1;
		while(gdLoss > 1) {
			ArrayList<Double> trainingOutputs = new ArrayList<Double>();
			gdLoss = 0;
			double sumOutputsError = 0;
			for(int i = 0; i < trainingData.size(); i++) {
				trainingOutputs.add(forwardPath(real_outputs.get(i), trainingData.get(i), gdlearningRate, gdWeights1, gdWeights2, false));
				sumOutputsError+= Math.pow((trainingOutputs.get(i) - real_outputs.get(i)), 2);
			}
			gdLoss = sumOutputsError / 2;
			if ((minLoss - gdLoss) < 0.0001){
				System.out.println(gdLoss);
				break;
			}
			else {
				minLoss = gdLoss;
				System.out.println(gdLoss);
			}
		}

		System.out.println("GRADIENT DESCENT TRAINING COMPLETED!");

		// begin training using stochastic gradient descent
		File trainWeight1File = new File(args[3]);
		try {
			Scanner weight1sc = new Scanner(trainWeight1File);
			int row1 = 0;
			while(weight1sc.hasNextLine()) {
				String[] weights1_tmp = weight1sc.nextLine().split(",");
				for (int i = 0; i<3; i++) {
					stWeights1[i][row1] = Double.parseDouble(weights1_tmp[i]);
				}
				row1++;
			}
			weight1sc.close();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		File trainWeight2File = new File(args[4]);
		try {
			Scanner weight2sc = new Scanner(trainWeight2File);
			int row2 = 0;
			while(weight2sc.hasNextLine()) {
				String weights2_tmp = weight2sc.nextLine();
				stWeights2[row2] = Double.parseDouble(weights2_tmp);
				row2++;
			}
			weight2sc.close();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		double stlearningRate = 0.4;
		for (int times = 0; times < 15; times++) {
			ArrayList<Double> trainingOutputs = new ArrayList<Double>();
			double stLoss = 0;
			double sumOutputsError = 0;
			for(int i = 0; i < trainingData.size(); i++) {
				trainingOutputs.add(forwardPath(real_outputs.get(i), trainingData.get(i), stlearningRate, stWeights1, stWeights2, false));
				sumOutputsError+= Math.pow((real_outputs.get(i) - trainingOutputs.get(i)), 2);
			}
			stLoss = sumOutputsError / 2;
			System.out.println(stLoss);
		}
		System.out.println("STOCHASTIC GRADIENT DESCENT TRAINING COMPLETED! NOW PREDICTING.");
		// begin prediction
		File testFile = new File(args[2]);
		//File testLFile = new File(args[5]);
		try {
			//Scanner testLsc = new Scanner(testLFile);
			Scanner testsc = new Scanner(testFile);
			boolean firstline = true;
			double real_test_out = 0;
			double testError = 0;
			while(testsc.hasNextLine()) {
				//if(testLsc.hasNextLine()) {
				//	if(testLsc.nextLine().equals("yes")) {
				//		real_test_out = 1;
				//	} else {
				//		real_test_out = 0;
				//	}
				//}
				if(firstline) {
					firstline = false;
					testsc.nextLine();
					continue;
				} else {
					String[] testinputValues = testsc.nextLine().split(",");
					double[] testinputs = new double[testinputValues.length+1];
					// inputs[0] will always be bias 1
					testinputs[0] = 1;
					// Normalize and store the inputs
					for(int i = 1; i < testinputs.length; i++) {
						if(i == 1){
							testinputs[i] = (Double.parseDouble(testinputValues[i-1]) - 1900) / 100;
						} else if (i == 2) {
							testinputs[i] = Double.parseDouble(testinputValues[i-1]) / 7;
						} else {
							if(testinputValues[i-1].equals("yes")) {
								testinputs[i] = 1;
							} else {
								testinputs[i] = 0;
							}
						}
					}
					double test_out = forwardPath(-1, testinputs, stlearningRate, gdWeights1, gdWeights2, true);
					if(test_out >= 0.5) {
						System.out.println("yes");
					} else {
						System.out.println("no");
					}
					//testError += Math.pow((real_test_out - test_out), 2);
				}
			}
			//System.out.println("Test Error: " + testError / 2);
			testsc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}



	}
	private static double[] matrixMultiply(double[] matrixA, double[][] matrixB) {
		double[] results = new double[matrixB.length];
		for(int i = 0; i < results.length; i++) {
			for(int j = 0; j < matrixA.length; j++) {
				results[i] += matrixA[j] * matrixB[i][j];
			}
		}
		return results;
	}

	private static double sigmoid(double sum) {
		double result = Math.exp(-sum);
		result = 1 / (1 + result);
		return result;
	}

	private static double forwardPath(double real_output, double[] example, double learningRate, double[][] weights1, double[] weights2, boolean test) {
		double[] neurons = matrixMultiply(example, weights1);
		double this_output = 1*weights2[0];
		double weightError2 = 0;
		double[] weightError1 = new double[3];
		for (int i = 0; i < neurons.length; i++) {
			neurons[i] = sigmoid(neurons[i]);
			this_output += neurons[i] * weights2[i+1];
		}
		this_output = sigmoid(this_output);
		if(!test) {
			weightError2 = this_output*(1-this_output) * (real_output - this_output);
			for(int i = 0; i < weightError1.length; i++) {
				weightError1[i] = neurons[i]*(1-neurons[i])* (weightError2*weights2[i+1]);
				//System.out.println("Layer1 " + i + "th error: " + weightError1[i]);
			}
			//System.out.println("Layer2 error: " + weightError2);
			for (int i = 0; i < weights2.length; i++) {
				if(i == 0) {
					weights2[i] += learningRate*weightError2;
				}else {
					weights2[i] += learningRate*weightError2*neurons[i - 1];
				}
			}
			for (int i = 0; i < weights1.length; i++) {
				for (int j = 0; j < weights1[0].length; j++) {
					weights1[i][j] += learningRate*weightError1[i]*example[j];
				}
			}
		}
		return this_output;		
	}

}
