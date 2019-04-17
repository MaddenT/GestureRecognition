package hmm;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import data.DataReader;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.analysis.function.Log;

public class HMM {
	
	private ArrayList<ArrayList<ArrayList<Double>>> trainingData;
	private int numberOfStates;
	private ArrayList<State> states;
	private double[][] transitionMatrix;
	public String name;
	
	public HMM(ArrayList<ArrayList<ArrayList<Integer>>> data, int numberOfStates, String name) {
		Instant start = Instant.now();
		this.name = name;
		this.trainingData = normalizeData(data);
		//System.out.println("Data normalized");
		this.numberOfStates = numberOfStates;
		this.states = new ArrayList<State>();
		initializeStates(this.trainingData);
		//System.out.println("States initialized");
		initializeTransitions();
		for (double[] row : this.transitionMatrix) {
			for (double d : row) {
				//System.out.println(d);
			}
		}
		//train();
		for (State state : states) {
			//System.out.println(state.getString());
			//System.out.println("State: " + i);
			//System.out.println("Means: " + states.get(i).getMeans().toString());
			//System.out.println("Cov: " + states.get(i).getCov().toString());
			//System.out.println("Weight: " + states.get(i).getWeight());
		}
		Instant end = Instant.now();
		//System.out.println("Exit, training took: " + Duration.between(start, end) + " seconds.");
	}
	
	public HMM (ArrayList<ArrayList<ArrayList<Integer>>> data, String name) {
		this.name = name;
		DataReader dr = new DataReader();
		this.states = dr.loadStates(name);
		this.numberOfStates = states.size();
		initializeTransitions();
	}
	private ArrayList<ArrayList<ArrayList<Double>>> normalizeData(ArrayList<ArrayList<ArrayList<Integer>>> data) {
		ArrayList<ArrayList<ArrayList<Double>>> normalizedData = new ArrayList<ArrayList<ArrayList<Double>>>();
		//Iterate through all of the characters in the list
		for (ArrayList<ArrayList<Integer>> character : data) {
			ArrayList<ArrayList<Double>> normalizedCharacter = normalizeCharacter(character);
			normalizedData.add(normalizedCharacter);
		}
		return normalizedData;
	}
	
	private ArrayList<ArrayList<Double>> normalizeCharacter(ArrayList<ArrayList<Integer>> character) {
		//ArrayList<ArrayList<Integer>> character = new ArrayList<ArrayList<Integer>>();
		
		//int pointCount = 0;
		//boolean pointsCollect = true;
		//while (pointsCollect) {
			//try {
				//character.add(origCharacter.get(pointCount * 10));
				//pointCount += 1;
			//} catch (Exception e) {
				//character.add(origCharacter.get(origCharacter.size() - 1));
				//pointsCollect = false;
		//	}
		//}
		
		//Iterate through all of the characters in the list
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
	
		return normalizedCharacter;
	}
	
	private void initializeStates(ArrayList<ArrayList<ArrayList<Double>>> data) {
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
			states.add(new State(means, covariance, (double) 1/numberOfStates));
		}
	}
	
	private void initializeTransitions() {
		double[][] transitionMatrix = new double[this.numberOfStates][this.numberOfStates];
		for (int i = 0 ; i < this.numberOfStates - 1; i++) {
			double[] row = new double[this.numberOfStates];
			for (int j = 0 ; j < this.numberOfStates ; j++) {
				if (j == i) {
					row[j] = 0.75;
				} else if (j == i + 1) {
					row[j] = 0.25;
				} else {
					row[j] = 0;
				}
			}
			transitionMatrix[i] = row;
		}
		
		double[] lastRow = new double[this.numberOfStates];
		for (int j = 0 ; j < this.numberOfStates ; j++) {
			if (j == this.numberOfStates - 1) {
				lastRow[j] = 1;
			} else {
				lastRow[j] = 0;
			}
		}
		transitionMatrix[this.numberOfStates - 1] = lastRow;
		
		this.transitionMatrix = transitionMatrix;
	}
	
	public ArrayList<ArrayList<ArrayList<Double>>> getData() {
		return this.trainingData;
	}
	
	
	public double evaluate(ArrayList<ArrayList<Integer>> character) {
		ArrayList<ArrayList<Double>> points = normalizeCharacter(character);
 		
		int numberOfPoints = points.size();
		//System.out.println(numberOfPoints);
		double[][] a = new double[this.numberOfStates][numberOfPoints];
		double[][] b = new double[this.numberOfStates][numberOfPoints];
		double counter = 0.0;
		for (int i = 0 ; i < this.numberOfStates ; i++) {
			double[] row = new double[numberOfPoints];
			for (int j = 0 ; j < numberOfPoints ; j++) {
				row[j] = counter;
				counter += 1.0;
			}
			a[i] = row.clone();
			b[i] = row.clone();
		}
		
		a[0][0] = 1.0;
		for (int i = 1 ; i < this.numberOfStates ; i++) {
			a[i][0] = 0.0;
		}
		
		for (int i = 0 ; i < this.numberOfStates ; i++) {
			b[i][numberOfPoints-1] = 1.0;
		}
		
		for (int i = 0 ; i < numberOfPoints - 1 ; i++) {
			for (int s = 0 ; s < this.numberOfStates ; s++) {
				double sumA = 0.0;
				for (int j = 0 ; j < this.numberOfStates ; j++) {
					sumA = sumA + (a[j][i]*this.transitionMatrix[j][s]);
				}
				a[s][i+1] = sumA * (new MultivariateNormalDistribution(this.states.get(s).getMeansArray(), 
						this.states.get(s).getCovArray())
						.density(points.get(i + 1).stream().mapToDouble(d -> d).toArray()));
			
			}
		}
		
		for (int i = 0 ; i < numberOfPoints - 1 ; i++) {
			for (int s = 0 ; s < this.numberOfStates ; s++) {
				double sumB = 0.0;
				for (int j = 0 ; j < this.numberOfStates ; j++) {
					sumB = sumB + (this.transitionMatrix[s][j] * (new MultivariateNormalDistribution(this.states.get(j).getMeansArray(), 
							this.states.get(j).getCovArray())
							.density(points.get(numberOfPoints-i-1).stream().mapToDouble(d -> d).toArray()))) * b[j][numberOfPoints-i-1];
				
				}
				b[s][numberOfPoints-i-2] = sumB;
			}
		}
		
		double probability = 0.0;
		for (int i = 0 ; i < numberOfPoints ; i++) {
			for (int s = 0 ; s < this.numberOfStates ; s++) {
				probability = probability + a[s][i]*b[s][i];
			}
		}
		
		return probability;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//SimplifyCharacter sp = new SimplifyCharacter();
		DataReader dr = new DataReader();
		HMM hMM = new HMM(dr.readInTraining("one", true), 2, "one");
		//System.out.println(hMM.evaluate(dr.readInTesting("one").get(0)));
		//ArrayList<ArrayList<ArrayList<Double>>> data = hMM.getData();
		}
	}

