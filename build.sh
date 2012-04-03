#!/bin/sh
export JEMACS_HOME=/home/shad/mysrc/jemacs/;
export JEMACS_PATH=$JEMACS_HOME/lib/kawa.jar:$JEMACS_HOME/build:$JEMACS_HOME/lib/ant.jar

#like to have this in rakefile
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lang. -C ./src/gnu/jemacs/lang/NumberOps.scm;
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lang. -C ./src/gnu/jemacs/lang/MiscOps.scm;
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.buffer. -C ./src/gnu/jemacs/buffer/emacs.scm;

rake jemacs.jar;
