package multiGR.errorReachableAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import multiGR.model.Model;
import multiGR.model.ModelInterface;
import multiGR.model.State;
import multiGR.model.Transition;
import multiGR.multiConcurrentModel.MultiConcurrentModel;
import multiGR.multiConcurrentModel.MultiConcurrentState;
import multiGR.multiConcurrentModel.MultiConcurrentTransition;

//�����f���Ɨv�����j�^�̃Z�b�g���烂�f���̕��񍇐����s���N���X�i�J�ڂ��ǉ����ꂽ���̕��񍇐����f���̍X�V���s���j
public class MultiConcurrentSystemModelMaker {
	static List<String> controllableAction=new ArrayList<String>();
	static int updateNumber=0;
	static List<Model> requirements;
	static ModelInterface environment;
	static String targetError;
	private static boolean isUpdate;
//	static List<MultiConcurrentTransition> errorTransitions;
	//���񍇐��J�n���\�b�h�F�����f���Ɨv�����j�^�̃Z�b�g,�R���g���[���u���A�N�V�����������Ɏ��
	private MultiConcurrentSystemModelMaker(){

	}

	public static MultiConcurrentModel makeConcurrentSystem(ModelInterface env,List<Model> req,List<String>cActions,String errorString){
		targetError=errorString;
		return makeConcurrentSystem(env,req,cActions);
	}
	public static MultiConcurrentModel makeConcurrentSystem(ModelInterface env,List<Model> req,List<String>cActions){
		isUpdate=false;
		environment=env;
		requirements=req;
		controllableAction=cActions;
		List<State> initReq=new ArrayList<State>();
		for(int i=0;i<req.size();i++){
			initReq.add(req.get(i).getInitialState());
		}
		MultiConcurrentModel mcm=new MultiConcurrentModel(new MultiConcurrentState(env.getInitialState(),initReq));

		return compose(new Current(env.getInitialState(),initReq,mcm.getConcurrentState(env.getInitialState(), initReq)),mcm,null);
	}


