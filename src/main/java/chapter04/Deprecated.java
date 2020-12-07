package chapter04;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author: payn
 * @date: 2020/12/4 8:44
 */
public class Deprecated {

	/*
	suspend()、resume()和stop()方法完成了线程的暂停、恢复和终止工作，
	而且非常“人性化”。但是这些API是过期的，也就是不建议使用的。

	* */
	public static void main(String[] args) throws InterruptedException {
		DateFormat format = new SimpleDateFormat("HH:mm:ss");
		Thread printThread = new Thread(new Runner(), "PrintThread");
		printThread.setDaemon(true);
		printThread.start();
		TimeUnit.SECONDS.sleep(3);
		//暂停
		printThread.suspend();
		System.out.println("main suspend PrintThread at " + format.format(new Date()));
		TimeUnit.SECONDS.sleep(3);
		//继续
		printThread.resume();
		System.out.println("main resume PrintThread at " + format.format(new Date()));
		TimeUnit.SECONDS.sleep(3);
		//停止
		printThread.stop();
		System.out.println("main stop PrintThread at " + format.format(new Date()));
		TimeUnit.SECONDS.sleep(3);
	}

	static class Runner implements Runnable {
		public void run() {
			DateFormat format = new SimpleDateFormat("HH:mm:ss");
			while (true) {
				System.out.println(Thread.currentThread().getName() +
						"run at " + format.format(new Date()));
				SleepUtils.second(1);
			}
		}
	}
}
