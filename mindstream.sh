#! /bin/bash
# MindStream by Eric Blue (http://eric-blue.com)

echo "Starting MindStream..."
 
export SCRIPT_HOME=`pwd`
export CLASSPATH="$SCRIPT_HOME/lib/*.jar"
export JAR="$SCRIPT_HOME/dist/mindstream.jar"

if [ -z "$JAVA_HOME" ]
 then
   echo "To start MindSteam, JAVA_HOME environment property must be set!"
   java -Djava.ext.dirs=lib -jar $JAR $1 $2 >/tmp/mindstream.log &
 else
   echo "Using JAVA_HOME: $JAVA_HOME"
   # TODO Fix classpath load issue, rather than load java.ext.dirs
   "$JAVA_HOME/bin/java" -cp "$CLASSPATH" -jar $JAR $1 $2
  #"$JAVA_HOME/bin/java" -Djava.ext.dirs=lib -jar $JAR $1 $2 >/tmp/mindstream.log &
fi


