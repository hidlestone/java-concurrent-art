package chapter04;

/**
 * @author: payn
 * @date: 2020/12/3 21:22
 */
public class Daemon {

	/*
	在构建Daemon线程时，不能依靠finally块中的内容来确保执行关闭或清理资源
	的逻辑。
	* */

	public static void main(String[] args) {
		Thread thread = new Thread(new DaemonRunner(), "DaemonRunner");
		thread.setDaemon(true);
		thread.start();
	}

	static class DaemonRunner implements Runnable {
		public void run() {
			try {
				SleepUtils.second(1000);
			} catch (Exception e) {
				System.out.println("DaemonThread finally run.");
			}
		}
	}
}
