#!/bin/sh
export JAVA_HOME=YOURJAVAHOME
export A9_HOME=YOURA9HOME
#export ACTIVE_PROFILE=development
export ACTIVE_PROFILE=production
export MEM_OPTS="-Xms1024m -Xmx2048m"

RUN_CMD="${JAVA_HOME}/bin/java ${MEM_OPTS}"
RUN_CMD="${RUN_CMD} -Dapp.name=applemint"
RUN_CMD="${RUN_CMD} -Dapple.mint.home=${A9_HOME}"
RUN_CMD="${RUN_CMD} -Dapple.mint.log.dir=YOURLOGHOME"
RUN_CMD="${RUN_CMD} -Dspring.profiles.active=${ACTIVE_PROFILE}"
RUN_CMD="${RUN_CMD} -Dspring.config.location=${A9_HOME}/config/application.yml"
RUN_CMD="${RUN_CMD} -jar ${A9_HOME}/a9-1.0.jar"

arg1=$1
if [ "$arg1" = "-f" ]; then
	echo "forground start..."
	${RUN_CMD}
else
    echo "background start..."
	nohup ${RUN_CMD} 1>/dev/null 2>&1 &
fi
