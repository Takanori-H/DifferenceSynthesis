package multiGR.errorReachableAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import multiGR.model.ModelInterface;
import multiGR.model.State;
import multiGR.model.Transition;
import multiGR.multiConcurrentModel.MultiConcurrentModel;
import multiGR.multiConcurrentModel.MultiConcurrentState;
import multiGR.multiConcurrentModel.MultiConcurrentTransition;

public class MultiRequirementParser {
	private MultiConcurrentModel mcm;
	private List<Integer> checker;
	private Queue<MultiConcurrentTransition> deadStack;

	public MultiRequirementParser(MultiConcurrentModel mcm,
			List<Integer> checker) {
		this.mcm = mcm;
		this.checker = checker;
		deadStack = new LinkedBlockingQueue<MultiConcurrentTransition>();
	}

	public MultiRequirementParser() {
		// テスト用に作ったコンストラクタ
	}

	public List<List<Integer>> checkSimulate(ModelInterface controller) {
		pasteDead();
		pasteController(controller);
		return checkCanSimulateM(this.mcm);
	}

	public List<List<Integer>> checkUpdatedSimulate(ModelInterface controller) {
		pasteUpdatedDead();
		this.pasteController(controller);
		return checkCanSimulateM((MultiConcurrentModel) mcm);
	}

	List<List<Integer>> checkCanSimulateM(MultiConcurrentModel rs) {
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		boolean isDead = false;
		for (int i = 0; i < rs.getSize(); i++) {
			MultiConcurrentState s = rs.getState(i);
			// if(s.isDead())System.out.println("checkUpdatedSimulate"+s);
			//if(s.isController())System.out.println("checkCanSimulate"+s+":"+s.isController()+","+s.isDead());
			if (s.isController() && s.isDead()) {
				isDead = true;
				result = integratingResult(result, s.getDeadList());
			}
		}
		/*
		 * for(int i=0;i<result.size();i++)//debug
		 * System.out.println("Degradation candidate:"+result.get(i).get(0));//
		 */// debug

		for (int i = 0; i < rs.getErrorSize(); i++) {
			if (rs.getErrorState(i).isController()) {
				isDead = true;
				System.out.println("controller cannot be generated: "
						+ rs.getErrorState(i));
				System.out.println("  " + rs.getErrorState(i));
				// break;
			}
		}
		if (!isDead) {
			System.out.println("controller can be generated");
		} else {
			System.out.println("controller cannot be generated:");
		}
		return result;
	}

	// TODO someday we have to try to solve dead end problem
	public List<List<Integer>> integratingResult(List<List<Integer>> oldResult,
			List<List<Integer>> current) {
		List<List<Integer>> newResult = new ArrayList<List<Integer>>();
		if (oldResult == null || oldResult.size() == 0)
			return current;
		for (int j = 0; j < current.size(); j++) {
			boolean isTarget = true;
			/*
			 * for(int k=0;k<checker.size();k++){
			 * if((current.get(j).get(k)|checker.get(k))!=checker.get(k)){
			 * isTarget=false;break; } }//ここは今は駄目。DeadEnd問題を効率的に解消した時
			 */
			if (isTarget)
				for (int i = 0; i < oldResult.size(); i++) {
					List<Integer> newR = new ArrayList<Integer>();
					for (int k = 0; k < current.get(j).size(); k++) {
						newR.add((int) oldResult.get(i).get(k)
								| current.get(j).get(k));
					}
					newResult.add(newR);
				}
		}

		return minimizeList(newResult);
	}

	public void pasteDead() {
		deadStack = mcm.getErrorTransition();
		pasteMultiDeadWithStack();
	}

