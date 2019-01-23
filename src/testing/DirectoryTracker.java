package testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import multiGR.model.Model;
import multiGR.model.State;
import multiGR.model.Transition;
import multiGR.multiConcurrentModel.MultiConcurrentModel;
import multiGR.errorReachableAnalyzer.MultiConcurrentSystemModelMaker;
import multiGR.errorReachableAnalyzer.MultiRequirementParser;

//評価実験用に用意したディレクトリ上で操作を行うクラス
public class DirectoryTracker {
	private File directory;
	private List<String> candidate;
	private List<String> cas;
	private List<Model> req;
	private Model e;
	private Model c;
	private boolean firstCheck;
	private MultiConcurrentModel cm;
	private MultiRequirementParser mrp;
	private String targetError = "";
	private List<Integer> checkerForMRP;
	private Map<String, Integer> priorityMap = null;

	private String sep = File.separator;

	public DirectoryTracker(String directory) {
		this.directory = new File(directory);
		if (this.directory.exists()) {
			candidate = new ArrayList<String>(Arrays.asList(this.directory
					.list()));
			candidate.remove("Controller");
			cas = new ArrayList<String>();
			setPriorityMap();
		}
		firstCheck = false;
	}

	public boolean setDirectory(String dir) {
		directory = new File(dir);
		if (directory.exists()) {
			candidate = new ArrayList<String>(Arrays.asList(directory.list()));
			candidate.remove("Controller");
			cas = new ArrayList<String>();
			return true;
		}
		return false;
	}

