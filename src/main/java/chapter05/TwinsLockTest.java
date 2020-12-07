package chapter05;

import chapter04.SleepUtils;
import javafx.concurrent.Worker;

import java.util.concurrent.locks.Lock;

/**
 * @author: payn
 * @date: 2020/12/7 10:32
 */
public class TwinsLockTest {

	public static void main(String[] args) {
		final Lock lock = new TwinsLock();

		class Worker extends Thread {
			@Override
			public void run() {
				while (true) {
					lock.lock();
					try {
						SleepUtils.second(1);
						System.out.println(Thread.currentThread().getName());
						SleepUtils.second(1);
					} finally {
						lock.unlock();
					}

				}
			}
		}

		for(int i=0;i<10;i++){
			Worker worker=new Worker();
			worker.setDaemon(true);
			worker.start();
		}

		for (int i = 0; i < 10; i++) {
			SleepUtils.second(1);
			System.out.println("---");
		}
	}
}
