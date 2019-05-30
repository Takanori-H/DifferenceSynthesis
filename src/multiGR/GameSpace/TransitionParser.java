package multiGR.GameSpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import multiGR.model.Model;
import multiGR.model.ModelInterface;
import multiGR.model.State;
import multiGR.model.Transition;

public class TransitionParser {//Transitionの構文解析ツール pastedead cansimulateが重要
	int trNum = 0; // debug
	List<GameModel> modelList;
	ModelInterface controller;
	Model con;
	boolean isController = false;
	List<String> transitionRecordsOfModel;//GameのTransitionの記録
	List<String> transitionRecordsOfController;//ControllerのTransitionの記録
	List<String> additionalTransitionSequences;
	List<String> controllableActions;//コントローラブルアクション
	String transitionSequence = "";
	String crlf = System.getProperty("line.separator");//改行コードを取得する
	List<String> eliminatedLabel;//削除されたラベル
	int currentIndex;//現在のレベル
	int MAX_LEVEL;
	//new
	int count;
	HashMap<Integer,GameModel> GameList;//レベルとゲームモデルを管理
	int max;//最高レベル
	int now;//今動いているコントローラのレベル
	List<State> WRCTmp;
	List<State> WRCnow;
	List<State> deltaWRC;
	HashMap<Integer,List<State>> WRC;//コントローラ側のWinning Region
	HashMap<String,State> C;
	HashMap<Integer,HashMap<String,State>> Controllers;
	int flag;
	List<Integer> nolevel;

	public TransitionParser(List<GameModel> modelList) {
		this.modelList = modelList;
		this.transitionRecordsOfModel = new ArrayList<String>();
		MAX_LEVEL = modelList.size();
		currentIndex = 0;
		//new
		now = max = modelList.size();
		GameList = new HashMap<Integer,GameModel>();
		for(int i=0;i<modelList.size();i++) {
			GameList.put(modelList.size()-i,modelList.get(i));
		}
		WRCTmp = new ArrayList<State>();
		WRCnow = new ArrayList<State>();
		WRC = new HashMap<Integer,List<State>>();
		C = new HashMap<String,State>();
		Controllers = new HashMap<Integer,HashMap<String,State>>();
		nolevel = new ArrayList<Integer>();
	}

	public TransitionParser(List<GameModel> model, ModelInterface controller) {//update時に使ってる
		this.modelList = model;
		this.controller = controller;
	}

	public void setCMList(List<GameModel> modelList) {
		this.modelList = modelList;
		for(int i=0;i<modelList.size();i++) {
			GameList.put(modelList.size()-i,modelList.get(i));
		}
	}

	public void setController(ModelInterface c) {
		this.controller = c;
	}

	public void setCon(Model c) {
		this.con = c;
	}

	void setEliminatedLabel(List<String> eliminatedLabel) {
		this.eliminatedLabel = eliminatedLabel;
	}

	void setModelList(List<GameModel> modelList) {
		this.modelList = modelList;
	}

	void setControllableActions(List<String> c) {
		this.controllableActions = c;
	}

	void pasteController(List<String> l) {
		this.setEliminatedLabel(l);
		for (ModelInterface model : modelList) {
			pasteCToState(controller.getInitialState(), model.getInitialState());
		}
	}

	void pasteController() {//重要
		this.setEliminatedLabel(new ArrayList<String>());
		// pasteCToState(controller.getInitState(),model.getInitState());
		for (ModelInterface model : modelList) {
			eraseContoller();
			pasteCToStateWithHash(controller.getInitialState(), model.getInitialState());
		}
	}

	void pasteCToState(State c, State m) {
		if (c.isController() || m.toString().equals("ERROR")) {
			return;
		}
		c.setIsController();
		m.setIsController();
		for (int i = 0; i < c.getToTransitionNum(); i++) {
			if (eliminatedLabel.contains(c.getToTransition(i).toString())) {
				pasteCToState(c.getToTransition(i).getTo(), m);
			} else {
				if (!c.getToTransition(i).isController()) {
					c.getToTransition(i).setIsController();
					m.getToTransition(c.getToTransition(i).toString()).setIsController();
					if (!c.getToTransition(i).toString().equals(m.getToTransition(c.getToTransition(i).toString()).toString()))
						System.out.println("ERROR");
					pasteCToState(c.getToTransition(i).getTo(), m.getToTransition(c.getToTransition(i).toString()).getTo());
				}
				// pasteCToTransition(c.getToTransition(i),m.getToTransition(c.getToTransition(i).toString()));
			}
		}
	}

