#!/bin/sh
#date: 2022/06/29
#desc: Start java program

# must exec this in crontab
source /etc/profile

activeProfile=pre

JAVA_HOME=/usr/local/jdk1.8.0_271
JVM_LOG_PATH=/data/kds/kds-biz-api/jvm
JAVA_OPTS="-server \
-Xms1g \
-Xmx6g \
-Xmn1g \
-XX:SurvivorRatio=8 \
-XX:MetaspaceSize=256m \
-XX:MaxMetaspaceSize=512m \
-XX:+UseParallelGC \
-XX:ParallelGCThreads=4 \
-XX:+UseParallelOldGC \
-XX:+UseAdaptiveSizePolicy \
-XX:+PrintGCDetails \
-XX:+PrintTenuringDistribution \
-XX:+PrintGCTimeStamps \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=${JVM_LOG_PATH}/heapdump.log \
-Xloggc:${JVM_LOG_PATH}/gc \
-XX:+UseGCLogFileRotation \
-XX:NumberOfGCLogFiles=5 \
-XX:GCLogFileSize=10M"


# Cannot get JAVA_HOME environment variable in crontab
if [ -z "$JAVA_HOME" ] ; then
  if [ -f "$JAVA_HOME/bin/java" ] ; then
    echo " - JAVA_HOME is defined: $JAVA_HOME"
  else
    echo " - Error: JAVA_HOME is not defined."
    exit 1
  fi
fi



# Iterate over all of a directory .jar
function recursive_dir(){
  for file in `ls $1`
  do
    if [ -d $1"/"$file ];then
      a=1
    else
      # compare
      if [[ $file =~ \.jar$ ]];then
        echo $file
        return 0
      fi
    fi
  done
}

# Find the number of files ending in .jar in a directory
function count(){
  mydir=$1

  count=$(ls -l $mydir | grep "^-" | grep *.jar | wc -l)
}



echo "Try to find jar file and execute it..."

# There can only be one jar file in the root directory
jarFileName=

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

# Check whether the process already exists before starting
pids=$(ps -ef | grep $jarFileName | grep -v 'grep' | awk '{print $2}')
for i in ${pids}
do
  echo " - The process already exists and cannot be started repeatedly with $jarFileName"
  exit 0
done

# Start java program
cd $currentpath
$JAVA_HOME/bin/java $JAVA_OPTS -jar $jarFileName --spring.profiles.active=$activeProfile -verbose:class > $currentpath/run.log 2>&1 &

echo " - Started: $jarFileName"


