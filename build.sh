#!/bin/sh
export JEMACS_HOME=.;
export JEMACS_PATH=$JEMACS_HOME/lib/kawa.jar:$JEMACS_HOME/lib/ant.jar:$JEMACS_HOME/target/jemacs.jar;

rake jemacs.jar;
#like to have this in rakefile
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lang. -C ./src/gnu/jemacs/lang/NumberOps.scm;
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lang. -C ./src/gnu/jemacs/lang/MiscOps.scm;
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.buffer. -C ./src/gnu/jemacs/buffer/emacs.scm;
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lisp. -C ./src/gnu/jemacs/lisp/primitives.el
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lisp. -C ./src/gnu/jemacs/lisp/alist.el
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lisp. -C ./src/gnu/jemacs/lisp/simple.el  
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lisp. -C ./src/gnu/jemacs/lisp/keymap.el  
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lisp. -C ./src/gnu/jemacs/lisp/keydefs.el  
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lisp. -C ./src/gnu/jemacs/lisp/hanoi.el  
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lisp. -C ./src/gnu/jemacs/lisp/rect.el  
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lisp. -C ./src/gnu/jemacs/lisp/editfns.el  
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lisp. -C ./src/gnu/jemacs/lisp/subr.el  
$JAVA_HOME/bin/java -classpath $JEMACS_PATH kawa.repl -d target/classes -P gnu.jemacs.lisp. -C ./src/gnu/jemacs/lisp/autoloads.el  

rake jemacs.jar;

