package testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import multiGR.model.Model;
import multiGR.model.State;
import multiGR.model.Transition;


//各モデルのテキストファイルを読み込むためのファイル
public class FSP_FileReader {
	BufferedReader reader;
	String rawData;
	String crlf=System.getProperty("line.separator");
	File f;

	public static void main(String[]args){
		FSP_FileReader fsp=new FSP_FileReader();
		String s ="[1].{arriveInduction{[0],[1]}, {arrivePicking, arriveReplenish}[0], arriveShipping{[0],[1]}} -> ERROR    |{[1].arrive{[0],[1]}{[0],[1]}, reset} -> Q0";
		String[] ss=s.split("\\|");

		List<TransitionTarget>ttList=fsp.makeTransitions("Q0",ss);
		for(TransitionTarget tt:ttList)System.out.println(tt);
	}

	private FSP_FileReader(){}

	public FSP_FileReader(String s) {
		try {
			System.out.println("generate FSPReader of "+s);
			reader=new BufferedReader(new FileReader(s));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public FSP_FileReader(File f){
		try{
			reader=new BufferedReader(new FileReader(f));
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	void setReader(String s){
		try {
			reader=new BufferedReader(new FileReader(s));
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	void readModelFile(){
		String buf ;
		rawData="";
		List<String> blocks=new ArrayList<String>();
		try {
			//long start =System.currentTimeMillis();
			int i=0,j=0;
			while((buf=reader.readLine())!=null){
				rawData+=buf.replace('	', ' ');
				if(i>999){
					j++;
					System.out.println("current is:" +j*1000);
					i=0;
					if(j%10==0){
						System.out.println("new String");
						blocks.add(rawData);
						rawData="";
					}
				}
				i++;//*/
			}
			if(!rawData.equals(""))blocks.add(rawData);
			rawData="";
			for(int k=0;k<blocks.size();k++){
				rawData+=blocks.get(k);
			}
//			System.out.println(rawData);
//			System.out.println("Size of blocks:"+blocks.size());
			long stop=System.currentTimeMillis();
//			System.out.println("readModelFile:"+(stop-start));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public Model getModel(){
		this.readModelFile();
		List<TransitionMaterials> tmList=new ArrayList<TransitionMaterials>();
		if(rawData.contains("STOP,")){
//			System.out.println("STOP");
			rawData=rawData.replaceAll("STOP,", "STOP\\),");
		}//*/
		String[] transitionDatas=rawData.split("Q0,")[1].split("\\),");
//		System.out.println(transitionDatas.length);
		if(transitionDatas[transitionDatas.length-1].indexOf(").")!=-1){
			transitionDatas[transitionDatas.length-1]=transitionDatas[transitionDatas.length-1].substring(0, transitionDatas[transitionDatas.length-1].indexOf(")."));
		}else{
//			System.out.println("ERROR");
			transitionDatas[transitionDatas.length-1]=transitionDatas[transitionDatas.length-1].substring(0, transitionDatas[transitionDatas.length-1].length()-1);

		}
		if(transitionDatas!=null)
		for(int i=0;i<transitionDatas.length;i++){
			tmList.add(processLine(transitionDatas[i]));
		}
		return makeStates(tmList);
/*		for(int j=0;j<tmList.size();j++){
			TransitionMaterials tm=tmList.get(j);
			System.out.println(tm.getSource());
			List<TransitionTarget> ttList=tm.getTTList();
			for(int i=0;i<ttList.size();i++){
				System.out.println("  "+ttList.get(i).getTransition()+"->"+ttList.get(i).getTarget());
			}
		}//*/

	}

	Model makeStates(List<TransitionMaterials> tmList){
		State[] stateMaterials=new State[tmList.size()+1];
		HashMap<String,State> stateMap=new HashMap<String,State>();
		stateMaterials[tmList.size()]=new State("ERROR");
		stateMaterials[tmList.size()].setIsDead();
		stateMap.put("ERROR", stateMaterials[tmList.size()]);
		for(int i=0;i<tmList.size();i++){
			TransitionTarget tt=tmList.get(i).getTT(0);
			if(tt.getTransition().equals("")&&tt.getTarget().equals("ERROR")){
				stateMaterials[i]=stateMaterials[tmList.size()];
//				System.out.println(tmList.get(i).getSource()+":isError");
				stateMap.put(tmList.get(i).getSource(), stateMaterials[tmList.size()]);
			}else{
				String s=tmList.get(i).getSource();
				stateMaterials[i]=new State(s);
				stateMap.put(s, stateMaterials[i]);
			}
		}
		for(int i=0;i<tmList.size();i++){
			TransitionMaterials tm=tmList.get(i);
			for(int j=0;j<tm.size();j++){
				TransitionTarget tt=tm.getTT(j);
				if(!tt.getTransition().equals("")){
					Transition t=new Transition(tt.getTransition(),stateMaterials[i],stateMap.get(tt.getTarget()));
					stateMaterials[i].addToTransition(t);
//					System.out.println(stateMap.get(tt.getTarget())+" "+tt.getTarget());//debug
					if(stateMap.get(tt.getTarget())!=stateMaterials[tmList.size()])stateMap.get(tt.getTarget()).addFromTransition(t);
				}
			}
		}
		return new Model(stateMaterials,stateMap);
	}

	TransitionMaterials processLine(String s){
		while(s.contains("[")&&s.contains("..")&&s.contains("]")){
			s=changeString(s);
//			System.out.println("processLine:"+s);
		}
		String[] materials=s.split("\\=");
		TransitionMaterials tm=new TransitionMaterials(materials[0].trim());
		String remain=materials[1].trim();
		remain=remain.substring(remain.indexOf("(")+1);

		if(remain.indexOf("|")!=-1){
			materials=remain.split("\\|");
			tm.addTransitionTarget(makeTransitions(tm.getSource(),materials));
		}else{
			String[] material={remain.trim()};
			tm.addTransitionTarget(makeTransitions(tm.getSource(),material));
		}
		return tm;
	}

	List<TransitionTarget> makeTransitions(String source,String[] materials){
		List<TransitionTarget> ttList=new ArrayList<TransitionTarget>();
		for(int i=0;i<materials.length;i++){
			String remain =materials[i];
//			System.out.println("materials.length:"+materials.length+remain);//debug
			if(remain.contains("->")){
				String target=remain.split("->")[1].trim();
				remain=remain.split("->")[0].trim();
//				System.out.println("Remain:"+remain);//debug
				List<String> list=makeActions(new Process(remain));
				for(int j=0;j<list.size();j++)ttList.add(new TransitionTarget(list.get(j).trim(),target));
			}else if(remain.contains("STOP")){
				ttList.add(new TransitionTarget("","ERROR"));
			}
		}
		return ttList;
	}

	List<String> makeActions(Process p){
		List<String> m=makeB(p);
//		System.out.println("debug Print:"+p.i+"=\""+p.s.charAt(p.i)+"\"");
		while(p.i<p.s.length()&&(p.s.charAt(p.i)==' '||p.s.charAt(p.i)=='	'))p.increment();
		while(p.i<p.s.length()&&p.s.charAt(p.i)==','){
			char op=p.s.charAt(p.i);
			p.increment();
			List<String> m2=makeB(p);
			if(op==','){
				m=sum(m,m2);
			}else{
				System.out.println("Error!!");
			}
		}
		return m;
	}
	List<String> makeB(Process p){
		List<String> m=material(p);
		while(p.i<p.s.length()&&(p.s.charAt(p.i)==' '||p.s.charAt(p.i)=='	'))p.increment();
		while(p.i<p.s.length()&&(p.s.charAt(p.i)=='.'||p.s.charAt(p.i)=='{'||p.s.charAt(p.i)=='[')){
			char op=p.s.charAt(p.i);

			if(p.s.charAt(p.i)=='.')p.increment();
			List<String> m2=material(p);
			if(op=='.'){
				m=concat(m,m2,".");
			}else if(op=='{'||op=='['){
				m=concat(m,m2,"");
			}
		}
		return m;
	}

	List<String>material(Process p){
		List<String> m;
		while(p.i<p.s.length()&&(p.s.charAt(p.i)==' '||p.s.charAt(p.i)=='	'))p.increment();
		if(p.s.charAt(p.i)=='{'){
			p.increment();
			m=makeActions(p);
			p.increment();
		}else{
			m=makeString(p);
		}
		return m;
	}
	List<String>makeString(Process p){
		List<String> m=new ArrayList<String>();
		int start=p.i;
		while(p.i<p.s.length()&&p.s.charAt(p.i)!=','&&p.s.charAt(p.i)!='.'&&p.s.charAt(p.i)!='}'&&p.s.charAt(p.i)!='{')p.increment();
		m.add(p.s.substring(start,p.i));
		return m;
	}

	List<String> concat(List<String>m1,List<String> m2,String concatStr){
		List<String> m=new ArrayList<String>();
		for(int i=0;i<m1.size();i++)
			for(int j=0;j<m2.size();j++)
				m.add(m1.get(i).trim()+concatStr+m2.get(j).trim());
		return m;
	}
	List<String> sum(List<String>m1,List<String>m2){
		m1.addAll(m2);
		return m1;
	}

	static String changeString(String s){
		String front="",back="",middle;
		String[] num;
		int from,to;
		while(s.contains("[")&&s.contains("..")&&s.contains("]")){
			if(s.indexOf("[")<s.indexOf("..")&&s.indexOf("..")<s.indexOf("]")){
				front=front.concat(s.substring(0,s.indexOf("[")));
				back=back.concat(s.substring(s.indexOf("]")+1));
				num=s.substring(s.indexOf("[")+1,s.indexOf("]")).split("\\.\\.");
				from=Integer.parseInt(num[0]);
				to=Integer.parseInt(num[1]);
				middle="{";
				for(int i=from;i<to;i++){
					middle+=("["+i+"]"+",");
				}
				middle+=("["+to+"]"+"}");
				return front+middle+back;
			}else{
				front=front.concat(s.substring(0,s.indexOf("]")+1));
				s=s.substring(s.indexOf("]")+1);
			}
		}
		return s;
	}
	class Process{
		int i;
		String s;
		Process(String s){
			this.i=0;
			this.s=s;
		}
		void increment(){
			//if(i<s.length()-1)
				i++;
		}
		int getI(){
			return new Integer(i);
		}
	}
	class TransitionMaterials{
		String source;
		List<TransitionTarget> setList;

		TransitionMaterials(String s){
			this.source=s;
			setList= new ArrayList<TransitionTarget>();
		}
		void addTransitionTarget(String transition,String target){
			setList.add(new TransitionTarget(transition,target));
		}
		void addTransitionTarget(List<TransitionTarget> ttList){
			setList.addAll(ttList);
		}
		List<TransitionTarget> getTTList(){
			return setList;
		}
		String getSource(){
			return source;
		}
		int size(){
			return setList.size();
		}
		TransitionTarget getTT(int i){
			return setList.get(i);
		}
	}
	class TransitionTarget{
		String transition;
		String target;
		TransitionTarget(String transition,String target){
			this.transition=transition;
			this.target=target;
		}
		String getTransition(){
			return this.transition;
		}
		String getTarget(){
			return this.target;
		}
		public String toString(){
			return "transition:"+transition+"->"+target;

		}

	}

}
