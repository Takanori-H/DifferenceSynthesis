package testing;

import static org.junit.Assert.*;

import org.junit.Test;

public class DifferentialControllerSynthesisTestOnKivaSystem {
	DirectoryTrackerForSingleWinningRegion dt;

	@Test
	public void test() {
		//fail("まだ実装されていません");
		String[][] cases={
				{ "case1.txt" },//case1
				{ "case2.txt" },//case2
				{ "case4.txt" },//case3
				{ "case5.txt" },//case4
				{ "case5.txt", "case5B.txt"},//case5
				{ "case2.txt", "case5.txt"},//case6
				{ "case2.txt", "case5.txt", "case5B.txt"},//case7
				{ "case2.txt", "case4.txt"},//case8
				{ "case4.txt", "case2.txt"},//case9
				{ "case5.txt", "case2.txt"},//case10
				{ "case5.txt", "case4.txt"},//case11
				{ "case5.txt", "case2.txt", "case5B.txt"},//case12
				{ "case5.txt", "case4.txt", "case5B.txt"},//case13
				{ "case5.txt", "case2.txt", "case5B.txt", "case4.txt"},//case14
				{ "case1.txt", "case4.txt"},//case15
		};
		int[] expected={16,15,14,16,12,15,11,8,8,15,14,11,10,5,14};
		//cases[0]={{level, State, Transition}, {1, 67, 103}, {2, 75, 104}, {3, 81, 119}, {4, 55, 76}, {5, 125, 161}, {nowlevel, 5}}
		//cases[1]={{level, State, Transition}, {1, 67, 100}, {2, 81, 116}, {3, 81, 116}, {4, 55, 76}, {5, noController}, {nowlevel, 4}}
		//cases[2]=
		//int actuals = doJointTest(cases[14]);
		//System.out.println("nowlevel " + actuals);
		int actuals[] = new int[15];
		for(int i=0;i<cases.length;i++){
			actuals[i]=doJointTest(cases[i]);
			System.out.println("RESULT");
			System.out.println("cases: "+i+", Expected "+expected[i]+", Actual "+actuals[i]);
			System.out.println();
		}
//		/*
		assertEquals(expected[0],actuals[0]);
		assertEquals(expected[1],actuals[1]);
		assertEquals(expected[2],actuals[2]);
		assertEquals(expected[3],actuals[3]);
		assertEquals(expected[4],actuals[4]);
		assertEquals(expected[5],actuals[5]);
		assertEquals(expected[6],actuals[6]);
		assertEquals(expected[7],actuals[7]);
		assertEquals(expected[8],actuals[8]);
		assertEquals(expected[9],actuals[9]);
		assertEquals(expected[10],actuals[10]);
		assertEquals(expected[11],actuals[11]);
		assertEquals(expected[12],actuals[12]);
		assertEquals(expected[13],actuals[13]);
		assertEquals(expected[14],actuals[14]);
//		*/
	}

	int doJointTest(String[] cases) {
		int tmp = -1;
		dt = new DirectoryTrackerForSingleWinningRegion("KIVAsystemSingle");
		int DesignTime[][] = dt.checkDesignTimeSynthesis();
		for(int i=DesignTime.length;i>0;i--) {
			System.out.println("level " + i + "," + "State数 " + DesignTime[i-1][0] + "," + "Transition数 " + DesignTime[i-1][1]);
			System.out.println();
		}
		for(int i=0;i<cases.length-1;i++)dt.checkUpdateControllerFromFile(cases[i]);
		tmp = dt.checkUpdateControllerFromFile(cases[cases.length-1]);
		return tmp;
	}
}
