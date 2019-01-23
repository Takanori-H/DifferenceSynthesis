package multiGR.model;

import java.util.HashMap;
import java.util.List;

public class Transition extends ModelMaterial{
	private State from;//Transitionが出てきたState
	private State to;//Transitionが入るState
	private boolean isControllable,used;//TransitionがControllableかUncontrollableか Transitionが使われたかどうか
	private HashMap<String,String> errorStringMap;//?
	public Transition(String name,State from,State to){
		super(name);//MofdelMaterialのコンストラクタに引数nameを渡す
		this.from=from;
		this.to=to;
		this.isControllable=false;
		this.used=false;
	}

	public Transition(String name) {
		super(name);
		this.isControllable=false;
	}
	public void setTo(State to){//set
		this.to=to;
	}
	public void setFrom(State from){//set
		this.from=from;
	}
	public State getFrom(){//get
//		System.out.println("getFrom:"+from);
		return this.from;
	}
	public State getTo(){//get
		return this.to;
	}
	public void setIsControllable(){
		isControllable=true;
	}

	public boolean isControllable(){//Controllableかどうか
		return isControllable;
	}

	@Override
	boolean hasNext() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return false;
	}

	@Override
	ModelMaterial next() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		used=true;
		return this.to;//行き先のStateを返す
	}

	@Override
	void reset() {//Transitionの探索をリセット
		used=false;//Transitionが使われたかどうかをリセット
	}

	public void setErrorStrings(State env,List<State> initReq) {//要求のどれがエラーしてるか的な？そんな感じ
		// TODO Auto-generated method stub
		used=true;
		if(errorStringMap==null)errorStringMap=new HashMap<String,String>();
		String errorString =env.getName(),error="";
		for(int i=0;i<initReq.size();i++){
			String tmp=initReq.get(i).toString();
			if(tmp.contains("ERROR")){//initReqのstateの名前にERRORが含まれているならば
				if(error!="")error=error.concat("&&");
				tmp=tmp+i;//String + int +演算子の左辺が文字列なので右辺の数値は文字列に変換される よって文字列＋文字列
				error=error.concat(tmp);//concat 文字列結合
			}
			errorString=errorString.concat(tmp);
			/*
			this.deadList.add(false);//*/
		}
		System.out.println("Set error string as"+errorString+" for "+error+ "@"+this);
		errorStringMap.put(error,errorString);
	}
	public String getErrorString(String error){
		if(errorStringMap==null||errorStringMap.get(error)==null){
			System.out.println("ERROR: no errorString with "+error+"@"+this+used);
			return null;
		}
		return errorStringMap.get(error);
	}
}
