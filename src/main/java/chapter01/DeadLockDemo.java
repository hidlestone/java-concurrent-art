package chapter01;

/**
 * @author: payn
 * @date: 2020/12/1 16:11
 */
public class DeadLockDemo {

	private static String A = "A";
	private static String B = "B";

	public static void main(String[] args) {
		DeadLockDemo.deadLock();
	}

	public static void deadLock() {
		Thread th1 = new Thread(new Runnable() {
			public void run() {
				synchronized (A) {
					try {
						Thread.currentThread().sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized (B) {
						System.out.println("---- 1 ---");
					}
				}
			}
		});

		Thread th2 = new Thread(new Runnable() {
			public void run() {
				synchronized (B) {
					synchronized (A) {
						System.out.println("--- 2 ---");
					}
				}
			}
		});
		th1.start();
		th2.start();
	}

}
