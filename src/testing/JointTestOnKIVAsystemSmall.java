package testing;

import static org.junit.Assert.*;

import java.util.List;

import multiGR.model.Model;
import multiGR.multiConcurrentModel.MultiConcurrentModel;

import org.junit.Test;

import multiGR.errorReachableAnalyzer.MultiRequirementParser;

public class JointTestOnKIVAsystemSmall {
	DirectoryTracker dt;
	MultiConcurrentModel mcm;
	MultiRequirementParser mrp;

	// ProductionCellの例

	// 修論で行った実験の内容。数時間を要します。

	@Test
	public void allCaseTest() {
		String[][] cases = {
				{ "case1.txt" },
/*				{ "case2.txt" },
				{ "case3.txt" },
				{ "case4.txt" },
				{ "case5.txt" },//
				{ "case6.txt" },
				{ "case2.txt", "case4.txt"},//case7
				{ "case2.txt", "case5.txt"},//case8
				{ "case4.txt", "case2.txt"},//case9
				{ "case4.txt", "case5.txt"},//case10
				{ "case5.txt", "case2.txt"},//case11
				{ "case5.txt", "case4.txt"},//case12
				{ "case2.txt", "case5.txt", "case4.txt"},//case13
				{ "case2.txt", "case4.txt", "case5.txt"},//case14
				{ "case4.txt", "case5.txt", "case2.txt"},//case15*/

		};
		String[] cont = {
				"Controller.txt",
				"Controller.txt",
				"Controller.txt",
				"Controller.txt",
				"Controller.txt",
				"Controller.txt",
				"Controller2.txt",
				"Controller2.txt",
				"Controller4.txt",
				"Controller4.txt",
				"Controller5.txt",
				"Controller5.txt",
				"Controller2+5.txt",
				"Controller4+2.txt",
				"Controller4+5.txt",
		};

		String[] expected = {
				"BOUNDED_LIVE0.txt",
				"InvalidReturn1.txt",
				""

		};
		String actual=null;
		for (int i = 0; i < cases.length; i++) {
			actual= doJointTest(cases[i], cont[i]);
			System.out.println(actual);
		}// */

		assertEquals(expected[0], actual);
		/*
		 * assertEquals(expected[1],actuals[1]);
		 * assertEquals(expected[2],actuals[2]);
		 * assertEquals(expected[3],actuals[3]);
		 * assertEquals(expected[4],actuals[4]);
		 * assertEquals(expected[5],actuals[5]);
		 * assertEquals(expected[6],actuals[6]);
		 * assertEquals(expected[7],actuals[7]);
		 * assertEquals(expected[8],actuals[8]);
		 * assertEquals(expected[9],actuals[9]);
		 * assertEquals(expected[10],actuals[10]);
		 * assertEquals(expected[11],actuals[11]);
		 * assertEquals(expected[12],actuals[12]);
		 * assertEquals(expected[13],actuals[13]);
		 * assertEquals(expected[14],actuals[14]);
		 */
	}

	String doJointTest(String[] cases, String controller) {
		String tmp = null;
		dt = new DirectoryTracker(
				/* "TransitionAnalyze"+File.separator+// */"KIVAsystemSingle");
		mcm = dt.getMCModelFromDirectory();
		System.out.println("test");
		dt.checkSimulate(controller);
		for (int i = 0; i < cases.length-1; i++)
			dt.checkUpdateFromFile(cases[i]);
		long start = System.currentTimeMillis();
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
