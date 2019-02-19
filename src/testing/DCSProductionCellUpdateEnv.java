package testing;

import org.junit.Test;

public class DCSProductionCellUpdateEnv {
	DirectoryTrackerForSingleWinningRegion dt;

	@Test
	public void test() {
		doJointTest();
	}

	void doJointTest() {
		dt = new DirectoryTrackerForSingleWinningRegion("Product2_machine3_11");
		long start=System.currentTimeMillis();
		//dt.checkDCSUpdateEnv();
		long stop=System.currentTimeMillis();
		System.out.print("Spending time: "+(stop-start)+"ms");
		return;
	}
}