	void pasteCToStateWithHash(State c, State m) {
		HashMap<State, ContSet> map = new HashMap<State, ContSet>();
		ContSet cu = new ContSet(c, m), next;
		cu.getController().setIsController();
		cu.getModel().setIsController();
//		int i = 0;
		while (cu.getController().hasNext()) {
//			i++;
			Transition t = (Transition) cu.getController().next();
			if (cu.getModel().getToTransition(t.toString()) == null) {
//				System.out.println("Caution!:" + t + " in " + cu.getModel());
//				for (int j = 0; j < cu.getModel().getToTransitionNum(); j++) {
//					System.out.println("  " + cu.getModel().getToTransition(j));
//				}
				cu.getModel().setIsController();
			} else {
				if (cu.getController().hasNext() && !map.containsValue(cu)) {
					map.put(cu.getController(), cu);
				}
				next = new ContSet(t.getTo(), cu.getModel()
						.getToStateByTransition(t.toString()));
				next.getController().setIsController();
				next.getModel().setIsController();
				if (!next.getController().hasNext() && !map.isEmpty()) {
					next = map.remove(map.keySet().toArray()[0]);
				} else if (map.containsKey(next.getController())) {
					map.remove(next.getController());
				}
				cu = next;
			}
		}
	}

	class ContSet {
		State controller, model;

		ContSet(State c, State m) {
			this.controller = c;
			this.model = m;
		}

		State getController() {
			return this.controller;
		}

		State getModel() {
			return this.model;
		}
	}

	void pasteCToTransition(Transition c, Transition m) {
		if (c.isController()) {
			return;
		}
		c.setIsController();
		m.setIsController();
		if (!c.toString().equals(m.toString()))
			System.out.println("ERROR");
		pasteCToState(c.getTo(), m.getTo());
	}

	void eraseContoller() {
		Model m = (Model) this.controller;
		for (int i = 0; i < m.getSize(); i++) {
			State s = m.getState(i);
			s.eraseIsController();
			s.reset();
			for (int j = 0; j < s.getFromTransitionNum(); j++) {
				s.getFromTransition(j).eraseIsController();
			}
			for (int j = 0; j < s.getToTransitionNum(); j++) {
				s.getToTransition(j).eraseIsController();
			}
		}

	}

	void eraseDead() {
		for (ModelInterface model : modelList) {
			State er = model.getErrorState();
			er.eraseIsDead();
			for (int i = 0; i < er.getFromTransitionNum(); i++) {
				eraseDFromTransition(er.getFromTransition(i));
			}
		}
	}

	private void eraseDFromTransition(Transition t) {
		if (!t.isDead())
			return;
		t.eraseIsDead();
		this.eraseDFromState(t.getFrom());
	}

	private void eraseDFromState(State s) {
		if (!s.isDead())
			return;
		s.eraseIsDead();
		for (int i = 0; i < s.getFromTransitionNum(); i++) {
			if (s.getFromTransition(i).isDead())
				eraseDFromTransition(s.getFromTransition(i));
		}
	}

	void DesignTimeSynthesis() {
		int level = max;
		List<State> WRClevel;
		while(level>0) {
			GameModel model = GameList.get(level);
			WRClevel = IdentifyWR(model, level);
			generateController(WRClevel, level);
			level--;
		}
		createSimulate(max);
	}

	void DCSUpdateEnv() {
		int level = max;
		List<State> WRClevel;
		while(level>0) {
			long start2 = System.currentTimeMillis();
			GameModel model = GameList.get(level);
			WRClevel = IdentifyWR(model, level);
			if(WRCTmp.size()==0) {
//				/*
				long stop=System.currentTimeMillis();
				System.out.println("Spending time of IdentifyWR: "+(stop-start2)+"ms");
				System.out.println("No Controller");
				System.out.println();
//				*/
				Controllers.remove(level);
				level--;
				continue;
			}
			long start=System.currentTimeMillis();
			generateController(WRClevel, level);
			long stop=System.currentTimeMillis();
//			/*
			System.out.println("Spending time of IdentifyWR+Synthesis: "+(stop-start2)+"ms");
			System.out.println("Spending time of Synthesis: "+(stop-start)+"ms");
			System.out.println("Synthesis of Controller Level: "+level);
//			*/
			if(checkContSimulate(Controllers.get(level), level)) {
//				/*
				System.out.println("Simulate");
//				*/
				return;
			}else {
				Controllers.remove(level);
//				/*
				System.out.println("No Simulate");
//				*/
			}
			System.out.println();
			level--;
		}
	}

