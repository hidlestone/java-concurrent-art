package chapter03;

/**
 * @author: payn
 * @date: 2020/12/2 21:36
 */
public class UnsafeLazyInitialzation {
	/*
	有时候可能需要推迟一些高开销的对象初始化操作，并且只有在使用这些
	对象时才进行初始化。此时，可能会采用延迟初始化。但要正确实现线程安全的延迟初
	始化需要一些技巧，否则很容易出现问题。
	在UnsafeLazyInitialization类中，假设A线程执行代码1的同时，B线程执行代码2。
	此时，线 程A可能会看到instance引用的对象还没有完成初始化
	* */
	private volatile static UnsafeLazyInitialzation instance;

	/*public synchronized UnsafeLazyInitialzation getInstance() {
		if (null == instance) {
			if (null == instance) {	//1：A线程执行
				instance = new UnsafeLazyInitialzation();//2：B线程执行
			}
		}
		return instance;
	}*/
	
	/*
	由于对getInstance()方法做了同步处理，synchronized将导致性能开销。
	如果getInstance()方 法被多个线程频繁的调用，将会导致程序执行性能的下降。
	反之，如果getInstance()方法不会被多个线程频繁的调用，那么这个延迟初始化方案将能提供令人满意的性能。
	* */
	/*public synchronized UnsafeLazyInitialzation getInstance() {
		synchronized (UnsafeLazyInitialzation.class) {//这里是线程不安全的
			if (null == instance) {
				instance = new UnsafeLazyInitialzation();
			}
		}
		return instance;
	}*/
	
	/*
	在早期的JVM中，synchronized（甚至是无竞争的synchronized）存在巨大的性能开销。
	因此， 人们想出了一个“聪明”的技巧：双重检查锁定（Double-Checked Locking）。
	人们想通过双重检查锁定来降低同步的开销。
	
	双重检查锁定看起来似乎很完美，但这是一个错误的优化！
	在线程执行到第4行，代码读 取到instance不为null时，instance引用的对象有可能还没有完成初始化。
	
	问题的根源
	instance = new UnsafeLazyInitialzation(); 创建了一个对象。这一 行代码可以分解为如下的3行伪代码。
	/-----------------------------------------------------------/
	memory = allocate();　　// 1：分配对象的内存空间 
	ctorInstance(memory);　 // 2：初始化对象 
	instance = memory;　　  // 3：设置instance指向刚分配的内存地址
	/-----------------------------------------------------------/
	上面3行伪代码中的2和3之间，可能会被重排序（在一些JIT编译器上，
	这种重排序是真实 发生的，详情见参考文献1的“Out-of-order writes”部分）。
	2和3之间重排序之后的执行时序如下。
	/-----------------------------------------------------------/
	memory = allocate();　　// 1：分配对象的内存空间 
	instance = memory;　　  // 3：设置instance指向刚分配的内存地址。 
						    // 注意，此时对象还没有被初始化！ 
	ctorInstance(memory);　 // 2：初始化对象
	/-----------------------------------------------------------/
	根据《The Java Language Specification,Java SE 7 Edition》（后文简称为Java语言规范），
	所有 线程在执行Java程序时必须要遵守intra-thread semantics。
	intra-thread semantics保证重排序不会 改变单线程内的程序执行结果。
	换句话说，intra-thread semantics允许那些在单线程内，不会改 变单线程程序执行结果的重排序。
	上面3行伪代码的2和3之间虽然被重排序了，但这个重排序 并不会违反intra-thread semantics。
	这个重排序在没有改变单线程程序执行结果的前提下，可以提高程序的执行性能。

	由于单线程内要遵守intra-thread semantics，从而能保证A线程的执行结果不会被改变。
	但是，当线程A和B(B线程未初始化对象)时序执行时，B线程将看到一个还没有被初始化的对象。

	回到本文的主题，DoubleCheckedLocking示例代码的第7行（instance=new Singleton();）
	如果发生重排序，另一个并发执行的线程B就有可能在第4行判断instance不为null。
	线程B接下来将 访问instance所引用的对象，但此时这个对象可能还没有被A线程初始化！
	
	这里A2和A3虽然重排序了，但Java内存模型的intra-thread semantics将确保A2一定会排在 A4前面执行。
	因此，线程A的intra-thread semantics没有改变，但A2和A3的重排序，将导致线程 B在B1处判断出instance不为空，
	线程B接下来将访问instance引用的对象。此时，【线程B将会访问到一个还未初始化的对象】。
	
	在知晓了问题发生的根源之后，可以想出两个办法来实现线程安全的延迟初始化。
	1）不允许2和3重排序。
	2）允许2和3重排序，但不允许其他线程“看到”这个重排序。
	* */
	/*public synchronized UnsafeLazyInitialzation getInstance() {
		if (null == instance) {
			synchronized (UnsafeLazyInitialzation.class) {//这里是线程不安全的
				if (null == instance) {
					instance = new UnsafeLazyInitialzation();
				}
			}
		}
		return instance;
	}*/

	/*
	 * 解决方式：
	 * 1、基于volatile的解决方案
	 * 这个解决方案需要JDK 5或更高版本（因为从JDK 5开始使用新的JSR-133内存模 型规范，
	 * 这个规范增强了volatile的语义）。
	 * 
	 * 2、基于类初始化的解决方式
	 * JVM在类的初始化阶段（即在Class被加载后，且被线程使用之前），
	 * 会执行类的初始化。在执行类的初始化期间，JVM会去获取一个锁。
	 * 这个锁可以同步多个线程对同一个类的初始化。
	 * 
	 * */
	private static class InstanceHolder {
		public static UnsafeLazyInitialzation instance = new UnsafeLazyInitialzation();
	}

	public static UnsafeLazyInitialzation getInstance() {
		return InstanceHolder.instance;//这里将导致InstanceHolder类被初始化
	}
}
