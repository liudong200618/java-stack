
https://mp.weixin.qq.com/s/RKqmy8dw7B7WtQc6Xy2CLA

1、
从Linux4.6开始，如果需要使用非root用户启动的进程中的perf_events，捕获内核调用堆栈的信息，需要设置两个系统运行时变量，
可以使用sysctl或按如下方式设置它们：
echo 1 > /proc/sys/kernel/perf_event_paranoid
echo 0 > /proc/sys/kernel/kptr_restrict



2、
jps 获取到java进程id 12289
./profiler.sh -d 30 -f ./flamegraph.html 12289

不输出文件 :
./profiler.sh -d 30  12289

3、

$ ./profiler.sh start 8983
$ ./profiler.sh stop 8983

4、
以Agent的方式启动：
如果需要在JVM启动后立即分析一些代码，而不是待应用程序启动完成后再使用profiler.sh脚本进行分析，则可以在命令行上附加async-profiler作为代理。例如：

$ java -agentpath:/path/to/libasyncProfiler.so=start,file=profile.svg ...


5、
有些参数是由profiler.sh脚本直接处理的。例如参数-d 5将导致3个操作：
使用start命令附加探查器agent；
休眠5秒；
然后使用stop命令再次附加代理。


6、
火焰图怎么看？可以参考阮一峰的文章

《如何读懂火焰图》 http://www.ruanyifeng.com/blog/2017/09/flame-graph.html


7、
其他参数介绍
-e event
event 可选参数用这个命令来查看

./profiler.sh list list 1232


8、
event 默认为cpu，也可以用alloc来查看内存分配
./profiler.sh -e alloc -d 30 -f ./nacos-alloc.svg 1232

lock查看锁情况：


9、
其他的模式这里就不一一尝试，用的最多的还是cpu模式。

-i N 设置采样频率，默认是10ms，可使用ms，us，s作为单位，无单位默认为纳秒，如
./profiler.sh -i 500us -d 30


-j N 设置栈采样深度
-o fmt 设置输出格式：可选的有 summary、traces、flat、jfr、collapsed、svg、tree，最常用的是svg



10、
原理介绍
看到这里相信你应该会用async-profiler来进行cpu剖析了，如果感兴趣可以了解下async-profiler实现的原理，这块有一篇文章介绍的很详细，可以参考

《JVM CPU Profiler技术原理及源码深度解析》 https://mp.weixin.qq.com/s/RKqmy8dw7B7WtQc6Xy2CLA


简单总结一下：

cpu profiler实现一般有两种方案：（1）Sampling，采样（2）Instrumentation，埋点；采样对性能损耗较小但没那么准确，埋点（类似AOP）精准但性能影响大
Sampling采样只能在Safe Ponit处采样，会导致统计结果偏差，也就是开头说的Safepoint bias problem，例如某些方法执行时间短，但执行频率高，正真占用了cpu，这部分如果Sampling的采样频率不能足够小，可能就采样不到，但采样频率过高又会影响性能，这就是一般的基于采样的cpu profiler的缺点
async-profiler是基于采样实现的，但它又没有Safepoint bias problem，它是通过一种叫做AsyncGetCallTrace的方式来采样，这种采样不需要在安全点处采样，但这个函数不是那么容易就调用到的，需要使用一些技巧（黑科技）的方式来获取
正是第三点原因我才写了这篇文章来推荐这款cpu分析利器，比如我用了一款其他的分析器（uber-common/jvm-profiler）来分析上面网关的cpu，得出了如下的火焰图

阿里开源的Arthas中的cpu分析也是使用的async-profiler







