package testing;

import org.junit.Test;

public class DiscreteControllerSynthesisOnAutomatedWarehouse {
	DirectoryTrackerForSingleWinningRegion dt;

	@Test
	public void test() {
		//fail("まだ実装されていません");
		String[][] cases={
				{"case1.txt"},
				{"case2.txt"},
				{"case3.txt"},
				{"case4.txt"},
				{"case5.txt"},
				{"case5.txt","case3.txt"},
				{"case5.txt","case3.txt","case4.txt"},
				{"case5.txt","case4.txt"},
				{"case5.txt","case4.txt","case3.txt"},
				{"case10.txt","case2_.txt"},
				{"case10.txt","case4.txt"},
				{"case10.txt","case3.txt"},
				{"case10.txt","case3.txt","case2.txt"},
				{"case10.txt","case4.txt","case2.txt"},
				{"case10.txt","case4.txt","case2.txt","case3.txt"}
		};
		int[] expected={5,4,3,2,4,3,1,2,1,4,2,3,3,2,1};
		//cases[0]={{level, State, Transition}, {1, 67, 103, 21ms}, {2, 75, 104, 27ms}, {3, 81, 119, 24ms}, {4, 55, 76, 48ms}, {5, 125, 161, 60ms}, {nowlevel, 5}}
		//cases[1]={{level, State, Transition}, {1, 67, 100}, {2, 81, 116}, {3, 81, 116}, {4, 55, 76}, {5, noController}, {nowlevel, 4}}
		//cases[2]={{level, State, Transition}, {1, 67, 106}, {2, 12, 16, no simulate}, {3, 81, 125}, {4, 11, 14, no simulate}, {5, noController}, {nowlevel, 3}}
		//cases[3]={{level, State, Transition}, {1, 67, 106}, {2, 81, 125}, {3, 3, 3, no simulate}, {4, 3, 3, no simulate}, {5, noController}, {nowlevel, 2}}
		//cases[4]={{level, State, Transition}, {1, 67, 103}, {2, 81, 119}, {3, 81, 119}, {4, 55, 79}, {5, noController}, {nowlevel, 4}}
		/*int actuals = doJointTest(cases[2]);
		System.out.println("nowlevel " + actuals);*/
		int actuals[] = new int[15];
		for(int i=0;i<cases.length;i++){
			actuals[i]=doJointTest(cases[i]);
			System.out.println("RESULT");
			//System.out.println("cases: "+(i+1)+", Expected "+expected[i]+", Actual "+actuals[i]);
			System.out.println("cases: "+i+", Expected "+expected[i]+", Actual "+actuals[i]);
			System.out.println();
		}

		/*assertEquals(expected[0],actuals[0]);
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
		assertEquals(expected[14],actuals[14]);*/

	}

	int doJointTest(String[] cases) {
		int tmp = -1;
		dt = new DirectoryTrackerForSingleWinningRegion("AutomatedWarehouse");
		int DesignTime[][] = dt.checkDesignTimeSynthesis();
		for(int i=DesignTime.length;i>0;i--) {
			System.out.println("level " + i + "," + "State数 " + DesignTime[i-1][0] + "," + "Transition数 " + DesignTime[i-1][1]);
			System.out.println();
		}
		for(int i=0;i<cases.length-1;i++)dt.checkUpdateDiscreteControllerFromFile(cases[i]);
		long start=System.currentTimeMillis();
		tmp = dt.checkUpdateDiscreteControllerFromFile(cases[cases.length-1]);
		long stop=System.currentTimeMillis();
		System.out.print("Spending time of ");
		for(int i=0;i<cases.length;i++){
			System.out.print(cases[i]+"_");
		}
		System.out.println(":"+(stop-start)+"ms, result is "+tmp);
		return tmp;
	}
}
