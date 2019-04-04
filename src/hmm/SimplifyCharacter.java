package hmm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SimplifyCharacter {

	public ArrayList<ArrayList<Integer>> simplifyChar(ArrayList<ArrayList<Integer>> original, double epsilon) {
		double dmax = 0;
		int index = 0;
		int end = original.size();
		for(int i = 1; i < end-1; i ++) {
			int a = original.get(0).get(1) - original.get(end - 1).get(1);
			int b = original.get(end - 1).get(0) - original.get(0).get(0);
			int c = (original.get(0).get(0) * original.get(end - 1).get(1)) 
					- (original.get(end-1).get(0) * original.get(0).get(1));
			
			double distance = Math.abs((a*original.get(i).get(0))+(b*original.get(i).get(1)) + c) / Math.sqrt((a*a) + (b*b));
			
			if (distance > dmax) {
				index = 1;
				dmax = distance;
			}
		}
		
		ArrayList<ArrayList<Integer>> results = new ArrayList<ArrayList<Integer>>();
		
		if (dmax > epsilon) {
			ArrayList<ArrayList<Integer>> part1 = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> part2 = new ArrayList<ArrayList<Integer>>();
			for (int i = 0; i < original.size() ; i++) {
				if (i < index) {
					part1.add(original.get(i));
				} else if (i == index) {
					part1.add(original.get(i));
					part2.add(original.get(i));
				} else {
					part2.add(original.get(i));
				}
			}
			
			ArrayList<ArrayList<Integer>> results1 = simplifyChar(part1, epsilon);
			ArrayList<ArrayList<Integer>> results2 = simplifyChar(part2, epsilon);
			
			
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<Integer>> points = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> p1 = new ArrayList<Integer>();
		p1.add(1);
		p1.add(2);
		ArrayList<Integer> p3 = new ArrayList<Integer>();
		p3.add(10);
		p3.add(20);
		ArrayList<Integer> p2 = new ArrayList<Integer>();
		p2.add(5);
		p2.add(4);
		points.add(p1);
		points.add(p3);
		points.add(p2);
		SimplifyCharacter sp = new SimplifyCharacter();
		sp.simplifyChar(points, 0.02);
	}

}
//function DouglasPeucker(PointList[], epsilon)
//// Find the point with the maximum distance
//dmax = 0
//index = 0
//end = length(PointList)
//for i = 2 to ( end - 1) {
//    d = perpendicularDistance(PointList[i], Line(PointList[1], PointList[end])) 
//    if ( d > dmax ) {
//        index = i
//        dmax = d
//    }
//}
//
//ResultList[] = empty;