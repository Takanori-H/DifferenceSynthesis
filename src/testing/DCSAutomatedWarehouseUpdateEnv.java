package testing;

import org.junit.Test;

public class DCSAutomatedWarehouseUpdateEnv {
	DirectoryTrackerForSingleWinningRegion dt;

	@Test
	public void test() {
		String[] cont={
				"Controller.txt",
				"Controller.txt",
				"Controller.txt",
				"Controller.txt",
				"Controller.txt",
				"Controller5.txt",
				"Controller6.txt",
				"Controller5.txt",
				"Controller8.txt",
				"Controller10.txt",
				"Controller10.txt",
				"Controller10.txt",
				"Controller12.txt",
				"Controller11.txt",
				"Controller14.txt"

		};
		doJointTest(cont[14]);
	}

	void doJointTest(String controller) {
		dt = new DirectoryTrackerForSingleWinningRegion("AutomatedWarehouse");
		long start=System.currentTimeMillis();
		dt.checkDCSUpdateEnv(controller);
		long stop=System.currentTimeMillis();
		System.out.print("Spending time: "+(stop-start)+"ms");
		return;
	}
}