6、执行命令

	start - 以半自动模式开始分析，即在显式调用stop命令之前，分析程序将会一直运行；
	resume - 启动或恢复先前已停止的分析会话，前面所有收集的数据仍然有效，分析选项不会在会话之间保留，应再次指定；
	stop - 停止分析并打印报告；
	status - 打印分析状态：分析程序是否处于活动状态以及持续多长时间；
	list - 显示可用分析事件的列表，此选项仍然需要PID，因为支持的事件可能因JVM版本而异；
	-d N - 分析持续时间，以秒为单位。如果未提供start、resume、stop或status选项，则探查器将在指定的时间段内运行，然后自动停止，示例：./profiler.sh - d 30 8983
	 
	-e event - 指定要分析的事件，如：cpu、alloc、lock、cache misses等。使用list参数查看可用事件的完整列表。
	在分配(alloc)分析模式中，每个调用跟踪的顶部框架是已分配对象的类，计数器是堆中的记录（已分配TLAB或TLAB之外的对象的总大小）。
	在锁(lock)分析模式下，顶部框架是锁/监视器的类，计数器是进入此锁/监视器所需的纳秒数。
	Linux上支持两种特殊事件类型：硬件断点和内核跟踪点：
	-e mem:<func>[:rwx] 在函数<func>处设置读/写/执行断点。mem事件的格式与perf-record相同。执行断点也可以由函数名指定，例如-e malloc将跟踪本地malloc函数的所有调用；
	-e trace:<id> 设置内核跟踪点。可以指定跟踪点符号名，例如-e syscalls:sys_enter_open将跟踪所有打开的系统调用；
	 
	-i N - 后面跟ms（毫秒）、us（微秒）或s（秒），则以纳秒或其他单位设置分析间隔。仅计算CPU活动的时间，CPU空闲时不收集样本，默认值为10000000（10ms）。
	示例：./profiler.sh - i 500us 8983
	 
	-j N - 设置Java堆栈分析深度。如果N大于默认值2048，则将忽略此选项。
	示例：./profiler.sh - j 30 8983
	 
	-b N - 设置帧缓冲区大小，以缓冲区中应该容纳的Java方法id的数量为单位。如果接收到有关帧缓冲区大小不足的消息，请将此值从默认值增加，示例：./profiler.sh - b 5000000 8983
	 
	-t - 对每个线程进行单独分析，每个堆栈跟踪都将以表示单个线程的帧结束，示例：./profiler.sh - t 8983
	-s - 打印简单类名而不是FQN(Full qulified name全类名)；
	-g - 打印方法签名；
	-a - 通过添加_[j]后缀来注释Java方法名；
	-o fmt - 指定分析结束时要转储的信息。fmt可以是以下选项之一：
	summary - 转储基本配置统计信息；
	traces[=N] - 转储调用跟踪（最多N个样本）；
	flat[=N] - dump flat profile（调用最多的前面N个方法）；
	 
	jfr - 以Java Mission Control可读的Java Flight Recorder格式转储事件,这不需要启用JDK商业功能；
	collapsed[=C] - 以FlameGraph脚本使用的格式转储调用跟踪的结果，这是调用堆栈的集合，其中每一行是一个分号分隔的帧列表，后跟一个计数器。
	svg[=C] - 生成svg格式的火焰图。
	tree[=C] - 以HTML格式生成调用树。
	--reverse 该选项将生成回溯视图。
	C是计数器类型：
	samples - 计数器是给定跟踪的若干样本；
	total - 计数器是收集的度量的总值，例如总分配大小。
	小结，跟踪和展开可以结合在一起。
	默认格式是summary，traces=200，flat=200。
	 
	--title TITLE，--width PX，--height PX，-- minwidth PX，--reverse -FlameGraph参数；
	示例：./profiler.sh - f profile.svg--title "示例CPU配置文件" --minwidth 0.58983
	 
	-f FILENAME - 要将配置文件信息转储到的文件名。
	%p - 被扩展到目标JVM的PID；
	%t - 到命令调用时的时间戳。
	示例： ./profiler.sh -o collapsed -f /tmp/traces-%t.txt 8983
	--all-user - 仅包括用户模式事件。当内核分析受perf_event_paranoid设置限制时，此选项非常有用。
	--all-kernel 表示只包含内核模式事件。
	--sync-walk - 首选同步JVMTI堆栈walker，而不是AsyncGetCallTrace。此选项可以提高分析JVM运行时函数（如VMThread::execute、G1CollectedHeap::humongus_obj_allocate等）时Java堆栈跟踪的准确性，除非您绝对确定，否则不要使用！如果使用不当，此模式将导致JVM崩溃！
	-v，--version - 打印探查器库的版本，如果指定了PID，则获取加载到给定进程中的库的版本。