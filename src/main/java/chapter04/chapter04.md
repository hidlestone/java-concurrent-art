## chapter04-Java内存模型

### 一、线程简介
#### 1.1、什么是线程
现代操作系统调度的最小单元是线程，也叫轻量级进程（Light Weight Process），在一个进程里可以创建多个线程，
这些线程都拥有各自的计数器、堆栈和局部变量等属性，并且能够访问共享的内存变量。

#### 1.2、为什么使用线程
```
1.更多的处理器核心：一个线程在一个时刻只能运行在一个处理器核心上
2.更快的响应时间
3.更好的编程模型
```

#### 1.3、线程优先级
操作系统基本采用时分的形式调度运行的线程，操作系统会分出一个个时间片，线程会分配到若干时间片，
当线程的时间片用完了就会发生线程调度，并等待着下次分配，线程分配到的时间片多少就决定了线程使用   
处理器资源的多少。

#### 1.4、线程的状态
6种，在给定的时刻只能处于一种状态
```
NEW：初始状态，线程被构建，但还没有调用start方法
RUNNABLE：运行状态，java线程将就绪和运行两种状态统称为运行状态
BLOCKED：阻塞状态，表明线程阻塞于锁
WAITING：等待状态，等待其他线程的通知或中断
TIME_WAITING：超时等待状态，可以在指定的时间自行返回
TERMINATED：终止状态，表示当前线程已经执行完毕
```

#### 1.5、Daemon线程（守护线程）
Daemon线程是一种支持型线程，因为它主要被用作程序中后台调度以及支持性工作。
这意味着，当一个Java虚拟机中不存在非Daemon线程的时候，Java虚拟机将会退出。
可以通过调用Thread.setDaemon(true)将线程设置为Daemon线程。

ps. 在构建Daemon线程时，不能依靠finally块中的内容来确保执行关闭或清理资源的逻辑

二、启动和终止线程
#### 2.1、构造线程
在运行线程之前首先要构造一个线程对象，线程对象在构造的时候需要提供线程所需要的属性，
如线程所属的线程组、线程优先级、是否是Daemon线程等信息。

一个新构造的线程对象是由其parent线程来进行空间分配的，而child线程继承了parent是否为Daemon、优先级和加载资源的contextClassLoader以及可继承的 ThreadLocal，同时还会分配一个唯一的ID来标识这个child线程。至此，一个能够运行的线程对
象就初始化好了，在堆内存中等待着运行。

#### 2.2、启动线程
线程对象在初始化完成之后，调用start()方法就可以启动这个线程。
线程start()方法的含义是：当前线程（即parent线程）同步告知Java虚拟机，
只要线程规划器空闲，应立即启动调用 start()方法的线程。

#### 2.3、理解中断
中断可以理解为线程的一个标识位属性，它表示一个运行中的线程是否被其他线程进行了中断操作。
中断好比其他线程对该线程打了个招呼，其他线程通过调用该线程的interrupt()方法对其进行中断操作。

#### 2.4、过期的suspend()、resume()和stop()
suspend()、resume()和stop()方法完成了线程的暂停、恢复和终止工作，
而且非常“人性化”。但是这些API是过期的，也就是不建议使用的。

不建议使用的原因主要有：以suspend()方法为例，在调用后，线程不会释放已经占有的资源（比如锁），
而是占有着资源进入睡眠状态，这样容易引发死锁问题。同样，stop()方法在终结
一个线程时不会保证线程的资源正常释放，通常是没有给予线程完成资源释放工作的机会，
因此会导致程序可能工作在不确定状态下。
	
正因为suspend()、resume()和stop()方法带来的副作用，这些方法才被标注为不建 议使用的过期方法，
而暂停和恢复操作可以用后面提到的等待/通知机制来替代。

#### 2.5、安全地终止线程

### 三、线程间通信
#### 3.1、volatile和synchronized关键字
多线程访问一个对象或者对象的成员变量，每个线程可以拥有这个变量的拷贝。
所以程序在执行过程中，一个线程看到的变量并不一定是最新的。

关键字volatile可以用来修饰字段（成员变量），就是告知从程序任何对该变量的访问均需要从
共享内存中获取，而对它的改变必须同步刷新会共享内存，它能保证所有线程对变量访问的可见性。