	public void setPriorityMap() {
		priorityMap = new HashMap<String, Integer>();
		int i = 1;
		File priorityFile = new File(directory.getAbsolutePath() + sep
				+ "Controller" + sep + "Priority.txt");
		if (priorityFile.isFile())
			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						priorityFile));

				String c;
				while ((c = reader.readLine()) != null) {
					priorityMap.put(c, i);
					i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			priorityMap = null;
	}

	private File searchEnvInDirectory() {
		return new File(directory.getAbsolutePath() + sep + "Controller" + sep
				+ "ENV.txt");
	}

	private void setControllableAction() {

		File caFile = new File(directory.getAbsolutePath() + sep + "Controller"
				+ sep + "CA.txt");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(caFile));
			String c;
			while ((c = reader.readLine()) != null) {
				// System.out.println("ControllablAction:"+c);
				cas.add(c);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String setDegradationTargets() {
		File caFile = new File(directory.getPath() + sep + "Controller" + sep
				+ "DegradationTargets.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(caFile));
			String c;
			checkerForMRP = new ArrayList<Integer>();
			for (int i = 0; i < (candidate.size() / 32 + 1); i++)
				checkerForMRP.add(0);
			while ((c = reader.readLine()) != null) {
				int i = candidate.indexOf(c);
				if (i != -1) {
					if (targetError == "")
						targetError = "ERROR" + i;
					else
						targetError = targetError + "&&ERROR" + i;
					int tmp = checkerForMRP.get(i / 32) + (1 << (i % 32));
					checkerForMRP.set(i / 32, tmp);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println(targetError);
		return targetError;
	}

	public MultiConcurrentModel getMCModelFromDirectory() {
		File env;
		FSP_FileReader reader;
		req = new ArrayList<Model>();

		if ((env = searchEnvInDirectory()) == null) {
			return null;
		}
		setControllableAction();
		reader = new FSP_FileReader(env);
		e = reader.getModel();
		for (int i = 0; i < candidate.size(); i++) {
			if (new File(directory.getPath() + sep + candidate.get(i)).isFile()) {
				reader = new FSP_FileReader(directory.getPath() + sep
						+ candidate.get(i));
				// System.out.println(candidate.get(i));
				req.add(reader.getModel());
			}
		}
		targetError = setDegradationTargets();
		return cm = MultiConcurrentSystemModelMaker.makeConcurrentSystem(e,
				req, cas, targetError);
	}

	public List<Model> getReqMonis() {
		FSP_FileReader reader;
		req = new ArrayList<Model>();
		for (int i = 0; i < candidate.size(); i++) {
			if (new File(directory.getPath() + sep + candidate.get(i)).isFile()) {
				reader = new FSP_FileReader(directory.getPath() + sep
						+ candidate.get(i));
				// System.out.println(candidate.get(i));
				req.add(reader.getModel());
			}
		}

		return req;
	}

	public List<List<Integer>> checkSimulate(String controller) {
		if (cm == null) {
			getMCModelFromDirectory();
		}
		FSP_FileReader reader = new FSP_FileReader(directory.getPath() + sep
				+ "Controller" + sep + controller);
		c = reader.getModel();
		mrp = new MultiRequirementParser(cm, checkerForMRP);
		firstCheck = true;
		return mrp.checkSimulate(c);

	}

	public String checkSimulateWithResult(String controller){
		if (cm == null) {
			getMCModelFromDirectory();
		}
		FSP_FileReader reader = new FSP_FileReader(directory.getPath() + sep
				+ "Controller" + sep + controller);
		c = reader.getModel();
		mrp = new MultiRequirementParser(cm, checkerForMRP);
		firstCheck = true;
		List<List<Integer>> resultBits= mrp.checkSimulate(c);
		Result finalResult=null;
		for (int i = 0; i < resultBits.size(); i++) {
			Result r=convertResults(resultBits.get(i));
//			System.out.println("checkUpdate:"+r.s+","+r.i);//
			if(finalResult==null || finalResult.i>r.i){
				finalResult=r;
			}
		}
		if(finalResult==null){
			return "";
		}else{
			return finalResult.s;
		}

	}


	private boolean modelUpdate(String updatedPart,
			ArrayList<Transition> updatePart) {
		if (!firstCheck) {
			System.out.println("ERROR: First check have not been done yet");
			return false;
		}
		String[] materials = updatedPart.split(",");
		if (materials.length != 3) {
			System.out.println("ERROR:Input type is wong");
			return false;
		}
		cm = MultiConcurrentSystemModelMaker.modelUpdate(cm, e, req,
				materials[0], materials[1], materials[2], updatePart);
		// DisplayModels.displayDeadStates(cm);//debug
		// System.out.println("modelUpdate:"+cm.getUpdatedPart().size());//debug
		return true;
	}

	public List<List<Integer>> checkUpdated() {
		if (mrp == null)
			mrp = new MultiRequirementParser(cm, checkerForMRP);
		return mrp.checkUpdatedSimulate(c);
	}

	public String checkUpdateFromFile(String fileName) {
		mrp = new MultiRequirementParser(cm, checkerForMRP);
		BufferedReader reader = null;
		Result finalResult = null;
		List<List<Integer>> resultBits = null;
		try {
			reader = new BufferedReader(new FileReader(new File(
					directory.getPath() + sep + "Controller" + sep + fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		try {
			String temp;
			ArrayList<Transition> updateTransitions = new ArrayList<Transition>();
			while ((temp = reader.readLine()) != null) {
				if (!modelUpdate(temp, updateTransitions))
					return null;
			}
			resultBits = checkUpdated();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < resultBits.size(); i++) {
			Result r=convertResults(resultBits.get(i));
	//		System.out.println("checkUpdate:"+r.s+","+r.i);//
			if(finalResult==null || finalResult.i>r.i){
				finalResult=r;
			}
		}
		if(finalResult==null){
			return "";
		}else{
			return finalResult.s;
		}
	}

	private Result convertResults(List<Integer> result) {
		Result finalResult=new Result();
		finalResult.s = "";
		finalResult.i=0;
		for (int i = 0; i < result.size(); i++) {
			int p = 0;
			for (int j = result.get(i); j > 0; j /= 2) {
				if (j % 2 == 1) {
					if (finalResult.s == ""){
						finalResult.s = candidate.get((i * 32) + p);
						//System.out.println("convertResult:"+candidate.get((i*32)+p));
						finalResult.i+=priorityMap.get(candidate.get((i*32)+p));
					}else
						finalResult.s += "&&" + candidate.get((i * 32) + p);
						finalResult.i+=priorityMap.get(candidate.get((i*32)+p));
				}
				p++;
			}
		}
		return finalResult;
	}

	// Extracting model's difference between 2 files.
	public void extractDifference(File before, File after) {
		Model m1 = new FSP_FileReader(before).getModel(), m2 = new FSP_FileReader(
				after).getModel();
		HashMap<String, State> stateMap = new HashMap<String, State>();
		String t = null;
		List<String> l = new ArrayList<String>();
		BufferedWriter f = null;
		if (m1.getSize() != m2.getSize()) {
			System.out.println("ERROR:These models are not similar");
			return;
		}
		makeStateMapWithoutR(stateMap, m1.getInitialState(),
				m2.getInitialState());

		for (int i = 0; i < m2.getSize(); i++) {
			State s2 = m2.getState(i);
			State s1 = stateMap.get(s2.toString());
			System.out.println("standard" + s2 + "," + s1);
			for (int j = 0; j < s2.getToTransitionNum(); j++) {
				t = s2.getToTransition(j).toString();
				if (s1.getToTransition(t) == null) {
					System.out.println(s2.getToStateByTransition(t)
							+ ","
							+ stateMap.get(s2.getToStateByTransition(t)
									.toString()));
					l.add(stateMap.get(s2.toString())
							+ ","
							+ t
							+ ","
							+ stateMap.get(
									s2.getToStateByTransition(t).toString())
									.toString());
				}
			}
		}// */
		if (l.size() == 0) {
			System.out.println("ERROR:There is no difference");
		}
		try {
			f = new BufferedWriter(new FileWriter(new File(directory.getPath()
					+ sep + "Controller" + sep + t + ".txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			f.write(l.get(0));
			for (int i = 1; i < l.size(); i++) {
				f.newLine();
				f.write(l.get(i));
			}
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(directory.getAbsolutePath());

	}

	// used for extractDifference()
	private void makeStateMapWithoutR(HashMap<String, State> stateMap,
			State s1, State s2) {
		Stack<State> stack = new Stack<State>();
		State c2 = s2;
		stateMap.put(s2.toString(), s1);
		stack.push(c2);
		while (!stack.isEmpty()) {
			c2 = stack.pop();
			s1 = stateMap.get(c2.toString());
			while (c2.hasNext()) {
				Transition t2 = (Transition) c2.next(), t1;
				if ((t1 = s1.getToTransition(t2.toString())) != null)
					if (stateMap.get(t2.getTo().toString()) == null) {
						if (t1 == t2)
							System.out.println(t1.getTo() + "," + t2.getTo());
						stateMap.put(t2.getTo().toString(), t1.getTo());
						stack.push(t2.getTo());
					}
			}
		}
		System.out.println("model size:" + stateMap.size());
	}// */

	public String toString() {
		return "Directory's path:" + this.directory.getAbsolutePath();
	}

	class Result{
		String s;
		Integer i;
	}

}
