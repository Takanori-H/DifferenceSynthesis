package multiGR.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//LTSの実装 EnvやReqMoni
public class Model implements ModelInterface{
	State[] states;//state型の配列
	State initialState,errorState;//初期状態とエラー状態
	String name;//識別子
	HashMap<String,State> map;
	public Model(State[] states,HashMap<String,State> map){
		this.states=states;
		this.map=map;
		this.setInitialState(states[0]);//state配列の0番目の要素が初期状態
		this.setErrorState(states[states.length-1]);//ErrorStateはstateの最後の配列に格納されてる？
	}
	Model(String name,State[] states,HashMap<String,State> map){
		this.name=name;
		this.states=states;
		this.map=map;
		this.setInitialState(states[0]);
		this.setErrorState(states[states.length-1]);
	}
	public State getState(int i){
		return states[i];//stateの配列の方からget
	}

	public State getState(String i){
		return map.get(i);//HashMapの方からget
	}
	public int getSize(){
		return states.length;//stateの配列の長さ state数
	}
	public State getInitialState(){
		return this.initialState;//初期状態返す
	}
	public void setInitialState(State s){
		this.initialState=s;//初期状態セット
	}
	public State getErrorState(){//エラー状態のget
		return this.errorState;
	}
	public void setErrorState(State s){//エラー状態のセット
		this.errorState=s;
	}

	public List<Transition> getUpdatedPart(){//Transitionのupdateしか考えていない？
		return new ArrayList<Transition>();//Transition型の要素をもつリスト
	}
	public String getName() {
		return name;
	}

}
