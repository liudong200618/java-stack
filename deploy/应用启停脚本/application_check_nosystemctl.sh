#!/bin/bash
#systemd service 文件
REQUIRED_SERVICE_1="/opt/consumer_dpplus/monitorstart.sh"
REQUIRED_SERVICE_2="/opt/consumer_esp1/monitorstart.sh"
REQUIRED_SERVICE_3="/opt/consumer_web/monitorstart.sh"
REQUIRED_SERVICE_4="/opt/consumer_smds/monitorstart.sh"
#检查进程的关键字
REQUIRED_PROCESS_1="/opt/consumer_dpplus/consumer-dpplus.jar"
REQUIRED_PROCESS_2="/opt/consumer_esp1/consumer-esp1.jar"
REQUIRED_PROCESS_3="/opt/consumer_web/esim-consumer-web.jar"
REQUIRED_PROCESS_4="/opt/consumer_smds/consumer-smds.jar"

#只回滚到最新更新的应用
rollback_file_dir=/opt/scripts
rollback_file=$rollback_file_dir/rsp_rollback_file.cnf

#update_app_xxx解压到的根目录
unzip_root_dir="/opt/rsp"
unzip_dppuls_dir="esim_consumer"
unzip_smweb_dir="esim_consumer_web"
unzip_es_dir="esim_consumer_es"

#rps备份的根目录
backup_root_dir="/opt/rsp_backups"

#应用根目录
dppuls_app_dir="/opt/consumer_dpplus"
smweb_app_dir="/opt/consumer_web"
es_app_dir="/opt/consumer_esp1"

#log level
print=0
info=1
error=2

#####################process check###########################
function stop_process_if_is_running() {
	PROCESS=$1
	pids=`ps -ef|grep $PROCESS|grep -v 'grep'|awk '{print $2}'`
	for i in $pids
		do
		  echo -e  "\033[43;35m kill $PROCESS ! \033[0m"
		  `kill -9 $i`
		done
}

#echo  返回的是函数返回值,return 返回指定函数退出状态码
function check(){
	PROCESS=$1
	count=`ps -ef |grep $PROCESS |grep -v "grep" |wc -l`
	if [[ 0 == $count ]];then
		return 0
	else
		return 1
	fi
}

function status_process() {
	PROCESS=$1
	count=`ps -ef |grep $PROCESS |grep -v "grep" |wc -l`
	if [ 0 == $count ];then
		echo -e  "$PROCESS is no running...!"
	else
		echo -e  "\033[43;35m $PROCESS is running...! \033[0m"
	fi
}

#####################service check###########################

function help(){
	PRINT_LOG "[help]"
    echo "Function: start\stop rsp service and dependence, and check status"
    echo "Usage: $0 {cicd xxx.zip|start|stop|status|help|rollback}"
}
 
function start_service_if_is_stoped(){
    SERVICE=$1
	sh $SERVICE
}

function start_service_if_process_noexist(){
	PROCESS=$1
	SERVICE=$2
    check $PROCESS
	REVAL=$?
	if [[ $REVAL -eq 0 ]]; then
        echo $PROCESS is no running,begin start...
		start_service_if_is_stoped $SERVICE
    else
        echo $PROCESS is  running, so not start $PROCESS.
    fi

}

#if process is exist ,shoud not start the service
#如果进程已经存在,则不启动相应的服务
function start(){
	PRINT_LOG "[start]"
	start_service_if_process_noexist $REQUIRED_PROCESS_1 $REQUIRED_SERVICE_1
	start_service_if_process_noexist $REQUIRED_PROCESS_2 $REQUIRED_SERVICE_2
	start_service_if_process_noexist $REQUIRED_PROCESS_3 $REQUIRED_SERVICE_3
	start_service_if_process_noexist $REQUIRED_PROCESS_4 $REQUIRED_SERVICE_4
}



#停止ota系统服务或者对应的进程
function stop(){
	PRINT_LOG "[stop]"
	stop_process_if_is_running $REQUIRED_PROCESS_1
	stop_process_if_is_running $REQUIRED_PROCESS_2
	stop_process_if_is_running $REQUIRED_PROCESS_3
	stop_process_if_is_running $REQUIRED_PROCESS_4
}
 
