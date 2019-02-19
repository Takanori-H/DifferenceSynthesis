package testing;

import org.junit.Test;

public class DesignTimeSynthesisTestOnProductionCell {
	DirectoryTrackerForSingleWinningRegion dt;

	@Test
	public void test() {
		//fail("まだ実装されていません");
		int expected[][]= {{67, 97},{81, 113},{81, 113},{55, 73},{15, 15}};
		for(int i=expected.length;i>0;i--) {
			System.out.println("level " + i + "," + "State数 " + expected[i-1][0] + "," + "Transition数 " + expected[i-1][1]);
			System.out.println();
		}
		int actuals[][] = doJointTest();
		for(int i=actuals.length;i>0;i--) {
			System.out.println("level " + i + "," + "State数 " + actuals[i-1][0] + "," + "Transition数 " + actuals[i-1][1]);
			System.out.println();
		}
		/*assertEquals(expected[0][0],actuals[0][0]);
		assertEquals(expected[0][1],actuals[0][1]);
		assertEquals(expected[1][0],actuals[1][0]);
		assertEquals(expected[1][1],actuals[1][1]);
		assertEquals(expected[2][0],actuals[2][0]);
		assertEquals(expected[2][1],actuals[2][1]);
		assertEquals(expected[3][0],actuals[3][0]);
		assertEquals(expected[3][1],actuals[3][1]);
		assertEquals(expected[4][0],actuals[4][0]);
		assertEquals(expected[4][1],actuals[4][1]);
		assertEquals(expected[5][0],actuals[5][0]);
		assertEquals(expected[5][1],actuals[5][1]);
		assertEquals(expected[6][0],actuals[6][0]);
		assertEquals(expected[6][1],actuals[6][1]);
		assertEquals(expected[7][0],actuals[7][0]);
		assertEquals(expected[7][1],actuals[7][1]);
		assertEquals(expected[8][0],actuals[8][0]);
		assertEquals(expected[8][1],actuals[8][1]);
		assertEquals(expected[9][0],actuals[9][0]);
		assertEquals(expected[9][1],actuals[9][1]);
		assertEquals(expected[10][0],actuals[10][0]);
		assertEquals(expected[10][1],actuals[10][1]);
		assertEquals(expected[11][0],actuals[11][0]);
		assertEquals(expected[11][1],actuals[11][1]);
		assertEquals(expected[12][0],actuals[12][0]);
		assertEquals(expected[12][1],actuals[12][1]);
		assertEquals(expected[13][0],actuals[13][0]);
		assertEquals(expected[13][1],actuals[13][1]);
		assertEquals(expected[14][0],actuals[14][0]);
		assertEquals(expected[14][1],actuals[14][1]);*/
	}

	int[][] doJointTest() {
		dt = new DirectoryTrackerForSingleWinningRegion("Product2_machine3_11");
		return dt.checkDesignTimeSynthesis();
	}
}
