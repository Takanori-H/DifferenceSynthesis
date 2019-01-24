package testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import multiGR.model.ModelInterface;
import multiGR.model.State;
import multiGR.model.Transition;
import multiGR.multiConcurrentModel.MultiConcurrentModel;
import multiGR.multiConcurrentModel.MultiConcurrentState;

public class DisplayModels {
	private DisplayModels 		dm=new DisplayModels();

	private DisplayModels(){
	}
	public static void displayModelWithT(ModelInterface mcm){
		for(int i=0;i<mcm.getSize();i++){
			State s=mcm.getState(i);
			System.out.println("State:"+s+"::Transition:"+s.getToTransitionNum());
		}
	}
	public static void displayStatesRate(MultiConcurrentModel mcm){
		List<String> list=new ArrayList<String>();
		HashMap<String,Integer> map=new HashMap<String,Integer>();
		for(int i=0;i<mcm.getSize();i++){
			MultiConcurrentState mcs=mcm.getState(i);
			if(list.contains(mcs.getEnv())){
				int c=map.get(mcs.getEnv())+1;
				map.put(mcs.getEnv(),c);
			}else{
				map.put(mcs.getEnv(), 1);
				list.add(mcs.getEnv());
			}
		}
		for(int i=0;i<list.size();i++)System.out.println(list.get(i)+":"+map.get(list.get(i))+"("+((float)map.get(list.get(i))/mcm.getSize()*100)+")");
	}
	public static void displayModel(ModelInterface mcm){
		int num=0;
		for(int i=mcm.getSize()-1;i>=0;i--){
			State mcs=mcm.getState(i);
			System.out.println(mcs+":"+mcs.isDead()+mcs.isController());
			num+=mcs.getToTransitionNum();
			System.out.println(" From:");
			for(int j=0;j<mcs.getFromTransitionNum();j++){
				System.out.println("  "+mcs.getFromTransition(j));

			}//*/
			System.out.println(" To:");
			for(int j=0;j<mcs.getToTransitionNum();j++){
				System.out.println("  "+mcs.getToTransition(j)+"->"+mcs.getToTransition(j).getTo());
			}//*/
		}
		System.out.println("Transition num:"+num);
	}

	public static void displayDeadStates(ModelInterface mi){
		int num=0,num2=0;
		for(int i=0;i<mi.getSize();i++){
			State mcs=mi.getState(i);
			if(mcs.isDead()){
				System.out.println(mcs+":"+mcs.isDead());
				num++;
			}
			if(mcs.isController()){
				num2++;
			}
		}
		System.out.println(num+"/"+num2+"/"+mi.getSize());
	}

	public static void displayErrorStates(MultiConcurrentModel mcm){
		for(int i=0;i<mcm.getErrorSize();i++){
			State e=mcm.getErrorState(i);
			System.out.println(e);

			for(int j=0;j<e.getFromTransitionNum();j++){
				System.out.println("  "+e.getFromTransition(j)+"<-"+e.getFromTransition(j).getFrom());

			}
		}
	}
	public static void displayWhiteStates(MultiConcurrentModel mcm){
		for(int i=0;i<mcm.getSize();i++){
			State e=mcm.getState(i);
			if(!e.isController()&&!e.isDead()){
				System.out.println(e);
				for(int j=0;j<e.getToTransitionNum();j++){
					System.out.println("  "+e.getToTransition(j)+"->"+e.getToTransition(j).getTo()+e.getToTransition(j).getTo().isDead());
				}
			}
		}
	}
	public static void displayWhiteStatesC(MultiConcurrentModel mcm){
		for(int i=0;i<mcm.getSize();i++){
			State e=mcm.getState(i);
			if(!e.isController()&&!e.isDead()){
				boolean first=true;
				State s=e;
				while(e.hasNext()){
					Transition t=(Transition)e.next();
					if(!t.getTo().isDead()&&!t.getTo().isController()){
						if(first){
							System.out.println(e);
							first=false;
						}
						e=t.getTo();
					}
//					System.out.println("  :"+t.getFrom()+","+t+","+t.getTo()+t.getTo().isDead);
				}
				Transition t2=null;
				while(s!=null){
					State temp=s;
					for(int j=0;j<s.getFromTransitionNum();j++){
						t2=(Transition)s.getFromTransition(j);
						if(!t2.getFrom().isDead()&&!t2.getFrom().isController()){
							s=t2.getFrom();
//							System.out.println(" :"+t2.getTo()+","+t2+","+t2.getFrom()+t2.getFrom().isDead);
							break;
						}
						else
						System.out.println("  :"+t2.getTo()+","+t2+","+t2.getFrom()+t2.getFrom().isDead());
					}
					if(temp==s)s=null;
				}
//				if(t2!=null)System.out.println("   :"+t2.getTo()+","+t2+","+t2.getFrom()+t2.getFrom().isDead);
			}
		}
	}

	public static void displayStopStates(MultiConcurrentModel mcm){
		int num=0;
		for(int i=0;i<mcm.getSize();i++){
			State s=mcm.getState(i);
			for(int j=0;j<s.getToTransitionNum();j++){
				Transition t=s.getToTransition(j);
				if(t.getTo().toString().equals("STOP")){
					System.out.println(s+":"+t+"->"+t.getTo());
					num++;
				}
			}
		}
		System.out.println("AllTransitionsToSTOP:"+num);
	}

	public static void displayControllerState(MultiConcurrentModel mcm){
		int num=0;
		for(int i=0;i<mcm.getSize();i++){
			State s=mcm.getState(i);
			if(!s.isDead()){
				System.out.println(s);
				num++;
			}
		}
		System.out.println("Controller Size:"+num);
	}

	public static void displayT(ModelInterface mcm){
		int num=0;
		for(int i=0;i<mcm.getSize();i++){
			State mcs=mcm.getState(i);
			num+=mcs.getToTransitionNum();
		}//*/
		System.out.println("Transition num:"+num);
		System.out.println("Model size :"+mcm.getSize());
	}



}
