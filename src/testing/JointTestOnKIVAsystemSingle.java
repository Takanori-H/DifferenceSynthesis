package testing;

import static org.junit.Assert.*;

import java.util.List;

import multiGR.model.Model;
import multiGR.multiConcurrentModel.MultiConcurrentModel;

import org.junit.Test;

import multiGR.errorReachableAnalyzer.MultiRequirementParser;

public class JointTestOnKIVAsystemSingle {
	DirectoryTracker dt;
	MultiConcurrentModel mcm;
	MultiRequirementParser mrp;

	// ProductionCellの例

	// 修論で行った実験の内容。数時間を要します。

	@Test
	public void allCaseTest() {
		String[][] cases = {
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
		String[] cont = {
				"Controller.txt",
				"Controller.txt",
				"Controller.txt",
				"Controller.txt",
				"Controller5.txt",
				"Controller2.txt",
				"Controller5+2.txt",
				"Controller2.txt",
				"Controller4.txt",
				"Controller5.txt",
				"Controller5.txt",
				"Controller5+2.txt",
				"Controller5+4.txt",
				"Controller5+2B.txt",
				"Controller1.txt"
		};

		String[] expected = {
				"BOUNDED_LIVE0.txt",
				"InvalidReturn1.txt",
				"InvalidReturn2.txt",
				"BOUNDED_LIVE0.txt",
				"BOUNDED_LIVE0.txt&&InvalidArriveStation.txt",
				"BOUNDED_LIVE0.txt&&InvalidReturn1.txt",
				"BOUNDED_LIVE0.txt&&InvalidArriveStation.txt&&InvalidReturn1.txt",
				"BOUNDED_LIVE0.txt&&InvalidReturn1.txt&&InvalidReturn2.txt",
				"BOUNDED_LIVE0.txt&&InvalidReturn1.txt&&InvalidReturn2.txt",
				"BOUNDED_LIVE0.txt&&InvalidReturn1.txt",
				"BOUNDED_LIVE0.txt&&InvalidReturn2.txt",
				"BOUNDED_LIVE0.txt&&InvalidArriveStation.txt&&InvalidReturn1.txt",
				"BOUNDED_LIVE0.txt&&InvalidArriveStation.txt&&InvalidReturn2.txt",
				"BOUNDED_LIVE0.txt&&InvalidArriveStation.txt&&InvalidReturn1.txt&&InvalidReturn2.txt",
				"BOUNDED_LIVE0.txt&&InvalidReturn2.txt",
		};
		String[] actuals=new String[15];
		for (int i = 0; i < cases.length; i++) {
			actuals[i]= doJointTest(cases[i], cont[i]);
			System.out.println(actuals[i]);
		}// */

		assertEquals(expected[0], actuals[0]);

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

	}

	String doJointTest(String[] cases, String controller) {
		String tmp = null;
		dt = new DirectoryTracker(
				/* "TransitionAnalyze"+File.separator+// */"KIVAsystemSingle");
		mcm = dt.getMCModelFromDirectory();
		System.out.println("test");
		long start = System.currentTimeMillis();
		dt.checkSimulate(controller);
		for (int i = 0; i < cases.length-1; i++)
			dt.checkUpdateFromFile(cases[i]);
		tmp = dt.checkUpdateFromFile(cases[cases.length - 1]);
		long stop = System.currentTimeMillis();
		System.out.print("Spending time of ");
		for (int i = 0; i < cases.length; i++) {
			System.out.print(cases[i] + "_");
		}
		System.out.println(":" + (stop - start) + "ms");
		return tmp;
	}

	public void test() {
		dt = new DirectoryTracker(
				/* "TransitionAnalyze"+File.separator+// */"KIVAsystemSingle");
		List<Model> monis = dt.getReqMonis();

		for (Model m : monis)
			DisplayModels.displayModel(m);

	}
}