	boolean checkContSimulate(HashMap<String,State> controller, int level) {
		flag = 0;
		State tmpC, tmpC2;
		tmpC=con.getInitialState();
		tmpC2=controller.get(WRCTmp.get(0).getName());
		pasteContSimulate(tmpC, tmpC2, level);
		if(flag==0) {
			flag=0;
			return true;
		}else {
			flag=0;
			return false;
		}
	}

	void pasteContSimulate(State tmpC, State tmpC2, int level) {
		tmpC2.setSimulate(level);
		for(int i=0;i<tmpC.getToTransitionNum();i++) {
			checkContTransition(tmpC.getToTransition(i), tmpC2, level);
			if(flag==1)return;
		}
	}

	void checkContTransition(Transition ctr, State tmpC2, int level) {
		if(flag==1)return;
		if(ctr.isDead())return;
		String ctrName = ctr.getName();
		try {
			if(tmpC2.getToTransition(ctrName)!=null) {
				Transition ctr2 = tmpC2.getToTransition(ctrName);
				checkContState(ctr.getTo(),ctr2.getTo(), level);
				if(flag==1)return;
			}else {
				throw new NullPointerException();
			}
		}catch(NullPointerException e) {
			flag=1;
			return;
		}
	}

	void checkContState(State tmpC, State tmpC2, int level) {
		if(flag==1)return;
		if(tmpC2.isSimulate(level)) return;
		tmpC2.setSimulate(level);//simulate関係をセット
		//System.out.println(jState.getName());
		//System.out.println(iState.getName());
		for(int i=0;i<tmpC.getToTransitionNum();i++) {
			checkContTransition(tmpC.getToTransition(i), tmpC2, level);
			if(flag==1)return;
		}
	}

	List<State> IdentifyWR(GameModel model, int level) {
		WRCTmp = new ArrayList<State>();
		count = 0;
		State er = model.getErrorState();
		er.setIsDead();
		count++;
		for(int i=0;i<er.getFromTransitionNum();i++) {
			pasteDToTransition(er.getFromTransition(i));
		}
		System.out.println("Level: " + level);
		System.out.println("GameState: " + model.getSize());
		System.out.println("count: " + count);
		State initial = model.getInitialState();
		WRCTmp.add(initial);
		for(int i=0;i<initial.getToTransitionNum();i++) {
			countWRC(initial.getToTransition(i).getTo());
		}
		System.out.println("WRCTmp: " + WRCTmp.size());
		System.out.println();
		WRC.put(level, WRCTmp);
		return WRCTmp;
	}

	void countWRC(State tmp) {
		if(tmp.isDead() || WRCTmp.contains(tmp))return;
		WRCTmp.add(tmp);
		for(int i=0;i<tmp.getToTransitionNum();i++) {
			countWRC(tmp.getToTransition(i).getTo());
		}
	}

	void pasteDToTransition(Transition m) {
		if (m.isDead())
			return;
		// System.out.println("This is dead transition:["+m+"]");//debug
		m.setIsDead();
		this.pasteDToState(m.getFrom());
	}

	void pasteDToState(State m) {//バックワードプロパゲーション
		if (m.isDead())
			return;
		boolean dead = true;
		//System.out.println("state "+m);
		for (int i = 0; i < m.getToTransitionNum(); i++) {
			Transition t = m.getToTransition(i);
			//System.out.println("  Transition:"+t+" isControllable:"+t.isControllable()+" isDead:"+t.isDead());
			if (!t.isControllable() && t.isDead()) {//UnControllable Actionで環境側のWinning Regionに繋がるならば
				m.setIsDead();//そのStateはDead
				//WRETmp.add(m);
				count++;
				//System.out.println("Dead state:["+m+"]");//debug
				for (int j = 0; j < m.getFromTransitionNum(); j++) {
					pasteDToTransition(m.getFromTransition(j));
				}
				return;
			} else if (!t.isDead()) {//環境側のWinning Regionにつながっていないならば
				dead = false;//環境側のWRにつながっているUnControllableActionがなければok
			}
		}

		if (dead) {
			m.setIsDead();
			//WRETmp.add(m);
			count++;
		}
		//System.out.println(" isDead:"+m.isDead());
		if (m.isDead()) {
			for (int i = 0; i < m.getFromTransitionNum(); i++) {
				pasteDToTransition(m.getFromTransition(i));
			}
		}
	}

