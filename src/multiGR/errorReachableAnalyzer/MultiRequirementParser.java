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
		// �e�X�g�p�ɍ�����R���X�g���N�^
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
			 * isTarget=false;break; } }//�����͍��͑ʖځBDeadEnd���������I�ɉ���������
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
		 * �����m�[�h��Losing region�ɑ����邩�ۂ��𔻒肵�A������ꍇ�͂���Losing region����肷��B ���肵��Losing
		 * region�͈����m�[�h�ɕێ������B m���ȉ��̂ǂ��炩�̏����𖞂����ꍇ�ALosing region�ɑ�����B �܂��ALosing
		 * region�̓�����@�͖����������ɉ����ĈقȂ�̂ł�������L����B
		 * �������A�ȉ���"AND"��"OR"�͉��L"Losing region"�̊Ǘ����@�̋L�ڂɉ������̂Ƃ��� ����1�i�ȉ�AND�����j:
		 * �J�ڐ�̃m�[�h��Losing region�ɑ�����悤��Uncontrollable�ȑJ�ڂ��P�ȏ㑶�݂���ꍇ ������@�F
		 * m����o�Ă���Uncontrollable�ȑJ�ڂ̐�̃m�[�h��������Losing region��S�Ē��o��
		 * ������AND�ɂ���Čq��������m��Losing region�ƂȂ� ����2�i�ȉ�OR�����j�F
		 * AND�����𖞂������A���S�Ă̑J�ڂɂ����āA�J�ڐ�̃m�[�h��Losing region�ɑ�����悤�ȏꍇ ������@�F
		 * m����o�Ă���S�đJ�ڂ̐�̃m�[�h��������Losing region�𒊏o�� ������OR�ɂ���Čq��������m��Losing
		 * region�ƂȂ�
		 *
		 * ��L�̏���1,����2�ɍ��v���Ȃ����̂͂������Losing region�ɂ��܂܂�Ȃ��B ���̏ꍇ���A���ʂ�m�ɕێ������B
		 */

		/*
		 * Losing region�̊Ǘ����@ a.Losing region��AND��OR��p�����_�����ɂ���ĕ\���\�ł���B
		 * a-0.�_�������\������_���v�fp1�͂��̗v�f�ɑΉ����邠��safety property���ۏ؏o���Ȃ��Ȃ������Ƃ�\������B
		 * a-1.AND�i&&�j�͂���2��safety property p1��p2�������ɕۏ؏o���Ȃ��Ȃ�悤��Losing
		 * region��\������̂ɗp���� "p1 && p2" �ƕ\�������Ȃ�A����Losing
		 * region��p1��p2�̂ǂ�����ۏؕs�\�ł��� a-2.OR(||)�͂���2��safety property
		 * p1��p2�̂ǂ��炩������ۏ؏o���Ȃ��悤��Losing region��\������̂ɗp����
		 * "p1 || p2"�ƕ\�������Ȃ�A����Losing region�͈���̕ۏ؂���߂Ȃ���Α����̕ۏ؂��o���Ȃ���Ԃł���B
		 * b.��ʓI�Ș_�����̕ό`���\�Ȃ��߁A���G��Losing region�̎���
		 * ���̗�̂悤��AND�݂̂ō\����������OR�Ōq�����`�ɕό`���� ��1: (p1 || p2)&&(p3 || p4)=(p1 &&
		 * p3)||(p1 && p4)||(p2 && p3)||(p2 && p4) ��2: p1 &&(p2 || p3)=(p1 &&
		 * p2)||(p1 || p3) ��3:�@(p1 || p2) && p3 &&(p3 || p2) = (p1 && p3 &&
		 * p3)||(p1 && p2 && p3)||(p2 && p3 && p3)||(p2 && p2&& p3) =(p1 &&
		 * p3)||(p2 && p3) ���v�Z�ʂ��팸���邽�߁A�K�v�ɉ����ė�3�̂悤�Ɏ��̊ȒP����}��
		 * c.��La.b.����������ɓ������Ă�int�^�z���v�f�Ɏ����X�g�ŕ\����� c-0.�ǂ�safety
		 * property���ۏ؏o���Ȃ��Ȃ����̂���bit��ɂ���ĕ\�������B
		 * ��"int"�Ƃ��Ă��邪�A���Ԃ�bit��ł���B�����bit��ɑ΂��čs����Bint�^�ł��鎖���̂ɈӖ��͂Ȃ��B
		 * ���z��ɂ����̂�int�^��32bit�ł��邽�߁A33�ȏ��safety property���������߂̑[�u�ł���B
		 * c-1.int�^�z���AND�݂̂ō\����������\���Ă���B �Ⴆ��p1-p6�܂ł�6��safety
		 * property�������V�X�e���ɂ����� p1��p3���ۏ؏o���Ȃ�(���Ȃ킿�Ap1 && p3�ł���)�悤��Losing
		 * region��"000101"�ƕ\�������B �iint�^�Ƃ��Ĉ����Ă���̂Ő��l�Ƃ��Ă�"5"�ƕ\�������j
		 * c-2.int�^�z��̗v�f�����X�g�Ƃ��Ċi�[���鎖�ł��̗v�f�Ԃ�OR�Ōq���Ă��邱�Ƃ��Ӗ����Ă���B
		 * �Ⴆ��p1-p6�܂ł�6��safety property�������V�X�e���ɂ����� (p1 && p3)||(p2 && p5 &&
		 * p6)�Ƃ���Losing region�� "000101"��"110010"�Ƃ���2�̗v�f�������X�g�ɂ���ĕ\�����B
		 * ���̂悤�ɂ��邱�ƂŁALosing region���ǂ̂悤��safety property�ɋN�����č\�z���Ă��邩 �i����Losing
		 * region��ł͂ǂ̑g�ݍ��킹��safety property�̕ۏ؂���߂�Ηǂ��̂��j
		 * ���e�Ղɂ킩��B��̏ꍇ��p1,p3��2����߂邩�Ap2,p5,p6��3����߂邩�̓���ƂȂ�B
		 */

		// AND���������������ꍇ�ɁALosing region�̓���ɗ��p����Losing region��ێ����邽�߂̃��X�g
		List<List<List<Integer>>> andDeadLists = new ArrayList<List<List<Integer>>>();

		// OR���������������ꍇ�ɁALosing region�̓���ɗ��p����Losing region��ێ����邽�߂̃��X�g
		List<List<Integer>> orDeadExp = new ArrayList<List<Integer>>();

		// �m�[�h��AND�����𖞂������AOR�����𖞂������𔻒f���邽�߂�bool�ϐ�
		boolean andDead = false, orDead = true;
		// �m�[�h�����J�ڂ�擪���珈�����邽�߂̑O����
		m.reset();

		// �m�[�h�����J�ڂ����Ƀ`�F�b�N���AAND�����A�܂���OR�����𖞂��������`�F�b�N����
		while (m.hasNext()) {
			// �`�F�b�N����J�ڂ�mct�Ƃ���B
			MultiConcurrentTransition mct = (MultiConcurrentTransition) m
					.next();

			// m��AND�����𖞂�������mct��p���Ĕ��f����
			// ����if������x�ł�true�ɂȂ��m��AND�����𖞂������Ƃ��m�肷��B
			if ((((MultiConcurrentState) mct.getTo()).isDead() || mct.isDead())
					&& !mct.isControllable()) {
				/*
				 * �ȉ��Am��Losing region����肷�邽�߂�Losing region��ۑ����邽�߂̍��
				 * �������f���ł̓m�[�h�Ƃ͓Ɨ����đJ�ڂ�Losing region�ɑ�����ꍇ�����݂���mct�̑J�ڐ悪Losing
				 * region�ɑ����Ă��邩mct���̂�Losing region�ɑ����Ă��邩
				 * ���邢�͂��̂ǂ�������ɂ���āA�����ɏ����𕪂��Ă��邪�A�����܂ŉ������ł���B�܂��A���ɑ��̑J�ڐ�œ��肵��Losing
				 * region�Əd�Ȃ肪����悤��Losing region�͂��̎��_�ŏȂ����iLosing region�̊Ǘ����@
				 * b.�ŐG�ꂽ�A���̊ȒP�����s���Ă���j
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
				// m��OR�����𖞂�������mct��p���Ĕ��f����
				// �{while�̑S�Ẵ��[�v�ɂ����Ă���if����true�ƂȂ����ꍇ�̂݁AOR���������������B
				// �t�Ɍ����΁A��x�ł���������Ȃ����OR��������������邱�Ƃ͂Ȃ�
			} else if (orDead
					&& (((MultiConcurrentState) mct.getTo()).isDead() || mct
							.isDead()) && mct.isControllable()) {
				/*
				 * �ȉ��Am��Losing region����肷�邽�߂�Losing region��ۑ����邽�߂̍��
				 * AND�̏ꍇ�Ɛ����͓����ł���B
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
				// m��AND������OR�������������Ȃ�����mct��p���Ĕ��f����
				// ����else������x�ł��ʂ��OR�����𖞂������͂Ȃ���
				// AND�����𔻒f���镪�����x�ł��ʂ��Ă��܂����ꍇ�AAND�������������ꂽ���̂ƂȂ�
			} else {
				orDead = false;
			}
		}
		m.reset();

		// �ȉ��AOR�������������ꂽ�ꍇ�AAND�������������ꂽ�ꍇ�A������̏�������������Ȃ������ꍇ��
		// Losing region�̍X�V���s����B
		/*
		 * OR�����̏ꍇ�́A�������ŏW�߂�Losing region��OR�Ōq����A
		 * ���Ȃ킿�A���X�g�̗v�f�ɓ���邾���Ȃ̂ŁA���̂܂ܓ���ւ���`�ƂȂ�B
		 */
		if (orDead) {
			// System.out.println("stateDeadCheckForAllReq:orDead");
			return m.replaceDeadList(orDeadExp);
		}
		/*
		 * AND�����̏ꍇ�́A�������ŏW�߂�Losing region��AND�Ōq����A�P����AND�Ōq�����ꍇ�A�Ǘ����@�̕��j�ƈقȂ��Ă��܂��̂�
		 * ���ό`�ɑ������鏈���������ōs���AOR�Ōq���ꂽ�`�ŕ\������Ȃ����B
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
		 * OR������AND�������������Ȃ��ꍇ��Losing region������������B
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

	// �n�b�V���}�b�v��p�����R���g���[���̃V�~�����[�V��������
	// �R���g���[���̃��f���̑S��ԂƑS�J�ڂɑΉ�������񍇐����f���̏�ԂƑJ�ڂ���肵�A�����Ƀ}�[�N�����Ă������\�b�h
	// �R���g���[���̏�ԑJ�ڂɂ���ĕ��񍇐����f�����ERROR�ɓ��B�����ꍇ�͂��̎��_�Ōx�����ē����ł��؂�
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

	// �R���g���[���̃V�~�����[�V�����`�F�b�N�̂��߂̏���ێ����邽�߂̃N���X
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

	/* �����Ŏg���Ă���updated part�̒��g���s�\���B�{����MultiSystemModelMaker������compose����ďo������Ԃ��K�v */
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