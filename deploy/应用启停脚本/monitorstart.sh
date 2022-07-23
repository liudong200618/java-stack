#!/bin/sh

#must exec this in crontab
#source /etc/profile
JAVA_HOME=/usr/local/jdk1.8.0_301

#There can only be one jar file in the root directory
jarFileName=
#Iterate over all of a directory .jar
function recursive_dir(){
	for file in `ls $1`
	do
		if [ -d $1"/"$file ];then
			a=1
		else
			filename=$file
			#compare
			if [[ $file =~ \.jar$ ]];then
				echo "$filename"
				return 0
			fi
		fi
   done

}

#Find the number of files ending in .jar in a directory
function count(){
	mydir=$1
	echo "dir=$mydir"
	count=$(ls -l  $mydir|grep "^-"|grep *.jar|wc -l)
	echo count=$count
}


#Cannot get JAVA_HOME environment variable in crontab
if [ -z "$JAVA_HOME" ] ; then
  if [ -f "$JAVA_HOME/bin/java" ] ; then
    echo "JAVA_HOME is defined,$JAVA_HOME"
  else
    echo "Error: JAVA_HOME is not defined."
	exit 1
  fi
fi


JAVA="$JAVA_HOME/bin/java"


#Current directory (the directory where the shell script is located)
#can not use this when in crontab 
#currentpath="$PWD"

currentpath=$(cd "$(dirname "$0")";pwd)
echo "currentpath:$currentpath"

#The upper level directory (the directory where xxx.jar is located)
upperpath=$(dirname $(cd "$(dirname "$0")";pwd))

jarFileName=$(recursive_dir $currentpath)

if [ -z "$jarFileName" ]; then
	echo "jarFileName is null,please check!"
	exit 0
fi


echo "jarFileName=$jarFileName"

#absolute currentpath of jar
jarFilePath=$currentpath"/"$jarFileName


#Check whether the process already exists before starting
pids=$(ps -ef|grep $jarFilePath|grep -v 'grep'|awk '{print $2}')
for i in ${pids}
 do
   echo "The process already exists and cannot be started repeatedly!$jarFilePath"
   exit 0
 done


#执行命令启动服务
cd $currentpath
"$JAVA" -jar  $jarFilePath > /dev/null 2>&1 &