	void generateController(List<State> WRClevel, int level) {
		State tmpState, cState, fromState, toState, fromCState, toCState;
		Transition tmpTransition, cTransition;
		C = new HashMap<String,State>();
		for(int i=0;i<WRClevel.size();i++) {//状態だけ用意
			tmpState = WRClevel.get(i);
			cState = new State(tmpState.getName());
			C.put(cState.getName(), cState);
		}
		for(int i=0;i<WRClevel.size();i++) {
			tmpState = WRClevel.get(i);
			cState = C.get(tmpState.getName());
			for(int j=0;j<tmpState.getFromTransitionNum();j++) {
				tmpTransition = tmpState.getFromTransition(j);
				if(!tmpTransition.isDead()) {
					fromState = tmpTransition.getFrom();//Winning Region上でのTransitionのfromState
					fromCState = C.get(fromState.getName());//コントローラ上でのTransitionのfromState
					toCState = cState;//cStateのfromTransitionのtoStateはcState
					cTransition = new Transition(tmpTransition.getName(), fromCState, toCState);
					if(tmpTransition.isControllable())cTransition.setIsControllable();
					cState.addFromTransition(cTransition);
				}
			}
			for(int j=0;j<tmpState.getToTransitionNum();j++) {
				tmpTransition = tmpState.getToTransition(j);
				if(!tmpTransition.isDead()) {
					//fromState = tmpTransition.getFrom();//Winning Region上でのTransitionのfromState
					//fromCState = C.get(fromState.getName());//コントローラ上でのTransitionのfromState
					fromCState = cState;//cStateのtoTransitionのfromStateはcState
					toState = tmpTransition.getTo();//Winning Region上でのTransitionのtoState
					toCState = C.get(toState.getName());//コントローラ上でのTransitionのtoState
					cTransition = new Transition(tmpTransition.getName(), fromCState, toCState);
					if(tmpTransition.isControllable())cTransition.setIsControllable();
					cState.addToTransition(cTransition);
				}
			}
		}
		Controllers.put(level,C);
	}

	//コントローラ側のWinning Regionのsimulation関係を取る 設計時
	void createSimulate(int level) {
		flag=0;
		//for(int i=1;i<max;i++) {
		for(int i=1;i<=level;i++) {
			//GameModel imodel = GameList.get(i);
			List<State> iWRC = WRC.get(i);
			//for(int j=i+1;j<=max;j++) {
			for(int j=i;j<=level;j++) {
				//GameModel jmodel = GameList.get(j);
				List<State> jWRC = WRC.get(j);
				pasteSimulate(iWRC, jWRC, j);
				if(flag==0)System.out.println(i + " is simulate " + j);
				if(flag==1)System.out.println(i + " is not simulate " + j);
				flag=0;
			}
		}
	}

	void pasteSimulate(List<State> iWRC, List<State> jWRC, int level) {
		State jInitialState = jWRC.get(0);
		State iInitialState = iWRC.get(0);
		iInitialState.setSimulate(level);
		//System.out.println(jInitialState.getName());
		//System.out.println(iInitialState.getName());
		for(int i=0;i<jInitialState.getToTransitionNum();i++) {
			pasteSFromTransition(jInitialState.getToTransition(i), iInitialState, level, iWRC);
			if(flag==1)return;
		}
	}

	void pasteSFromTransition(Transition jtr, State iState, int level, List<State> iWRC) {
		if(flag==1)return;
		if(jtr.isDead()) return;
		String jName = jtr.getName();
		try {
			if(iState.getToTransition(jName)!=null) {
				Transition itr = iState.getToTransition(jName);
				//System.out.println(jName);//debug
				//System.out.println(itr.toString());
				//System.out.println();
				pasteSFromState(jtr.getTo(),itr.getTo(), level, iWRC);
				if(flag==1)return;
			}else {
				throw new NullPointerException();
			}
		}catch(NullPointerException e) {
			flag=1;
			pasteSNull(iWRC, level);
			return;
		}
		//if(itr==null)System.out.println("true");
		//if(itr==null)return;
	}

	void pasteSFromState(State jState, State iState, int level, List<State> iWRC) {
		if(flag==1)return;
		if(iState.isSimulate(level)) return;
		iState.setSimulate(level);//simulate関係をセット
		//System.out.println(jState.getName());
		//System.out.println(iState.getName());
		for(int i=0;i<jState.getToTransitionNum();i++) {
			pasteSFromTransition(jState.getToTransition(i), iState, level, iWRC);
			if(flag==1)return;
		}
	}

	void pasteSNull(List<State> iWRC, int level) {
		for(int i=0;i<iWRC.size();i++) {
			if(iWRC.get(i).isSimulate(level))iWRC.get(i).removeSimulate(level);
			//System.out.println(level);
			/*if(iWRC.get(i).isSimulate(level)) {
				System.out.println("true");
			}else {
				System.out.println("false");
			}*/
		}
	}

