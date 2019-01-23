package testing;

import static org.junit.Assert.*;

import org.junit.Test;

public class DesignTimeSynthesisTestOnAutomatedWarehouse {
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
		assertEquals(expected[0][0],actuals[0][0]);
		assertEquals(expected[0][1],actuals[0][1]);
		assertEquals(expected[1][0],actuals[1][0]);
		assertEquals(expected[1][1],actuals[1][1]);
		assertEquals(expected[2][0],actuals[2][0]);
		assertEquals(expected[2][1],actuals[2][1]);
		assertEquals(expected[3][0],actuals[3][0]);
		assertEquals(expected[3][1],actuals[3][1]);
		assertEquals(expected[4][0],actuals[4][0]);
		assertEquals(expected[4][1],actuals[4][1]);
	}

	int[][] doJointTest() {
		dt = new DirectoryTrackerForSingleWinningRegion("AutomatedWarehouse");
		return dt.checkDesignTimeSynthesis();
	}

}
