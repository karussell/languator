# MAVEN_HOME/bin/mvn
MVN=mvn
VERSION=03-2014

ARGS="-DgroupId=com.graphhopper -DartifactId=jsonic -Dversion=$VERSION -Dpackaging=jar -Dfile=jsonic-1.2.0.jar"
$MVN install:install-file $ARGS