	void createUpdateSimulate(int level) {
		flag=0;
		//for(int i=1;i<max;i++) {
		for(int i=1;i<=level;i++) {
			//GameModel imodel = GameList.get(i);
			List<State> iWRC = WRC.get(i);
			for(int j=0;j<iWRC.size();j++) {
				iWRC.get(i).clearSimulate();
			}
			if(!Controllers.containsKey(i)){
				continue;
			}
			for(int j=i;j<=level;j++) {
				//GameModel jmodel = GameList.get(j);
				if(!Controllers.containsKey(j)) {
					//System.out.println(i + " is not simulate " + j);
					continue;
				}
				List<State> jWRC = WRC.get(j);
				pasteSimulate(iWRC, jWRC, j);
				if(flag==0)System.out.println(i + " is simulate " + j);
				if(flag==1)System.out.println(i + " is not simulate " + j);
				flag=0;
			}
		}
	}

	int nowlevel() {
		return now;
	}

	//環境側のWinning Regionのupdate
	void pasteUpdatedDead() {
		//int j=MAX_LEVEL;
		for (GameModel model : modelList) {
			int level = 0;
			for(int i=max;i>0;i++) {
				if(GameList.get(i)==model)level=i;
			}
			List<Transition> l = model.getUpdatedPart();
			//System.out.println("update pasteDeadEnd for model"+j--);
//			WRETmp = new ArrayList<State>();
			WRCTmp = new ArrayList<State>();
//			deltaWRE = new ArrayList<State>();
//			WRETmp = WRE.get(level);
			for (int i = 0; i < l.size(); i++) {
				if (l.get(i).getTo().isDead()) {
				//	System.out.println("  pasteDead "+l.get(i)+"->"+l.get(i).getTo());
					pasteUpdateDToTransition(l.get(i));
				}
			}
//			WRE.put(level, WRETmp);
			State initial = model.getInitialState();
			for(int i=0;i<model.getSize();i++) {
				State tmp = model.getState(i);
				if(tmp==initial && WRCTmp.size()>0) {
					State change = WRCTmp.get(0);
//					if(!WRETmp.contains(tmp))WRCTmp.set(0, tmp);
					WRCTmp.add(change);
				}//else if(!WRETmp.contains(tmp))WRCTmp.add(tmp);
			}
			System.out.println(model.getSize());
//			System.out.println(WRETmp.size());
//			System.out.println(deltaWRE.size());
			System.out.println(WRCTmp.size());
			WRC.put(level, WRCTmp);
		}
	}

	void DifferenceSynthesis() {
		int level = now;//緩和だけならnowからで良い 全部やる必要があるのか？
		int nowClevel = now;
		List<State> deltaWRClevel = new ArrayList<State>();
		//HashMap<String,State> tmpC = new HashMap<String,State>();
		while(level>0) {
			System.out.println(level);
			if(nolevel.contains(level)) {
				level--;
				continue;
			}
			long start2 = System.currentTimeMillis();
			GameModel model = GameList.get(level);
			deltaWRClevel = IdentifyUpdateWR(model, level);//updateGameとidentifyWR
			long start=System.currentTimeMillis();
			UpdateController(model, level);
			if(!checkSynthesis(nowClevel, deltaWRClevel)) {//今動いているコントローラとsimulateしているかどうか
				//System.out.println("false");
				long stop=System.currentTimeMillis();
				System.out.println("Spending time of IdentifyWR+Synthesis:  "+(stop-start2)+"ms");
				System.out.println("Spending time of Synthesis:  "+(stop-start)+"ms");
				if(WRCTmp.size()==0) {
					System.out.println("No Controller");
				}else {
					System.out.println("No Simulate");
				}
				System.out.println();
				Controllers.remove(level);//simulateしていなかったらコントローラ取り除く
				nolevel.add(level);//コントローラを取り除いたレベルを管理
				level--;
				//now = level;
				continue;
			}else if(level==nowClevel) {//deltaWRE==0の場合
				long stop=System.currentTimeMillis();
				System.out.println("Spending time of IdentifyWR+Synthesis: "+(stop-start2)+"ms");
				System.out.println("Spending time of Synthesis: "+(stop-start)+"ms");
				System.out.println("Synthesis of Controller Level: "+level);
				System.out.println();
				level--;
				continue;
			}
			//createUpdateSimulate(level);
			generateUpdateController(deltaWRClevel, level);
			//if(Controllers.containsKey(level))System.out.println("true");
			long stop=System.currentTimeMillis();
			System.out.println("Spending time of IdentifyWR+Synthesis: "+(stop-start2)+"ms");
			System.out.println("Spending time of Synthesis: "+(stop-start)+"ms");
			System.out.println("Synthesis of Controller Level: "+level);
			System.out.println();
			level--;
		}
		for(int i=max;i>0;i--) {
			if(Controllers.containsKey(i)) {
				now = i;
				break;
			}
		}
		createUpdateSimulate(now);
	}

