package chapter03;

/**
 * @author: payn
 * @date: 2020/12/2 20:25
 */
public class FinalExample {

	int i;
	final int j = 0;

	static FinalExample obj;

	public FinalExample() {
		i = 1;      //写普通域
//		j = 2;		//写final 域
	}

	public static void weiter() {//写线程A执行
		obj = new FinalExample();
	}

	public static void reader() {//读线程B执行
		FinalExample object = obj;//读对象引用
		int a = object.i;    //读普通域
		int b = object.j;    //读final域
	}
}
