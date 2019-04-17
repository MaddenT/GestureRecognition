package hmm;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import data.DataReader;

public class Controller {
	private HMM zero;
	private HMM one;
	private HMM two;
	private HMM three;
	private HMM four;
	private HMM five;
	private HMM six;
	private HMM seven;
	private HMM eight;
	private HMM nine;
	private ArrayList<HMM> hmms;
	protected int[][] confusionMatrix;
	
	public Controller () {
		DataReader dr = new DataReader();
		System.out.println("Training Zero");
		this.zero = new HMM(dr.readInTraining("zero", true), 4, "zero");//4 good setting
		System.out.println("Training One");
		this.one = new HMM(dr.readInTraining("one", true), 4, "one");
		System.out.println("Training Two");
		this.two = new HMM(dr.readInTraining("two", true), 4, "two");
		System.out.println("Training Three");
		this.three = new HMM(dr.readInTraining("three", true), 3, "three");
		System.out.println("Training Four");
		this.four = new HMM(dr.readInTraining("four", true), 3, "four");//orig 3
		System.out.println("Training Five");
		this.five = new HMM(dr.readInTraining("five", true), 4, "five");//6 good setting
		System.out.println("Training Six");
		this.six = new HMM(dr.readInTraining("six", true) , 4, "six");//4
		System.out.println("Training Seven");
		this.seven = new HMM(dr.readInTraining("seven", true), 3, "seven");//4 good setting
		System.out.println("Training Eight");
		this.eight = new HMM(dr.readInTraining("eight", true), 4, "eight");//6 good setting
		System.out.println("Training Nine");
		this.nine = new HMM(dr.readInTraining("nine", true), 3, "nine");
		ArrayList<HMM> tempHMMs = new ArrayList<HMM>();
		tempHMMs.add(this.zero);
		tempHMMs.add(this.one);
		tempHMMs.add(this.two);
		tempHMMs.add(this.three);
		tempHMMs.add(this.four);
		tempHMMs.add(this.five);
		tempHMMs.add(this.six);
		tempHMMs.add(this.seven);
		tempHMMs.add(this.eight);
		tempHMMs.add(this.nine);
		this.hmms = tempHMMs;
	}
	
	public Controller (int[] states) {
		DataReader dr = new DataReader();
		this.zero = new HMM(dr.readInTraining("zero", true), states[0], "zero");//4 good setting
		this.one = new HMM(dr.readInTraining("one", true), states[1], "one");
		this.two = new HMM(dr.readInTraining("two", true), states[2], "two");
		this.three = new HMM(dr.readInTraining("three", true), states[3], "three");
		this.four = new HMM(dr.readInTraining("four", true), states[4], "four");//orig 3
		this.five = new HMM(dr.readInTraining("five", true), states[5], "five");//6 good setting
		this.six = new HMM(dr.readInTraining("six", true), states[6], "six");//4
		this.seven = new HMM(dr.readInTraining("seven", true), states[7], "seven");//4 good setting
		this.eight = new HMM(dr.readInTraining("eight", true), states[8], "eight");//6 good setting
		this.nine = new HMM(dr.readInTraining("nine", true), states[9], "nine");
		ArrayList<HMM> tempHMMs = new ArrayList<HMM>();
		tempHMMs.add(this.zero);
		tempHMMs.add(this.one);
		tempHMMs.add(this.two);
		tempHMMs.add(this.three);
		tempHMMs.add(this.four);
		tempHMMs.add(this.five);
		tempHMMs.add(this.six);
		tempHMMs.add(this.seven);
		tempHMMs.add(this.eight);
		tempHMMs.add(this.nine);
		this.hmms = tempHMMs;
	}
	
