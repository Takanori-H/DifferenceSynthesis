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

import multiGR.GameSpace.ConcurrentSystemModelMaker;
import multiGR.GameSpace.GameModel;
import multiGR.GameSpace.TransitionParser;
import multiGR.model.Model;
import multiGR.model.State;
import multiGR.model.Transition;
//�]�������p�ɗp�ӂ����f�B���N�g����ő�����s���N���X
public class DirectoryTrackerForSingleWinningRegion {
	private File directory;
	private List<String> candidate;
	private List<String> cas;
	private List<Model[]> reqs;
	private Model e;
	private Model c;
	private boolean firstCheck;
	private List<GameModel> cmList;
	private TransitionParser tp;
	private String targetError="";
	private int[] checkerForMRP;
	private String sep=File.separator;


	public DirectoryTrackerForSingleWinningRegion(String directory) {
		this.directory=new File(directory);
		if(this.directory.exists()){
			candidate=new ArrayList<String>(Arrays.asList(this.directory.list()));
			candidate.remove("Controller");
			cas=new ArrayList<String>();
			cmList=new ArrayList<GameModel>();
		}
		firstCheck=false;
	}
	public boolean setDirectory(String dir){
		directory=new File(dir);
		if(directory.exists()){
			candidate=new ArrayList<String>(Arrays.asList(directory.list()));
			candidate.remove("Controller");
			cas=new ArrayList<String>();
			return true;
		}
		return false;
	}



	private File searchEnvInDirectory(){
		return new File(directory.getAbsolutePath()+sep+"Controller"+sep+"ENV.txt");
	}

