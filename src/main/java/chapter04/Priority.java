package chapter04;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: payn
 * @date: 2020/12/3 16:32
 */
public class Priority {

	private static volatile boolean notStart = true;
	private static volatile boolean notEnd = true;

	public static void main(String[] args) throws InterruptedException {
		List<Job> jobs = new ArrayList<Job>();
		for (int i = 0; i < 10; i++) {
			int priority = i < 5 ? Thread.MIN_PRIORITY : Thread.MAX_PRIORITY;
			Job job = new Job(priority);
			jobs.add(job);
			Thread thread = new Thread(job, "Thread " + i);
			thread.setPriority(priority);
			thread.start();
		}
		notStart = false;
		TimeUnit.SECONDS.sleep(10);
		notEnd = false;
		for (Job job : jobs) {
			System.out.println("Job Priority : " + job.priority + ",Count : " + job.jobCount);
		}
	}

	private static class Job implements Runnable {
		private int priority;
		private long jobCount;

		public Job(int priority) {
			this.priority = priority;
		}

		public void run() {
			while (notStart) {
				Thread.yield();
			}
			while (notEnd) {
				Thread.yield();
				jobCount++;
			}
		}
	}
	
	/*
	Job Priority : 1,Count : 394617
	Job Priority : 1,Count : 394602
	Job Priority : 1,Count : 394561
	Job Priority : 1,Count : 394661
	Job Priority : 1,Count : 394739
	Job Priority : 10,Count : 3308522
	Job Priority : 10,Count : 3309240
	Job Priority : 10,Count : 3288342
	Job Priority : 10,Count : 3297855
	Job Priority : 10,Count : 3308054
	
	从输出可以看到线程优先级没有生效，优先级1和优先级10的Job计数的结果非常相近，
	没有明显差距。这表示程序正确性不能依赖线程的优先级高低。
	* */
}
