This project was created by Matthew Martin for the ECS 170 (Atrificial Intelligence) class in Winter of 2017.
This is the final project in the class.

The assignment was to create a simple neural network using Sigmoid units that is capible of predicting whether a 120x128 greyscale image is a picture of a male or female. Training data was provided in folders - one female and one male. The data was encoded as 15360 (120 * 128) integer values that represent grey-scale brightness. A test set of images was also provided. Included in the assignment submission was a "predictions" file, which contains the predictions of the program for the images in the test set, along with confidence values.

Usage of the program is as follows:
java MatthewMartin -train DirMale DirFemale
	- Running the program the "-train" option will produce a "NeuralNetwork.data" file that contains all the connection weights of the trained neural network.
java MatthewMartin -test DirTest
	- Running the program with the "-test" option uses the trained neural network data ("NeuralNetwork.data") in order to produce predicions for whether each image in the test directory is male or female. 

	
The project report is included in Project3_NeuralNetworks.pdf. This file outlines the structure of the neural network, how it works, and the prediction accuracy based on crossfold validation.