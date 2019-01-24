package testing;

import java.io.File;

import org.junit.Test;

public class ExtractingDifference {

	String sep=File.separator;
	@Test
	public void test() {
		DirectoryTracker dt;
		dt=new DirectoryTracker(/*"TransitionAnalyze"+File.separator+*/"KIVAsystemSingle");

		File before =new File("KIVAsystemSingle"+sep+"Controller"+sep+"ENV.txt");
		File after=new File("KIVAsystemSingle"+sep+"Controller"+sep+"ENV2.txt");

		dt.extractDifference(before, after);



	}

}
