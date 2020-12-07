## chapter01-并发编程的挑战

### 一、上下文切换
即使是单核处理器也支持多线程执行代码，CPU通过给每个线程分配CPU时间片（一般为几十毫秒）实现这个机制。

当前任务执行一个时间片后会切换到下一个任务。在切换前会保存上一个任务的状态，以便下次
切换回这个任务时，可以再加载这个任务的状态。任务从保存到再加载的过程就是一次上下文切换。

如何减少上下文切换？
1. 无锁并发编程。多线程竞争锁时，会引起上下文切换，可以用一些办法避免使用锁，如将数据
   的id取模分段，不同的线程处理不同段的数据
2. CAS算法：Java的Atomic包使用CAS算法来更新数据，不需要加锁
3. 使用最少线程，避免创建不需要的线程
4. 协程：在单线程里实现多任务的调度，并在单线程里维持多个任务间的切换

### 二、死锁
避免死锁的几个常见方法：
1. 避免一个线程获取多个锁
2. 避免一个线程在锁内同时占用多个资源，尽量保证每个锁只占用一个资源
3. 尝试使用定时锁，使用lock.tryLock(timeout)来替代使用内部锁机制
4. 对于数据库锁，加锁和解锁必须在一个数据库连接里，否则会出现解锁失败的情况

### 三、资源限制的挑战
问题：如果将某段串行的代码并发执行，因为受限于资源，仍然在串行执行，这时候程序不仅不会加快执行，
反而会更慢，因为增加了上下文切换和资源调度的时间。

如何解决：  
对于硬件资源的限制，使用机群并行执行程序，通过搭建服务器集群，不同的机器处理不同的数据  
对于软件资源的限制，可以考虑使用资源池将资源复用  
根据不同的资源限制调整程序的并发度  