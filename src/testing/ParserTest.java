package testing;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import multiGR.multiConcurrentModel.MultiConcurrentState;
import multiGR.multiConcurrentModel.MultiConcurrentTransition;

import org.junit.Test;

import multiGR.errorReachableAnalyzer.MultiRequirementParser;

public class ParserTest {

	int[] a={6,9,11},b={4,0,0},c={4,0,8},d={7,5,2},f={3,4,0},g={16,0,0};
	MultiConcurrentState s1=new MultiConcurrentState("s1");
	MultiConcurrentState s2=new MultiConcurrentState("s2");
	MultiConcurrentState s3=new MultiConcurrentState("s3");
	MultiConcurrentState s4=new MultiConcurrentState("s4");
	MultiConcurrentTransition t1=new MultiConcurrentTransition("t1");
	MultiConcurrentTransition t2=new MultiConcurrentTransition("t2");
	MultiConcurrentTransition t3=new MultiConcurrentTransition("t3");

	MultiRequirementParser mrp=new MultiRequirementParser();


	@SuppressWarnings("unchecked")
	@Test
	public void minimizeListTest(){
		List<int[]> actual2=new ArrayList<int[]>(),actual1=new ArrayList<int[]>(),expected1=new ArrayList<int[]>(),expected2=new ArrayList<int[]>();
		actual1.add(a);actual1.add(b);actual1.add(c);
		actual2.add(a);actual2.add(d);actual2.add(f);
		expected1.add(b);
		expected2.add(a);expected2.add(f);

		try {
			Method method=MultiRequirementParser.class.getDeclaredMethod("minimizeList", List.class);
			method.setAccessible(true);

			actual1=(List<int[]>)method.invoke(mrp, actual1);
			actual2=(List<int[]>)method.invoke(mrp,actual2);
			assertArrayEquals(expected1.get(0), actual1.get(0));
			assertEquals(expected2.size(), actual2.size());
			if(expected2.size()==actual2.size())
				for(int i=0;i<expected2.size();i++)
					assertArrayEquals(expected2.get(i), actual2.get(i));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void checkContainsTest(){
		try {
			Method method=MultiRequirementParser.class.getDeclaredMethod("checkContains", int[].class,int[].class);
			method.setAccessible(true);

			assertEquals(false,(boolean)method.invoke(mrp, a,b));
			assertEquals(true,(boolean)method.invoke(mrp, b,a));
			assertEquals(false,(boolean)method.invoke(mrp, d,f));
			assertEquals(true,(boolean)method.invoke(mrp, f,d));
			assertEquals(false,(boolean)method.invoke(mrp, a,d));
			assertEquals(false,(boolean)method.invoke(mrp, d,a));

		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void composeListTest(){
		try {
			Method method=MultiRequirementParser.class.getDeclaredMethod("composeList", List.class,List.class);
			method.setAccessible(true);
			int[] exp1={6,9,11},exp2={7,5,2};
			List<int[]> actual2=new ArrayList<int[]>(),actual1=new ArrayList<int[]>(),expected=new ArrayList<int[]>();
			actual1.add(a);actual1.add(d);
			actual2.add(c);actual2.add(f);
			expected.add(exp1);expected.add(exp2);

			actual1=(List<int[]>) method.invoke(mrp,actual1,actual2);

			if(expected.size()==actual1.size())
				for(int i=0;i<expected.size();i++)
					assertArrayEquals(expected.get(i), actual1.get(i));

		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void stateDeadCheckForAllReqTest(){
//		s1.addToTransition(t1);s1.addToTransition(t2);s1.addToTransition(t3);
//		s2.addFromTransition(t1);s3.addFromTransition(t2);s4.addFromTransition(t3);
//		t1.setFrom(s1);t2.setFrom(s1);t3.setFrom(s1);
//		t1.setTo(s2);t2.setTo(s3);t3.setTo(s4);
//
//
//		try {
//			Method method=MultiRequirementParser.class.getDeclaredMethod("stateDeadCheckForAllReq", MultiConcurrentState.class);
//			method.setAccessible(true);
//			s2.setIsDead(a);
//			s3.setIsDead(d);
//			s4.setIsDead(c);s4.setIsDead(f);
//
//			t1.setIsDead(g);
//
//			int[] exp1={23,13,11};
//
//			method.invoke(mrp, s1);
//
//			List<int[]> act=s1.getDeadList();
//			assertEquals(1,act.size());
//			assertArrayEquals(exp1,act.get(0));
//
//			t1.setIsControllable();
//			t2.setIsControllable();
//			t3.setIsControllable();
//
//			method.invoke(mrp, s1);
//
//			act=s1.getDeadList();
//			System.out.println(act.size());
//			assertEquals(2,act.size());
//			assertArrayEquals(c,act.get(0));
//			assertArrayEquals(f,act.get(1));
//
//		} catch (NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void ExpandTest(){
//		s1.addToTransition(t1);s1.addToTransition(t2);s4.addToTransition(t3);
//		s2.addFromTransition(t1);s3.addFromTransition(t2);s1.addFromTransition(t3);
//		t1.setFrom(s1);t2.setFrom(s1);t3.setFrom(s4);
//		t1.setTo(s2);t2.setTo(s3);t3.setTo(s1);
//		MultiConcurrentModel mcm=new MultiConcurrentModel(s4);
//		mcm.addMultiConcurrentState(s1);mcm.addMultiConcurrentState(s2);mcm.addMultiConcurrentState(s3);
//		mrp=new MultiRequirementParser(mcm, null);
//		s2.setIsDead(a);
//		s3.setIsDead(d);
//
//		try {
//			Method method=MultiRequirementParser.class.getDeclaredMethod("pasteUpdatedDead");
//			method.setAccessible(true);
//
//			List<Transition> updatePart=new ArrayList<Transition>();
//			updatePart.add(t1);
//
//			mcm.setUpdatedPart(updatePart);
//
//			int[] exp1={7,13,11};
//
//			method.invoke(mrp);
//
//			List<int[]> act=s1.getDeadList();
//			assertEquals(1,act.size());
//			assertArrayEquals(exp1,act.get(0));
//
//			act=s4.getDeadList();
//			assertArrayEquals(exp1,act.get(0));
//
//		} catch (NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void shrinkTest(){
//		s1.addToTransition(t1);s1.addToTransition(t2);s4.addToTransition(t3);
//		s2.addFromTransition(t1);s3.addFromTransition(t2);s1.addFromTransition(t3);
//		t1.setFrom(s1);t2.setFrom(s1);t3.setFrom(s4);
//		t1.setTo(s2);t2.setTo(s3);t3.setTo(s1);
//		MultiConcurrentModel mcm=new MultiConcurrentModel(s4);
//		mcm.addMultiConcurrentState(s1);mcm.addMultiConcurrentState(s2);mcm.addMultiConcurrentState(s3);
//		mrp=new MultiRequirementParser(mcm, null);
//		s2.setIsDead(a);
//		s1.setIsDead(a);
//		s4.setIsDead(a);
//		t1.setIsControllable();
//		try {
//			Method method=MultiRequirementParser.class.getDeclaredMethod("pasteUpdatedDead");
//			method.setAccessible(true);
//
//			List<Transition> updatePart=new ArrayList<Transition>();
//			updatePart.add(t2);
//
//			mcm.setUpdatedPart(updatePart);
//
//
//			method.invoke(mrp);
//
//			List<int[]> act=s1.getDeadList();
//			assertEquals(0,act.size());
//
//			act=s4.getDeadList();
//			assertEquals(0,act.size());
//
//		} catch (NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//
//	}

}

