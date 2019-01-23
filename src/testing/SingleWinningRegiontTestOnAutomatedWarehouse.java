package testing;

	import static org.junit.Assert.*;

import org.junit.Test;
	public class SingleWinningRegiontTestOnAutomatedWarehouse {
		DirectoryTrackerForSingleWinningRegion dt;
		public void oneCaseTest(){
			dt=new DirectoryTrackerForSingleWinningRegion(/*"TransitionAnalyze"+File.separator+*/"AutomatedWarehouse");
			System.out.println(dt);
//			dt.getCModelFromDirectory();
			String[] cases ={"case1.txt"};
			assertEquals(5,doJointTest(cases,"Controller.txt"));

		}
		//�C�_�ōs���������̓��e�B�����Ԃ�v���܂��B
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

/*			String[] expected={
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
			};//*/
			int[] expected={5,4,3,2,4,3,1,2,1,4,2,3,3,2,1};

			int[] actuals=new int[15];
			actuals[4] = doJointTest(cases[4], cont[4]);
			/*for(int i=0;i<cases.length;i++){
				actuals[i]=doJointTest(cases[i],cont[i]);;
				System.out.println("SingleWinningRegionTest:"+i+","+actuals[i]);
				System.out.println();
			}//*/



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


		int doJointTest(String[] cases,String controller){
			int tmp=-1;
			dt=new DirectoryTrackerForSingleWinningRegion(/*"TransitionAnalyze"+File.separator+*/"AutomatedWarehouse");
//			dt.getCModelFromDirectory();
			dt.checkSimulate(controller);
			for(int i=0;i<cases.length-1;i++)
				dt.checkUpdateFromFile(cases[i]);//cases内をfor文で回している case1.txtやcase2.txtの中身 今までの環境のupdateの情報

//			List<ConcurrentModel>cmList=dt.getCModelFromDirectory();
//			for(ConcurrentModel cm:cmList){
//				//System.out.println("Start displayModel");
//				DisplayModels.displayDeadStates(cm);
//			}

			long start=System.currentTimeMillis();
			tmp = dt.checkUpdateFromFile(cases[cases.length-1]);//最後に今回のupdateの情報
			long stop=System.currentTimeMillis();
			System.out.print("Spending time of ");
			for(int i=0;i<cases.length;i++){
				System.out.print(cases[i]+"_");
			}
			System.out.println(":"+(stop-start)+"ms, result is "+tmp);
			return tmp;
		}


	}

