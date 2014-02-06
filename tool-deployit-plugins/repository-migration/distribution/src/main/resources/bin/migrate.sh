#!/bin/sh
#
# Shell script to start the Deployit Server
#

absdirname ()
{
  _dir="`dirname \"$1\"`"
  cd "$_dir"
  echo "`pwd`"
}

resolvelink() {
  _dir=`dirname "$1"`
  _dest=`readlink "$1"`
  case "$_dest" in
  /* ) echo "$_dest" ;;
  *  ) echo "$_dir/$_dest" ;;
  esac
}

# Get Java executable
if [ -z "$JAVA_HOME" ] ; then
  JAVACMD=java
else
  JAVACMD="${JAVA_HOME}/bin/java"
fi

# Get JVM options
if [ -z "$DEPLOYIT_SERVER_OPTS" ] ; then
  DEPLOYIT_SERVER_OPTS="-Xmx1024m -XX:MaxPermSize=128m"
fi

# Get logging-related options
if [ -z "$DEPLOYIT_SERVER_LOG_OPTS" ] ; then
  DEPLOYIT_SERVER_LOG_OPTS="-Dlogback.configurationFile=conf/logback.xml -Dderby.stream.error.file=log/derby.log"
fi

# Get Deployit server home dir
if [ -z "$DEPLOYIT_SERVER_HOME" ] ; then
  self="$0"
  if [ -h "$self" ]; then
    self=`resolvelink "$self"`
  fi
  BIN_DIR=`absdirname "$self"`
  DEPLOYIT_SERVER_HOME=`dirname "$BIN_DIR"`
elif [ ! -d "$DEPLOYIT_SERVER_HOME" ] ; then
  echo "Directory $DEPLOYIT_SERVER_HOME does not exist"
  exit 1
fi

cd "$DEPLOYIT_SERVER_HOME"

# Build Deployit server classpath
DEPLOYIT_SERVER_CLASSPATH='conf:ext'
for each in `ls hotfix/*.jar lib/*.jar plugins/*.jar 2>/dev/null`
do
  if [ -f $each ]; then
    DEPLOYIT_SERVER_CLASSPATH=${DEPLOYIT_SERVER_CLASSPATH}:${each}
  fi
done

ls plugins/* > /dev/null 2>&1
if [ $? -eq 0 ]; then
  for expandedPluginDir in `ls plugins/*`
  do
    if [ -d $expandedPluginDir ]; then
      DEPLOYIT_SERVER_CLASSPATH=${DEPLOYIT_SERVER_CLASSPATH}:${expandedPluginDir}
    fi
  done
fi

# Run Deployit server
$JAVACMD $DEPLOYIT_SERVER_OPTS $DEPLOYIT_SERVER_LOG_OPTS -classpath "${DEPLOYIT_SERVER_CLASSPATH}" com.xebialabs.deployit.tools.RepositoryMigration "$@"