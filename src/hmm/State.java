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
