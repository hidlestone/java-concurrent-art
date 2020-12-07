package chapter04;

/**
 * @author: payn
 * @date: 2020/12/4 10:27
 */
public class Join {

	/*
	创建了10个线程，编号0~9，每个线程调用前一个线程的 join()方法，也就是线程0结束了，
	线程1才能从join()方法中返回，而线程0需要等待main线程结束。
	
	0 terminate.
	1 terminate.
	2 terminate.
	3 terminate.
	4 terminate.
	5 terminate.
	6 terminate.
	7 terminate.
	8 terminate.
	9 terminate.
	* */
	
	public static void main(String[] args) {
		Thread previous = Thread.currentThread();
		for (int i = 0; i < 10; i++) {
			//每个线程拥有前一个线程的引用，需要等待前一个线程终止，才能从等待中返回。
			Thread thread = new Thread(new Domino(previous), i + "");
			thread.start();
			previous = thread;
		}
	}

	static class Domino implements Runnable {
		private Thread thread;

		public Domino(Thread thread) {
			this.thread = thread;
		}

		public void run() {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + " terminate.");
		}
	}

}
