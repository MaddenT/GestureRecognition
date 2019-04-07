package hmm;

import java.util.ArrayList;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;

import data.DataReader;

public class State {
	private ArrayList<Double> means;
	private RealMatrix covariance;
	private double weight;
	
	public State (ArrayList<Double> means, RealMatrix covariance, double weight) {
		this.means = means;
		this.covariance = covariance;
		this.weight = weight;
	}
	
	public State (String[] stateData) {
		loadState(stateData);
	}
	
	public RealMatrix getCov() {
		return covariance;
	}
	
	public double[][] getCovArray() {
		return covariance.getData();
	}
	
	public void setCov(RealMatrix covariance) {
		this.covariance = covariance;
	}
	
	public ArrayList<Double> getMeans() {
		return means;
	}
	
	public void setMeans(double meanX, double meanY) {
		this.means.set(0, meanX);
		this.means.set(1, meanY);
	}
	
	public void setMeans(ArrayList<Double> means) {
		this.means = means;
	}
	
	public double[] getMeansArray() {
		return means.stream().mapToDouble(d -> d).toArray();
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public String getString() {
		String output = "[" + means.toString() + "%" + "{" + Double.toString(covariance.getRow(0)[0]) + "," + Double.toString(covariance.getRow(0)[1])
				+ "}&{" + Double.toString(covariance.getRow(1)[0]) + "," + Double.toString(covariance.getRow(1)[1]) + "}%" 
				+ Double.toString(weight) + "]";
		return output;
	}
	
	private void loadState(String[] stateData) {
		String[] tempMeans = stateData[0].replaceAll("\\[", "").replaceAll("\\]", "").split(",");
		ArrayList<Double> loadedMeans = new ArrayList<Double>();
		loadedMeans.add(Double.parseDouble(tempMeans[0]));
		loadedMeans.add(Double.parseDouble(tempMeans[1]));
		this.means = loadedMeans;
		
		String[] tempCovariance = stateData[1].split("&");
		String[] tempArray1 = tempCovariance[0].substring(1, tempCovariance[0].length() - 1).split(",");
		String[] tempArray2 = tempCovariance[1].substring(1, tempCovariance[1].length() - 1).split(",");
		
		double[] doubleArray1 = new double[2];
		double[] doubleArray2 = new double[2];
		
		for (int i = 0 ; i < 2 ; i++) {
			doubleArray1[i] = Double.parseDouble(tempArray1[i]);
			doubleArray2[i] = Double.parseDouble(tempArray2[i]);
		}
		
		double[][] tempCovarianceArray = {doubleArray1, doubleArray2};
		RealMatrix tempRealMatrix = MatrixUtils.createRealMatrix(tempCovarianceArray);
		this.covariance = tempRealMatrix;
		
		this.weight = Double.parseDouble(stateData[2]);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//double[] x = new double[2];
		//x[0] = -3.0;
		//x[1] = -3.0;
		//double[] y = new double[2];
		//y[0] = 1.0;
		//y[1] = 1.0;
		double[][] xy = {{-3.0, -3.0}, {1.0, 1.0}};
		RealMatrix gg = MatrixUtils.createRealMatrix(xy);
		System.out.println(gg.getRowDimension());
		//System.out.println(gg.getEntry(1, 1));
		System.out.println(gg.toString());
		System.out.println(gg.transpose().toString());
		System.out.println(gg.preMultiply(gg.transpose()).toString());
		}
}