	public void pasteMultiDeadWithStack() {
		while (!deadStack.isEmpty()) {
			MultiConcurrentTransition tmp = deadStack.remove();
			MultiConcurrentState s = (MultiConcurrentState) tmp.getFrom();
			/*
			 * System.out.println("pasteMultiDeadWithStack");
			 * System.out.println("target:"+s+"deadList"+s.getDeadList());
			 * //debug for(int i=0;i<s.getToTransitionNum();i++) //debug
			 * System.out
			 * .println("  "+s.getToTransition(i)+((MultiConcurrentState
			 * )s.getToTransition(i).getTo()).getDeadList()); //debug
			 */
			if (stateDeadCheckForAllReq(s)) {
				for (int i = 0; i < s.getFromTransitionNum(); i++) {
					MultiConcurrentTransition t = (MultiConcurrentTransition) s
							.getFromTransition(i);
					if (!deadStack.contains(t))
						deadStack.add(t);
				}
			}
			// System.out.println(deadStack.size());
		}
	}

	boolean stateDeadCheckForAllReq(MultiConcurrentState m) {

		/*
		 * 引数ノードがLosing regionに属するか否かを判定し、属する場合はそのLosing regionを特定する。 特定したLosing
		 * regionは引数ノードに保持される。 mが以下のどちらかの条件を満たす場合、Losing regionに属する。 また、Losing
		 * regionの特定方法は満たす条件に応じて異なるのでそれも併記する。
		 * ただし、以下の"AND"と"OR"は下記"Losing region"の管理方法の記載に沿うものとする 条件1（以下AND条件）:
		 * 遷移先のノードがLosing regionに属するようなUncontrollableな遷移が１つ以上存在する場合 特定方法：
		 * mから出ているUncontrollableな遷移の先のノードが属するLosing regionを全て抽出し
		 * それらをANDによって繋げた式がmのLosing regionとなる 条件2（以下OR条件）：
		 * AND条件を満たさず、かつ全ての遷移において、遷移先のノードがLosing regionに属するような場合 特定方法：
		 * mから出ている全て遷移の先のノードが属するLosing regionを抽出し それらをORによって繋げた式がmのLosing
		 * regionとなる
		 *
		 * 上記の条件1,条件2に合致しないものはいずれのLosing regionにも含まれない。 この場合も、結果がmに保持される。
		 */

		/*
		 * Losing regionの管理方法 a.Losing regionはANDとORを用いた論理式によって表現可能である。
		 * a-0.論理式を構成する論理要素p1はその要素に対応するあるsafety propertyが保証出来なくなったことを表現する。
		 * a-1.AND（&&）はある2つのsafety property p1とp2が同時に保証出来なくなるようなLosing
		 * regionを表現するのに用いる "p1 && p2" と表現されるなら、そのLosing
		 * regionはp1とp2のどちらも保証不可能である a-2.OR(||)はある2つのsafety property
		 * p1とp2のどちらか一方が保証出来ないようなLosing regionを表現するのに用いる
		 * "p1 || p2"と表現されるなら、そのLosing regionは一方の保証を諦めなければ他方の保証が出来ない状態である。
		 * b.一般的な論理式の変形が可能なため、複雑なLosing regionの式は
		 * 次の例のようにANDのみで構成した式をORで繋げた形に変形する 例1: (p1 || p2)&&(p3 || p4)=(p1 &&
		 * p3)||(p1 && p4)||(p2 && p3)||(p2 && p4) 例2: p1 &&(p2 || p3)=(p1 &&
		 * p2)||(p1 || p3) 例3:　(p1 || p2) && p3 &&(p3 || p2) = (p1 && p3 &&
		 * p3)||(p1 && p2 && p3)||(p2 && p3 && p3)||(p2 && p2&& p3) =(p1 &&
		 * p3)||(p2 && p3) ※計算量を削減するため、必要に応じて例3のように式の簡単化を図る
		 * c.上記a.b.を実装するに当たってはint型配列を要素に持つリストで表される c-0.どのsafety
		 * propertyが保証出来なくなったのかはbit列によって表現される。
		 * ※"int"としているが、実態はbit列である。操作もbit列に対して行われる。int型である事自体に意味はない。
		 * ※配列にしたのはint型が32bitであるため、33個以上のsafety propertyを扱うための措置である。
		 * c-1.int型配列はANDのみで構成した式を表している。 例えばp1-p6までの6個のsafety
		 * propertyを扱うシステムにおいて p1とp3が保証出来ない(すなわち、p1 && p3である)ようなLosing
		 * regionは"000101"と表現される。 （int型として扱っているので数値としては"5"と表示される）
		 * c-2.int型配列の要素をリストとして格納する事でその要素間をORで繋げていることを意味している。
		 * 例えばp1-p6までの6個のsafety propertyを扱うシステムにおいて (p1 && p3)||(p2 && p5 &&
		 * p6)というLosing regionは "000101"と"110010"という2つの要素を持つリストによって表される。
		 * このようにすることで、Losing regionがどのようなsafety propertyに起因して構築しているか （そのLosing
		 * region上ではどの組み合わせでsafety propertyの保証を諦めれば良いのか）
		 * が容易にわかる。例の場合はp1,p3の2つを諦めるか、p2,p5,p6の3つを諦めるかの二択となる。
		 */

		// AND条件が成立した場合に、Losing regionの特定に利用するLosing regionを保持するためのリスト
		List<List<List<Integer>>> andDeadLists = new ArrayList<List<List<Integer>>>();

		// OR条件が成立した場合に、Losing regionの特定に利用するLosing regionを保持するためのリスト
		List<List<Integer>> orDeadExp = new ArrayList<List<Integer>>();

		// ノードがAND条件を満たすか、OR条件を満たすかを判断するためのbool変数
		boolean andDead = false, orDead = true;
		// ノードが持つ遷移を先頭から処理するための前処理
		m.reset();

		// ノードが持つ遷移を順にチェックし、AND条件、またはOR条件を満たすかをチェックする
		while (m.hasNext()) {
			// チェックする遷移をmctとする。
			MultiConcurrentTransition mct = (MultiConcurrentTransition) m
					.next();

			// mがAND条件を満たすかをmctを用いて判断する
			// このif文が一度でもtrueになればmはAND条件を満たすことが確定する。
			if ((((MultiConcurrentState) mct.getTo()).isDead() || mct.isDead())
					&& !mct.isControllable()) {
				/*
				 * 以下、mのLosing regionを特定するためのLosing regionを保存するための作業
				 * 扱うモデルではノードとは独立して遷移がLosing regionに属する場合も存在するmctの遷移先がLosing
				 * regionに属しているかmct自体がLosing regionに属しているか
				 * あるいはそのどちらもかによって、微妙に処理を分けているが、あくまで下準備である。また、既に他の遷移先で特定したLosing
				 * regionと重なりがあるようなLosing regionはこの時点で省かれる（Losing regionの管理方法
				 * b.で触れた、式の簡単化を行っている）
				 */
				List<List<Integer>> l = ((MultiConcurrentState) mct.getTo())
						.getDeadList();
				if (l != null && !l.isEmpty()) {
					if (mct.getDead() != null) {
						List<List<Integer>> ll = new ArrayList<List<Integer>>();
						for (int i = 0; i < l.size(); i++) {
							List<Integer> tmp = new ArrayList<Integer>();
							for (int j = 0; j < l.get(i).size(); j++)
								tmp.add((int) l.get(i).get(j)
										| mct.getDead().get(j));
							boolean contains = true;
							for (int j = 0; j < ll.size(); j++) {
								if (!checkContains(ll.get(j), tmp))
									contains = false;
							}
							if (!contains || ll.isEmpty())
								ll.add(tmp);
						}
						andDeadLists.add(ll);

					} else {
						List<List<Integer>> ll = new ArrayList<List<Integer>>();
						for (int i = 0; i < l.size(); i++) {
							List<Integer> tmp = new ArrayList<Integer>();
							for (int j = 0; j < l.get(i).size(); j++) {
								tmp.add((int) l.get(i).get(j));
							}
							ll.add(tmp);
						}
						andDeadLists.add(ll);
					}
				} else {
					List<List<Integer>> in = new ArrayList<List<Integer>>();
					List<Integer> tmp = new ArrayList<Integer>();
					for (int i = 0; i < mct.getDead().size(); i++)
						tmp.add((int) mct.getDead().get(i));
					in.add(tmp);
					andDeadLists.add(in);
				}
				andDead = true;
				orDead = false;
				// mがOR条件を満たすかをmctを用いて判断する
				// 本whileの全てのループにおいてこのif文がtrueとなった場合のみ、OR条件が満たされる。
				// 逆に言えば、一度でも満たされなければOR条件が満たされることはない
			} else if (orDead
					&& (((MultiConcurrentState) mct.getTo()).isDead() || mct
							.isDead()) && mct.isControllable()) {
				/*
				 * 以下、mのLosing regionを特定するためのLosing regionを保存するための作業
				 * ANDの場合と説明は同じである。
				 */
				List<List<Integer>> l = ((MultiConcurrentState) mct.getTo())
						.getDeadList();
				List<List<Integer>> newl = new ArrayList<List<Integer>>();
				if (mct.getDead() != null) {
					if (l == null || l.isEmpty()) {
						List<Integer> tmp = new ArrayList<Integer>();
						for (int j = 0; j < mct.getDead().size(); j++) {
							tmp.add((int) mct.getDead().get(j));
						}
						newl.add(tmp);
					} else
						for (int i = 0; i < l.size(); i++) {
							List<Integer> tmp = new ArrayList<Integer>();
							// System.out.println(l.get(i)); //debug
							for (int j = 0; j < mct.getDead().size(); j++) {
								tmp.add((int) mct.getDead().get(j)
										| l.get(i).get(j));
							}
							newl.add(tmp);
						}
				} else {
					for (int i = 0; i < l.size(); i++) {
						List<Integer> tmp = new ArrayList<Integer>();
						for (int j = 0; j < l.get(i).size(); j++) {
							tmp.add((int) l.get(i).get(j));
						}
						newl.add(tmp);
					}
				}
				orDeadExp.addAll(newl);
				orDeadExp = minimizeList(orDeadExp);
				// mがAND条件もOR条件も満たさないかをmctを用いて判断する
				// このelse文を一度でも通ればOR条件を満たす事はないが
				// AND条件を判断する分岐を一度でも通ってしまった場合、AND条件が満たされたものとなる
			} else {
				orDead = false;
			}
		}
		m.reset();

		// 以下、OR条件が満たされた場合、AND条件が満たされた場合、いずれの条件も満たされなかった場合の
		// Losing regionの更新が行われる。
		/*
		 * OR条件の場合は、下準備で集めたLosing regionをORで繋げる、
		 * すなわち、リストの要素に入れるだけなので、そのまま入れ替える形となる。
		 */
		if (orDead) {
			// System.out.println("stateDeadCheckForAllReq:orDead");
			return m.replaceDeadList(orDeadExp);
		}
		/*
		 * AND条件の場合は、下準備で集めたLosing regionをANDで繋げる、単純にANDで繋げた場合、管理方法の方針と異なってしまうので
		 * 式変形に相当する処理がここで行われ、ORで繋がれた形で表現されなおす。
		 */
		else if (andDead) {
			List<List<Integer>> newl = new ArrayList<List<Integer>>();
			for (int i = 0; i < andDeadLists.size(); i++) {
				newl = composeList(newl, andDeadLists.get(i));
			}
			// System.out.println("stateDeadCheckForAllReq:andDead");
			return m.replaceDeadList(newl);
		}
		/*
		 * OR条件もAND条件も満たさない場合はLosing regionを初期化する。
		 */
		else {
			// System.out.println("stateDeadCheckForAllReq:else");
			return m.replaceDeadList(new ArrayList<List<Integer>>());
		}
	}

