package chapter06;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * @author: payn
 * @date: 2020/12/7 20:40
 */
public class CountTask extends RecursiveTask<Integer> { 
	
	/*
	ForkJoinTask，ForkJoinTask与一般任务的主要区别在于它 需要实现compute方法，在这个方法里，
	首先需要判断任务是否足够小，如果足够小就直接执 行任务。如果不足够小，就必须分割成两个子任务，
	每个子任务在调用fork方法时，又会进入 compute方法，看看当前子任务是否需要继续分割成子任务，
	如果不需要继续分割，则执行当 前子任务并返回结果。使用join方法会等待子任务执行完并得到其结果。
	* */
	private static final int THRESHOLD = 2;//阈值

	private int start;
	private int end;

	public CountTask(int start, int end) {
		this.start = start;
		this.end = end;
	}

	@Override
	protected Integer compute() {
		int sum=0;
		boolean canCompute=(end-start)<=THRESHOLD;
		if (canCompute) {
			for (int i=start;i<=end;i++){
				sum+=i;
			}
		}else {
			int middle=(start+end)/2;
			CountTask leftTask=new CountTask(start,middle);
			CountTask rightTask=new CountTask(middle+1,end);
			leftTask.fork();
			rightTask.fork();
			int leftResult=leftTask.join();
			int rightResult=rightTask.join();
			sum=leftResult+rightResult;
		}
		return sum;
	}

	public static void main(String[] args){
		ForkJoinPool forkJoinPool=new ForkJoinPool();
		CountTask countTask=new CountTask(1,4);
		Future<Integer> result=forkJoinPool.submit(countTask);
		try {
			System.out.println(result.get());
		}catch (Exception e){

		}
	}
	
}
