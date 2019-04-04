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
	
	private ArrayList<ArrayList<ArrayList<Double>>> data;
	private int numberOfStates;
	private ArrayList<State> states;
	private double[][] transitionMatrix;
	
	public HMM(ArrayList<ArrayList<ArrayList<Integer>>> data, int numberOfStates) {
		Instant start = Instant.now();
		this.data = normalizeData(data);
		System.out.println("Data normalized");
		this.numberOfStates = numberOfStates;
		this.states = new ArrayList<State>();
		initializeStates();
		System.out.println("States initialized");
		initializeTransitions();
		train();
		for (int i = 0 ; i < numberOfStates ; i++) {
			System.out.println("State: " + i);
			System.out.println("Means: " + states.get(i).getMeans().toString());
			System.out.println("Cov: " + states.get(i).getCov().toString());
			System.out.println("Weight: " + states.get(i).getWeight());
		}
		Instant end = Instant.now();
		System.out.println("Exit, training took: " + Duration.between(start, end) + " seconds.");
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
			states.add(new State(means, covariance, (double) 1/numberOfStates));
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
	
	private void train() {
		ArrayList<RealMatrix> prevCov = new ArrayList<RealMatrix>();
		ArrayList<ArrayList<Double>> prevMeans = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> prevWeight = new ArrayList<Double>();
		double prevProb = logLikelihood();
		
		for(int i = 0 ; i < numberOfStates ; i++) {
			prevCov.add(states.get(i).getCov());
			prevMeans.add(states.get(i).getMeans());
			prevWeight.add(states.get(i).getWeight());
		}

		boolean run = true;
		int iterations = 0;
		while (run) {
			ArrayList<Double> res = new ArrayList<Double>();
			ArrayList<Double> mc = new ArrayList<Double>();
			for (ArrayList<ArrayList<Double>> chara : data) {
				for (ArrayList<Double> point : chara) {
					double normal = 0.0;
					ArrayList<Double> pdfs = new ArrayList<Double>();
					for (State state : states) {
						double pdf = new MultivariateNormalDistribution(state.getMeansArray(), 
								state.getCovArray())
								.density(point.stream().mapToDouble(d -> d).toArray());
						normal = normal + state.getWeight() * pdf;
						pdfs.add(pdf);
					}
					
					for (int i = 0 ; i < numberOfStates; i++) {
						double r = (states.get(i).getWeight() * pdfs.get(i))/normal;
						res.add(r);
						if (mc.size() >= 0 && mc.size() < numberOfStates) {
							mc.add(r);
						} else {
							mc.set(i, mc.get(i) + r);
						}
					}
					pdfs.clear();
				}
			}
			
			double weightSum = mc.stream().mapToDouble(d -> d).sum();
			
			for (int i = 0 ; i < numberOfStates ; i++) {
				states.get(i).setWeight((double) mc.get(i)/weightSum);
			}
			
			ArrayList<Double> meanX = new ArrayList<Double>();
			ArrayList<Double> meanY = new ArrayList<Double>();
			
			int rCount = 0;
			
			for (ArrayList<ArrayList<Double>> chara : data) {
				for (ArrayList<Double> point : chara) {
					for (int i = 0 ; i < numberOfStates ; i++) {
						if (meanX.size() >= 0 && meanX.size() < numberOfStates) {
							meanX.add(point.get(0)*res.get(rCount));
							meanY.add(point.get(1)*res.get(rCount));
						} else {
							meanX.set(i, meanX.get(i) + point.get(0)*res.get(rCount));
							meanY.set(i, meanY.get(i) + point.get(1)*res.get(rCount));
						}
						rCount += 1;
					}
				}
			}
			
			for (int i = 0; i < numberOfStates ; i++) {
				meanX.set(i, meanX.get(i)/mc.get(i));
				meanY.set(i, meanY.get(i)/mc.get(i));
				states.get(i).setMeans(meanX.get(i), meanY.get(i));
			}
			
			ArrayList<RealMatrix> covariances = new ArrayList<RealMatrix>();
			rCount = 0;
			
			for (ArrayList<ArrayList<Double>> chara : data) {
				for (ArrayList<Double> point : chara) {
					for(int i = 0 ; i < numberOfStates ; i++) {
						double xDiff = point.get(0) - states.get(i).getMeans().get(0);
						double yDiff = point.get(1) - states.get(i).getMeans().get(1);
						double[][] newArray = {{xDiff, xDiff}, {yDiff, yDiff}};
						RealMatrix matrix = MatrixUtils.createRealMatrix(newArray);
						RealMatrix matrixTransposed = matrix.transpose();
						double firstEntry = res.get(rCount) * (matrix.getEntry(0, 0) * matrixTransposed.getEntry(0, 0));
						double secondEntry = res.get(rCount) * (matrix.getEntry(0, 1) * matrixTransposed.getEntry(0, 1));
						double thirdEntry = res.get(rCount) * (matrix.getEntry(1, 0) * matrixTransposed.getEntry(1, 0));
						double fourthEntry = res.get(rCount) * (matrix.getEntry(1, 1) * matrixTransposed.getEntry(1, 1));
						double[][] multipliedArray = {{firstEntry, secondEntry}, {thirdEntry, fourthEntry}};
						RealMatrix multipliedMatrix = MatrixUtils.createRealMatrix(multipliedArray);
						if (covariances.size() >= 0 && covariances.size() < numberOfStates) {
							covariances.add(multipliedMatrix);
						} else {
							double rowZeroColZero = covariances.get(i).getEntry(0, 0) + multipliedMatrix.getEntry(0, 0);
							double rowZeroColOne = covariances.get(i).getEntry(0, 1) + multipliedMatrix.getEntry(0, 1);
							double rowOneColZero = covariances.get(i).getEntry(1, 0) + multipliedMatrix.getEntry(1, 0);
							double rowOneColOne = covariances.get(i).getEntry(1, 1) + multipliedMatrix.getEntry(1, 1);
							double[][] newCovArray = {{rowZeroColZero, rowZeroColOne}, {rowOneColZero, rowOneColOne}};
							RealMatrix newCovMatrix = MatrixUtils.createRealMatrix(newCovArray);
							covariances.set(i, newCovMatrix);
						}
						rCount += 1;
					}
				}
			}
			
			for (int i = 0 ; i < numberOfStates ; i++) {
				RealMatrix newCov = covariances.get(i).scalarMultiply(1/mc.get(i));
				covariances.set(i, newCov);
				states.get(i).setCov(newCov);
			}
			
			res = null;
			mc = null;
			meanX = null;
			meanY = null;
			covariances = null;
			
			try {
				double logLikelihood = logLikelihood();
				//System.out.println(logLikelihood);
				if (iterations % 200 == 0) {
					System.out.println("Iteration #: " + iterations);
					System.out.println("LogLL: " + logLikelihood);
					for (int i = 0; i < numberOfStates ; i++) {
						System.out.println("State #: "  + i);
						System.out.println("Means: "  + states.get(i).getMeans().toString());
						System.out.println("Cov: "  + states.get(i).getCov().toString());
						System.out.println("Weight: "  + states.get(i).getWeight());
					}
					System.out.println("*********************************************");
				}
				if (logLikelihood < prevProb) {
					for (int i = 0 ; i < numberOfStates ; i++) {
						states.get(i).setCov(prevCov.get(i));
						states.get(i).setMeans(prevMeans.get(i));
						states.get(i).setWeight(prevWeight.get(i));
					}
					System.out.println("LogLL: " + logLikelihood);
					System.out.println("Iterations: " + (iterations + 1));
					run = false;
				} else {
					prevProb = logLikelihood;
					for (int i = 0; i < numberOfStates ; i++) {
						//System.out.println("Means: "  + states.get(i).getMeans().toString());
						//System.out.println("Cov: "  + states.get(i).getCov().toString());
						//System.out.println("Weight: "  + states.get(i).getWeight());
						prevCov.set(i, states.get(i).getCov());
						prevMeans.set(i, states.get(i).getMeans());
						prevWeight.set(i, states.get(i).getWeight());
					}
					iterations += 1;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				for (int i = 0 ; i < numberOfStates ; i++) {
					states.get(i).setCov(prevCov.get(i));
					states.get(i).setMeans(prevMeans.get(i));
					states.get(i).setWeight(prevWeight.get(i));
				}
				run = false;
			}
		}
	}
	
	private double logLikelihood() {
		ArrayList<Double> logSums = new ArrayList<Double>();
		for (ArrayList<ArrayList<Double>> chara : data) {
			double logSum = 0.0;
			for (ArrayList<Double> point : chara) {
				double tempSum = 0.0;
				for (State state : states) {
					tempSum = tempSum + state.getWeight() * (new MultivariateNormalDistribution(state.getMeansArray(), 
							state.getCovArray())
							.density(point.stream().mapToDouble(d -> d).toArray()));
				}
				if (tempSum == 0.0) {
					tempSum = 0.0000001;
				}
				logSum = logSum + (new Log().value(tempSum));
			}
			logSums.add(logSum);
		}
		double p = (logSums.stream().mapToDouble(d -> d).sum())/logSums.size();
		return p;
	}
	
	
	public ArrayList<ArrayList<ArrayList<Double>>> getData() {
		return this.data;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DataReader readData = new DataReader();
		HMM hMM = new HMM(readData.readIn(), 4);
		//ArrayList<ArrayList<ArrayList<Double>>> data = hMM.getData();
		}
	}

