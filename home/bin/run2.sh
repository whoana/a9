#!/bin/sh
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
export A9_HOME=/Users/whoana/DEV/workspaces/vsc/a9/home
export ACTIVE_PROFILE=development
export MEM_OPTS="-Xms1024m -Xmx2048m"

CLS="${A9_HOME}/lib/a9-1.0.jar"
CLS="${CLS}:${A9_HOME}/lib/ext/t9-cache-1.0.0.jar"
CLS="${CLS}:${A9_HOME}/lib/ext/infinispan-core-9.4.16.Final.jar"
CLS="${CLS}:${A9_HOME}/lib/ext/infinispan-commons-9.4.16.Final.jar"
CLS="${CLS}:${A9_HOME}/lib/ext/jboss-logging-3.4.1.Final.jar"
CLS="${CLS}:${A9_HOME}/lib/ext/jboss-marshalling-osgi-2.0.6.Final.jar"
CLS="${CLS}:${A9_HOME}/lib/ext/jboss-transaction-api_1.2_spec-1.1.1.Final.jar"
CLS="${CLS}:${A9_HOME}/lib/ext/s9-utility-1.0.0.jar"
CLS="${CLS}:${A9_HOME}/lib/ext/t9-cache-1.0.0.jar"
CLS="${CLS}:${A9_HOME}/lib/ext/caffeine-2.8.0.jar"
CLS="${CLS}:${A9_HOME}/lib/ext/reactive-streams-1.0.3.jar"
CLS="${CLS}:${A9_HOME}/lib/ext/rxjava-2.2.15.jar"

RUN_CMD="${JAVA_HOME}/bin/java"
RUN_CMD="${RUN_CMD} -cp ${CLS}"
RUN_CMD="${RUN_CMD} -Dapp.name=applemint"
RUN_CMD="${RUN_CMD} -Dapple.mint.home=${A9_HOME}"
RUN_CMD="${RUN_CMD} -Drose.mary.home=${A9_HOME}"
RUN_CMD="${RUN_CMD} -Dspring.profiles.active=${ACTIVE_PROFILE}"
RUN_CMD="${RUN_CMD} -Dspring.config.location=${A9_HOME}/config/application.yml"
RUN_CMD="${RUN_CMD} -Dspring.devtools.restart.enabled=true"
RUN_CMD="${RUN_CMD} -Dloader.main=apple.mint.agent.A9 org.springframework.boot.loader.T9L"
echo "${RUN_CMD}"

arg1=$1
if [ "$arg1" = "-f" ]; then
	echo "forground start..."
	${RUN_CMD}
else
    echo "background start..."
	nohup ${RUN_CMD} 1>/dev/null 2>&1 &
fi

CLS="${TEST_HOME}/lib/jdom.jar"
CLS="${CLS}:${TEST_HOME}/lib/log4j-1.2.17a.jar"
CLS="${CLS}:${TEST_HOME}/lib/log4j.jar"
CLS="${CLS}:${TEST_HOME}/lib/mi_common.jar"
CLS="${CLS}:${TEST_HOME}/lib/mi_server_api.jar"
CLS="${CLS}:${TEST_HOME}/lib/mint-solution-mi-product-3.0.0.jar"
CLS="${CLS}:${TEST_HOME}/lib/xercesImpl.jar"
CLS="${CLS}:${TEST_HOME}/lib/xml-apis.jar"