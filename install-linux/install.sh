#!/bin/sh
echo "------------------------------------------------------------------------------------------"
echo "A9에이전트 설치를 시작합니다."
date
echo "------------------------------------------------------------------------------------------"

INSTALL_FILE=$1
if [ -z $INSTALL_FILE ]; then
	INSTALL_FILE=a9-install.tar
fi

if [ -z $INSTALL_FILE ]; then
	echo "현재위치에 설치파일 $INSTALL_FILE 가 존재하지 않습니다."
	echo "설치파일 확인 후 다시 진행해 주세요."
	exit
fi

echo "설치파일:$INSTALL_FILE"

echo "설치 위치를 입력해 주세요."
echo "INSTALL_PATH:"
read INSTALL_PATH
if [ -d $INSTALL_PATH ]; then
	rm -R $INSTALL_PATH
fi
mkdir -p $INSTALL_PATH

tar -xvf $INSTALL_FILE
mv a9-install/* $INSTALL_PATH 
rm -R a9-install

sleep 2

cd $INSTALL_PATH

echo "------------------------------------------------------------------------------------------"
echo "A9에이전트 설정을 시작합니다."
echo "------------------------------------------------------------------------------------------"
A9_HOME=$(pwd)
echo "A9_HOME=$A9_HOME"
echo ""

if [ -d $A9_HOME/config ]; then
	echo "A9에이전트 세팅을 시작합니다."
else
	echo "A9에이전트 설치위치가 아닙니다."
	echo "A9에이전트 설치위치로 이동 후 세팅을 진행해 주십시오."
	exit
fi

echo "------------------------------------------------------------------------------------------"
echo "JAVA_HOME 환경변수를 세팅해 주십시오."
echo "버전은 1.8 이상이어야 합니다."
echo "------------------------------------------------------------------------------------------"
echo "JAVA_HOME:"
read JAVA_HOME
if [ -d $JAVA_HOME ]; then
	if [ -e $JAVA_HOME/bin/java ]; then
		echo "JAVA_HOME 을 세팅하였습니다."
		echo "JAVA_HOME=$JAVA_HOME"
	else
		echo "유효한 JAVA_HOME 이 아닙니다. 확인 후 다시 진행해 주세요."
		exit
	fi
else
	echo "입력하신 디렉토리가 존재하지 않습니다. :$JAVA_HOME"
	exit
fi

echo "------------------------------------------------------------------------------------------"
echo "LOG_HOME 환경변수를 세팅해 주십시오."
echo "A9에이전트가 로그파일을 기록하는 디렉토리를 의미합니다."
echo "지정하지 않으면(Enter) A9_HOME/logs[$A9_HOME/logs] 폴더에 기록합니다."
echo "------------------------------------------------------------------------------------------"
echo "LOG_HOME:"
read LOG_HOME
if [ -z $LOG_HOME ]; then
	LOG_HOME=$A9_HOME/logs
fi
echo "LOG_HOME: $LOG_HOME"

if [ -e $A9_HOME/bin/run.sh ]; then
	rm $A9_HOME/bin/run.sh
fi

if [ -e $A9_HOME/bin/run.sh ]; then
	rm $A9_HOME/bin/stop.sh
fi

awk '{gsub("YOURA9HOME", "'$A9_HOME'", $0); gsub("YOURJAVAHOME", "'$JAVA_HOME'", $0); gsub("YOURLOGHOME", "'$LOG_HOME'", $0); print}' $A9_HOME/tpl/run.sh.tpl > $A9_HOME/bin/run.sh
awk '{gsub("YOURA9HOME", "'$A9_HOME'", $0); print}' $A9_HOME/tpl/stop.sh.tpl > $A9_HOME/bin/stop.sh
chmod +x $A9_HOME/bin/*.sh

echo "시작 종료 쉘파일 세팅을 완료하였습니다."
sleep 1

echo "------------------------------------------------------------------------------------------"
echo "에이전트이름을 입력해 주십시오."
echo "에이전트이름은 IIP서버에 등록할(한) 값입니다."
echo "에이전트이름을 모를 경우 확인 후 진행해 주십시오."
echo "------------------------------------------------------------------------------------------"
echo "AGENT_NM:"
read AGENT_NM
if [ -z $AGENT_NM ]; then
	echo "require AGENT_NM"
	exit
fi
echo "AGENT_NM: $AGENT_NM"

echo "------------------------------------------------------------------------------------------"
echo "에이전트포트를 입력해 주십시오."
echo "에이전트포트는 IIP서버에 등록할(한) 값입니다."
echo "에이전트포트를 모를 경우 확인 후 진행해 주십시오."
echo "------------------------------------------------------------------------------------------"
echo "AGENT_PORT(Enter:9090):"
read AGENT_PORT
if [ -z $AGENT_PORT ]; then
	echo "use default port: 9090"
	AGENT_PORT=9090
fi
echo "AGENT_PORT: $AGENT_PORT"

echo "------------------------------------------------------------------------------------------"
echo "IIP SERVER ADDRESS를 입력해 주십시오."
echo "------------------------------------------------------------------------------------------"
echo "IIP_SERVER_ADDRESS:"
read SERVER_IP
if [ -z $SERVER_IP ]; then
	echo "require IIP_SERVER_ADDRESS"
	exit
fi
echo "IIP_SERVER_ADDRESS: $IIP_SERVER_ADDRESS"

echo "------------------------------------------------------------------------------------------"
echo "IIP SERVER PORT를 입력해 주십시오."
echo "------------------------------------------------------------------------------------------"
echo "IIP_SERVER_PORT:"
read SERVER_PORT
if [ -z $SERVER_PORT ]; then
	echo "require IIP_SERVER_PORT"
	exit
fi
echo "SERVER_PORT: $SERVER_PORT"

if [ -e $A9_HOME/config/config.json ]; then
	rm $A9_HOME/config/config.json
fi
awk '{gsub("AGENTNM", "'$AGENT_NM'", $0); gsub("SERVERADDRESS", "'$SERVER_IP'", $0); gsub("SERVERPORT", "'$SERVER_PORT'", $0); print}' $A9_HOME/tpl/config.json.tpl > $A9_HOME/config/config.json

if [ -e $A9_HOME/config/application.yml ]; then
	rm $A9_HOME/config/application.yml
fi
awk '{gsub("AGENTPORT", "'$AGENT_PORT'", $0); print}' $A9_HOME/tpl/application.yml.tpl > $A9_HOME/config/application.yml

rm -R $A9_HOME/tpl

echo "------------------------------------------------------------------------------------------"
echo "세팅을 완료하였습니다."
echo "$A9_HOME/bin/run.sh 를 실행하여 A9에이전트를 시작시킬 수 있습니다."
echo "$A9_HOME/how-to-install.txt 문서의 4번 항목을 참고해 주십시오."
echo "------------------------------------------------------------------------------------------"
echo "A9에이전트 설치 및 설정을 완료합니다."
date

#sleep 2
#echo "------------------------------------------------------------------------------------------"
#echo "how-to-install.txt"
#echo "------------------------------------------------------------------------------------------"
#cat $A9_HOME/how-to-install.txt
