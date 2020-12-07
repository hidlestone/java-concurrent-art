package chapter03;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: payn
 * @date: 2020/12/3 9:16
 */
public class ReentrantLockExample {

	int a = 0;
	ReentrantLock lock = new ReentrantLock();

	public void writer() {
		lock.lock();//获取锁
		try {
			a++;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();//释放锁
		}
	}

	public void reader() {
		lock.unlock();
		try {
			int i = a;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

}
