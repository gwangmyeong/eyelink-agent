#!/usr/bin/env bash

this="${BASH_SOURCE-$0}"
while [ -h "$this" ]; do
  ls=`ls -ld "$this"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    this="$link"
  else
    this=`dirname "$this"`/"$link"
  fi
done

# convert relative path to absolute path
bin=`dirname "$this"`
script=`basename "$this"`
bin=`cd "$bin">/dev/null; pwd`
this="$bin/$script"

BASE_DIR=`dirname "$bin"`

LOGS_DIR=$BASE_DIR/logs
LOG_FILE=testapp.log

PID_DIR=$BASE_DIR/logs/pid
PID_FILE=testapp.pid

testapp_IDENTIFIER=eyelink-testapp
IDENTIFIER=maven.eyelink.identifier=$testapp_IDENTIFIER

function func_close_process
{
        echo "---$testapp_IDENTIFIER destroy started..---"
        pidFiles=`find ${PID_DIR}/test/*.pid`
        for pidFile in ${pidFiles}
        do
            pid=`cat ${pidFile}`
            echo "shutting down pid=${pid}."
            kill -9 ${pid}
        done
}

function func_clear_log
{
        echo "---clear test logs.---"
        echo "${LOGS_DIR}/test will be deleted"
        rm -r -f ${LOGS_DIR}/test
	echo "${PID_DIR}/test will be deleted"
	rm -r -f ${PID_DIR}/test
        # will add validation log file.
}

func_close_process
func_clear_log
