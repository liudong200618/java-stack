
https://mp.weixin.qq.com/s/RKqmy8dw7B7WtQc6Xy2CLA

1��
��Linux4.6��ʼ�������Ҫʹ�÷�root�û������Ľ����е�perf_events�������ں˵��ö�ջ����Ϣ����Ҫ��������ϵͳ����ʱ������
����ʹ��sysctl�����·�ʽ�������ǣ�
echo 1 > /proc/sys/kernel/perf_event_paranoid
echo 0 > /proc/sys/kernel/kptr_restrict



2��
jps ��ȡ��java����id 12289
./profiler.sh -d 30 -f ./flamegraph.html 12289

������ļ� :
./profiler.sh -d 30  12289

3��

$ ./profiler.sh start 8983
$ ./profiler.sh stop 8983

4��
��Agent�ķ�ʽ������
�����Ҫ��JVM��������������һЩ���룬�����Ǵ�Ӧ�ó���������ɺ���ʹ��profiler.sh�ű����з�������������������ϸ���async-profiler��Ϊ�������磺

$ java -agentpath:/path/to/libasyncProfiler.so=start,file=profile.svg ...


5��
��Щ��������profiler.sh�ű�ֱ�Ӵ���ġ��������-d 5������3��������
ʹ��start�����̽����agent��
����5�룻
Ȼ��ʹ��stop�����ٴθ��Ӵ���


6��
����ͼ��ô�������Բο���һ�������

����ζ�������ͼ�� http://www.ruanyifeng.com/blog/2017/09/flame-graph.html


7��
������������
-e event
event ��ѡ����������������鿴

./profiler.sh list list 1232


8��
event Ĭ��Ϊcpu��Ҳ������alloc���鿴�ڴ����
./profiler.sh -e alloc -d 30 -f ./nacos-alloc.svg 1232

lock�鿴�������


9��
������ģʽ����Ͳ�һһ���ԣ��õ����Ļ���cpuģʽ��

-i N ���ò���Ƶ�ʣ�Ĭ����10ms����ʹ��ms��us��s��Ϊ��λ���޵�λĬ��Ϊ���룬��
./profiler.sh -i 500us -d 30


-j N ����ջ�������
-o fmt ���������ʽ����ѡ���� summary��traces��flat��jfr��collapsed��svg��tree����õ���svg



10��
ԭ�����
��������������Ӧ�û���async-profiler������cpu�����ˣ��������Ȥ�����˽���async-profilerʵ�ֵ�ԭ�������һƪ���½��ܵĺ���ϸ�����Բο�

��JVM CPU Profiler����ԭ��Դ����Ƚ����� https://mp.weixin.qq.com/s/RKqmy8dw7B7WtQc6Xy2CLA


���ܽ�һ�£�

cpu profilerʵ��һ�������ַ�������1��Sampling��������2��Instrumentation����㣻������������Ľ�С��û��ô׼ȷ����㣨����AOP����׼������Ӱ���
Sampling����ֻ����Safe Ponit���������ᵼ��ͳ�ƽ��ƫ�Ҳ���ǿ�ͷ˵��Safepoint bias problem������ĳЩ����ִ��ʱ��̣���ִ��Ƶ�ʸߣ�����ռ����cpu���ⲿ�����Sampling�Ĳ���Ƶ�ʲ����㹻С�����ܾͲ���������������Ƶ�ʹ����ֻ�Ӱ�����ܣ������һ��Ļ��ڲ�����cpu profiler��ȱ��
async-profiler�ǻ��ڲ���ʵ�ֵģ�������û��Safepoint bias problem������ͨ��һ�ֽ���AsyncGetCallTrace�ķ�ʽ�����������ֲ�������Ҫ�ڰ�ȫ�㴦���������������������ô���׾͵��õ��ģ���Ҫʹ��һЩ���ɣ��ڿƼ����ķ�ʽ����ȡ
���ǵ�����ԭ���Ҳ�д����ƪ�������Ƽ����cpu��������������������һ�������ķ�������uber-common/jvm-profiler���������������ص�cpu���ó������µĻ���ͼ

���￪Դ��Arthas�е�cpu����Ҳ��ʹ�õ�async-profiler







