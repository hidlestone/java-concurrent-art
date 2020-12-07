## chapter06-Java并发容器和框架

TODO：  
为什么使用：HashMap不安全，HashTable效率低

### 一、ConcurrentHashMap的实现原理与使用
ConcurrentHashMap是线程安全且高效的HashMap。
在保证线程安全的同时又能保证高效的操作。

#### 1.1、为什么要使用ConcurrentHashMap
在并发编程中使用HashMap可能导致程序死循环。而使用线程安全的HashTable效率又非常低下。

**HashMap**：并发执行put操作时会引起死循环，因为多线程会导致HashMap的Entry链表形成环形数据结构，
Entry的next节点永远不为空，就会产生死循环获取Entry。  
**HashTable**：HashTable使用synchronized来保证线程安全，在线程竞争激烈的情况下其效率十分低下。  
**ConcurrentHashMap**：使用锁分段技术：首先将数据分成一段一段地存储，然后给每一段数据配一把锁，
当一个线程占用锁访问其中一个段数据的时候，其他段的数据也能被其他线程访问。

#### 1.2、为什么要使用ConcurrentHashMap
ConcurrentHashMap是由Segment数组结构和HashEntry数组结构组成。
Segment是一种可重入锁（ReentrantLock），在ConcurrentHashMap里扮演锁的角色；
HashEntry则用于存储键值对数 据。一个ConcurrentHashMap里包含一个Segment数组。
Segment的结构和HashMap类似，是一种 数组和链表结构。一个Segment里包含一个HashEntry数组，
每个HashEntry是一个链表结构的元 素，每个Segment守护着一个HashEntry数组里的元素，
当对HashEntry数组的数据进行修改时， 必须首先获得与它对应的Segment锁

#### 1.3、ConcurrentHashMap的初始化
1.初始化segments数组
2.初始化segmentShift和segmentMask
3.初始化每个segment

#### 1.4、定位Segment
hash的散列算法

#### 1.5、ConCurrentHashMap的操作
（1）get
整个get过程不需要加锁，除非读到的值是空的才会加锁重读。  
做法：get方法里将要使用的共享变量都定义成volatile类型。定义成volatile的变量，
能够在线程之间保持可见性，能够被多线程同时读，并且保证不会读到过期的值，但是只能被单线程写。  
>之所以不会读到过期的值，是因为根据java内存模型的happens before原则，
 对volatile变量的写入操作先于读操作。
 
（2）put
加锁、扩容、定位添加

（3）size
先尝试两次不加锁的统计（直接累加各segment的count），比对modcount变量看是不是发生变化，如果发生变化再通过加锁的方式统计。

### 二、ConcurrentLinkedQueue
实现线程安全的队列两种方式：
```
1.阻塞方式：使用一个锁（入队出队同一个）或两个锁（入队出队不同锁）
2.非阻塞方式：使用循环CAS
```
ConcurrentLinkedQueue是一个基于链接节点的无界线程安全队列，它采用先进先出的规则对节点进行排序，
当我们添加一个元素的时候，它会添加到队列的尾部；当我们获取一个元素时，它会返回队列头部的元素。
它采用了“wait-free”算法（即CAS算法）来实现，该算法在 Michael&Scott算法上进行了一些修改。

#### 2.1、ConcurrentLinkedQueue的结构

#### 2.2、入队列
入队列的过程    
（1）将入队节点设置成当前队列尾节点的下一个节点
（2）更新tail节点，如果tail节点的next节点不为空，则将入队节点设置成tail节点，如果tail节点的next节点为空，则将入队节点设置成tail的next节点，所以tail节点并不总是尾节点

#### 2.3、出队列
出队列过程
并不是每次出队时都更新head节点，当head节点里有元素时，直接弹出head里的元素，而不会更新head节点。
只有head节点里没有元素时，出队操作才会更新head节点。这种做法也是通过hops变量来减少使用CAS更新head节点的消耗，从而提高出队效率

### 三、Java中的阻塞队列
#### 3.1、什么是阻塞队列
阻塞队列（BlockingQueue）是一个支持两个附加操作的队列。这两个附加的操作支持阻塞的插入和移除方法。
1）支持阻塞的插入方法：意思是当队列满时，队列会阻塞插入元素的线程，直到队列不满。
2）支持阻塞的移除方法：意思是在队列为空时，获取元素的线程会等待队列变为非空。

阻塞队列常用于生产者和消费者的场景，生产者是向队列里添加元素的线程，消费者是
从队列里取元素的线程。阻塞队列就是生产者用来存放元素、消费者用来获取元素的容器。
```
抛出异常：add、remove
返回特殊值：offer、poll
一直阻塞：put、take
```
注意：如果是无界阻塞队列，队列不可能会出现满的情况，所以使用put或offer方法永 远不会被阻塞，而且使用offer方法时，该方法永远返回true。

#### 3.2、Java里的阻塞队列
1. ArrayBlockingQueue  
数组实现的有界队列，FIFO，默认情况下不保证线程公平的访问队列
2. LinkedBlockingQueue  
用链表实现的有界阻塞队列，FIFO
3. PriorityBlockingQueue  
支持优先级的无界阻塞队列，默认情况下元素采取自然顺序升序排列，也可以自定义比较方法来指定元素排列规则
4. DelayQueue  
支持延时获取元素的无界阻塞队列。队列使用PriorityQueue来实现。队列中的元素必须实现Delay接口，在创建
元素时可以指定多久才能从队列中获取当前元素。只有在延迟期满时才能从队列中提取元素。  
DelayQueue的应用场景：  
缓存系统的设计：用DelayQueue保存缓存元素的有效期，使用一个线程循环查询DelqyQueue，一旦能从DelayQueue  
中获取元素，表示缓存有效期到了。  
定时任务调度：使用DelayQueue保存当天将会执行的任务和执行时间，一旦从DelayQueue中获取到任务就开始执行，  
比如TimerQueue就是使用DelayQueue实现的。  

5. SynchronousQueue  
不存储元素的阻塞队列。每一个put操作必须等待一个take操作，否则不能继续添加元素。  
SynchronousQueue可以看做一个传球手，负责把生产者线程处理的数据直接传递给消费者线程，队列本身并不
存储任何元素，非常适合传递性场景
6. LinkedTransferQueue  
由链表结构组成的无界阻塞TransferQueue队列，注意其transfer方法
7. LinkedBlockingDeque  
链表结构组成的双向阻塞队列

#### 3.3、阻塞队列的实现原理
使用通知模式实现（condition）

### 四、Fork/Join框架
#### 4.1、什么是Fork/Join框架
Fork/Join框架是Java 7提供的一个用于并行执行任务的框架，是一个把大任务分割成若干个小任务，最终汇总每个小任务结果后得到大任务结果的框架。

#### 4.2、工作窃取算法
工作窃取（work-stealing）算法是指某个线程从其他队列里窃取任务来执行。
工作窃取算法的优点：  
充分利用线程进行并行计算，减少了线程间的竞争。  
工作窃取算法的缺点：  
在某些情况下还是存在竞争，比如双端队列里只有一个任务时。并且该算法会消耗了更多的系统资源，比如创建多个线程和多个双端队列。

#### 4.3、Fork/Join框架的设计

#### 4.4、使用Fork/Join框架
#### 4.5、Fork/Join框架的异常处理

#### 4.6、Fork/Join框架的实现原理
ForkJoinPool由ForkJoinTask数组和ForkJoinWorkerThread数组组成，
ForkJoinTask数组负责 将存放程序提交给ForkJoinPool的任务，
而ForkJoinWorkerThread数组负责执行这些任务。

