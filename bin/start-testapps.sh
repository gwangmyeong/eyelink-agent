#!/usr/bin/env bash

UNAME=`uname`
OS_TYPE="linux";
if [[ "$UNAME" == "Darwin" ]]; then
        OS_TYPE="mac"
fi

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
AGENT_DIR=$BASE_DIR/agent
AGENT_BOOTSTRAP_DIR=$AGENT_DIR
TESTAPP_DIR=$BASE_DIR/testapp

CONF_DIR=$BASE_DIR/conf
CONF_FILE=elagent.properties

LOGS_DIR=$BASE_DIR/logs
LOG_FILE=testapp.log

PID_DIR=$BASE_DIR/logs/pid
PID_FILE=testapps.pid

TESTAPP_IDENTIFIER=eyelink-testapp
IDENTIFIER=maven.eyelink.identifier=$TESTAPP_IDENTIFIER

UNIT_TIME=5
CHECK_COUNT=36
CLOSE_WAIT_TIME=`expr $UNIT_TIME \* $CHECK_COUNT`

PROPERTIES=`cat $CONF_DIR/$CONF_FILE 2>/dev/null`
KEY_VERSION="elagent.testapp.version"
KEY_PORT="elagent.testapp.port"

TESTAPP_SIZE=$1
if [ -z "$1" ]; then
    TESTAPP_SIZE=5
fi
TESTAPP_ID=$2
if [ -z "$2" ]; then
    TESTAPP_ID="testapp"
fi
TESTAPP_NAME=$3
if [ -z "$3" ]; then
    TESTAPP_NAME="TestApp"
fi

function func_start_testapps
{
        local agentId=$1
        local name=$2
        local port=$3

        version=$( func_read_properties "$KEY_VERSION" )
        maven_opt=$MAVEN_OPTS
        eyelink_agent=$AGENT_BOOTSTRAP_DIR/eyelink-bootstrap-$version-SNAPSHOT.jar
        eyelink_opt="-javaagent:$eyelink_agent -Deyelink.agentId=$agentId -Deyelink.applicationName=$name -Delagent.testapp.port=$port -Delagent.testapp.context.path=/"
        export MAVEN_OPTS=$eyelink_opt

        pid=`nohup mvn -f $TESTAPP_DIR/pom.xml clean package tomcat7:run -D$IDENTIFIER -Dmaven.testapp.version=$version > $LOGS_DIR/test/$agentId".log" 2>&1 & echo $!`
        echo $pid > $PID_DIR/test/$agentId".pid"
        export MAVEN_OPTS=$maven_opt

        echo "---$TESTAPP_IDENTIFIER initialization started. pid=$pid.---"
}

function func_read_properties
{
        key="^"$1"="

        for entry in $PROPERTIES;
        do
                value=`echo $entry | grep $key`

                if [ ! -z $value ]; then
                        echo $entry | cut -d '=' -f2
                        break
                fi
        done
}

for number in $(seq 1 ${TESTAPP_SIZE})
do
    echo "current loop $number"

    agentId="$TESTAPP_ID$number"
    applicationName="$TESTAPP_NAME$number"
    port=$((8080 + ${number}))
    func_start_testapps ${agentId} ${applicationName} ${port}
done