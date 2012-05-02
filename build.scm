(require "lib/kawa-ant.scm")

(define p (org.apache.tools.ant.Project))
(define classpath (org.apache.tools.ant.types.Path p ".:./lib/ant.jar:./lib/kawa.jar"))

(mkdir "./build")
(javac p "./build" "./src" classpath)
(kawac p "gnu.jemacs.lang." "./src/gnu/jemacs/lang" 
       (make String[] 
             "MiscOps.scm"
             "NumberOps.scm")
       "scheme")
(kawac p "gnu.jemacs.buffer."  "./src/gnu/jemacs/buffer" 
       (make String[] "emacs.scm")
       "scheme")
(kawac p "gnu.jemacs.lisp." "./src/gnu/jemacs/lisp" 
       (make String[] 
             "primitives.el"
             "alist.el"
             "simple.el"
             "keymap.el"
             "keydefs.el"
             "hanoi.el"
             "rect.el"
             "editfns.el"
             "subr.el"
             "autoloads.el")
       "elisp")

(jar p "jemacs.jar" "./build" (make String[] "gnu/**/*.class") 
     (java.io.File (as <String> "src/jar-manifest")))