	//�����f����ŏ�ԑJ�ڂ��J��Ԃ��Ȃ�����񍇐����f���̏�Ԃ𐶐����Ă������\�b�h
	//���ۂ̏�Ԑ�����addConcurrentState�ōs��
	private static MultiConcurrentModel compose(Current c,MultiConcurrentModel mcm,List<Transition> updatePart){
		Stack<Current> currentStack=new Stack<Current>();
		Current cu;
		currentStack.push(c);
		while(!currentStack.isEmpty()){
			cu=currentStack.pop();
//			if(mcm.getSize()%1000==0)System.out.println("compose model size:"+mcm.getSize()+"error size:"+mcm.getErrorSize());//debug
			if(cu.env.hasNext()){
				currentStack.push(cu);
				if((cu=addConcurrentState(cu,mcm,updatePart))!=null)currentStack.push(cu);
			}
		}
		return mcm;

	}
	//�^����ꂽ�����f���̏�ԂƗv�����j�^�̏�Ԃ̂�����񍇐����f���̏�Ԃ����ۂɐ������郁�\�b�h
	private static Current addConcurrentState(Current c,MultiConcurrentModel mcm,List<Transition>updatePart){
		Transition t=(Transition)c.env.next();
		MultiConcurrentState mcs;
		MultiConcurrentTransition newT;
		if(t.getTo().isDead()){
			List<Integer> stop=new ArrayList<Integer>();
			for(int i=0;i<(requirements.size()/32+1);i++)stop.add(0);
			newT= connection(c.mcs,t,mcm.getErrorState(stop),updatePart);
			return null;
		}else{
			List<State> newReqMoni=getNewReqMoni(c,t,mcm);
			if(newReqMoni==null){
				System.out.println("addConcurrentState connect to error or critical error");
				return null;
			}else if(containsError(newReqMoni)){
				//System.out.println("addConcurrentState Contains Error!!");	//debug
				return addErrorState(c,mcm,t,newReqMoni,updatePart);
			}else if(!mcm.existsConcurrentState(t.getTo(), newReqMoni)){
				mcs=new MultiConcurrentState(t.getTo(),newReqMoni);
				//System.out.println("add "+mcs+",size:"+mcm.getSize());//debug
				newT=connection(c.mcs, t, mcs,updatePart);
				if(isUpdate)updatePart.add(newT);
				mcm.addMultiConcurrentState(mcs);
				return new Current(t.getTo().getClone(),newReqMoni,mcs);
			}else{
				mcs=mcm.getConcurrentState(t.getTo(),newReqMoni);
				//System.out.println("addConcurrentState: exists"+c.mcs+"->"+t+"->"+mcs);//debug
				newT=connection(c.mcs,t,mcs,updatePart);
				return null;
			}
		}
	}
	//���񍇐����f���𐶐����邽�߂ɗ��p
	//�����f���̑J�ڂŗv�����j�^���őJ�ڂ��N���邩�𒲂ׁA�J�ڂ��N�����ꍇ�͑J�ڐ�̗v�����j�^�̏�Ԃ�Ԃ����\�b�h
	private static List<State> getNewReqMoni(Current c,Transition t,MultiConcurrentModel mcm){
		List<State> newReqMoni=new ArrayList<State>();
		for(int i=0;i<c.reqMoni.size();i++){
			State m=c.reqMoni.get(i);
			State nextM;

			if(m.containsToTransition(t.toString())){
				nextM=m.getToStateByTransition(t.toString());
//				System.out.println("getNewReqMoni containsToTransition is True@"+m+"->"+nextM); //debug
				newReqMoni.add(nextM);
			}else{
				newReqMoni.add(m);

			}
		}
		return newReqMoni;
	}
	private static boolean containsError(List<State> newReqMoni){
		int i=0;
		while(i<newReqMoni.size()){
			if(newReqMoni.get(i).toString().equals("ERROR"))return true;
			i++;
		}
		return false;
	}
	//�Q��MultiConcurrentState��Transition�̏�񂩂�q���邽�߂̃��\�b�h�BMultiConcurrentTransition���V���ɐ��������B
	public static MultiConcurrentTransition connection(MultiConcurrentState from,Transition t,MultiConcurrentState to, List<Transition> updatePart){
		MultiConcurrentTransition newT=new MultiConcurrentTransition(t.toString());
		if(controllableAction.contains(newT.toString())){
//			System.out.println("ControllableAction"+newT.toString());
			newT.setIsControllable();
		}else{
//			System.out.println("MonitorableAction"+newT.toString());
		}
		from.addToTransition(newT);
		newT.setFrom(from);
		newT.setTo(to);
		to.addFromTransition(newT);

//		System.out.println("connection: "+newT.getFrom()+"->"+newT+"->"+newT.getTo());	//debug

		if(isUpdate&&to.isDead())updatePart.add(newT);
		return newT;
	}
	private static Current addErrorState(Current c,MultiConcurrentModel mcm,Transition t,List<State> reqMoni,List<Transition> updatePart){
		List<Integer> errors=new ArrayList<Integer>();
		for(int i=0;i<(reqMoni.size()/32+1);i++){
			errors.add(0);
		}
		String e="";
		Stack<Integer> size=new Stack<Integer>();
		for(int i=0;i<reqMoni.size();i++){
			if(reqMoni.get(i).toString().equals("ERROR")){
				size.add(i);
				int error;
				int count=0;
				for(int j=i;j>=0;j-=32){
					error=1<<(j);
					errors.set(count,errors.get(count)|error);
//					if(errors.size()>1)
//					System.out.println("addErrorState "+errors+","+i);
					count++;
				}

				if(!(targetError!=null&&targetError.contains("ERROR"+i))){
					if(!e.equals(""))e=e.concat("&&");
					e=e.concat("ERROR"+i);
				}
			}
		}

		searchReqMoni(mcm,t.getTo(),reqMoni,size);
		MultiConcurrentTransition newT;
		if(e.equals("")){
//			System.out.println("addErrorState "+e+","+errors);
			if(mcm.existsConcurrentState(t.getTo(), reqMoni)){
				newT=connectionWithError(c.mcs,t,mcm.getConcurrentState(t.getTo(),reqMoni),errors,updatePart);
				mcm.addErrorTransition(newT);
				return null;
			}else{
				MultiConcurrentState mcs=new MultiConcurrentState(t.getTo(),reqMoni);
				newT=connectionWithError(c.mcs, t, mcs,errors,updatePart);
				mcm.addErrorTransition(newT);
				mcm.addMultiConcurrentState(mcs);
				return new Current(t.getTo().getClone(),reqMoni,mcs);
			}
		}else{
			newT=connectionWithError(c.mcs,t,mcm.getErrorState(errors),errors,updatePart);
			mcm.addErrorTransition(newT);
			return null;
		}
	}
	private static List<State> searchReqMoni(MultiConcurrentModel mcm,State env,List<State> reqMoni,Stack<Integer> stack){
		if(stack.size()>1){
			int p=stack.pop();
			for(int i=0;i<requirements.get(p).getSize();i++){
				reqMoni.set(p, requirements.get(p).getState(i));
				List<State> r=searchReqMoni(mcm,env,reqMoni,stack);
				if(r!=null){
/*					for(int j=0;j<reqMoni.size();j++)
						System.out.print(reqMoni.get(j));
					System.out.println(" is new ReqMoni");//*/
					return r;
				}
				reqMoni.set(p, requirements.get(p).getInitialState());
			}
		}else if(!stack.isEmpty()){
			int p=stack.pop();
			for(int i=1;i<requirements.get(p).getSize();i++){
				reqMoni.set(p, requirements.get(p).getState(i));
				if(mcm.existsConcurrentState(env, reqMoni))return reqMoni;
			}
			reqMoni.set(p, requirements.get(p).getInitialState());
		}
		return null;
	}

