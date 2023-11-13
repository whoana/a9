# cp ../target/a9-1.0.jar ./build-tar-src
# cd ./build-tar-src
# tar -cvf a9-install.tar a9-install
# cd ..
# rm a9-install.tar
# cp ./build-tar-src/a9-install.tar .
BUILD_TAR_HOME=/Users/whoana/DEV/workspaces/vsc/a9/install-linux
TAR_SRC_HOME=$BUILD_TAR_HOME/build-tar-src
cd $TAR_SRC_HOME
tar -cvf a9-install.tar a9-install
cd $BUILD_TAR_HOME
rm a9-install.tar
cp $TAR_SRC_HOME/a9-install.tar .
