//差分Synthesis用のテスト
package testing;

import org.junit.Test;

public class DifferentialControllerSynthesisTestOnProductionCell {
	DirectoryTrackerForSingleWinningRegion dt;

	@Test
	public void cases0test() {//16 -> 16
		String[] cases = {"case1.txt"};
		int expected= 14;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+0+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases1test() {//16 -> 14
		String[] cases = {"case2.txt"};
		int expected= 14;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+1+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases2test() {//16 -> 12
		String[] cases = {"case3.txt"};
		int expected= 12;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+2+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases3test() {//16 -> 15
		String[] cases = {"case4.txt"};
		int expected= 15;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+3+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases4test() {//16 -> 15 -> 9
		String[] cases = {"case4.txt", "case5.txt"};
		int expected= 13;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+4+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases5test() {//16 -> 13
		String[] cases = {"case6.txt"};
		int expected= 11;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+5+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases6test() {//16 -> 14 -> 11
		String[] cases = {"case2.txt","case4.txt"};
		int expected= 9;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+6+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases7test() {//16 -> 15 -> 11
		String[] cases = {"case4.txt","case8.txt"};
		int expected= 5;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+7+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases8test() {//16 -> 14 -> 11 -> 11
		String[] cases = {"case2.txt","case4.txt", "case8.txt"};
		int expected= 6;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+8+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases9test() {//16 -> 12 -> 9 -> 4
		String[] cases = {"case3.txt","case4.txt", "case10.txt"};
		int expected= 9;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+9+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	/*@Test
	public void cases10test() {
		String[] cases = {"case6.txt","case1.txt"};
		int expected= 10;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+10+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases11test() {
		String[] cases = {"case4.txt","case2.txt"};
		int expected= 11;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+11+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases12test() {
		String[] cases = {"case3.txt","case6.txt","case1.txt"};
		int expected= 9;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+12+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases13test() {
		String[] cases = {"case4.txt","case3.txt","case8.txt"};
		int expected= 4;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+13+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}

	@Test
	public void cases14test() {
		String[] cases = {"case1.txt","case4.txt","case8.txt","case3.txt"};
		int expected= 1;
		int actuals = doJointTest(cases);
		System.out.println("RESULT");
		System.out.println("cases: "+14+", Expected "+expected+", Actual "+actuals);
		System.out.println();
	}*/

	@Test
	public void test() {
		//fail("まだ実装されていません");
		String[][] cases={
				{"case1.txt"},
				{"case2.txt"},
				{"case3.txt"},
				{"case4.txt"},
				{"case6.txt"},
				{"case1.txt","case4.txt"},
				{"case4.txt","case3.txt"},
				{"case1.txt","case4.txt", "case8.txt"},
				{"case1.txt","case3.txt"},
				{"case3.txt","case6.txt"},
				{"case6.txt","case1.txt"},
				{"case4.txt","case2.txt"},
				{"case3.txt","case6.txt","case1.txt"},
				{"case4.txt","case3.txt","case8.txt"},
				{"case1.txt","case4.txt","case8.txt","case3.txt"}
		};
		int[] expected={14,14,12,15,13,11,9,5,6,9,10,11,9,4,1};
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
		/*
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
		*/
	}

	int doJointTest(String[] cases) {
		int tmp = -1;
		dt=new DirectoryTrackerForSingleWinningRegion("Product2_machine3_11");
		int DesignTime[][] = dt.checkDesignTimeSynthesis();
		for(int i=DesignTime.length;i>0;i--) {
			System.out.println("level " + i + "," + "State数 " + DesignTime[i-1][0] + "," + "Transition数 " + DesignTime[i-1][1]);
			System.out.println();
		}
		for(int i=0;i<cases.length-1;i++)dt.checkUpdateControllerFromFile(cases[i]);
		long start=System.currentTimeMillis();
		tmp = dt.checkUpdateControllerFromFile(cases[cases.length-1]);
		long stop=System.currentTimeMillis();
		System.out.print("Spending time of ");
		for(int i=0;i<cases.length;i++){
			System.out.print(cases[i]+"_");
		}
		System.out.println(":"+(stop-start)+"ms, result is "+tmp);
		return tmp;
	}

}