	List<List<Integer>> composeList(List<List<Integer>> a, List<List<Integer>> b) {
		if (a == null || a.isEmpty()) {
			b = minimizeList(b);

			return b;
		} else if (b == null || b.isEmpty()) {
			a = minimizeList(a);
			return a;
		}
		List<List<Integer>> ab = new ArrayList<List<Integer>>();
		for (int i = 0; i < a.size(); i++)
			for (int j = 0; j < b.size(); j++) {
				List<Integer> tmpa = new ArrayList<Integer>();
				for (int k = 0; k < a.get(i).size(); k++) {
					tmpa.add((int) a.get(i).get(k) | b.get(j).get(k));
				}
				ab.add(tmpa);
			}
		ab = minimizeList(ab);

		return ab;
	}

	void pasteController(ModelInterface controller) {
		for (int i = 0; i < controller.getSize(); i++)
			controller.getState(i).reset();
		pasteCToStateWithHash(controller.getInitialState(),
				mcm.getInitialState());
	}

	// ハッシュマップを用いたコントローラのシミュレーション判定
	// コントローラのモデルの全状態と全遷移に対応する並列合成モデルの状態と遷移を特定し、それらにマークをしていくメソッド
	// コントローラの状態遷移によって並列合成モデル上でERRORに到達した場合はその時点で警告して特定を打ち切る
	void pasteCToStateWithHash(State c, State m) {
		HashMap<State, ContSet> map = new HashMap<State, ContSet>();
		c.setIsController();
		m.setIsController();
		map.put(c, new ContSet(c, m));
		int i=0;
		while (!map.isEmpty()) {
			ContSet current=map.remove(map.keySet().toArray()[0]);
			while (current.getController().hasNext()) {
				Transition t = (Transition) current.getController().next();
				//System.out.println("pasteCToStateWithHash"+t);//debug
				if (current.getModel().getToTransition(t.toString()) != null/*&&!current.getController().getToTransition(t.toString()).getTo().isController()*/) {i++;
					ContSet next= new ContSet(t.getTo(), current.getModel()
							.getToStateByTransition(t.toString()));
					next.getController().setIsController();
					next.getModel().setIsController();
					if(next.getController().hasNext()&&!map.containsValue(next.getController()))
							map.put(next.getController(), next);
				}
			}
			//System.out.println("pasteCToStateWithHash"+current.getModel() + "," + current.getController());
		}
		//System.out.println("pasteCToStateWithHashMap:"+i);
	}

