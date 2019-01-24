package multiGR.multiConcurrentModel;

import java.util.List;

import multiGR.model.State;
import multiGR.model.Transition;

public class MultiConcurrentTransition extends Transition {
	MultiConcurrentState defaultState;
	List<Integer> dead;

	public MultiConcurrentTransition(String name, State from, State to) {
		super(name, from, to);
	}
	public MultiConcurrentTransition(String name) {
		super(name);
	}

	public void setIsDead(List<Integer> error){
		this.dead=error;
	}
	public List<Integer> getDead(){
		return dead;
	}
	@Override
	public boolean isDead(){
		if(dead==null)return false;
		for(int i=0;i<dead.size();i++){
			if(dead.get(i)>0)return true;
		}
		return false;
	}
}
