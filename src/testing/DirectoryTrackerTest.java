package testing;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

public class DirectoryTrackerTest {
	private String sep =File.separator;
	DirectoryTracker dt;
	@Test
	public void degradationTargetTest1() {
		dt=new DirectoryTracker("."+sep+"AutomatedWarehouse");
		try {
			Method method=DirectoryTracker.class.getDeclaredMethod("setDegradationTargets");
			method.setAccessible(true);

			assertEquals("ERROR0&&ERROR2&&ERROR3",method.invoke(dt));
			
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
	@Test
	public void convertResultTest1() {
		dt=new DirectoryTracker("."+sep+"AutomatedWarehouse");
		try {
			Method method=DirectoryTracker.class.getDeclaredMethod("convertResults",int[].class);
			method.setAccessible(true);
			int[] t1={1},t2={5},t3={13};
			assertEquals("COUNT.txt",method.invoke(dt,t1));
			assertEquals("COUNT.txt&&Req2.txt",method.invoke(dt,t2));
			assertEquals("COUNT.txt&&Req2.txt&&Req3.txt",method.invoke(dt,t3));

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
	@Test
	public void degradationTargetTest2() {
		dt=new DirectoryTracker("."+sep+"Product2_machine3_11");
		try {
			Method method=DirectoryTracker.class.getDeclaredMethod("setDegradationTargets");
			method.setAccessible(true);

			assertEquals("ERROR3&&ERROR4&&ERROR5&&ERROR6",method.invoke(dt));
			
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

}
