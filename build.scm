(define-alias <javac> <org.apache.tools.ant.taskdefs.Javac>)
(define-alias <jar> <org.apache.tools.ant.taskdefs.Jar>)
(define-alias <property> <org.apache.tools.ant.taskdefs.Property>)
(define-alias <fileset> <org.apache.tools.ant.types.FileSet>)
(define-alias <project> <org.apache.tools.ant.Project>)
(define-alias <taskdef> <org.apache.tools.ant.taskdefs.Taskdef>)
(define-alias <kawac> <gnu.kawa.ant.Kawac>)
(define-alias <path> <org.apache.tools.ant.types.Path>)
(define	javac (org.apache.tools.ant.taskdefs.Javac))
(define	jar (org.apache.tools.ant.taskdefs.Jar))
(define p (org.apache.tools.ant.Project))
(define srcPath (org.apache.tools.ant.types.Path p "./src"))
(define classpath (org.apache.tools.ant.types.Path p ".:./lib/ant.jar:./lib/kawa.jar"))

;compile
(invoke (as <javac> javac) 'setProject p)
(invoke (as <javac> javac) 'setSrcdir srcPath)
(invoke (as <javac> javac) 'setDestdir (java.io.File "./build"))
(invoke (as <javac> javac) 'setClasspath classpath)
(invoke (as <javac> javac) 'execute)

(define compile-kawa
  (lambda (p prefix dir files lang)
    (let ((kawac (gnu.kawa.ant.Kawac))
          (fileset (org.apache.tools.ant.types.FileSet)))
      (invoke (as <kawac> kawac) 'setTaskName "kawac")
      (invoke (as <kawac> kawac) 'setDestdir (java.io.File "./build"))
      (invoke (as <kawac> kawac) 'setProject p)
      (invoke (as <kawac> kawac) 'setLanguage lang)
      (invoke (as <kawac> kawac) 'setClasspath (org.apache.tools.ant.types.Path p "lib/ant.jar:lib/kawa.jar:lib/kawa-tools.jar:build"))
      (invoke (as <kawac> kawac) 'setPrefix prefix)
      (invoke (as <fileset> fileset) 'setDir (java.io.File (as <String> dir)))
      (invoke (as <fileset> fileset) 'appendIncludes files)
      (invoke (as <kawac> kawac) 'addFileset fileset)
      (invoke (as <kawac> kawac) 'execute))))

      
;kawac
(compile-kawa p 
              "gnu.jemacs.lang." 
              "./src/gnu/jemacs/lang" 
              (make String[] 
                    "MiscOps.scm"
                    "NumberOps.scm")
              "scheme")
(compile-kawa p 
              "gnu.jemacs.buffer." 
              "./src/gnu/jemacs/buffer" 
              (make String[] "emacs.scm")
              "scheme")
(compile-kawa p 
              "gnu.jemacs.lisp." 
              "./src/gnu/jemacs/lisp" 
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

;build the jar
(define build-fileset (org.apache.tools.ant.types.FileSet))
(invoke (as <fileset> build-fileset) 'setDir (java.io.File "./build"))
(invoke (as <fileset> build-fileset) 'appendIncludes (make String[] "gnu/**/*.class" ))
(invoke (as <jar> jar) 'setDestFile (java.io.File "jemacs.jar"))
(invoke (as <jar> jar) 'setProject p)
(invoke (as <jar> jar) 'addFileset build-fileset)
(invoke (as <jar> jar) 'execute)
