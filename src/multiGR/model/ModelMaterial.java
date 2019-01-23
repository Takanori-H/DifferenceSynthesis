package multiGR.model;

public abstract class ModelMaterial {
	private boolean isController;//Controllerか否か。
	protected boolean isDead;//環境側のwinning regionに含まれているか否か。 trueなら含まれている falseなら含まれていない
	//protected boolean containNWS;//今動いているコントローラのwinning strategyに含まれるstateかどうか
	protected String name;//StateやTransitionを識別するため変数

	protected ModelMaterial(String name){
		this.name=name;
		isController=false;
		isDead=false;
		//containNWS=false;
	}

	@Override
	public String toString(){//nameはもともとstring型なのに何でこんなもの必要なんだ？
		return name;
	}

	public boolean isDead(){//環境側のwinning regionに含まれているかいないかを返す
		return isDead;
	}
	public boolean isController(){//Controllerかどうかを返す
		return isController;
	}

	public void setIsDead(){//セット
		isDead=true;
	}
	public void setIsController(){
		isController=true;
	}

	public void eraseIsDead(){
		isDead=false;
	}
	public void eraseIsController(){
		isController=false;
	}

	public String getName(){
		return name;
	}

	abstract boolean hasNext();//最後の要素に到達していたらfalse, それ以外はtrueを返す
	abstract ModelMaterial next();//次の要素を取得する
	abstract void reset();
}
