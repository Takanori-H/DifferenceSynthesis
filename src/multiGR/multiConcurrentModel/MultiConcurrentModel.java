package multiGR.multiConcurrentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import multiGR.model.ModelInterface;
import multiGR.model.State;
import multiGR.model.Transition;
import multiGR.errorReachableAnalyzer.MultiRequirementParser;

//並列合成モデルを表したクラス
public class MultiConcurrentModel implements ModelInterface {
	String name;
	HashMap<String,MultiConcurrentState> states;
	List<String> statesName;
	HashMap<List<Integer>,MultiConcurrentState> errorStatesMap;
	List<List<Integer>> errorStates;
	MultiRequirementParser mpc;
	List<Transition> updatePart=new ArrayList<Transition>();
	MultiConcurrentState initialState;
	List<MultiConcurrentTransition> errorTransitions;

	public MultiConcurrentModel(MultiConcurrentState initState) {
		this.initialState=initState;
		this.setInitialState(initState);
		this.errorStatesMap=new HashMap<List<Integer>,MultiConcurrentState>();
		this.errorStates=new ArrayList<List<Integer>>();
		this.states=new HashMap<String,MultiConcurrentState>();
		this.statesName=new ArrayList<String>();
		this.addMultiConcurrentState(initState);
	}
	public void addMultiConcurrentState(MultiConcurrentState mcs){
		states.put(mcs.getName(), mcs);
		statesName.add(mcs.getName());
	}
	public MultiConcurrentState getConcurrentState(State env,List<State> reqMoni){
		String s=env.toString();
		for(int i=0;i<reqMoni.size();i++)s=s.concat(reqMoni.get(i).toString());
		return states.get(s);
	}
	public boolean existsConcurrentState(State env,List<State> reqMoni){
		String s=env.toString();
		for(int i=0;i<reqMoni.size();i++)
			s=s.concat(reqMoni.get(i).toString());
		return states.get(s)!=null;
	}
	public MultiConcurrentState getState(int i){
		return states.get(statesName.get(i));
	}
	@Override
	public int getSize() {
		return statesName.size();
	}

	public int getErrorSize(){
		return errorStates.size();
	}
	@Override
	public State getInitialState() {
		return states.get(statesName.get(0));
	}

	@Override
	public State getErrorState() {
		return this.errorStatesMap.get(errorStates.get(0));
	}
	public MultiConcurrentState getErrorState(List<Integer> s) {
		if(this.errorStatesMap.get(s)==null){
			MultiConcurrentState emcs=new MultiConcurrentState(s);
			emcs.setIsDead(s);
			this.errorStatesMap.put(s,emcs);
			this.errorStates.add(s);
		}
		return this.errorStatesMap.get(s);
	}
	public MultiConcurrentState getErrorState(int i){
		return this.errorStatesMap.get(errorStates.get(i));
	}

	public void setUpdatedPart(List<Transition> t){
		updatePart=t;
	}
	@Override
	public List<Transition> getUpdatedPart() {
		return updatePart;
	}
	public MultiConcurrentState getState(String s) {
		return states.get(s);
	}
	public String getName() {
		return name;
	}
	public void setInitialState(MultiConcurrentState s){
		this.initialState=s;
	}
	@Override
	public void setInitialState(State s) {
		this.initialState=(MultiConcurrentState)s;
	}
	public List<List<Integer>> getErrorList(){
		return errorStates;
	}
	public void addErrorTransition(MultiConcurrentTransition mct){
		if(errorTransitions==null)errorTransitions=new ArrayList<MultiConcurrentTransition>();
		errorTransitions.add(mct);
	}
	public Queue<MultiConcurrentTransition> getErrorTransition(){
		Queue<MultiConcurrentTransition> stack=new LinkedBlockingQueue<MultiConcurrentTransition>();
					for(int i=0;i<errorTransitions.size();i++){
			stack.add(errorTransitions.get(i));
		}
		return stack;
	}
}