关键字synchronized罗修饰方法或者以同步快的形式来进行使用，它主要确保多个线程在同一时刻，
只能有一个线程处于方法或者同步快中，它保证线程对变量访问的可见性和排他性。

关于synchronized：本质是对一个对象的监视器（monitor）的获取，而这个获取过程是排他的，也就是同一时刻
只能有一个线程获取到有synchronized所保护对象的监视器。任意一个对象都拥有自己的监视器。
```
任意线程对Object的访问，首先要获得Object的监视器。如果获取失败，线程进入同步队列，
线程状态变为BLOCKED。当访问Object的前驱（获得了锁的线程）释放了锁，则该释放操作
唤醒阻塞在同步队列中的线程，使其重新尝试对监视器的获取。
```

#### 3.2、等待/通知机制
等待/通知机制，是指一个线程A调用了对象O的wait方法进入等待状态，而另一个线程B调用了对象O的notify
或notifyall方法，线程A收到通知后从对象O的wait方法返回，进而执行后续操作。

注意： 
```
（1）使用wait、notify、notifyAll时需要先对调用对象加锁
（2）调用wait方法后，线程状态由RUNNING变为WAITING，并将当前线程防止到对象的等待队列
（3）notify或notifyAll方法调用后，等待线程依旧不会从wait返回，需要调用notify或notifyAll的线程释放锁之后，等待线程才有机会从wait返回
（4）notify方法将等待队列中的等待线程从等待队列中移到同步队列中，被移动的线程从WAITING变为BLOCKED
（5）从wait方法返回的前提是获得了调用对象的锁
```

#### 3.3、等待通知经典范例
```
等待方：1.获取对象的锁
       2.如果条件不满足，那么调用对象的wait方法，被通知后仍要检查条件
       3.条件满足则执行对应的逻辑
       伪代码：
          synchronized(对象){
               while(条件不满足){
                   对象.wait();
               }
               对应的处理逻辑
          }
通知方：1.获得对象的锁
        2.改变条件
       3.通知所有等待在对象上的线程
       伪代码：
          synchronized(对象){
               改变条件
               对象.notify();
          }
```

#### 3.4、管道输入/输出流
管道输入/输出流和普通的文件输入/输出流或者网络输入/输出流不同之处在于，它主要
用于线程之间的数据传输，而传输的媒介为内存。
管道输入/输出流主要包括了如下4种具体实现：PipedOutputStream、PipedInputStream、 PipedReader和PipedWriter，
前两种面向字节，而后两种面向字符。

对于piped类型的流，使用时必须先调用connect方法进行绑定，否则会抛出异常。

#### 3.5、Thread.join()的使用
如果一个线程A执行了thread.join()语句，其含义是：当前线程A等待thread线程终止之后才从thread.join()返回。
线程Thread除了提供join()方法之外，还提供了join(long millis)和join(long millis,int nanos)两个具备超时特性的方法。
这两个超时方法表示，如果线程thread在给定的超时时间里没有终止，那么将会从该超时方法中返回。

#### 3.6、ThreadLocal的使用
ThreadLocal，即线程变量，是一个以ThreadLocal对象为键、任意对象为值的存储结构。
这个结构被附带在线程上，也就是说一个线程可以根据一个ThreadLocal对象查询到绑定在这个线程上的一个值。

### 四、线程应用实例
#### 4.1、等待超时模式

#### 4.2、一个简单的数据库连接池示例

#### 4.3、线程池技术及示例
预先创建了若干数量的线程，并且不能由用户直接对线程的创建进行控制，
在这个前提下重复使用固定或较为固定数目的线程来完成任务的执行。
这样做的好处是，一方面，消除了频繁创建和消亡线程的系统资源开销，另一方面，
面对过量任务的提交能够平缓的劣化。

#### 4.4、一个基于线程池技术的简单Web服务器
如果Web服务器是单线程的，多线程的浏览器也没有用武之地，因为服务端还是一个请求一个请求的顺序处理。
因此，大部分Web服务器都是支持并发访问的。常用的Java Web服务器， 如Tomcat、Jetty，在其处理请求的过程中都使用到了线程池技术。

