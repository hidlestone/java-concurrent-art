package chapter04;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author: payn
 * @date: 2020/12/4 9:34
 */
public class WaitNotify {

	/*
	Thread[waitThread,5,main] flag is true, wait @ 10:02:04
	Thread[notifyThread,5,main] hold lock, notify @ 10:02:05
	lock.wait();  after ---
	Thread[waitThread,5,main] flag is false, running @ 10:02:10
	Thread[notifyThread,5,main] hold lock again, sleep @ 10:02:10
	* */
	
	private static boolean flag = true;
	private static Object lock = new Object();

	public static void main(String[] args) throws InterruptedException {
		Thread waitThread = new Thread(new Wait(), "waitThread");
		waitThread.start();
		TimeUnit.SECONDS.sleep(1);
		Thread notifyThread = new Thread(new Notify(), "notifyThread");
		notifyThread.start();
	}

	static class Wait implements Runnable {
		public void run() {
			//加锁，拥有lock的Monitor
			synchronized (lock) {
				//当条件不满足时，继续wait，同时释放了lock的锁
				while (flag) {
					try {
						System.out.println(Thread.currentThread() + " flag is true, wait @ "
								+ new SimpleDateFormat("HH:mm:ss").format(new Date()));
						lock.wait();
						System.out.println("lock.wait();  after ---");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println(Thread.currentThread() + " flag is false, running @ "
						+ new SimpleDateFormat("HH:mm:ss").format(new Date()));
			}
		}
	}

	static class Notify implements Runnable {
		public void run() {
			//加锁
			synchronized (lock) {
				//获取锁，然后进行通知，通知时不会释放lock锁
				//直到当前线程释放lock后，WaitThread才能从wait方法中返回
				System.out.println(Thread.currentThread() + " hold lock, notify @ " +
						new SimpleDateFormat("HH:mm:ss").format(new Date()));
				lock.notifyAll();
				flag = false;
				SleepUtils.second(5);
			}
			synchronized (lock) {
				System.out.println(Thread.currentThread() + " hold lock again, sleep @ " +
						new SimpleDateFormat("HH:mm:ss").format(new Date()));
				SleepUtils.second(5);
			}
		}
	}

}