function restart() {
	PRINT_LOG "[restart]"
	echo stop...
	stop
	sleep 2
	echo start...
	start
}
#检查进程和服务状态(某个ota服务可能由脚本启动,可能作为系统服务的方式启动,系统服务不存在可能进程存在)
function status(){
	PRINT_LOG "[status]"
    status_process $REQUIRED_PROCESS_1
	status_process $REQUIRED_PROCESS_2
	status_process $REQUIRED_PROCESS_3
	status_process $REQUIRED_PROCESS_4
}

#会备份所有应用
function backup(){
	PRINT_LOG "[backup]"
	#当前时间 : 2020-02-14-17-56-42  v_time=`date "+%Y-%m-%d-%H-%M-%S"`
	v_time=`date "+%Y-%m-%d-%H-%M-%S"`
	bakstr="[Start auto backup rsp project:]"${v_time}
	echo ${bakstr}

	#备份的目录名 otaBackup-2020-02-14-17-56-42
	dirName=$backup_root_dir/rspBackup-${v_time}
	
	#如果备份文件夹已经存在,则需要更改目录名,主要是考虑到回滚的时候查找当天的回滚目录
	if [  -d $dirName ];then
      echo "备份目录$dirName已经存在,如果仍要备份请先修改该目录名再执行!"
	  exit_and_warning 1
	  exit 0
	fi
	
	esim_consumer_dir=${dirName}/$unzip_dppuls_dir
	esim_consumer_es_dir=${dirName}/$unzip_es_dir
	esim_consumer_web_dir=${dirName}/$unzip_smweb_dir
	#创建三个备份目录
	mkdir -p ${esim_consumer_dir}
	mkdir -p ${esim_consumer_es_dir}
	mkdir -p ${esim_consumer_web_dir}

	echo_and_printf $print "备份旧版本到${dirName}"
	#拷贝某个文件到刚创建的备份目录
	cp -rf $dppuls_app_dir/*   ${esim_consumer_dir}
	#拷贝某个目录到刚创建的备份目录
	cp -rf $smweb_app_dir/*    ${esim_consumer_web_dir}
	
	cp -rf $es_app_dir/*   ${esim_consumer_es_dir}
	
	#备份成功后,更新回滚文件
	alterConfig $rollback_file rsprollback latestdir $dirName

}
#ROOT  ROOT.war  web  web.war
#检查目录是否存在,如果存在继续检查该目录是否存在文件
#如果目录且文件存在返回0否则返回1
function check_dirAndfile_if_exist(){
	dirName=$1
	if [  -d $dirName ];then
      echo "$dirName目录存在"
	  #继续检查目录下是否存在文件
	  if [ "`ls -A ${dirName}`" = "" ];then
			echo "$dirName目录为空目录"
			return 1
	  else
			echo "$dirName目录下存在文件"
			echo "文件列表:"`ls -A ${dirName}`""
			return 0
	  fi
	  
	else
		 echo "$dirName目录不存在"
		 return 1
	fi

}

#只检查目录,如果存在返回0
function check_dir(){
	dirName=$1
	if [  -d $dirName ];then
      echo "$dirName目录存在"
	  return 0
	else
		 echo "$dirName目录不存在"
		 return 1
	fi
}

#cecho "100;10"  "43;35"  "36"  
#0:正常message  1:醒目提示  2:错误提示
function echo_and_printf(){
	message=$2
	if [ $1 -eq $print ];then
		echo $message
	elif [ $1 -eq $info ];then
		cecho "1"  $message
	elif [ $1 -eq $error ];then
		cecho "100;10"  $message
	else
		echo $message
	fi
	PRINT_LOG $message
}

#如果解压到 $unzip_root_dir/ 的对应目录不为空且存在文件则删除旧版本应用
function deleteOldProjectIFNeedUpdate(){
	PRINT_LOG "[deleteOldProjectIFNeedUpdate]"
	PRINT_LOG "[删除旧版本的应用数据]"
	
	#检查对应的目录和文件是否存在,如果存在才删除对应的旧版本应用
	
	#开始检查dp+应用
	check_dirAndfile_if_exist "$unzip_root_dir/$unzip_dppuls_dir"
	REVAL1=$?
	if [[ $REVAL1 -eq 0 ]]; then
        echo_and_printf $print "需要更新tomcat-dppuls"
		echo_and_printf $print "开始删除$dppuls_app_dir/下的应用"
		#rm -rf $dppuls_app_dir/*
    else
       echo_and_printf $print "不需要更新tomcat-dppuls"
    fi
	
	#开始检查consumer_esp1应用,不删除 logs等其他文件
	check_dirAndfile_if_exist "$unzip_root_dir/$unzip_es_dir"
	REVAL2=$?
	if [[ $REVAL2 -eq 0 ]]; then
		echo_and_printf $print "需要更新consumer_esp1"
		echo_and_printf $print "开始删除$es_app_dir/consumer-esp1.jar下的应用"
		#rm -rf $es_app_dir/consumer-esp1.jar
    else
       echo_and_printf $print "不需要更新consumer_esp1"
    fi
	
	#开始检查web应用
	check_dirAndfile_if_exist "$unzip_root_dir/$unzip_smweb_dir"
	REVAL3=$?
	if [[ $REVAL3 -eq 0 ]]; then
        echo_and_printf $print "需要更新tomcat-smweb"
		echo_and_printf $print "开始删除$smweb_app_dir/下的应用"
		#rm -rf $smweb_app_dir/*
    else
       echo_and_printf $print "不需要更新tomcat-smweb"
    fi
	
}


function copy() {
	PRINT_LOG "[copy]"
	PRINT_LOG "开始拷贝文件到相应工程目录..."
   
	#修改名字
	#if [  -f "$unzip_root_dir/$unzip_dppuls_dir/esim-personal-api.war" ]; then
	#	PRINT_LOG "修改esim-personal-api.war->ROOT.war"
	#	mv $unzip_root_dir/$unzip_dppuls_dir/esim-personal-api.war  $unzip_root_dir/$unzip_dppuls_dir/ROOT.war
	#fi
	
	#if [  -f "$unzip_root_dir/$unzip_smweb_dir/esim-personal-web.war" ]; then
	#	PRINT_LOG "修改esim-personal-web.war->web.war"
	#	mv $unzip_root_dir/$unzip_smweb_dir/esim-personal-web.war  $unzip_root_dir/$unzip_smweb_dir/web.war
	#fi
	
	#拷贝覆盖
	PRINT_LOG "拷贝新应用到指定目录"
	
	#如果对应更新包文件存在才拷贝
	#开始检查dp+应用
	check_dirAndfile_if_exist "$unzip_root_dir/$unzip_dppuls_dir"
	REVAL1=$?
	if [[ $REVAL1 -eq 0 ]]; then
        echo_and_printf $info "需要更新tomcat-dppuls"
		echo_and_printf $print "开始拷贝到$dppuls_app_dir/"
		cp -rf $unzip_root_dir/$unzip_dppuls_dir/*        $dppuls_app_dir/
		#更新回滚文件
		alterConfig $rollback_file rsprollback dppuls yes
    else
       echo_and_printf $info "不需要更新$dppuls_app_dir"
	   #更新回滚文件
	   alterConfig $rollback_file rsprollback dppuls no
    fi
	
	#开始检查consumer_esp1应用
	check_dirAndfile_if_exist "$unzip_root_dir/$unzip_es_dir"
	REVAL2=$?
	if [[ $REVAL2 -eq 0 ]]; then
		echo_and_printf $info "需要更新consumer_esp1"
		echo_and_printf $print "开始拷贝到$es_app_dir/"
		cp -rf $unzip_root_dir/$unzip_es_dir/*   $es_app_dir/
		#更新回滚文件
		alterConfig $rollback_file rsprollback es yes
    else
       echo_and_printf $info "不需要更新$es_app_dir/"
	   #更新回滚文件
	   alterConfig $rollback_file rsprollback es no
    fi
	
		#开始检查web应用
	check_dirAndfile_if_exist "$unzip_root_dir/$unzip_smweb_dir"
	REVAL3=$?
	if [[ $REVAL3 -eq 0 ]]; then
        echo_and_printf $info "需要更新tomcat-smweb"
		echo_and_printf $print "开始拷贝到$smweb_app_dir/"
		cp -rf $unzip_root_dir/$unzip_smweb_dir/*     $smweb_app_dir/
		#更新回滚文件
		alterConfig $rollback_file rsprollback web yes
    else
       echo_and_printf $info "不需要更新$smweb_app_dir/"
	   #更新回滚文件
	   alterConfig $rollback_file rsprollback web no
    fi
	
}


#解压后目录检查,如果三个目录都不存在则退出
function check_after_unzip(){
	if [ ! -d "$unzip_root_dir/$unzip_dppuls_dir" ] && [ ! -d "$unzip_root_dir/$unzip_es_dir" ] && [ ! -d "$unzip_root_dir/$unzip_smweb_dir" ];then
      echo_and_printf $error "解压后目录下$unzip_root_dir对应目录都不存在,请检查"
	  exit 0
	fi
}

#解压
function unzip_archive(){
	echo_and_printf $print "[unzip_archive解压]"
	#v_time=`date "+%Y%m%d"`
	#update_app_${v_time}.zip
	#压缩包名
	if [[ $1 == '' ]];then
		echo_and_printf $error "未指定更新包xx.zip"
		help
		exit 0
	fi
	
	archiveName=$1
	if [ -f $archiveName ];then
		echo_and_printf $print "文件存在,文件名:$archiveName"
	else
		echo_and_printf $error "文件不存在,请指定正确的zip全路径"
		exit 1
	fi
	
	#如果目录不存在先创建目录$unzip_root_dir
	check_dir "$unzip_root_dir"
	REVAL=$?
	if [[  $REVAL -eq 1   ]];then
		mkdir -p $unzip_root_dir
	fi
	
	#解压
	unzip $archiveName -d $unzip_root_dir

	REVAL=$?
    if [[ $REVAL -eq 0 ]]; then
        echo_and_printf $print "unzipSuccess解压成功"
		check_after_unzip
    else
        echo_and_printf $error "unzipFailed解压失败"
		exit 1
    fi
	
}

#拷贝以后删除 $unzip_root_dir 目录下的文件,不删除 压缩包
function delete_after_copy(){
	check_dir "$unzip_root_dir/$unzip_dppuls_dir"
	REVAL1=$?
	if [[ $REVAL1 -eq 0 ]]; then
        echo_and_printf $print "删除$unzip_root_dir/$unzip_dppuls_dir目录"
		rm -rf  $unzip_root_dir/$unzip_dppuls_dir
    fi
	
	check_dir "$unzip_root_dir/$unzip_es_dir"
	REVAL2=$?
	if [[ $REVAL2 -eq 0 ]]; then
        echo_and_printf $print "删除$unzip_root_dir/$unzip_es_dir目录"
		rm -rf  $unzip_root_dir/$unzip_es_dir
    fi
	
	check_dir "$unzip_root_dir/$unzip_smweb_dir"
	REVAL3=$?
	if [[ $REVAL3 -eq 0 ]]; then
        echo_and_printf $print "删除$unzip_root_dir/$unzip_smweb_dir目录"
		rm -rf  $unzip_root_dir/$unzip_smweb_dir
    fi

}

#只回滚更新过的应用
function exec_rollback(){
	#检查之前是否已经备份到了指定目录
	v_time=`date "+%Y-%m-%d"`
	bakstr="[Start rollback rsp project:]"${v_time}
	echo ${bakstr}

	#备份的目录名 otaBackup-2020-02-14-17-56-42
	#最近的回滚目录从回滚文件中获取
	getConfig $rollback_file rsprollback latestdir
	echo_and_printf $info "从目录$value中回滚"
	dirName=$value

	esim_consumer_dir=${dirName}/$unzip_dppuls_dir
	esim_consumer_es_dir=${dirName}/$unzip_es_dir
	esim_consumer_web_dir=${dirName}/$unzip_smweb_dir
	
	#检查目录下是否存在对应的文件,任何一个工程没有备份都不执行回滚操作
	#开始检查dp+应用
	getConfig $rollback_file rsprollback dppuls
	echo_and_printf $info "是否需要回滚dppuls:$value"
	needrbdppuls=$value
	if [[ $needrbdppuls = "yes" ]];then
		check_dirAndfile_if_exist ${esim_consumer_dir}
		REVAL1=$?
		if [[ $REVAL1 -eq 0 ]]; then
			echo_and_printf $print "可以回滚tomcat-dppuls"
		else
			echo_and_printf $print "需要回滚dppuls但是回滚目录${esim_consumer_dir}不存在,清先检查"
			exit 0
		fi
	elif [[ $needrbdppuls = "no" ]];then
		echo_and_printf $print "不需要回滚tomcat-dppuls"
	else
		echo_and_printf $error "回滚文件可能存在错误请检查"
		exit 0
	fi
	
	
		#判断是否需要回滚web
	getConfig $rollback_file rsprollback es
	echo_and_printf $info "是否需要回滚es:$value"
	needrbes=$value
	#开始检查es应用
	if [[ $needrbes = "yes" ]];then
		check_dirAndfile_if_exist ${esim_consumer_es_dir}
		REVAL2=$?
		if [[ $REVAL2 -eq 0 ]]; then
			echo_and_printf $print "可以回滚consumer_esp1"
		else
			echo_and_printf $print "需要回滚consumer_esp1,但是回滚目录${esim_consumer_es_dir}不存在,请先检查"
			exit 0
		fi
	elif  [[ $needrbes = "no" ]];then
		echo_and_printf $print "不需要回滚consumer_esp1"
	else
		echo_and_printf $error "回滚文件可能存在错误请检查"
		exit 0
	fi
	
	
	getConfig $rollback_file rsprollback web
	echo_and_printf $info "是否需要回滚web:$value"
	needrbweb=$value
	if [[ $needrbweb = "yes" ]];then
		#开始检查dp+ web应用
		check_dirAndfile_if_exist ${esim_consumer_web_dir}
		REVAL3=$?
		if [[ $REVAL3 -eq 0 ]]; then
			echo_and_printf $print "可以回滚tomcat-smweb"
		else
			echo_and_printf $print "需要回滚tomcat-smweb,但是回滚目录${esim_consumer_web_dir}不存在,请先检查"
			exit 0
		fi
	elif  [[ $needrbweb = "no" ]];then
		echo_and_printf $print "不需要回滚tomcat-smweb"
	else
		echo_and_printf $error "回滚文件可能存在错误请检查"
		exit 0
	fi
	
	echo_and_printf $print "开始执行回滚操作"
	#拷贝某个文件到刚创建的备份目录
	#判断是否需要回滚dppuls
	if [[ $needrbdppuls = "yes" ]];then
		#停止 删除 拷贝
		echo_and_printf $info "回滚dppuls"
		#删除新应用
		rm -rf $dppuls_app_dir/*
		cp -rf  ${esim_consumer_dir}/*      $dppuls_app_dir/
	else
		echo_and_printf $info "不回滚dppuls"
	fi
	

	if [[ $needrbweb = "yes" ]];then
		#停止 删除 拷贝
		echo_and_printf $info "回滚web"
		#删除新应用
		rm -rf $smweb_app_dir/*
		#复制旧应用
		cp -rf  ${esim_consumer_web_dir}/*  $smweb_app_dir/
	else
		echo_and_printf $info "不回滚web"
	fi
	
	#判断是否需要回滚es1
	if [[ $needrbes = "yes" ]];then
		#停止 删除 拷贝
		echo_and_printf $info "回滚es"
		#删除新应用
		rm -rf $es_app_dir/consumer-esp1.jar
		#复制旧应用
		cp -rf  ${esim_consumer_es_dir}/*   $es_app_dir/
		#启动服务
	else
		echo_and_printf $info "不回滚es"
	fi
	
}

#如果更新后发现存在问题则回滚到旧版本,回滚操作只限当天操作(通过日期查找对应的版本)
function rollback(){
	confirm_or_exit "确定进行回滚吗?[y or n]"
	exec_rollback
}

function confirm_or_exit(){
	confirmMsg=$1
	read -p "$confirmMsg" user_input_msg
	if [[ "y" = $user_input_msg || "Y" = $user_input_msg ]];then
		echo yes
	elif [[ "n" = $user_input_msg || "N" = $user_input_msg ]];
	then
		echo no
		exit 0
	else
	    echo no
		exit 0
	fi
}

#脚本执行流程
function cicd() {
	echo_and_printf $print "【begin】"
	confirm_or_exit "确定停止并更新应用吗?[y or n]"

	#解压
	echo_and_printf $print "【解压更新包xx.zip】"
	unzip_archive $1
	
	#停止服务
	echo_and_printf $print "【停止服务】"
	stop
	
	#初始化回滚文件
	echo_and_printf $print "【初始化回滚文件】"
	init_rollback_dir
	
	 #覆盖之前先备份旧版本应用
	echo_and_printf $print "【备份旧版本应用】"
	backup
	
	#备份后删除需要更新的旧版本的应用
	echo_and_printf $print "【删除旧版本应用】"
	deleteOldProjectIFNeedUpdate
	#复制新应用
	echo_and_printf $print "【拷贝新版本应用到相应目录】"
	copy
    sleep 2
	
	#启动服务
	echo_and_printf $print "【启动服务】"
	start
	#校验服务状态(通过查询对应服务的日志)
	
	#delete_after_copy;删除解压的$unzip_root_dir下的目录
	echo_and_printf $print "【删除解压的更新包】"
	delete_after_copy
	
	echo_and_printf $print "【end】"
}

#$1代表颜色编号,$2代表输出内容
cecho(){
	echo -e "\033[$1m$2\033[0m"
}

function gui_test(){
	OPTION=$(whiptail --title "Test Menu Dialog" --menu "Choose your option" 15 60 4 \ "1" "Grilled Spicy Sausage" \ "2" "Grilled Halloumi Cheese" \ "3" "Charcoaled Chicken Wings" \ "4" "Fried Aubergine" 3>&1 1>&2 2>&3) 
	exitstatus=$? 
	if [ $exitstatus = 0 ];then 
		echo "Your chosen option:" $OPTION 
	else 
	 echo "You chose Cancel." 
	fi 
}

function gui_cicd(){
	OPTION=$(whiptail --title "RSP Menu Dialog" --menu "Choose your option" 15 60 4 \ "1" "停止->备份->更新->启动" \ "2" "启动所有服务" \ "3" "停止所有服务" \ "4" "查看服务状态" \ "5" "服务回滚" 3>&1 1>&2 2>&3) 
	exitstatus=$? 
	if [ $exitstatus = 0 ];then 
		echo "Your chosen option:" $OPTION 
	else 
	 echo "You chose Cancel." 
	 exit 0
	fi 
	if [[ $OPTION -eq 1 ]];then
		gui_form
	fi
}

#表单输入框
function gui_form(){
	zipName=$(whiptail --title "input updatePackage's absolute path" --inputbox "What is your update_app_*'s name?" 10 60 "please input xx.zip absolute path" 3>&1 1>&2 2>&3) 
	exitstatus=$? 
	if [ $exitstatus = 0 ]; then 
	  echo "Your xx.zip name is:" $zipName 
	else 
		echo "You chose Cancel." 
		exit 0
	fi

	cicd $zipName 
}

function PRINT_LOG(){
	#当前时间 : 2020-02-14-17-56-42
	current_time=`date "+%Y-%m-%d-%H-%M-%S"`
	#当前日期 : 2020-02-14
	v_time=`date "+%Y-%m-%d"`
	bakstr="[date:]"${v_time}

	message=$1
	CRTDIR=$(pwd)
	#日志全路径
	logpath=$CRTDIR/logs/rsp_check_log_${v_time}.log
	mkdir -p $CRTDIR/logs
	if [ ! -f "$logpath" ]; then
		cecho 36 "日志文件不存在,创建文件$logpath" 
		touch "$logpath"
	fi
	echo $current_time:"$message" >> $logpath
}

##############################################
function write_rollback_content(){
	echo "[rsprollback]" >> $rollback_file
	echo "latestdir=init" >> $rollback_file
	echo "dppuls=no" >> $rollback_file
	echo "web=no" >> $rollback_file
	echo "es=no" >> $rollback_file
}
function init_rollback_file(){
	if [ -f  $rollback_file ];then
		echo_and_printf $print "$rollback_file文件已经存在"
	else
		echo_and_printf $info "$rollback_file文件不存在"
		echo_and_printf $info "创建文件$rollback_file"
		touch "$rollback_file"
		REVAL=$?
		if [[ $REVAL -eq 0 ]]; then
			echo_and_printf $info "创建文件$rollback_file成功"
			echo_and_printf $print "初始化内容写入$rollback_file"
			write_rollback_content
		else
			echo_and_printf $error "创建文件$rollback_file失败,请检查"
			exit_and_warning 1
			exit 0
		fi
	fi


}

function exit_and_warning(){
	exitcode=$1
	#message=$2
	if [[ $exitcode -eq 0 ]];then
		echo_and_printf $print "服务正常退出"
		exit 0
	else
		echo_and_printf $error "服务可能已经停止,请检查并启动"
		exit 0
	fi
	
}

#rollback_file用于存放最近更新的目录,用于回滚
#备份和拷贝的时候会更新回滚文件,备份的时候写备份目录,拷贝的时候需要检查是否更新了某个应用
function init_rollback_dir(){
	
	if [  -d $rollback_file_dir ];then
		echo_and_printf $print "$rollback_file_dir目录已经存在"
		init_rollback_file
	else
		echo_and_printf $print "$rollback_file_dir目录不存在"
		echo_and_printf $print "创建目录$rollback_file_dir"
		mkdir -p $rollback_file_dir
		REVAL=$?
		if [[ $REVAL -eq 0 ]]; then
			echo_and_printf $info "创建目录$rollback_file_dir成功"
			init_rollback_file
		else
			echo_and_printf $error "创建目录$rollback_file_dir失败,请检查"
			exit_and_warning 1
			exit 0
		fi
		
	fi


}

#################读写配置文件开始##################
getLine(){
    file=$1
	if [ ! -f $file ]; then
		echo_and_printf $error "文件$file不存在,请先检查"
		exit 0
	fi
    section=$2
    option=$3
    tl=$(cat $file | wc -l)
    lines=$(sed -n -e "/$section/=" $file)
    tn=0
    lindedata=''
    for i in $lines
    do
        nlines=$(cat $file | awk -v i=$i '/^\[.*\]$/ {if(NR>i)print NR}')
        nline=$(echo $nlines | awk '{print $1}')
        if [ -n $nline ]
        then
            sl=$((i+1))
            el=$((nline-1))
            if [ $el -eq -1 ]
            then
                el=$tl
            fi
            #echo $tl $nline
            if [ $((tl-nline)) -ge 0 ]
            then
                rs=$(cat $file | head -n $el | tail -n "+"$sl | grep -n '^'$option | tail -n 1)
                #echo $rs
                flag=$(awk -v a="$rs" -v b=":" 'BEGIN{print index(a,b)}')
                if [ $flag -ne 0 ]
                then
                    px=$(echo $rs | awk -F ':' '{print $1}')
                    tn=$((i+px))
                fi
            fi
        fi
    done
    return $tn
 
}
getConfig(){
    file=$1
    section=$2
    option=$3
    getLine $file $section $option
    line=$?
    #echo $line
    content=$(cat $file | awk '{if(NR=="'$line'"){print}}')
    #echo $content
    value=$(echo $content | awk -F '=' '{print $2}')
    #echo $value
 
}
 
alterConfig(){
    file=$1
    section=$2
    option=$3
    rvalue=$4
    getConfig $file $section $option
    str=$line's#'$value'#'$rvalue'#'
    sed -i "$str" $file
}
#################读写配置文件结束##################

function test_readwriteConfigFile(){
	echo #########latestdir
	getConfig test.conf rsprollback latestdir
	echo $line $value
	alterConfig test.conf rsprollback latestdir "/opt/rsp_backups/rspBackup-2020-09-99"
	#echo $line $value
	getConfig test.conf rsprollback latestdir
	echo $line $value
	echo #########dppuls
	getConfig test.conf rsprollback dppuls
	echo $line $value
	alterConfig test.conf rsprollback dppuls yes
	#echo $line $value
	getConfig test.conf rsprollback dppuls
	echo $line $value
	echo #########web
	getConfig test.conf rsprollback web
	echo $line $value
	alterConfig test.conf rsprollback web yes
	#echo $line $value
	getConfig test.conf rsprollback web
	echo $line $value
	echo #########es
	getConfig test.conf rsprollback es
	echo $line $value
	alterConfig test.conf rsprollback es yes
	#echo $line $value
	getConfig test.conf rsprollback es
	echo $line $value
}
 
case "$1" in 
    start)
        start
    ;;
    stop)
        stop
    ;;
	restart)
		 restart
	;;
    status)
        status
    ;;
	 backup)
        backup
    ;;
	 cicd)
        cicd $2
    ;;
	copy)
		 copy
	;;
	config)
		 test_readwriteConfigFile
	;;
	rollback)
		rollback
	;;
	gui)
		gui_cicd
	;;
	
	printf)
		 PRINT_LOG $2
	;;
	*)
        help
        exit 1
    ;;
esac
