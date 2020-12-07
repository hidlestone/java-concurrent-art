package chapter04;

import java.util.concurrent.TimeUnit;

/**
 * @author: payn
 * @date: 2020/12/3 21:50
 */
public class Interrupted {

	/*
	SleepThread interrupted is false 
	BusyThread interrupted is true
	
	抛出InterruptedException的线程SleepThread，其中断标识位被清除了， 
	而一直忙碌运作的线程BusyThread，中断标识位没有被清除。
	* */
	public static void main(String[] args) throws InterruptedException {
		//sleepThread 不停地尝试睡眠
		Thread sleepThread = new Thread(new SleepRunner(), "SleepRunner");
		sleepThread.setDaemon(true);
		//BusyThread 不断的运行
		Thread busyThread = new Thread(new Busyrunner(), "Busyrunner");
		busyThread.setDaemon(true);
		sleepThread.start();
		busyThread.start();
		//休眠5s，让sleepThread和busyThread充分运行
		TimeUnit.SECONDS.sleep(5);
		sleepThread.interrupt();
		busyThread.interrupt();
		System.out.println("SleepThread interrupted is " + sleepThread.isInterrupted());
		System.out.println("BusyThread interrupted is " + busyThread.isInterrupted());
		// 防止sleepThread和busyThread立刻退出
		SleepUtils.second(2);
	}

	static class SleepRunner implements Runnable {
		public void run() {
			while (true) {
				SleepUtils.second(10);
			}
		}
	}

	static class Busyrunner implements Runnable {
		public void run() {
			while (true) {
			}
		}
	}

}
