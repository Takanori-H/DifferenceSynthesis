package multiGR.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class State extends ModelMaterial implements Cloneable{//ModelMaterialの子クラス
	private HashMap<String,Transition> toTransition;//Stateから出てる次の状態へのTransition
	private List<String> toTransitionNameList;//Stateから出ているTransitionのアクションのList
	private List<Transition> fromTransitionList;//Stateに入ってくるTransition
	//private HashMap<String,Transition> fromTransition;
	//private List<String> fromTransitionNameList;
	private int toTransitionPointer;//あるStateから出ているTransitionのなかで幾つのTransitionを探索したか
	private HashMap<Integer,Boolean> simulate;
	//private HashMap<String,String> SimulateState;
	//private HashMap<Integer,HashMap<String,String>> Simulate;

	public State(String name){
		super(name);//ModelMaterialのコンストラクタに引数nameを渡してインスタンス化
		toTransition=new HashMap<String,Transition>();
		toTransitionNameList=new ArrayList<String>();
		fromTransitionList=new ArrayList<Transition>();
		//fromTransition = new HashMap<String,Transition>();
		//fromTransitionNameList=new ArrayList<String>();
		simulate = new HashMap<Integer,Boolean>();
		//SimulateState = new HashMap<String,String>();
		//Simulate = new HashMap<Integer,HashMap<String,String>>();
	}

	/*public void SimulateStateInit() {
		SimulateState = new HashMap<String,String>();
	}

	public void setSimulate(int level, State iState, State jState) {
		SimulateState.put(jState.getName(), iState.getName());
		Simulate.put(level, SimulateState);
	}

	public boolean containSimulateState(int level, State jState) {
		if(Simulate.isEmpty())return false;
		if(!Simulate.containsKey(level))return false;
		return Simulate.get(level).containsKey(jState.getName());
	}

	public boolean isSimulate(int level){
		return Simulate.containsKey(level);
	}*/
	public void setSimulate(int level) {
		simulate.put(level, true);
	}

	/*public boolean isSimulate(int level) {
		try {
			if(simulate.get(level) != null) {
				return true;
			}else {
				throw new NullPointerException();
			}
		}catch(NullPointerException e) {
			return false;
		}
	}*/
	public boolean isSimulate(int level) {
		return simulate.containsKey(level);
	}

	public void removeSimulate(int level) {
		if(simulate.containsKey(level)) {
			simulate.remove(level);
		}
	}

	public void clearSimulate() {
		simulate.clear();
	}

	public void addToTransition(Transition tr){
		if(!containsToTransition(tr.toString())){//あるStateから出るTransitionに引数として与えられたTransitionが含まれていなかったらToTransitionに加える
//			System.out.println("addToTransition "+this+"->"+tr+"->"+tr.getTo());	//debug
			toTransitionNameList.add(tr.toString());
			toTransition.put(tr.toString(), tr);//HashMapに要素格納
		}
	}
	public void addFromTransition(Transition tr){//あるState入るTransitionを加える
		fromTransitionList.add(tr);
		//fromTransitionNameList.add(tr.toString());
		//fromTransition.put(tr.toString(), tr);
	}

	public void eraseToTransition(String name) {
		toTransition.remove(name);
	}

	public void eraseToTransitionNameList(String name) {
		toTransitionNameList.remove(name);
	}

	public void erasefromTransition(Transition tr) {
		//fromTransition.remove(name);
		for(int i=0;i<fromTransitionList.size();i++) {
			if(tr==fromTransitionList.get(i)) {
				fromTransitionList.remove(i);
			}
		}
	}

	/*public void erasefromTransitionNameList(String name) {
		fromTransitionNameList.remove(name);
	}*/

	public boolean containsToTransition(String name){//あるStateから出るTransitionのなかにnameのアクションが含まれているかどうか
/*		System.out.println(" check "+name+":"+toTransitionNameList.contains(name));//debug
		for(String s:toTransitionNameList){
			System.out.println("  "+s+","+name+":"+s.equals(name));
		}//*/
		return this.toTransitionNameList.contains(name);//boolean
	}

	public boolean containsFromTransition(String name) {
		for(int i=0;i<fromTransitionList.size();i++) {
			Transition tr = fromTransitionList.get(i);
			if(tr.getName()==name)return true;
		}
		return false;
	}

	public State getToStateByTransition(String tr){//あるStateからTransitionによって遷移できる状態
		return this.toTransition.get(tr).getTo();//getToはTransitionのメソッド
	}
	Boolean hasToTransitions(){//あるStateから遷移できるかどうか つまりDead endかどうか 遷移できるならtrue 遷移できないならfalse
		return !toTransition.isEmpty();
	}

	public int getToTransitionNum(){//あるstateから幾つのTransitionが出ているか
		return this.toTransitionNameList.size();
	}
	public Transition getToTransition(int i){//引数int toTransitionNameListからname持ってきてHashMapのtoTransitionからTransitionをget
		return this.toTransition.get(this.toTransitionNameList.get(i));
	}
	public Transition getToTransition(String name){//引き数String NameListを経由せず直接HashMapからget
		return this.toTransition.get(name);
	}
	public int getFromTransitionNum(){//あるStateに幾つのTransitionが入ってくるかどうか
		return this.fromTransitionList.size();
		//return this.fromTransitionNameList.size();
	}

	public Transition getFromTransition(int i){//fromTransitionをget
		return this.fromTransitionList.get(i);
		//return this.fromTransition.get(this.fromTransitionNameList.get(i));
	}

	/*public Transition getfromTransition(String name){//引き数String NameListを経由せず直接HashMapからget
		return this.fromTransition.get(name);
	}*/
	@Override
	public boolean hasNext() {//あるStateで他に道があるかどうか 環境側のwinning regionに入らないような道を探す
		//ある状態で探索してないTransitionがまだあるかどうか
		return toTransitionPointer< toTransition.size();
	}
	@Override
	public ModelMaterial next() {//ある状態で探索し終わったTransitionの次のTransitionをget
		return getToTransition(toTransitionPointer++);
	}
	@Override
	public void reset() {//探索のリセット
		toTransitionPointer=0;
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}
	public State getClone(){//?
		State s=null;
		try {
			s =(State) this.clone();
			s.reset();
		} catch (CloneNotSupportedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return s;
	}
}
