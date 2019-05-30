package testing;

import static org.junit.Assert.*;

import org.junit.Test;

import multiGR.errorReachableAnalyzer.MultiRequirementParser;
import multiGR.multiConcurrentModel.MultiConcurrentModel;

public class SingleWinningRegionTestOnProductionCell {
	DirectoryTrackerForSingleWinningRegion dt;
	MultiConcurrentModel mcm;
	MultiRequirementParser mrp;
	//�C�_�ōs���������̓��e�B�����Ԃ�v���܂��B
	@Test
	public void CaseTest1(){
		String[] cases ={"case1.txt"};
		int l=doJointTest(cases,"Controller.txt");
		System.out.println(l);
		if(l==13)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest2(){
		String[] cases ={"case2.txt"};
		int l=doJointTest(cases,"Controller.txt");
		System.out.println(l);
		if(l==13)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest3(){
		String[] cases ={"case3.txt"};
		int l=doJointTest(cases,"Controller.txt");
		System.out.println(l);
		if(l==11)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest4(){
		String[] cases ={"case4.txt"};
		int l=doJointTest(cases,"Controller.txt");
		System.out.println(l);
		if(l==14)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest5(){
		String[] cases ={"case6.txt"};
		int l=doJointTest(cases,"Controller.txt");
		System.out.println(l);
		if(l==12)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest6(){
		String[] cases ={"case1.txt","case4.txt"};
		int l=doJointTest(cases,"Controller_case1.txt");
		System.out.println(l);
		if(l==10)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest7(){
		String[] cases ={"case4.txt","case3.txt"};
		int l=doJointTest(cases,"Controller_case5.txt");
		System.out.println(l);
		if(l==8)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest8(){
		String[] cases ={"case1.txt","case4.txt","case8.txt"};
		int l=doJointTest(cases,"Controller_case9.txt");
		System.out.println(l);
		if(l==4)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest9(){
		String[] cases ={"case1.txt","case3.txt"};
		int l=doJointTest(cases,"Controller_case1.txt");
		System.out.println(l);
		if(l==5)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest10(){
		String[] cases ={"case3.txt","case6.txt"};
		int l=doJointTest(cases,"Controller_case3.txt");
		System.out.println(l);
		if(l==8)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest11(){
		String[] cases ={"case6.txt","case1.txt"};
		int l=doJointTest(cases,"Controller_case6.txt");
		System.out.println(l);
		if(l==9)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest12(){
		String[] cases ={"case4.txt","case2.txt"};
		int l=doJointTest(cases,"Controller_case5.txt");
		System.out.println(l);
		if(l==10)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest13(){
		String[] cases ={"case3.txt","case6.txt","case1.txt"};
		int l=doJointTest(cases,"Controller_case3+case6.txt");
		System.out.println(l);
		if(l==8)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest14(){
		String[] cases ={"case4.txt","case3.txt","case8.txt"};
		int l=doJointTest(cases,"Controller_case4+case5.txt");
		System.out.println(l);
		if(l==3)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest15(){
		String[] cases ={"case1.txt","case4.txt","case8.txt","case3.txt"};
		int l=doJointTest(cases,"Controller_case1+case4+case8.txt");
		System.out.println(l);
		if(l==0)
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}

	//�C�_�ōs���������̓��e�B�����Ԃ�v���܂��B
				/*
				"DontMoveWithNoMeanT.txt",
				"DontMoveWithNoMeanP.txt",
				"DontMoveWithNoMeanO.txt",
				"DontMoveWithNoMeanD.txt",
				"DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanT.txt",[
				"DontMoveWithNoMeanO.txt&&DontMoveWithNoMeanT.txt",
				"DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanT.txt",
				"DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanO.txt",
				"DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanO.txt",
				"DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanD.txt",
				"DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanT.txt&&DontMoveWithNoMeanO.txt",
				"DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanT.txt&&DontMoveWithNoMeanD.txt",
				"DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanT.txt&&DontMoveWithNoMeanO.txt",
				"DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanO.txt",
				"case0:DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanT.txt&&DontMoveWithNoMeanO.txt&&DontMoveWithNoMeanD.txt"
				*/

	int doJointTest(String[] cases,String controller){
		dt=new DirectoryTrackerForSingleWinningRegion(".\\Product2_machine3_11");
		int tmp=-1;
		//dt.checkSimulate(controller);
		for(int i=0;i<cases.length;i++)
			dt.checkUpdateFromFile(cases[i]);
		long start=System.currentTimeMillis();
		tmp=dt.checkUpdateFromFile(cases[cases.length-1]);
		long stop=System.currentTimeMillis();
		System.out.print("Spending time of ");
		for(int i=0;i<cases.length;i++){
			System.out.print(cases[i]+"_");
		}
		System.out.println(":"+(stop-start)+"ms");
		return tmp;
	}


}