	List<State> IdentifyUpdateWR(GameModel model, int level) {//Updateがちゃんとできていない可能性あり
		//WRETmp = new ArrayList<State>();
		WRCTmp = new ArrayList<State>();
		List<State> oldWRC = new ArrayList<State>();
		deltaWRC = new ArrayList<State>();
		count=0;
		//WRETmp = WRE.get(level);
		oldWRC = WRC.get(level);
		List<Transition> l = model.getUpdatedPart();//updateはTransitionのみを考えている？
		for(int i=0;i<l.size();i++) {
			if(l.get(i).getTo().isDead()) {
				pasteUpdateDToTransition(l.get(i));
			}
		}
		//WRE.put(level, WRETmp);
		State initial = model.getInitialState();
		WRCTmp.add(initial);
		for(int i=0;i<initial.getToTransitionNum();i++) {
			countWRC(initial.getToTransition(i).getTo());
		}
		for(int i=0;i<oldWRC.size();i++) {
			State tmp = oldWRC.get(i);
			if(!WRCTmp.contains(tmp))deltaWRC.add(tmp);
		}
		System.out.println("GameState: "+ model.getSize());
		//System.out.println("WRETmp: " + WRETmp.size());
		//System.out.println("deltaWRE: " + deltaWRE.size());
		System.out.println("count :" + count);
		System.out.println("WRCTmp: " + WRCTmp.size());
		System.out.println("deltaWRC: " + deltaWRC.size());
		WRC.put(level, WRCTmp);
		//UpdateController(l,level);
		return deltaWRC;
		//return WRCTmp;
	}

	void pasteUpdateDToTransition(Transition m) {
		if (m.isDead())
			return;
		// System.out.println("This is dead transition:["+m+"]");//debug
		m.setIsDead();
		this.pasteUpdateDToState(m.getFrom());
	}

	void pasteUpdateDToState(State m) {//バックワードプロパゲーション
		if (m.isDead())
			return;
		boolean dead = true;
		//System.out.println("state "+m);
		for (int i = 0; i < m.getToTransitionNum(); i++) {
			Transition t = m.getToTransition(i);
			//System.out.println("  Transition:"+t+" isControllable:"+t.isControllable()+" isDead:"+t.isDead());
			if (!t.isControllable() && t.isDead()) {//UnControllable Actionで環境側のWinning Regionに繋がるならば
				m.setIsDead();//そのStateはDead
				//System.out.println(m.getName());
//				WRETmp.add(m);
//				deltaWRE.add(m);
				count++;
				//System.out.println("Dead state:["+m+"]");//debug
				for (int j = 0; j < m.getFromTransitionNum(); j++) {
					pasteUpdateDToTransition(m.getFromTransition(j));
				}
				return;
			} else if (!t.isDead()) {//環境側のWinning Regionにつながっていないならば
				dead = false;//環境側のWRにつながっているUnControllableActionがなければok
			}
		}

		if (dead) {
			m.setIsDead();
			count++;
//			WRETmp.add(m);
//			deltaWRE.add(m);
		}
		//System.out.println(" isDead:"+m.isDead());
		if (m.isDead()) {
			for (int i = 0; i < m.getFromTransitionNum(); i++) {
				pasteUpdateDToTransition(m.getFromTransition(i));
			}
		}
	}

	boolean checkSynthesis(int nowClevel, List<State> deltaWRElevel) {
		for(int i=0;i<deltaWRElevel.size();i++) {
			State tmp = deltaWRElevel.get(i);
			//if(tmp.isSimulate(nowClevel))System.out.println("false");
			if(tmp.isSimulate(nowClevel))return false;
		}
		return true;
	}