6��ִ������

	start - �԰��Զ�ģʽ��ʼ������������ʽ����stop����֮ǰ���������򽫻�һֱ���У�
	resume - ������ָ���ǰ��ֹͣ�ķ����Ự��ǰ�������ռ���������Ȼ��Ч������ѡ����ڻỰ֮�䱣����Ӧ�ٴ�ָ����
	stop - ֹͣ��������ӡ���棻
	status - ��ӡ����״̬�����������Ƿ��ڻ״̬�Լ������೤ʱ�䣻
	list - ��ʾ���÷����¼����б���ѡ����Ȼ��ҪPID����Ϊ֧�ֵ��¼�������JVM�汾���죻
	-d N - ��������ʱ�䣬����Ϊ��λ�����δ�ṩstart��resume��stop��statusѡ���̽��������ָ����ʱ��������У�Ȼ���Զ�ֹͣ��ʾ����./profiler.sh - d 30 8983
	 
	-e event - ָ��Ҫ�������¼����磺cpu��alloc��lock��cache misses�ȡ�ʹ��list�����鿴�����¼��������б�
	�ڷ���(alloc)����ģʽ�У�ÿ�����ø��ٵĶ���������ѷ��������࣬�������Ƕ��еļ�¼���ѷ���TLAB��TLAB֮��Ķ�����ܴ�С����
	����(lock)����ģʽ�£������������/���������࣬�������ǽ������/�������������������
	Linux��֧�����������¼����ͣ�Ӳ���ϵ���ں˸��ٵ㣺
	-e mem:<func>[:rwx] �ں���<func>�����ö�/д/ִ�жϵ㡣mem�¼��ĸ�ʽ��perf-record��ͬ��ִ�жϵ�Ҳ�����ɺ�����ָ��������-e malloc�����ٱ���malloc���������е��ã�
	-e trace:<id> �����ں˸��ٵ㡣����ָ�����ٵ������������-e syscalls:sys_enter_open���������д򿪵�ϵͳ���ã�
	 
	-i N - �����ms�����룩��us��΢�룩��s���룩�����������������λ���÷��������������CPU���ʱ�䣬CPU����ʱ���ռ�������Ĭ��ֵΪ10000000��10ms����
	ʾ����./profiler.sh - i 500us 8983
	 
	-j N - ����Java��ջ������ȡ����N����Ĭ��ֵ2048���򽫺��Դ�ѡ�
	ʾ����./profiler.sh - j 30 8983
	 
	-b N - ����֡��������С���Ի�������Ӧ�����ɵ�Java����id������Ϊ��λ��������յ��й�֡��������С�������Ϣ���뽫��ֵ��Ĭ��ֵ���ӣ�ʾ����./profiler.sh - b 5000000 8983
	 
	-t - ��ÿ���߳̽��е���������ÿ����ջ���ٶ����Ա�ʾ�����̵߳�֡������ʾ����./profiler.sh - t 8983
	-s - ��ӡ������������FQN(Full qulified nameȫ����)��
	-g - ��ӡ����ǩ����
	-a - ͨ�����_[j]��׺��ע��Java��������
	-o fmt - ָ����������ʱҪת������Ϣ��fmt����������ѡ��֮һ��
	summary - ת����������ͳ����Ϣ��
	traces[=N] - ת�����ø��٣����N����������
	flat[=N] - dump flat profile����������ǰ��N����������
	 
	jfr - ��Java Mission Control�ɶ���Java Flight Recorder��ʽת���¼�,�ⲻ��Ҫ����JDK��ҵ���ܣ�
	collapsed[=C] - ��FlameGraph�ű�ʹ�õĸ�ʽת�����ø��ٵĽ�������ǵ��ö�ջ�ļ��ϣ�����ÿһ����һ���ֺŷָ���֡�б����һ����������
	svg[=C] - ����svg��ʽ�Ļ���ͼ��
	tree[=C] - ��HTML��ʽ���ɵ�������
	--reverse ��ѡ����ɻ�����ͼ��
	C�Ǽ��������ͣ�
	samples - �������Ǹ������ٵ�����������
	total - ���������ռ��Ķ�������ֵ�������ܷ����С��
	С�ᣬ���ٺ�չ�����Խ����һ��
	Ĭ�ϸ�ʽ��summary��traces=200��flat=200��
	 
	--title TITLE��--width PX��--height PX��-- minwidth PX��--reverse -FlameGraph������
	ʾ����./profiler.sh - f profile.svg--title "ʾ��CPU�����ļ�" --minwidth 0.58983
	 
	-f FILENAME - Ҫ�������ļ���Ϣת�������ļ�����
	%p - ����չ��Ŀ��JVM��PID��
	%t - ���������ʱ��ʱ�����
	ʾ���� ./profiler.sh -o collapsed -f /tmp/traces-%t.txt 8983
	--all-user - �������û�ģʽ�¼������ں˷�����perf_event_paranoid��������ʱ����ѡ��ǳ����á�
	--all-kernel ��ʾֻ�����ں�ģʽ�¼���
	--sync-walk - ��ѡͬ��JVMTI��ջwalker��������AsyncGetCallTrace����ѡ�������߷���JVM����ʱ��������VMThread::execute��G1CollectedHeap::humongus_obj_allocate�ȣ�ʱJava��ջ���ٵ�׼ȷ�ԣ�����������ȷ��������Ҫʹ�ã����ʹ�ò�������ģʽ������JVM������
	-v��--version - ��ӡ̽������İ汾�����ָ����PID�����ȡ���ص����������еĿ�İ汾��