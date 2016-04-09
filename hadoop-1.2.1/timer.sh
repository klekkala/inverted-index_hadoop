#! /bin/bash

STARTTIME=$(date +%s)

echo ''

if [ $# -eq 0 ]; then
	echo 'Not enough arguments. Usage:'
	echo '	timer action [params...]'
	echo '		sa: aka "bin/start-all.sh"'
	echo '		ka: aka "bin/stop-all.sh"'
	echo '		sd: aka "bin/start-dfs.sh"'
	echo '		sm: aka "bin/start-mapred.sh"'
	echo '		km: aka "bin/stop-mapred.sh"'
	echo '		kd: aka "bin/stop-dfs.sh"'
	echo '		restart: aka "bin/stop-all.sh ; bin/start-all.sh"'
	echo '		cp [arg]: aka "sudo netstat -atnp | grep [arg]"'
	echo ' 		cp: aka "sudo netstat -atnp"'
	echo '		lsm: aka "bin/hadoop dfsadmin -safemode leave"'
	echo '		wordcount: aka "bin/hadoop jar hadoop-examples-0.20.205.0.jar wordcount /rime/pg4300.txt /rime/output"'
	echo '		wc [arg1] [arg2] [arg3]: aka "bin/hadoop jar hadoop-examples-0.20.205.0.jar [arg1] [arg2] [arg3]"'
	echo '		cl aka rm -rf logs/*'
	echo '		ch aka rm -rf hdfs/*'
	echo '		cf [arg] aka rm -rf [arg]'

else 
	case $1 in
		cl) echo 'Deleting logs folder'
			rm -rf logs/*
			;;
		ch) echo 'Deleting hdfs folder contents' 
			rm -rf hdfs/*
			;;
		cf) if [ $# eq 2 ]; then
			rm -rf $2
		    else
			echo 'Not enough arguments: Usage'
			echo '		timer cf [folder-to-delete]'
		    fi
			;;
		sa) bin/start-all.sh
			;;
		ka) bin/stop-all.sh
			;;
		sd) bin/start-dfs.sh
			;;
		sm) bin/start-mapred.sh
			;;
		km) bin/stop-mapred.sh
			;;
		kd) bin/stop-dfs.sh
			;;
		lsm) bin/hadoop dfsadmin -safemode leave
			;;
		wordcount) bin/hadoop jar hadoop-examples-0.20.205.0.jar wordcount /rime/pg4300.txt /rime/output
			;;
		wc)	if [ $# -eq 3 ]; then
				bin/hadoop $1 $2 $3
			else
				echo ''
				echo 'Not enough arguments for examples execution. You need at least 2 arguments more:'
				echo ''
				echo 'usage: timer -wc example-name input output'
			fi
			;;
		restart) bin/stop-all.sh;bin/start-all.sh
			;;
		cp) if [ $# -eq 2 ]; then
				sudo netstat -atnp | grep $2
			else
				sudo netstat -atnp
			fi
			;;
		fo) bin/hadoop namenode -format
	esac
fi

ENDTIME=$(date +%s)
echo ''
echo "It took $[$ENDTIME - $STARTTIME] seconds to complete this task..."
