package chapter04;

import java.util.concurrent.TimeUnit;

/**
 * @author: payn
 * @date: 2020/12/4 9:00
 */
public class Shutdown {

	/*
	中，main线程通过中断操作和cancel()方法均可使CountThread得以终止。
	这种通过标识位或者中断操作的方式能够使线程在终止时有机会去清理资源，而不是武断地
	将线程停止，因此这种终止线程的做法显得更加安全和优雅。
	* */
	public static void main(String[] args) throws InterruptedException {
		Runner one = new Runner();
		Thread countThread = new Thread(one, "countThread");
		countThread.start();
		//睡眠
		TimeUnit.SECONDS.sleep(1);
		countThread.interrupt();
		Runner two = new Runner();
		countThread = new Thread(two, "countThread");
		countThread.start();
		TimeUnit.SECONDS.sleep(1);
		two.cancel();
	}

	private static class Runner implements Runnable {
		private long i;
		private volatile boolean on = true;

		public void run() {
			while (on && (!Thread.currentThread().isInterrupted())) {
				i++;
			}
			System.out.println("count i = " + i);
		}

		public void cancel() {
			on = false;
		}
	}
}
