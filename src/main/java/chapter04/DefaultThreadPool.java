package chapter04;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: payn
 * @date: 2020/12/4 15:16
 */
public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {

	private static final int MAX_WORKER_NUMBERS = 10;
	private static final int DEFAULT_WORKER_NUMBERS = 5;
	private static final int MIN_WORKER_NUMBERS = 1;

	private final LinkedList<Job> jobs = new LinkedList<Job>();
	private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());

	private int workerNum = DEFAULT_WORKER_NUMBERS;
	private AtomicLong threadNum = new AtomicLong();

	public DefaultThreadPool() {
		initializeWorkers(DEFAULT_WORKER_NUMBERS);
	}

	public DefaultThreadPool(int workerNum) {
		this.workerNum = workerNum > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : workerNum < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : workerNum;
		initializeWorkers(workerNum);
	}

	private void initializeWorkers(int defaultWorkerNumbers) {
		for (int i = 0; i < defaultWorkerNumbers; i++) {
			Worker worker = new Worker();
			workers.add(worker);
			Thread thread = new Thread(worker, "ThreadPool-worker-" + threadNum.incrementAndGet());
			thread.start();
		}
	}

	public void execute(Job job) {
		if (job != null) {
			synchronized (jobs) {
				jobs.addLast(job);
				jobs.notify();
			}
		}
	}

	public void shutDown() {
		for (Worker worker : workers) {
			worker.shutDown();
		}
	}

	public void addWorkers(int num) {
		synchronized (jobs) {
			if (num + this.workerNum > MAX_WORKER_NUMBERS) {
				num = MAX_WORKER_NUMBERS - this.workerNum;
			}
			initializeWorkers(num);
			this.workerNum += num;
		}
	}

	public void removeWorker(int num) {
		synchronized (jobs) {
			if (num >= this.workerNum) {
				throw new IllegalArgumentException("beyond workNum");
			}
			int count = 0;
			while (count < num) {
				Worker worker = workers.get(count);
				if (workers.remove(worker)) {
					worker.shutDown();
					count++;
				}
			}
			this.workerNum -= count;
		}
	}

	public int getJobSize() {
		return jobs.size();
	}

	class Worker implements Runnable {
		private volatile boolean isRunning = false;

		public void run() {
			while (isRunning) {
				Job job = null;
				synchronized (jobs) {
					while (jobs.isEmpty()) {
						try {
							job.wait();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							return;
						}
					}
					job = jobs.removeFirst();
				}
				if (job != null) {
					try {
						job.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void shutDown() {
			isRunning = false;
		}
	}

}
