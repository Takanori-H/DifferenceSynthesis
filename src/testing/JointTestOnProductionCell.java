package testing;

import static org.junit.Assert.*;
import multiGR.multiConcurrentModel.MultiConcurrentModel;

import org.junit.Test;

import multiGR.errorReachableAnalyzer.MultiRequirementParser;

public class JointTestOnProductionCell {
	DirectoryTracker dt;
	MultiConcurrentModel mcm;
	MultiRequirementParser mrp;
	//修論で行った実験の内容。数時間を要します。




	@Test
	public void CaseTest1(){
		String[] cases ={"case1.txt"};
		String l=doJointTest(cases,"Controller.txt");
		if(l.contains("DontMoveWithNoMeanP.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest2(){
		String[] cases ={"case2.txt"};
		String l=doJointTest(cases,"Controller.txt");
		if(l.contains("DontMoveWithNoMeanP.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest3(){
		String[] cases ={"case3.txt"};
		String l=doJointTest(cases,"Controller.txt");
		if(l.contains("DontMoveWithNoMeanD.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest4(){
		String[] cases ={"case4.txt"};
		String l=doJointTest(cases,"Controller.txt");
		if(l.contains("DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest5(){
		String[] cases ={"case6.txt"};
		String l=doJointTest(cases,"Controller.txt");
		if(l.contains("DontMoveWithNoMeanO.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest6(){
		String[] cases ={"case1.txt","case4.txt"};
		String l=doJointTest(cases,"Controller_case1.txt");
		if(l.contains("DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest7(){
		String[] cases ={"case4.txt","case3.txt"};
		String l=doJointTest(cases,"Controller_case5.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest8(){
		String[] cases ={"case1.txt","case4.txt","case8.txt"};
		String l=doJointTest(cases,"Controller_case9.txt");
		if(l.contains("DontMoveWithNoMeanO.txt&&DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest9(){
		String[] cases ={"case1.txt","case3.txt"};
		String l=doJointTest(cases,"Controller_case1.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanP.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest10(){
		String[] cases ={"case3.txt","case6.txt"};
		String l=doJointTest(cases,"Controller_case3.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest11(){
		String[] cases ={"case6.txt","case1.txt"};
		String l=doJointTest(cases,"Controller_case6.txt");
		if(l.contains("DontMoveWithNoMeanO.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest12(){
		String[] cases ={"case4.txt","case2.txt"};
		String l=doJointTest(cases,"Controller_case5.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest13(){
		String[] cases ={"case3.txt","case6.txt","case1.txt"};
		String l=doJointTest(cases,"Controller_case3+case6.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest14(){
		String[] cases ={"case4.txt","case3.txt","case8.txt"};
		String l=doJointTest(cases,"Controller_case4+case5.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanO.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest15(){
		String[] cases ={"case1.txt","case4.txt","case8.txt","case3.txt"};
		String l=doJointTest(cases,"Controller_case1+case4+case8.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanO.txt&&DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}//*/
/*	@Test
	public void CaseTest1s(){
		String[] cases ={"case1.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller.txt");
		if(l.contains("DontMoveWithNoMeanP.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest2s(){
		String[] cases ={"case2.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller.txt");
		if(l.contains("DontMoveWithNoMeanP.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest3s(){
		String[] cases ={"case3.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller.txt");
		if(l.contains("DontMoveWithNoMeanD.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest4s(){
		String[] cases ={"case4.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller.txt");
		if(l.contains("DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest5s(){
		String[] cases ={"case6.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller.txt");
		if(l.contains("DontMoveWithNoMeanO.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest6s(){
		String[] cases ={"case1.txt","case4.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller_case1.txt");
		if(l.contains("DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest7s(){
		String[] cases ={"case4.txt","case3.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller_case5.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}
	}
	@Test
	public void CaseTest8s(){
		String[] cases ={"case1.txt","case4.txt","case8.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller_case9.txt");
		if(l.contains("DontMoveWithNoMeanO.txt&&DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest9s(){
		String[] cases ={"case1.txt","case3.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller_case1.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanP.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest10s(){
		String[] cases ={"case3.txt","case6.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller_case3.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest11s(){
		String[] cases ={"case6.txt","case1.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller_case6.txt");
		if(l.contains("DontMoveWithNoMeanO.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest12s(){
		String[] cases ={"case4.txt","case2.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller_case5.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest13s(){
		String[] cases ={"case3.txt","case6.txt","case1.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller_case3+case6.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest14s(){
		String[] cases ={"case4.txt","case3.txt","case8.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller_case4+case5.txt");
		for(int i=0;i<l.size();i++){
			System.out.println("============="+l.get(i));
		}
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanO.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}
	@Test
	public void CaseTest15s(){
		String[] cases ={"case1.txt","case4.txt","case8.txt","case3.txt"};
		List<String> l=doJointTestFromScratch(cases,"Controller_case1+case4+case8.txt");
		if(l.contains("DontMoveWithNoMeanD.txt&&DontMoveWithNoMeanO.txt&&DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanT.txt"))
			assertTrue(true);
		else{fail("An expected result is not included in actuals");}

	}//*/

	//修論で行った実験の内容。数時間を要します。
				/*
				"DontMoveWithNoMeanT.txt",
				"DontMoveWithNoMeanP.txt",
				"DontMoveWithNoMeanO.txt",
				"DontMoveWithNoMeanD.txt",
				"DontMoveWithNoMeanP.txt&&DontMoveWithNoMeanT.txt",
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

	String doJointTest(String[] cases,String controller){
		dt=new DirectoryTracker(".\\Product2_machine3_11");
		String tmp=null;
		dt.checkSimulate(controller);
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

	String doJointTestFromScratch(String[] cases,String controller){
		dt=new DirectoryTracker(".\\Product2_machine3_11");
		String tmp=null;
		long start=System.currentTimeMillis();
		dt.checkSimulate(controller);
		for(int i=0;i<cases.length;i++)
			dt.checkUpdateFromFile(cases[i]);
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
