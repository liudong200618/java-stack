#!/bin/sh
#Date: 2022/06/29
#Description: The safenet server must be started with the start.sh script to execute sh stop.sh to kill the process


# There can only be one jar file in the root directory
jarFileName=


# Iterate over all of a directory .jar
function recursive_dir(){
  for file in `ls $1`
  do  
    if [ -d $1"/"$file ];then
      a=1 
    else
      if [[ $file =~ \.jar$ ]];then
        echo $file
        return 0
      fi  
    fi
  done
}

function killProcessByName(){
  echo -e " - Kill process start"
  
  processName=$1
  pids=$(ps -ef | grep $processName | grep -v 'grep' | awk '{print $2}')
  
  if [ -z "$pids" ]; then
    echo "   - No process found"
  else
    for i in ${pids}
    do  
      echo -n "   - Kill process: pid=$i, processName=$processName"
      
      `kill -9 $i`
      sleep 0.5
       
      echo "; SUCCESS"
    done
  fi  

  echo -e " - Kill process end"
}


echo "Try to find jar file and stop it..."

# Current directory (the directory where the shell script is located)
# can not use this when in crontab 
#currentpath="$PWD"
currentpath=$(cd "$(dirname "$0")";pwd)
echo " - Current path is: $currentpath"

# Try to find first file ending in .jar in currentpath
jarFileName=$(recursive_dir $currentpath)

if [ -z "$jarFileName" ]; then
  echo " - Not found file ending in .jar in path: $currentpath"
  exit 0
else
  jarFileName=$currentpath"/"$jarFileName
  echo " - Found file ending in .jar: $jarFileName"
fi

# Kill process
killProcessByName $jarFileName