	void UpdateController(/*List<Transition> l,*/GameModel model, int level) {//コントローラ内にtransitionが増えていた場合にコントローラをアップデート
		State toState, fromState, toCState, fromCState, tmpToState, tmpFromState, tmpToCState, tmpFromCState;
		Transition newTransition, cTransition, tmpTransition;
		C = Controllers.get(level);
		if(C==null)System.out.println("true");
		List<Transition> l = model.getUpdatedPart();
		for(int i=0;i<l.size();i++) {
			Transition tr = l.get(i);
			if(!tr.isDead()) {
				toState = tr.getTo();
				fromState = tr.getFrom();
				if(toState.getName()==null)System.out.println("true");
				toCState = C.get(toState.getName());
				fromCState = C.get(fromState.getName());
//				/*
				if(toCState==null && fromCState == null) {
					if(toState.isDead())continue;
					if(fromState.isDead())continue;
					toCState=new State(toState.getName());
					fromCState=new State(fromState.getName());
					newTransition = new Transition(tr.getName(), fromCState, toCState);
					if(tr.isControllable())newTransition.setIsControllable();
					toCState.addFromTransition(newTransition);
					fromCState.addToTransition(newTransition);
					C.put(toCState.getName(),toCState);
					C.put(fromCState.getName(), fromCState);
				}else if(toCState==null) {
					if(toState.isDead())continue;
					toCState=new State(toState.getName());
					newTransition = new Transition(tr.getName(), fromCState, toCState);
					if(tr.isControllable())newTransition.setIsControllable();
					toCState.addFromTransition(newTransition);
					fromCState.addToTransition(newTransition);
					C.put(toCState.getName(), toCState);
				}else if(fromCState==null) {
					if(fromState.isDead())continue;
					fromCState=new State(fromState.getName());
					newTransition = new Transition(tr.getName(), fromCState, toCState);
					if(tr.isControllable())newTransition.setIsControllable();
					toCState.addFromTransition(newTransition);
					fromCState.addToTransition(newTransition);
					C.put(fromCState.getName(), fromCState);
				}else {
					newTransition = new Transition(tr.getName(), fromCState, toCState);
					if(tr.isControllable())newTransition.setIsControllable();
					toCState.addFromTransition(newTransition);
					fromCState.addToTransition(newTransition);
				}
//				*/
			}
		}
		Controllers.put(level, C);
	}

	void generateUpdateController(List<State> deltaWRElevel, int level) {//差分更新
		State tmpState, cState, from, to;
		Transition cTransition;
		String cTransitionName;
		C = Controllers.get(level);
		for(int i=0;i<deltaWRElevel.size();i++) {
			tmpState = deltaWRElevel.get(i);
			cState = C.get(tmpState.getName());
			for(int j=0;j<cState.getFromTransitionNum();j++) {
				cTransition = cState.getFromTransition(j);
				cTransitionName = cTransition.getName();
				from = cTransition.getFrom();
				from.eraseToTransition(cTransitionName);
				from.eraseToTransitionNameList(cTransitionName);
			}
			for(int j=0;j<cState.getToTransitionNum();j++) {
				cTransition = cState.getToTransition(j);
				cTransitionName=cTransition.getName();
				to = cTransition.getTo();
				to.erasefromTransition(cTransition);
				//to.erasefromTransitionNameList(cTransitionName);
			}
			C.remove(cState.getName());
		}
		Controllers.put(level, C);
	}

	public int[][] checkDesignTimeSynthesis() {
		int level = max;
		int Csize[][] = new int[modelList.size()][2];
		HashMap<String,State> tmpC = new HashMap<String, State>();
		DesignTimeSynthesis();
		while(level>0) {
			//GameModel model = GameList.get(level);
			tmpC = Controllers.get(level);
			Csize[level-1][0] = tmpC.size();
			Csize[level-1][1] = CountTransitionNum(level, tmpC);
			level--;
		}
		return Csize;
	}

	public void checkDCSUpdateEnv(Model controller) {
		this.setCon(controller);
		long start=System.currentTimeMillis();
		checkDCSUpdateEnv();
		long stop=System.currentTimeMillis();
		System.out.print("Spending time: "+(stop-start)+"ms");
		System.out.println();
		return;
	}

	public void checkDCSUpdateEnv() {
		DCSUpdateEnv();
		return;
	}

	public int checkUpdateControllerSynthesis() {
		int level = now;
		HashMap<String,State> tmpC = new HashMap<String,State>();
		DesignTimeSynthesis();
		while(level>0) {
			tmpC = Controllers.get(level);
			System.out.println("level " + level + "," + "State数 " + tmpC.size() + "," + "Transition数 " + CountTransitionNum(level, tmpC));
			System.out.println();
			level--;
		}
		return now;
	}

	public int checkDifferentialControllerSynthesis() {
		HashMap<String,State> tmpC = new HashMap<String,State>();
		//DesignTimeSynthesis();
		DifferenceSynthesis();
		System.out.println("nowlevel " + now);
		int level = now;
		while(level>0) {
			if(Controllers.containsKey(level)){
			tmpC = Controllers.get(level);
			if(tmpC==null)System.out.println("null");
			System.out.println("level " + level + "," + "State数 " + tmpC.size()/* + "," + "Transition数 " + CountTransitionNum(level, tmpC)*/);
			System.out.println();
			}
			level--;
		}
		//return Csize;
		return now;
	}

