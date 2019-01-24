package multiGR.multiConcurrentModel;

import java.util.ArrayList;
import java.util.List;

import multiGR.model.State;


public class MultiConcurrentState extends State {
	String env;
	List<String> reqMoniList;
	List<List<Integer>> deadList;
	int updateNumber;
	public MultiConcurrentState(String name) {
		super(name);
		reqMoniList=new ArrayList<String>();
		deadList=new ArrayList<List<Integer>>();
	}

	public MultiConcurrentState(List<Integer> s){
		this("");
		String name="ERROR";
		for(int i=0;i<s.size();i++){
			if(i!=0)name=name+",";
			name=name+s.get(i);
		}
		this.name=name;
		this.deadList.add(s);
	}
	public MultiConcurrentState(State env,List<State> initReq){
		this(env.toString());
		for(int i=0;i<initReq.size();i++){
			String tmp=initReq.get(i).toString();
			this.reqMoniList.add(tmp);
			this.name=this.name.concat(tmp);
		}
		this.env=env.toString();
	}

	public void setIsDead(List<Integer> s){
		boolean setDead=true;
		for(int i=0;i<deadList.size();i++){
			boolean tmp=true,remove=true;
			for(int j=0;j<s.size();j++){
				if((deadList.get(i).get(j)|s.get(j))!=s.get(j)){
					tmp=false;
				}
				if((deadList.get(i).get(j)|s.get(j))!=deadList.get(i).get(j))
					remove=false;
			}
			if(tmp){
				setDead=false;break;
			}
			if(remove){
				System.out.println("remove");
				deadList.remove(i);
			}
		}
		if(setDead)deadList.add(s);
	}

	@Override
	public boolean isDead(){
			return !deadList.isEmpty();
	}

	public boolean isDead(int[] req){
			return deadList.contains(req);
	}
	void addReqMoni(String s){
		this.name.concat(s);
		this.reqMoniList.add(s);
	}
	public String getEnv() {
		return env;
	}
	public List<List<Integer>> getDeadList(){
		List<List<Integer>> clone =new ArrayList<List<Integer>>();
		for(int i=0;i<deadList.size();i++){
			clone.add(deadList.get(i));
		}
		return clone;
	}//*/
	public boolean replaceDeadList(List<List<Integer>> d){
		if(getName().contains("ERROR")){
			return false;
		}
		if(deadList.size()==d.size()){
			boolean allSame=true;
			for(int i=0;i<deadList.size();i++)
				for(int j=0;j<deadList.get(i).size();j++)
					if(!deadList.get(i).get(j).equals(d.get(i).get(j))){
						allSame=false;
						break;
					}
			if(allSame){
				return false;
			}else{
/*				System.out.println("replaceDeadList:"+this);
				System.out.println(" before:"+this.getDeadList());
				System.out.println(" after:"+d);//*/
				this.deadList=d;
				return true;
			}
		}else{
/*			System.out.println("replaceDeadList:"+this);
			System.out.println(" before:"+this.getDeadList());
			System.out.println(" after:"+d);//*/
			this.deadList=d;
			return true;
		}

	}

	public void clearDeaedList(){
		deadList.clear();
	}

	public List<String> getReqMoniList(){
		return reqMoniList;
	}
}
