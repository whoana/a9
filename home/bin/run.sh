#!/bin/sh
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_251.jdk/Contents/Home
#export A9_HOME=/Users/whoana/DEV/workspace-vs/a9/home
export A9_HOME=/Users/whoana/DEV/workspace-vs/a9/home
#export ACTIVE_PROFILE=development
export ACTIVE_PROFILE=production
export MEM_OPTS="-Xms1024m -Xmx2048m"

# -Dapple.mint.home=/Users/whoana/DEV/workspace-vs/a9/home 
# -Dspring.profiles.active=development 
# -Dspring.config.location=/Users/whoana/DEV/workspace-vs/a9/home/config/application.yml 
# -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=54333 
# -Dcom.sun.management.jmxremote.authenticate=false 
# -Dcom.sun.management.jmxremote.ssl=false 
# -Dspring.jmx.enabled=true -Djava.rmi.server.hostname=localhost 
# -Dspring.application.admin.enabled=true 
# -Dspring.boot.project.name=a9 
# -Dmanagement.endpoints.jmx.exposure.include=\* 
# -cp /var/folders/j5/7kvv44hn0ndfrd340ydnpf5c0000gn/T/cp_4vu8t6m4a2gl9y2z4g030rx1d.jar apple.mint.agent.A9

RUN_CMD="${JAVA_HOME}/bin/java ${MEM_OPTS}"
RUN_CMD="${RUN_CMD} -Dapp.name=applemint"
RUN_CMD="${RUN_CMD} -Dapple.mint.home=${A9_HOME}"
RUN_CMD="${RUN_CMD} -Dspring.profiles.active=${ACTIVE_PROFILE}"
RUN_CMD="${RUN_CMD} -Dspring.config.location=${A9_HOME}/config/application.yml"
RUN_CMD="${RUN_CMD} -jar ${A9_HOME}/a9-1.0.jar"
#RUN_CMD="${RUN_CMD} -cp ${A9_HOME}/a9-1.0.jar apple.mint.agent.A9"

arg1=$1
if [ "$arg1" = "-f" ]; then
	echo "forground start..."
	${RUN_CMD}
else
    echo "background start..."
	nohup ${RUN_CMD} 1>/dev/null 2>&1 &
fi