	// コントローラのシミュレーションチェックのための情報を保持するためのクラス
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

	/* ここで使っているupdated partの中身が不十分。本当はMultiSystemModelMaker内部でcomposeされて出来た状態も必要 */
	void pasteUpdatedDead() {
		List<Transition> l = mcm.getUpdatedPart();
		// System.out.println("pasteUpdatedDead");//debug
		if (l.size() == 0) {
			return;
		}
		for (int i = 0; i < l.size(); i++) {

			Transition t = l.get(i);
			// System.out.println(" "+t.getFrom()+":"+((MultiConcurrentState)t.getFrom()).getDeadList()+"->"+t+"->"+t.getTo()+":"+((MultiConcurrentState)t.getTo()).getDeadList());//debug*/

			if (((MultiConcurrentState) l.get(i).getTo()).isDead()
					|| ((MultiConcurrentTransition) l.get(i)).getDead() != null
					|| (!((MultiConcurrentState) l.get(i).getTo()).isDead() && ((MultiConcurrentState) l
							.get(i).getFrom()).isDead())) {
				deadStack.add((MultiConcurrentTransition) l.get(i));
			} else {
				// System.out.println("This updated part is not need to be analyzed");
			}
		}
		pasteMultiDeadWithStack();
	}

	boolean checkContains(List<Integer> container, List<Integer> containee) {
		for (int i = 0; i < container.size(); i++) {
			// System.out.println(container+","+containee); //debug
			if ((container.get(i) | containee.get(i)) != containee.get(i)) {
				return false;
			}
		}
		return true;
	}

	List<List<Integer>> minimizeList(List<List<Integer>> a) {
		List<List<Integer>> ab = new ArrayList<List<Integer>>();
		for (int i = 0; i < a.size(); i++) {
			List<Integer> tmp = new ArrayList<Integer>();
			for (int j = 0; j < a.get(i).size(); j++)
				tmp.add((int) a.get(i).get(j));
			ab.add(tmp);
		}
		for (int i = 0; i < ab.size(); i++) {
			for (int j = i + 1; j < ab.size(); j++)
				if (checkContains(ab.get(i), ab.get(j))) {
					ab.remove(j);
					j--;
				} else if (checkContains(ab.get(j), ab.get(i))) {
					ab.remove(i);
					j = i;
				}
		}
		return ab;

	}
}