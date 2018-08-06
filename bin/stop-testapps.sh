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
        pids=`find ${PID_DIR}/test/*.pid`
        for pid in ${pids}
        do
            pid=`cat ${pid}`
            echo "shutting down pid=${pid}."
#            kill pid -9
        done
}

function func_clear_log
{
        echo "---clear test logs.---"
        if [ -f  ${LOGS_DIR}/test ]; then
                echo "rm $LOGS_DIR/$LOG_FILE."
                rm -r -f ${LOGS_DIR}/test
        fi

        if [ -f  {$PID_DIR}/test ]; then
                echo "rm $PID_DIR/$PID_FILE."
                rm -r -f ${PID_DIR}/test
        fi

        # will add validation log file.
}

func_close_process
func_clear_log
