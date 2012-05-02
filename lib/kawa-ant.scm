(define-alias <kawac> <gnu.kawa.ant.Kawac>)
(define-alias <fileset> <org.apache.tools.ant.types.FileSet>)
(define-alias <javac> <org.apache.tools.ant.taskdefs.Javac>)
(define-alias <jar> <org.apache.tools.ant.taskdefs.Jar>)
(define-alias <mkdir> <org.apache.tools.ant.taskdefs.Mkdir>)

(define kawac
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

(define javac
  (lambda (p destdir srcdir classpath)
    (let ((javac (org.apache.tools.ant.taskdefs.Javac)))
      (invoke (as <javac> javac) 'setProject p)
      (invoke (as <javac> javac) 'setSrcdir 
              (org.apache.tools.ant.types.Path p srcdir))
      (invoke (as <javac> javac) 'setDestdir 
              (java.io.File (as <String> destdir)))
      (invoke (as <javac> javac) 'setClasspath classpath)
      (invoke (as <javac> javac) 'execute))))

(define jar
  (lambda (p destfile dir includes manifest)
    (let 
        ((jar (org.apache.tools.ant.taskdefs.Jar))
         (fileset (org.apache.tools.ant.types.FileSet)))
      (invoke (as <jar> jar) 'setProject p)
      (invoke (as <jar> jar) 'setDestFile 
              (java.io.File (as <String> destfile)))
      (invoke (as <fileset> fileset) 'setDir 
              (java.io.File (as <String> dir)))
      (invoke (as <fileset> fileset) 'appendIncludes includes)
      (invoke (as <jar> jar) 'setManifest manifest)
      (invoke (as <jar> jar) 'addFileset fileset)
      (invoke (as <jar> jar) 'execute))))

(define mkdir
  (lambda (dir)
    (let ((mkdir (org.apache.tools.ant.taskdefs.Mkdir)))
      (invoke (as <mkdir> mkdir) 'setDir (java.io.File (as <String> dir)))
      (invoke (as <mkdir> mkdir) 'execute))))
