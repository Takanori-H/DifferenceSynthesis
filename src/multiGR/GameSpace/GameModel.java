package multiGR.GameSpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import multiGR.model.ModelInterface;
import multiGR.model.State;
import multiGR.model.Transition;

public class GameModel implements ModelInterface{
	GameState initialState;//初期状態
	String name;//識別子
	HashMap<String,GameState> states;
	List<String> statesName;//stateの識別子
	List<Transition> updatedPart=new ArrayList<Transition>();//差分 更新箇所

	GameModel(HashMap<String,GameState> states,List<Transition> updatePart) {
		this.name="concurrentModel";
		this.setStateMap(states);
		this.updatedPart=updatePart;
		Iterator<String> it=this.states.keySet().iterator();//イテレータ key取得
		this.statesName=new ArrayList<String>();
		//System.out.println("this is a test"+it.hasNext());
		while(it.hasNext()){//値がある場合はループ継続
			this.statesName.add(it.next());//mapのkey取得
		}
	}

	void setStateMap(HashMap<String,GameState> states){
		this.states=states;
		Iterator<String> it=this.states.keySet().iterator();//イテレータ key取得
		this.statesName=new ArrayList<String>();
		//System.out.println("this is a test"+it.hasNext());
		while(it.hasNext()){//値がある場合はループ継続
			this.statesName.add(it.next());//mapのkey取得
		}
//		System.out.println("setStatesMap"+this.statesName.size()+","+this.states.size());

	}
	public void setModelMaterials(HashMap<String,GameState> states,GameState initState){
		this.setInitialState(initState);
		this.setStateMap(states);
	}


	public GameModel() {//コンストラクタ
		states=new HashMap<String,GameState>();
		statesName=new ArrayList<String>();

	}
	public GameState getConcurrentState(int i){
		return states.get(statesName.get(i));
	}

	GameState getConcurrentState(String s){
		return states.get(s);
	}
	public State getInitialState(){
		return initialState;
	}

	public void setInitialState(State initState){
		this.initialState=(GameState)initState;
		this.statesName.add(initState.getName());
	}

	public int getSize(){
		return states.size();
	}
	String getName(int i){
		return this.statesName.get(i);
	}
	public State getState(int i){
//		System.out.println(statesName.size()+","+states.size()+","+i+":"+statesName.get(i));
		return states.get(statesName.get(i));
	}
	public State getErrorState(){
		return states.get("ERROR");
	}
	public List<Transition> getUpdatedPart(){
		return this.updatedPart;
	}

	public String getName(){
		return this.name;
	}

	public HashMap<String, GameState> getStates() {
		// TODO Auto-generated method stub
		return this.states;
	}
}
