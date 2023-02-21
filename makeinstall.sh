# --------------------------------------------------------
# 설치기본위치 
# --------------------------------------------------------
A9_CORE_HOME=/Users/whoana/DEV/workspace-vs/a9-core
A9_HOME=/Users/whoana/DEV/workspace-vs/a9
INSTALL_HOME="${A9_HOME}/home"
BUILD_HOME="${A9_HOME}/install-linux"

# --------------------------------------------------------
# maven 빌드 
# --------------------------------------------------------
cd ${A9_CORE_HOME}
mvn clean package install

cd ${A9_HOME}
mvn clean package

# --------------------------------------------------------
# 기존 소스 삭제  
# --------------------------------------------------------
# rm -R ${INSTALL_HOME}/lib/a9-core-1.0.jar
# rm -R ${INSTALL_HOME}/classes
rm -R ${INSTALL_HOME}/a9-1.0.jar
rm -R ${BUILD_HOME}/build-tar-src/a9-install/a9-1.0.jar
# --------------------------------------------------------
# lib, classes 카피 
# --------------------------------------------------------
# cp ${A9_CORE_HOME}/target/a9-core-1.0.jar ${INSTALL_HOME}/lib
# cp -R ${A9_HOME}/target/classes ${INSTALL_HOME}/
cp ${A9_HOME}/target/a9-1.0.jar ${INSTALL_HOME}/
cp ${A9_HOME}/target/a9-1.0.jar ${BUILD_HOME}/build-tar-src/a9-install/

# --------------------------------------------------------
# MANIFEST.MF, a9-1.0.jar 생성
# MANIFEST.MF 내의 클래스 패스는 a9-1.0.jar 의 위치를 기준으로 상태
# 패스로 인식한다. 
# --------------------------------------------------------
# jar -cfm ${INSTALL_HOME}/a9-1.0.jar manifest.i.mf

${BUILD_HOME}/build-tar.sh