	private static MultiConcurrentTransition connectionWithError(MultiConcurrentState from,Transition t,MultiConcurrentState to,List<Integer> error,List<Transition>updatePart){
		MultiConcurrentTransition newT=new MultiConcurrentTransition(t.toString());
		newT.setIsDead(error);
		if(controllableAction.contains(newT.toString())){
//			System.out.println("Controllable Action"+newT.toString());
			newT.setIsControllable();
		}else{
//			System.out.println("Monitorable Action"+newT.toString());
		}
		from.addToTransition(newT);
		newT.setFrom(from);
		newT.setTo(to);
		to.addFromTransition(newT);
		if(isUpdate&&to.isDead())updatePart.add(newT);
		return newT;

	}
	//�����f�����X�V���邽�߂̃��\�b�h
	//�����f���ƒǉ������J�ڂƂ��̑J�ڂ��ǉ������ꏊ�������Ɏ��
	public static Model attachTransition(Model e,String from,String t,String to){
		Transition tr= new Transition(t,e.getState(from),e.getState(to));
		e.getState(from).addToTransition(tr);
		e.getState(to).addFromTransition(tr);
		return e;
	}
	//�ǉ����ꂽ�J�ڂɂ���ĕ��񍇐����f�����X�V���邽�߂̃��\�b�h
	//���񍇐����f���Ƃ��̌��ƂȂ��������f��,�v�����j�^,�X�ɒǉ����ꂽ�J�ڂɊւ�����������Ɏ��
	//���񍇐����f���̍X�V���ꂽ�����̓��A�v���̃`�F�b�N�ɗ��p���镔���͕��񍇐����f�����ɋL�����Ă���
	public static MultiConcurrentModel modelUpdate(MultiConcurrentModel cm,Model e,List<Model> moni,String from,String t,String to,ArrayList<Transition> updatePart){
		updateNumber++;
		isUpdate=true;
		//updatePart=new ArrayList<Transition>();
		e=attachTransition(e,from,t,to);

		Transition target=e.getState(from).getToTransition(t);
//		System.out.println("modelUpdate"+target.getFrom()+"->"+target.toString()+"->"+target.getTo());//debug
		List<MultiConcurrentState> lcs=getCandidate(target,cm);
		for(int i=0;i<lcs.size();i++){
			List<State> reqMoniStates=new ArrayList<State>();
			List<String> moniStatesNames=lcs.get(i).getReqMoniList();
			if(lcs.get(i).getReqMoniList().size()!=0){
				//System.out.println("modelUpdate basic");
				for(int j=0;j<moni.size();j++)reqMoniStates.add(moni.get(j).getState(moniStatesNames.get(j)));
				cm=compose(new Current(target.getFrom().getClone(),reqMoniStates,lcs.get(i)),cm,updatePart);
				if(lcs.get(i).getToTransition(t).getTo().getName().contains("ERROR")){

				}
				updatePart.add(lcs.get(i).getToTransition(t));
//				System.out.println("modelUpdate:list size "+updatePart.size());//debug
			}
		}
		cm.setUpdatedPart(updatePart);
		return cm;
	}

	//���񍇐����f���̏�Ԃ̓��A�����f���̍X�V�ɔ����đJ�ڂ����������Ԃ̃Z�b�g���擾���郁�\�b�h
	private static List<MultiConcurrentState> getCandidate(Transition tr,MultiConcurrentModel mcm){
		List<MultiConcurrentState> l=new ArrayList<MultiConcurrentState>();
		for(int i=0;i<mcm.getSize();i++){
			MultiConcurrentState cs=mcm.getState(i);
			//System.out.println("getCandidate"+i+":"+cs.getEnv()+","+tr.getFrom().toString()+":"+cs.getEnv().equals(tr.getFrom().toString()));//debug
			if(cs.getEnv().equals(tr.getFrom().toString())){
				l.add(cs);
			}
		}
		return l;
	}

	static Current retreiveStates(String stateString,MultiConcurrentState mcs){
		if(stateString.contains("ERROR")){
			System.out.println("ERROR is included in retrieveing process");
			return null;
		}
		String[] strings=stateString.split("Q");
		State e=environment.getState(Integer.parseInt(strings[1]));
		e=e.getClone();
		e.reset();
		List<State> reqs=new ArrayList<State>();
		for(int i=0;i<strings.length-2;i++){
			reqs.add(requirements.get(i).getState(Integer.parseInt(strings[i+2])));
		}
		return new Current(e,reqs,mcs);
	}
}

//���񍇐����s���ۂɕK�v�Ȋ����f���Ɨv���Z�b�g�̏�Ԃ��L�����Ă������߂̃N���X
class Current{
	State env;
	List<State> reqMoni;
	MultiConcurrentState mcs;
	Current(State env,List<State> initReq,MultiConcurrentState mcs){
		this.env=env;
		this.reqMoni=initReq;
		this.mcs=mcs;
	}
	State env(){
		return env;
	}
	List<State> reqMoni(){
		return reqMoni;
	}
}
