package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

import hmm.State;

public class DataReader {
	public ArrayList<ArrayList<ArrayList<Integer>>> readInTraining(String charString) {
		File folder = new File("C:\\Users\\madde\\git\\GestureRecognition\\src\\resources\\training-data\\all-training\\" + charString);
		String[] files = folder.list();
		
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for (int i = 0; i < 220; i++) {
			//indexes.add(getRandNum(indexes));
			indexes.add(i);
		}

		ArrayList<ArrayList<ArrayList<Integer>>> data = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for (int index : indexes) {
			File f = new File("C:\\Users\\madde\\git\\GestureRecognition\\src\\resources\\training-data\\all-training\\" + charString + "\\" + files[index]);
			ArrayList<ArrayList<Integer>> chara = new ArrayList<ArrayList<Integer>>();
			try {
				Scanner scan = new Scanner(f);
				while(scan.hasNextLine()) {
					String[] x = scan.nextLine().split(" ");
					if (!x[0].equals("\"x\"")) {
						ArrayList<Integer> point = new ArrayList<Integer>();
						point.add(Integer.parseInt(x[0]));
						point.add(Integer.parseInt(x[1]));
						chara.add(point);
					}
				}
				scan.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			data.add(chara);
		}	
		//System.out.println("Data read, size = " + data.size());
		return data;
	}
	
	public ArrayList<ArrayList<ArrayList<Integer>>> readInTesting(String charString) {
		File folder = new File("C:\\Users\\madde\\git\\GestureRecognition\\src\\resources\\training-data\\testing\\" + charString);
		String[] files = folder.list();
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for (int i = 0; i < files.length; i++) {
			//indexes.add(getRandNum(indexes));
			indexes.add(i);
		}

		ArrayList<ArrayList<ArrayList<Integer>>> data = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for (int index : indexes) {
			File f = new File("C:\\Users\\madde\\git\\GestureRecognition\\src\\resources\\training-data\\testing\\" + charString + "\\" + files[index]);
			ArrayList<ArrayList<Integer>> chara = new ArrayList<ArrayList<Integer>>();
			try {
				Scanner scan = new Scanner(f);
				while(scan.hasNextLine()) {
					String[] x = scan.nextLine().split(" ");
					if (!x[0].equals("\"x\"")) {
						ArrayList<Integer> point = new ArrayList<Integer>();
						point.add(Integer.parseInt(x[0]));
						point.add(Integer.parseInt(x[1]));
						chara.add(point);
					}
				}
				scan.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			data.add(chara);
		}	
		//System.out.println("Data read, size = " + data.size());
		return data;
	}
	
	public int[][] readMatrix() {
		
		File f = new File("C:\\Users\\madde\\git\\GestureRecognition\\src\\resources\\experiment.txt");
		int[][] matrix = new int[10][10];
		try {
			Scanner scan = new Scanner(f);
			int count = 0;
			while(scan.hasNextLine()) {
				int[] row = new int[10];
				String[] x = scan.nextLine().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(",");
				for (int i = 0 ; i < x.length ; i++) {
					row[i] = Integer.parseInt(x[i]);
				}
				matrix[count] = row;
				count++;
			}
			scan.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return matrix;
	}	
	
	public ArrayList<State> loadStates(String modelName) {
		File f = new File("C:\\Users\\madde\\git\\GestureRecognition\\src\\resources\\hmm-persisted-states\\" + modelName);
		ArrayList<State> states = new ArrayList<State>();
		try {
			Scanner scan = new Scanner(f);
			while(scan.hasNextLine()) {
				String nextLine = scan.nextLine();
				String[] processedLine = nextLine.substring(1, nextLine.length() - 1).split("%");
				State state = new State(processedLine);
				states.add(state);
			}
			scan.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return states;
	}
	public int getRandNum(ArrayList<Integer> randList) {
		Random rand = new Random();		
		int randNum = rand.nextInt((1000-10) + 1) + 10;

		if (!randList.contains(randNum)) {
			return randNum;
		} else {
			return getRandNum(randList);
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DataReader readData = new DataReader();
		//readData.loadStates("one");
		ArrayList<ArrayList<ArrayList<Integer>>> data = readData.readInTraining("one");
		//readData.test();
	}

}
