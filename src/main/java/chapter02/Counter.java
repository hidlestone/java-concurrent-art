package chapter02;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 以下代码实现了一个基于CAS线程安全的计数器 方法safeCount和一个非线程安全的计数器count。
 * AtomicInteger（用原子方式更新的int值）
 * 
 * @author: payn
 * @date: 2020/12/2 9:05
 */
public class Counter {

	private AtomicInteger atomicInteger = new AtomicInteger(0);
	private int i = 0;

	public void safeCount() {
		for (; ; ) {
			int i = atomicInteger.get();
			boolean suc = atomicInteger.compareAndSet(i, ++i);
			if (suc) {
				break;
			}
		}
	}

	private void count() {
		i++;
	}

	public static void main(String[] args) {
		final Counter cas = new Counter();
		List<Thread> ts = new ArrayList<Thread>(600);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					for (int j = 0; j < 10000; j++) {
						cas.count();
						cas.safeCount();
					}
				}
			});
			ts.add(t);
		}
		for (Thread t : ts) {
			t.start();
		}
		for (Thread t : ts) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(cas.i);
		System.out.println(cas.atomicInteger.get());
		System.out.println(System.currentTimeMillis() - start + "ms");
	}

}
