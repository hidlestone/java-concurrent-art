package chapter04;

/**
 * @author: payn
 * @date: 2020/12/3 20:00
 */
public class ThreadState {

	/*
	E:\CODESPACE\PhoenixPlan\java-concurrent-art>Jps
	9456 Launcher
	12344 RemoteMavenServer
	12284 ThreadState
	19436 Jps
	7820
	
	jstack 12284
	"DestroyJavaVM" #17 prio=5 os_prio=0 tid=0x0000000002993800 nid=0x12b4 waiting on condition [0x0000000000000000]
		java.lang.Thread.State: RUNNABLE
	"Blocked-Thread02" #16 prio=5 os_prio=0 tid=0x000000001d533000 nid=0x1a78 waiting for monitor entry [0x000000001e59f000]
  		java.lang.Thread.State: BLOCKED (on object monitor)	
	"Blocked-Thread01" #15 prio=5 os_prio=0 tid=0x000000001d532000 nid=0x37d0 waiting on condition [0x000000001e49e000]
   		java.lang.Thread.State: TIMED_WAITING (sleeping)
	"Waiting-Thread" #14 prio=5 os_prio=0 tid=0x000000001d518000 nid=0x2b5c in Object.wait() [0x000000001e39e000]
   		java.lang.Thread.State: WAITING (on object monitor)
	"TimeWaiting-Thread" #13 prio=5 os_prio=0 tid=0x000000001d517000 nid=0x5fc waiting on condition [0x000000001e29f000]
   		java.lang.Thread.State: TIMED_WAITING (sleeping)
	* */
	
	public static void main(String[] args) {
		new Thread(new TimeWaiting(), "TimeWaiting-Thread").start();
		new Thread(new Waiting(), "Waiting-Thread").start();
		//使用两个Blocked线程，一个获取锁成功，一个被阻塞
		new Thread(new Blocked(),"Blocked-Thread01").start();
		new Thread(new Blocked(),"Blocked-Thread02").start();
	}

	//该线程不断进行睡眠
	static class TimeWaiting implements Runnable {
		public void run() {
			while (true) {
				SleepUtils.second(100);
			}
		}
	}

	//该线程在Waiting.class实例上等待
	static class Waiting implements Runnable {
		public void run() {
			while (true) {
				synchronized (Waiting.class) {
					try {
						Waiting.class.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	//该线程在Blocked.class实力上加锁后，不会释放该锁
	static class Blocked implements Runnable {
		public void run() {
			synchronized (Blocked.class) {
				while (true) {
					SleepUtils.second(100);
				}
			}
		}
	}

}
