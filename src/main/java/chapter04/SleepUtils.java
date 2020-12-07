package chapter04;

import java.util.concurrent.TimeUnit;

/**
 * @author: payn
 * @date: 2020/12/3 20:01
 */
public class SleepUtils {
	
	public static final void second(long seconds){
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