	public int checkDiscreteControllerSynthesis() {
		HashMap<String,State> tmpC = new HashMap<String,State>();
//		DiscreteControllerSynthesis();
		System.out.println("nowlevel " + now);
		int level = now;
		while(level>0) {
			if(Controllers.containsKey(level)){
			tmpC = Controllers.get(level);
			if(tmpC==null)System.out.println("null");
			System.out.println("level " + level + "," + "State数 " + tmpC.size()/* + "," + "Transition数 " + CountTransitionNum(level, tmpC)*/);
			System.out.println();
			}
			level--;
		}
		//return Csize;
		return now;
	}

	public int CountTransitionNum(int level, HashMap<String,State> tmpC) {
		List<State> WR = WRC.get(level);
		int count = 0;
		for(int i=0;i<tmpC.size();i++) {
			State cState = tmpC.get(WR.get(i).getName());
			count += cState.getToTransitionNum();
		}
		return count;
	}

	boolean checkCanSimulate(ModelInterface rs, int level) {
		boolean isDead = false;
//		int count=0;
		for (int i = 0; i < rs.getSize(); i++) {
			State s = rs.getState(i);
			//if(s.isController())
				//System.out.println((++count)+"state:"+s+",isDead:"+s.isDead()+" isController:"+s.isController());
			if (s.isController() && s.isDead()) {
				isDead = true;
				//System.out.println("level "+level+" controller cannot be generated:" + s);
				return isDead;
			}
		}
		if (!isDead) {
			//System.out.println("controller can be generated");
			return isDead;
		}
		return isDead;
	}

	public int checkUpdatedSimulate() {
		pasteUpdatedDead();
		pasteController();
		for (int i = currentIndex; i < MAX_LEVEL; i++) {
			currentIndex = i;
			if (!checkCanSimulate(this.modelList.get(i),(MAX_LEVEL-i))) {
				//System.out.println("checkUpdatedSimulateEnd:"+(MAX_LEVEL-i));
				break;
			}
		}
		return MAX_LEVEL - currentIndex;
	}

	//new
	public void generatedController() {//アルゴリズム上のextractWSとtranslateWSを混ぜたもの
		List<State> tmp;
		//State initialState;
		State tmpState, cState, fromState, toState, fromCState, toCState;
		Transition tmpTransition, cTransition;
		//HashMap<String,State> C; と定義されてる　上で
		//List<HashMap<String,State>> Controllers; と定義されてる　上で
		for(GameModel model : modelList) {
			C = new HashMap<String,State>();
			//System.out.println(model.getSize());
			tmp = WRC.get(model);
			//System.out.println(tmp.size());
			for(int i=0;i<tmp.size();i++) {//状態だけ用意
				tmpState=tmp.get(i);
				cState = new State(tmpState.getName());
				C.put(cState.getName(),cState);
				//System.out.println(C.size());
			}
			//System.out.println(C.size());
			for(int i=0;i<tmp.size();i++) {
				tmpState = tmp.get(i);
				cState = C.get(tmpState.getName());
				for(int j=0;j<tmpState.getFromTransitionNum();j++) {
					tmpTransition = tmpState.getFromTransition(j);
					if(!tmpTransition.isDead()) {
						fromState = tmpTransition.getFrom();//Winning Region上でのTransitionのfromState
						fromCState = C.get(fromState.getName());//コントローラ上でのTransitionのfromState
						//toState = tmpTransition.getTo();//Winning Region上でのTransitionのtoState
						//toCState = C.get(toState.getName());//コントローラ上でのTransitionのtoState
						toCState = cState;//cStateのfromTransitionのtoStateはcState
						cTransition = new Transition(tmpTransition.getName(), fromCState, toCState);
						if(tmpTransition.isControllable())cTransition.setIsControllable();
						cState.addFromTransition(cTransition);
					}
				}
				for(int j=0;j<tmpState.getToTransitionNum();j++) {
					tmpTransition = tmpState.getToTransition(j);
					if(!tmpTransition.isDead()) {
						//fromState = tmpTransition.getFrom();//Winning Region上でのTransitionのfromState
						//fromCState = C.get(fromState.getName());//コントローラ上でのTransitionのfromState
						fromCState = cState;//cStateのtoTransitionのfromStateはcState
						toState = tmpTransition.getTo();//Winning Region上でのTransitionのtoState
						toCState = C.get(toState.getName());//コントローラ上でのTransitionのtoState
						cTransition = new Transition(tmpTransition.getName(), fromCState, toCState);
						if(tmpTransition.isControllable())cTransition.setIsControllable();
						cState.addToTransition(cTransition);
					}
				}
			}
			//Controllers.add(C);
		}
	}

}