	private void setControllableAction(){

		File caFile=new File(directory.getAbsolutePath()+sep+"Controller"+sep+"CA.txt");

		try {
			BufferedReader reader =new BufferedReader(new FileReader(caFile));
			String c;
			while((c=reader.readLine())!=null){
				//System.out.println("ControllablAction:"+c);
				cas.add(c);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ConcurrentSystemModelMaker.setControllableAction(cas);

	}
	private String setDegradationTargets(){
		File caFile=new File(directory.getPath()+sep+"Controller"+sep+"DegradationTargets.txt");
		try {
			BufferedReader reader =new BufferedReader(new FileReader(caFile));
			String c;
			checkerForMRP=new int[(candidate.size()/32)+1];
			for(int i=0;i<checkerForMRP.length;i++)
				checkerForMRP[i]=0;
			while((c=reader.readLine())!=null){
				int i=candidate.indexOf(c);
				if(i!=-1){
					if(targetError=="")
						targetError="ERROR"+i;
					else
						targetError=targetError+"&&ERROR"+i;
					checkerForMRP[i/32]+=(1<<(i%32));
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(targetError);
		return targetError;
	}

	public List<GameModel> getCModelFromDirectory(){
		File env;
		reqs=new ArrayList<Model[]>();
		if((env=searchEnvInDirectory())==null){
			return null;
		}
		setControllableAction();
		FSP_FileReader reader;
		reader=new FSP_FileReader(env);
		e=reader.getModel();

		reqs=getReqs();

		targetError=setDegradationTargets();

		for(int i=0;i<reqs.size();i++){
			cmList.add(ConcurrentSystemModelMaker.makeConcurrentSystem(e, reqs.get(i),cas));
		}
		return cmList;
	}


	private List<Model[]> getReqs() {
		FSP_FileReader reader;
		Map<String,Model> originalReqs=new HashMap<String,Model>();
		File reqsFile=new File(directory.getPath()+sep+"Controller"+sep+"req_levels.txt");
		String c;
		List<Model[]> reqs =new ArrayList<Model[]>();

		for(int i=0;i<candidate.size();i++){
			if (new File(directory.getPath()+sep+candidate.get(i)).isFile()){
				reader=new FSP_FileReader(directory.getPath()+sep+candidate.get(i));
//				System.out.println(candidate.get(i));
				originalReqs.put(candidate.get(i),reader.getModel());
			}
		}
		if(reqsFile.isFile()){
			try {
				BufferedReader bReader=new BufferedReader(new FileReader(reqsFile));
				while((c=bReader.readLine())!=null){
					String[] reqStrings=c.split(",");
//					System.out.println("DirectoryTracker_getReqs:"+c);
					Model[] reqMoni=new Model[reqStrings.length];
					for(int i=0;i<reqStrings.length;i++){
						reqMoni[i]=originalReqs.get(reqStrings[i]+".txt");
					}
					reqs.add(reqMoni);
				}
				bReader.close();

			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}

		}
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		if(reqs==null||reqs.isEmpty())System.out.println("no reqirement level file");
		return reqs;
	}
	public int checkSimulate(String controller){//全体の流れ
		if(cmList==null||cmList.isEmpty()){
			getCModelFromDirectory();//EnvとReqMoniからGameを作成
		}
		FSP_FileReader reader=new FSP_FileReader(directory.getPath()+sep+"Controller"+sep+controller);//simulate用のController
		c=reader.getModel();
		tp=new TransitionParser(cmList);//コンストラクタ引数List<GameModel>
		firstCheck=true;
		return tp.checkSimulate(c);

	}
	private boolean modelUpdate(String updatedPart){
		if(!firstCheck){
			System.out.println("ERROR: First check have not been done yet");
			return false;
		}
		String[] materials=updatedPart.split(",");
		if(materials.length!=3){
			System.out.println("ERROR:Input type is wong");
			return false;
		}
		for(int i=0;i<cmList.size();i++){
			//System.out.println("level "+i+" model updating with "+materials[1]);
			cmList.set(i, ConcurrentSystemModelMaker.modelUpdate(cmList.get(i), e,  reqs.get(i), materials[0],materials[1], materials[2]));
		}
		return true;
	}


	public int checkUpdated(String updatedPart){
		if(!modelUpdate(updatedPart))return -1;
		if(tp==null)tp=new TransitionParser(cmList,c);
		//tp.setCMList(cmList);
		//tp.setController(c);
		//new
		/*if(tp==null) {
			tp = new TransitionParser(cmList);
			tp.setController(c);
		}*/
		return tp.checkUpdatedSimulate();
	}

	public int checkUpdateFromFile(String fileName){
		//tp=new TransitionParser(cmList);
		BufferedReader reader=null;
		int resultLevel=-1;
		try {
			reader=new BufferedReader(new FileReader(new File(directory.getPath()+sep+"Controller"+sep+fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return resultLevel;
		}
		try {
			String temp;
			while((temp=reader.readLine())!=null){
				resultLevel=checkUpdated(temp);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultLevel;
	}

	/*private String convertResults(int[] result){
		String resultString="";
		for(int i=0;i<result.length;i++){
			int p=0;
			for(int j=result[i];j>0;j/=2){
				if(j%2==1){
					if(resultString=="")
						resultString=candidate.get((i*32)+p);
					else
						resultString+="&&"+candidate.get((i*32)+p);

				}
				p++;
			}
		}
		return resultString;
	}*/

	//Extracting model's difference between 2 files.
	public void extractDifference(File before,File after){
		Model 	m1=new FSP_FileReader(before).getModel(),
				m2=new FSP_FileReader(after).getModel();
		HashMap<String,State> stateMap=new HashMap<String,State>();
		String t=null;
		List<String> l=new ArrayList<String>();
		BufferedWriter f=null;
		if(m1.getSize()!=m2.getSize()){
			System.out.println("ERROR:These models are not similar");
			return;
		}
		makeStateMapWithoutR(stateMap,m1.getInitialState(),m2.getInitialState());

		for(int i=0;i<m2.getSize();i++){
			State s2=m2.getState(i);
			State s1=stateMap.get(s2.toString());
			System.out.println("standard"+s2+","+s1);
			for(int j=0;j<s2.getToTransitionNum();j++){
				t=s2.getToTransition(j).toString();
				if(s1.getToTransition(t)==null){
					System.out.println(s2.getToStateByTransition(t)+","+stateMap.get(s2.getToStateByTransition(t).toString()));
					l.add(stateMap.get(s2.toString())+","+t+","+stateMap.get(s2.getToStateByTransition(t).toString()).toString());
				}
			}
		}//*/
		if(l.size()==0){
			System.out.println("ERROR:There is no difference");
		}
		try {
			 f=new BufferedWriter(new FileWriter(new File(directory.getPath()+sep+"Controller"+sep+t+".txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			f.write(l.get(0));
			for(int i=1;i<l.size();i++){
				f.newLine();
				f.write(l.get(i));
			}
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//used for extractDifference()
	private void makeStateMapWithoutR(HashMap<String,State>stateMap,State s1,State s2){
		Stack<State> stack=new Stack<State>();
		State c2=s2;
		stateMap.put(s2.toString(), s1);
		stack.push(c2);
		while(!stack.isEmpty()){
			c2=stack.pop();
			s1=stateMap.get(c2.toString());
			while(c2.hasNext()){
				Transition t2 =(Transition)c2.next(),t1;
				if((t1=s1.getToTransition(t2.toString()))!=null)
					if(stateMap.get(t2.getTo().toString())==null){
						if(t1==t2)System.out.println(t1.getTo()+","+t2.getTo());
						stateMap.put(t2.getTo().toString(),t1.getTo());
						stack.push(t2.getTo());
					}
			}
		}
		System.out.println("model size:"+stateMap.size());
	}//*/
	public String toString() {
		return "Directory's path:"+this.directory.getAbsolutePath();
	}

	//new
	/*public int checkSimulate(String controller){//全体の流れ
		if(cmList==null||cmList.isEmpty()){
			getCModelFromDirectory();//EnvとReqMoniからGameを作成
		}
		FSP_FileReader reader=new FSP_FileReader(directory.getPath()+sep+"Controller"+sep+controller);//simulate用のController
		c=reader.getModel();
		tp=new TransitionParser(cmList);//コンストラクタ引数List<GameModel>
		firstCheck=true;
		return tp.checkSimulate(c);

	}*/

	/*
	public int checkSimulate(String controller){//全体の流れ
		if(cmList==null||cmList.isEmpty()){
			getCModelFromDirectory();//EnvとReqMoniからGameを作成
		}
		FSP_FileReader reader=new FSP_FileReader(directory.getPath()+sep+"Controller"+sep+controller);//simulate用のController
		c=reader.getModel();
		tp=new TransitionParser(cmList);//コンストラクタ引数List<GameModel>
		firstCheck=true;
		return tp.checkSimulate(c);

	}
	*/
	public int[][] checkDesignTimeSynthesis() {
		if(cmList==null||cmList.isEmpty())getCModelFromDirectory();
		//FSP_FileReader reader=new FSP_FileReader(directory.getPath()+sep+"Controller"+sep+controller);//simulate用のController
		tp = new TransitionParser(cmList);
		firstCheck=true;
		return tp.checkDesignTimeSynthesis();
	}

	/*public int[][] checkDifferentialControllerSynthesis() {
		if(cmList==null||cmList.isEmpty())getCModelFromDirectory();
		//FSP_FileReader reader=new FSP_FileReader(directory.getPath()+sep+"Controller"+sep+controller);//simulate用のController
		tp = new TransitionParser(cmList);
		return tp.checkDifferentialControllerSynthesis();
	}*/

	/*
	public int checkUpdateFromFile(String fileName){
		//tp=new TransitionParser(cmList);
		BufferedReader reader=null;
		int resultLevel=-1;
		try {
			reader=new BufferedReader(new FileReader(new File(directory.getPath()+sep+"Controller"+sep+fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return resultLevel;
		}
		try {
			String temp;
			while((temp=reader.readLine())!=null){
				resultLevel=checkUpdated(temp);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultLevel;
	}
	*/
	public int checkUpdateControllerFromFile(String fileName){
		//tp=new TransitionParser(cmList);
		BufferedReader reader=null;
		int nowlevel = -1;
		try {
			reader=new BufferedReader(new FileReader(new File(directory.getPath()+sep+"Controller"+sep+fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return nowlevel;
		}
		try {
			String temp;
			while((temp=reader.readLine())!=null){
				nowlevel = checkUpdatedController(temp);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nowlevel;
	}

	/*
	public int checkUpdated(String updatedPart){
		if(!modelUpdate(updatedPart))return -1;
		if(tp==null)tp=new TransitionParser(cmList,c);
		return tp.checkUpdatedSimulate();
	}
	*/
	public int checkUpdatedController(String updatedPart){
		if(!modelUpdate(updatedPart))return -1;
		tp.setCMList(cmList);
		return tp.checkDifferentialControllerSynthesis();
		//return tp.checkUpdateControllerSynthesis();
	}
}
