package testing;

	import static org.junit.Assert.*;
import multiGR.multiConcurrentModel.MultiConcurrentModel;

import org.junit.Test;

import multiGR.errorReachableAnalyzer.MultiRequirementParser;

	public class JointTestOnAutomatedWarehouse {
		DirectoryTracker dt;
		MultiConcurrentModel mcm;
		MultiRequirementParser mrp;
		//ProductionCellの例
		@Test
		public void oneCaseTest(){
			dt=new DirectoryTracker(/*"TransitionAnalyze"+File.separator+*/"AutomatedWarehouse");
			System.out.println(dt);
			mcm=dt.getMCModelFromDirectory();
			String[] cases ={"case1.txt"};
			assertEquals("",doJointTest(cases,"Controller.txt"));

		}
		//修論で行った実験の内容。数時間を要します。
		@Test
		public void allCaseTest() {
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

			String[] expected={
					"",
					"COUNT.txt",
					"COUNT.txt&&Req2.txt",
					"COUNT.txt&&Req3.txt",
					"COUNT.txt",
					"COUNT.txt&&Req2.txt",
					"COUNT.txt&&Req2.txt&&Req3.txt",
					"COUNT.txt&&Req3.txt",
					"COUNT.txt&&Req2.txt&&Req3.txt",
					"COUNT.txt",
					"COUNT.txt&&Req3.txt",
					"COUNT.txt&&Req2.txt",
					"COUNT.txt&&Req2.txt",
					"COUNT.txt&&Req3.txt",
					"COUNT.txt&&Req2.txt&&Req3.txt"
			};
			String[] actuals=new String[15];
			for(int i=0;i<cases.length;i++){
				String l=doJointTest(cases[i],cont[i]);
				actuals[i]=l;
			}//*/



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

		}


		String doJointTest(String[] cases,String controller){
			String tmp=null;
			dt=new DirectoryTracker(/*"TransitionAnalyze"+File.separator+//*/"AutomatedWarehouse");
			long start=System.currentTimeMillis();
			mcm=dt.getMCModelFromDirectory();
			dt.checkSimulate(controller);
			for(int i=0;i<cases.length;i++)
				dt.checkUpdateFromFile(cases[i]);
			tmp = dt.checkUpdateFromFile(cases[cases.length-1]);
			long stop=System.currentTimeMillis();
			System.out.print("Spending time of ");
			for(int i=0;i<cases.length;i++){
				System.out.print(cases[i]+"_");
			}
			System.out.println(":"+(stop-start)+"ms");
			return tmp;
		}


	}

