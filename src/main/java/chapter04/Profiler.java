package chapter04;

import java.util.concurrent.TimeUnit;

/**
 * @author: payn
 * @date: 2020/12/4 10:43
 */
public class Profiler {

	//第一次get()方法调用时会初始化（如果set方法没有调用）每个线程调用一次
	private static final ThreadLocal<Long> TIME_THREADLOCAL = new ThreadLocal<Long>() {
		@Override
		protected Long initialValue() {
			return System.currentTimeMillis();
		}
	};

	public static final void begin() {
		TIME_THREADLOCAL.set(System.currentTimeMillis());
	}

	public static final Long end() {
		return System.currentTimeMillis() - TIME_THREADLOCAL.get();
	}

	public static void main(String[] args) throws InterruptedException {
		Profiler.begin();
		TimeUnit.SECONDS.sleep(1);
		System.out.println("Cost " + Profiler.end());
	}

}
