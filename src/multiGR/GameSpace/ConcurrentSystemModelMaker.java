package multiGR.GameSpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import multiGR.model.Model;
import multiGR.model.ModelInterface;
import multiGR.model.State;
import multiGR.model.Transition;

public class ConcurrentSystemModelMaker {
	static HashMap<String, GameState> states;
	static List<Transition> updatePart = new ArrayList<Transition>();
	static List<String> controllableActions ;
	static int updateNumber = 0;

	private static GameState makeModel(State e, State[] reqMoni) {
		GameState c = new GameState(e, reqMoni, updateNumber);
		states.put(c.getName(), c);
		return c;
	}

	private static void makeErrorModel() {
		GameState c = new GameState("ERROR");
		states.put("ERROR", c);
	}

	public static void setConcurrentModel(GameModel cm) {
		states = cm.states;
	}

	public static void setControllableAction(List<String> ca) {
		controllableActions = ca;
	}

	private static void compose(State e, State[] initialStates, GameState c) {
		for (int i = 0; i < e.getToTransitionNum(); i++) {
			transition(e.getToTransition(i), initialStates, c);
		}
	}

	private static void transition(Transition te, State[] moni, GameState c) {
		Transition[] tm = new Transition[moni.length];
		State[] toMoni = new State[moni.length];
		Transition tc = new Transition(te.toString());
		tc.setFrom(c);
		c.addToTransition(tc);
		updatePart.add(tc);
		if (controllableActions.contains(te.toString()))
			tc.setIsControllable();
		//System.out.println("transition:"+tc+","+tc.isControllable());
		for (int i = 0; i < moni.length; i++) {
			tm[i] = moni[i].getToTransition(te.toString());
			if (tm[i] != null) {
				toMoni[i] = tm[i].getTo();
				if (toMoni[i].getName().equals("ERROR")) {
					tc.setTo(states.get("ERROR"));
					states.get("ERROR").addFromTransition(tc);
					return;
				}
			} else {
				toMoni[i] = moni[i];
			}
		}
		GameState targetM = getConcurrentStateFromMap(te.getTo(),
				toMoni);
		if(targetM!=null){
			targetM.addFromTransition(tc);
			tc.setTo(targetM);
		}else{
			targetM = makeModel(te.getTo(), toMoni);
			targetM.addFromTransition(tc);
			tc.setTo(targetM);
			compose(te.getTo(), toMoni, targetM);

		}
	}

	public static GameState getConcurrentStateFromMap(State env,
			State[] moni) {
		String stateName = env.getName();
		for (int i = 0; i < moni.length; i++) {
			stateName += moni[i].getName();
		}

		return states.get(stateName);
	}

	public static GameModel makeConccurrentSystem(ModelInterface env,
			ModelInterface[] moni) {
		states=new HashMap<String,GameState>();
		State[] initialStates = new State[moni.length];
		for (int i = 0; i < moni.length; i++) {
			initialStates[i] = moni[i].getInitialState();
		}

		GameState c = makeModel(env.getInitialState(), initialStates);
		GameModel cm = new GameModel();

		makeErrorModel();
		compose(env.getInitialState(), initialStates, c);
		cm.setModelMaterials(states,c);
		return cm;
	}

	public static GameModel makeConcurrentSystem(Model e,
			Model[] reqs, List<String> cas) {
		GameModel cm = null;

		return makeConccurrentSystem(e,reqs);
	}

	public static Model attachTransition(Model e, String from, String t,
			String to) {
		Transition tr = new Transition(t, e.getState(from), e.getState(to));
		e.getState(from).addToTransition(tr);
		e.getState(to).addFromTransition(tr);
		return e;
	}

	private static List<GameState> getCandidate(Transition tr,
			GameModel cm) {
		List<GameState> l = new ArrayList<GameState>();
		for (int i = 0; i < cm.getSize(); i++) {
			GameState cs = cm.getConcurrentState(i);
			if (cs.getEnv()!=null&&cs.getEnv().equals(tr.getFrom().toString())) {
				l.add(cs);
			}
		}
		return l;
	}

	public static GameModel modelUpdate(GameModel cm, Model e,
			Model[] moni, String from, String t, String to) {
		updateNumber++;
		updatePart = new ArrayList<Transition>();
		states=cm.getStates();
		e = attachTransition(e, from, t, to);
		Transition target = e.getState(from).getToTransition(t);
		setConcurrentModel(cm);
		List<GameState> lcs = getCandidate(target, cm);
		for (int i = 0; i < lcs.size(); i++) {
			State[]m=new State[moni.length];
			for(int j=0;j<moni.length;j++){
				m[j] = moni[j].getState(lcs.get(i).reqMoni[j]);
			}
//			for(State s:m)System.out.println(s);
			transition(target, m, lcs.get(i));
		}
		GameModel newCm=new GameModel(states, updatePart);
		newCm.setInitialState(cm.getInitialState());
		return newCm;
	}

}
