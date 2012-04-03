#!/bin/sh
export JEMACS_PATH=./lib/ant.jar:./target/jemacs.jar:./lib/kawa.jar
$JAVA_HOME/bin/java -classpath $JEMACS_PATH gnu.jemacs.lang.ELisp
