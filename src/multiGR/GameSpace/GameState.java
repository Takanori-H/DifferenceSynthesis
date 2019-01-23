package multiGR.GameSpace;

import java.util.HashMap;

import multiGR.model.State;

public class GameState extends State {
	String env,reqMoni[];//環境モデルと要求のモニター？
	int updateNumber;
	HashMap<Integer,Boolean> nowWS;
	GameState(String name) {//コンストラクタの違い
		super(name);//Stateクラスのコンストラクタに引数nameを入れる
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}
	GameState(State env,State[] reqMoni,int upNum) {
		super(env.getName());
		String name=env.getName();
		this.env=env.getName();
		this.reqMoni=new String[reqMoni.length];//引数reqMoniの数分の配列
		for(int i=0;i<reqMoni.length;i++){
			this.reqMoni[i]=reqMoni[i].getName();
			name+=reqMoni[i].getName();//文字列連結
		}
		this.name=name;
		this.updateNumber=upNum;

	}
	GameState(String env,String reqMoni[],int upNum) {
		super(env.toString()+reqMoni.toString());//? String.toString() このオブジェクト(すでに文字列である)自身が返される
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
		this.env=env;
		this.reqMoni=reqMoni;
		this.updateNumber=upNum;
	}

	GameState(String env,String reqMoni[]) {
		super(env.toString()+reqMoni.toString());
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
		this.env=env;
		this.reqMoni=reqMoni;
	}
	String[] getReqMoni(){
		return this.reqMoni;
	}
	String getEnv(){
		return this.env;
	}
	int getUpdateStage(){
		return updateNumber;
	}
}
