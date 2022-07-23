#!/bin/sh

#The safenetServer must be started with the start.sh script to execute sh stop.sh to kill the process
jarFileName=
function recursive_dir(){
	for file in `ls $1`
	do
		if [ -d $1"/"$file ];then
			a=1
		else
			filename=$file
			if [[ $file =~ \.jar$ ]];then
				echo "$filename"
				return 0
			fi
		fi
   done
}

function killProcessByName(){
	processName=$1
	echo -e "kill process start"
	pids=$(ps -ef|grep $processName|grep -v 'grep'|awk '{print $2}')
	for i in ${pids}
	 do
	   echo kill pid=$i,processName=$processName
	  `kill -9 $i`
	 done
	echo -e "stop process success"
}


currentpath=$(cd "$(dirname "$0")";pwd)
echo "currentpath:$currentpath"

#The upper level directory (the directory where xxx.jar is located)
upperpath=$(dirname $(cd "$(dirname "$0")";pwd))




jarFileName=$(recursive_dir $currentpath)

echo "jarFileName=$jarFileName"

if [ -z "$jarFileName" ]; then
	echo "jarFileName is null,please check!"
	exit 0
fi


jarFilePath=$currentpath"/"$jarFileName

echo "jarFilePath=$jarFilePath"


killProcessByName $jarFilePath
