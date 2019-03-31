package hmm;

import java.util.ArrayList;
import java.util.Arrays;

import data.DataReader;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.Covariance;
public class HMM {
	
	private ArrayList<ArrayList<ArrayList<Double>>> data;
	private int numberOfStates;
	private ArrayList<State> states;
	private double[][] transitionMatrix;
	
	public HMM(ArrayList<ArrayList<ArrayList<Integer>>> data, int numberOfStates) {
		this.data = normalizeData(data);
		this.numberOfStates = numberOfStates;
		this.states = new ArrayList<State>();
		initializeStates();
		initializeTransitions();
	}
	
	private ArrayList<ArrayList<ArrayList<Double>>> normalizeData(ArrayList<ArrayList<ArrayList<Integer>>> data) {
		ArrayList<ArrayList<ArrayList<Double>>> normalizedData = new ArrayList<ArrayList<ArrayList<Double>>>();
		//Iterate through all of the characters in the list
		for (ArrayList<ArrayList<Integer>> character : data) {
			int sumX = 0;
			int sumY = 0;
			//Find the sum of all of the points in each character
			for (ArrayList<Integer> point : character) {
				sumX += point.get(0);
				sumY += point.get(1);
			}
			//Find the average of all the points in each character
			float meanX = sumX/character.size();
			float meanY = sumY/character.size();

			//Instantiate the variable to hold the summation used in a standard
			//deviation calculation
			int stdNumeratorX = 0;
			int stdNumeratorY = 0;
			/*Sum the numbers in the numerator of the std deviation formula.
			Multiply the number by itself to square it rather than using Math.pow
			since machine commands are O(1)*/
			for (ArrayList<Integer> point : character) {
				stdNumeratorX += ((point.get(0) - meanX) * (point.get(0) - meanX));
				stdNumeratorY += ((point.get(1) - meanY) * (point.get(1) - meanY));
			}
			
			//Calculate the standard deviation for x and y
			double stdX = Math.sqrt(stdNumeratorX / character.size());
			double stdY = Math.sqrt(stdNumeratorY / character.size());
			
			//Normalize the points and add them to a new character
			ArrayList<ArrayList<Double>> normalizedCharacter = new ArrayList<ArrayList<Double>>();
			for (ArrayList<Integer> point : character) {
				ArrayList<Double> normalizedPoint = new ArrayList<Double>();
				normalizedPoint.add(((point.get(0) - meanX) / stdX));
				normalizedPoint.add(((point.get(1) - meanY) / stdY));
				normalizedCharacter.add(normalizedPoint);
			}
			//Add the normalized character to the normalized dataset
			normalizedData.add(normalizedCharacter);
		}
		return normalizedData;
	}
	
	private void initializeStates() {
		int totalPoints = 0;
		for (ArrayList<ArrayList<Double>> chara : data) {
			totalPoints += chara.size();
		}
		int pointsPerState = (int) totalPoints/numberOfStates;
		int pointsPerStateInCharacter = (int) pointsPerState/data.size();

		for (int currentState = 0 ; currentState < numberOfStates ; currentState++) {
			ArrayList<Double> xPointsInState = new ArrayList<Double>();
			ArrayList<Double> yPointsInState = new ArrayList<Double>();
			for (ArrayList<ArrayList<Double>> chara : data) {
				for (int boundCounter = currentState*pointsPerStateInCharacter ; 
						boundCounter < pointsPerStateInCharacter + currentState*pointsPerStateInCharacter; 
						boundCounter ++) {
					if (boundCounter >= chara.size()) {
						break;
					}
					xPointsInState.add(chara.get(boundCounter).get(0));
					yPointsInState.add(chara.get(boundCounter).get(1));
				}
			}
			
			double[] xPointsInStateArray = xPointsInState.stream().mapToDouble(d -> d).toArray();
			double[] yPointsInStateArray = yPointsInState.stream().mapToDouble(d -> d).toArray();
			
			double[][] xyPointsInStateArray = new double[xPointsInStateArray.length][2];
			
			for (int pointCount = 0; pointCount < xPointsInStateArray.length; pointCount++) {
				xyPointsInStateArray[pointCount][0] = xPointsInStateArray[pointCount];
				xyPointsInStateArray[pointCount][1] = yPointsInStateArray[pointCount];
			}
			
			double xMean = StatUtils.mean(xPointsInStateArray);
			double yMean = StatUtils.mean(yPointsInStateArray);
			ArrayList<Double> means = new ArrayList<Double>();
			means.add(xMean);
			means.add(yMean);
			
			Covariance covarianceObj = new Covariance(xyPointsInStateArray);
			RealMatrix covariance = covarianceObj.getCovarianceMatrix();
			
			states.add(new State(means, covariance, 1/numberOfStates));
		}
	}
	
	private void initializeTransitions() {
		double[] transitionOne = new double[4];
		transitionOne[0] = 0.75;
		transitionOne[1] = 0.25;
		transitionOne[2] = 0.0;
		transitionOne[3] = 0.0;
		double[] transitionTwo = new double[4];
		transitionTwo[0] = 0.0;
		transitionTwo[1] = 0.75;
		transitionTwo[2] = 0.25;
		transitionTwo[3] = 0.0;
		double[] transitionThree = new double[4];
		transitionThree[0] = 0.0;
		transitionThree[1] = 0.0;
		transitionThree[2] = 0.75;
		transitionThree[3] = 0.25;
		double[] transitionFour = new double[4];
		transitionFour[0] = 0.0;
		transitionFour[1] = 0.0;
		transitionFour[2] = 0.0;
		transitionFour[3] = 1.0;
		
		double[][] transitionMatrix = {transitionOne, transitionTwo, transitionThree, transitionFour};
		this.transitionMatrix = transitionMatrix;
	}
	
	
	public ArrayList<ArrayList<ArrayList<Double>>> getData() {
		return this.data;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DataReader readData = new DataReader();
		HMM hMM = new HMM(readData.readIn(), 4);
		ArrayList<ArrayList<ArrayList<Double>>> data = hMM.getData();
		hMM.initializeStates();
		}
	}

