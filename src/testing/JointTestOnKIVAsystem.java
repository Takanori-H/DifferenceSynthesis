package testing;

	import static org.junit.Assert.*;

import java.util.List;

import multiGR.model.Model;
import multiGR.multiConcurrentModel.MultiConcurrentModel;

import org.junit.Test;

import multiGR.errorReachableAnalyzer.MultiRequirementParser;

	public class JointTestOnKIVAsystem {
		DirectoryTracker dt;
		MultiConcurrentModel mcm;
		MultiRequirementParser mrp;
		//ProductionCellの例

		//修論で行った実験の内容。数時間を要します。

		@Test
		public void allCaseTest() {
			String[][] cases={
					{"case1.txt"},
			};
			String[] cont={
					"Controller.txt",

			};

			String[] expected={
					"BOUNDED_LIVE0.txt"
			};
			String[] actuals=new String[15];
			for(int i=0;i<cases.length;i++){
				String l=doJointTest(cases[i],cont[i]);
				actuals[i]=l;
				System.out.println(actuals[i]);
			}//*/



			assertEquals(expected[0],actuals[0]);
/*			assertEquals(expected[1],actuals[1]);
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


		String doJointTest(String[] cases,String controller){
			String tmp=null;
			dt=new DirectoryTracker(/*"TransitionAnalyze"+File.separator+//*/"KIVAsystemSmall");
			mcm=dt.getMCModelFromDirectory();
			dt.checkSimulate(controller);
			for(int i=0;i<cases.length;i++)
				dt.checkUpdateFromFile(cases[i]);
			long start=System.currentTimeMillis();
			tmp = dt.checkUpdateFromFile(cases[cases.length-1]);
			long stop=System.currentTimeMillis();
			System.out.print("Spending time of ");
			for(int i=0;i<cases.length;i++){
				System.out.print(cases[i]+"_");
			}
			System.out.println(":"+(stop-start)+"ms");
			return tmp;
		}

		@Test
		public void test(){
			dt=new DirectoryTracker(/*"TransitionAnalyze"+File.separator+//*/"KIVAsystem");
			List<Model> monis=dt.getReqMonis();

			for(Model m:monis)DisplayModels.displayModel(m);

		}
	}

