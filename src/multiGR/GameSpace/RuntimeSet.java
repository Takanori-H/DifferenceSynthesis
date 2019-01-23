//���ꂢ�܂͎g���Ă��Ȃ�

package multiGR.GameSpace;

import java.util.ArrayList;
import java.util.List;

import multiGR.model.Model;

public class RuntimeSet {
	private Model currentEnv;
	private Model currentMonitor;//Monitorとは
	private List<Model> monitorSet;
	private int level;
	private GameModel concurrentSystem;
	private List<String> updateRecord;
	private int controllerlevel;

	public RuntimeSet(Model currentEnv,Model currentMonitor){
		this.currentEnv=currentEnv;
		this.currentMonitor=currentMonitor;
//		this.concurrentSystem=ConcurrentSystemModelMaker.makeConccurrentSystem(currentEnv, currentMonitor);
		updateRecord=new ArrayList<String>();
	}
	public RuntimeSet(Model currentEnv,List<Model> monitorSet){
		this.currentEnv=currentEnv;
		this.monitorSet=monitorSet;
		this.currentMonitor=monitorSet.get(monitorSet.size()-1);
		this.level=monitorSet.size()-1;
//		this.concurrentSystem=ConcurrentSystemModelMaker.makeConccurrentSystem(currentEnv, currentMonitor);
		updateRecord=new ArrayList<String>();
	}
	public void degradation(){
		level--;
		this.currentMonitor=monitorSet.get(level);
	}
	public int getCurrentLevel(){
		return level;
	}


	void setCurrentEnv(Model currentEnv){
		this.currentEnv=currentEnv;
	}

	void setCurrentMonitor(Model currentMonitor){
		this.currentMonitor=currentMonitor;
	}
	void setConcurrentSystem(GameModel concurrentSystem){
		this.concurrentSystem=concurrentSystem;
	}
	void envUpdate(String from,String t,String to){
		this.currentEnv=ConcurrentSystemModelMaker.attachTransition(this.currentEnv, from, t, to);
	}
	public void concurrentSystemUpdate(String from,String t,String to){
//		this.concurrentSystem=ConcurrentSystemModelMaker.modelUpdate(this.concurrentSystem, this.currentEnv, this.currentMonitor, from, t, to);
//		this.envUpdate(from, t, to);
		this.updateRecord.add(from+","+t+","+to);
	}

	public void reCompose(){
//		this.concurrentSystem=ConcurrentSystemModelMaker.makeConccurrentSystem(this.currentEnv, this.currentMonitor);
	}

	public GameModel getConcurrentSystem(){
		return  this.concurrentSystem;
	}
	public Model getCurrentEnv(){
		return this.currentEnv;
	}
	public Model getCurrentMonitor(){
		return this.currentMonitor;
	}
}