	public int testHMM() {
		ArrayList<String> names = new ArrayList<String>();
		for (HMM hmm : this.hmms) {
			names.add(hmm.name);
		}
		DataReader dr = new DataReader();
		int[][] confusion = new int[10][10];
		int count = 0;
		for (String curName : names) {
			System.out.println("Running test cases for: " + curName);
			ArrayList<ArrayList<ArrayList<Integer>>> curData = dr.readInTesting(curName);
			
			int[] rowMatrix = {0,0,0,0,0,0,0,0,0,0};
			int runCount = 0;
			for (ArrayList<ArrayList<Integer>> chara : curData) {
				double highestP = 0.0;
				String name = "";
				for (HMM hmm : this.hmms) {
					double localP = hmm.evaluate(chara);
					if (localP > highestP) {
						highestP = localP;
						name = hmm.name;
					}
				}
				if (name.equals("zero")) {
					int freq = rowMatrix[0];
					rowMatrix[0] = freq + 1;
				} else if (name.equals("one")) {
					int freq = rowMatrix[1];
					rowMatrix[1] = freq + 1;
				} else if (name.equals("two")) {
					int freq = rowMatrix[2];
					rowMatrix[2] = freq + 1;
				} else if (name.equals("three")) {
					int freq = rowMatrix[3];
					rowMatrix[3] = freq + 1;
				} else if (name.equals("four")) {
					int freq = rowMatrix[4];
					rowMatrix[4] = freq + 1;
				} else if (name.equals("five")) {
					int freq = rowMatrix[5];
					rowMatrix[5] = freq + 1;
				} else if (name.equals("six")) {
					int freq = rowMatrix[6];
					rowMatrix[6] = freq + 1;
				} else if (name.equals("seven")) {
					int freq = rowMatrix[7];
					rowMatrix[7] = freq + 1;
				} else if (name.equals("eight")) {
					int freq = rowMatrix[8];
					rowMatrix[8] = freq + 1;
				} else if (name.equals("nine")) {
					int freq = rowMatrix[9];
					rowMatrix[9] = freq + 1;
				}
				runCount += 1;
				//System.out.println(runCount);
			}
			System.out.println(Arrays.toString(rowMatrix));
			confusion[count] = rowMatrix;
			count += 1;
		}
		
		int sumRight = 0;
		for (int i = 0; i < confusion.length ; i++) {
			sumRight += confusion[i][i];
		}
		this.confusionMatrix = confusion;
		return sumRight;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Instant start = Instant.now();
		
		/*int[] initStateCount = {4, 4, 4, 3, 3, 4, 4, 2, 4, 2};
		int changeFact = 2;
		int count = 0;
		int highestSum = 53;
		int[] highestArray = initStateCount.clone();
		for (int i : initStateCount) { 
			System.out.println("%%%% Testing: " + count);
			int[] a = initStateCount.clone();
			//int[] b = initStateCount.clone();
			a[count] = i - changeFact;
			//b[count] = i - 1;
			int aSum = new Controller(a).testHMM();
			//int bSum = new Controller(b).testHMM();
			if (aSum > highestSum) {
				highestSum = aSum;
				highestArray = a;
				System.out.println("New highest array: ");
				System.out.println(Arrays.toString(a));
			}/* else if (bSum > aSum && bSum > highestSum) {
				highestSum = bSum;
				highestArray = b;
				System.out.println("New highest array: ");
				System.out.println(Arrays.toString(b));
			} else if (aSum == bSum && aSum > highestSum) {
				highestSum = aSum;
				highestArray = a;
				System.out.println("New highest array: ");
				System.out.println(Arrays.toString(a));
			}
			count += 1;
		} 
		System.out.println(Arrays.toString(highestArray));
		System.out.println(highestSum);*/
		Controller c = new Controller();
		int numRight = c.testHMM();
		
		Instant end = Instant.now();
		System.out.println("***********************************************************************************");
		System.out.println("***********************************************************************************");
		System.out.println("Exit, testing took: " + Duration.between(start, end) + " seconds.");
		System.out.println("Total number of correctly classified characters: " + numRight);
		double succRate = (double) numRight/10000;
		System.out.println("Success rate: " + Double.toString(succRate));
		System.out.println("Failure rate: " + Double.toString(1 - succRate));
		//System.out.println("Params: " + Arrays.toString(highestArray));
		System.out.println("Confusion Matrix: ");
		int countz = 0;
		for (int[] mat : c.confusionMatrix) {
			System.out.println(Arrays.toString(mat));
			System.out.println((double) mat[countz]/IntStream.of(mat).sum());
			countz += 1;
		}
		
	}

}


