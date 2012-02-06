SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_25
SET PATH=%PATH%;C:\Program Files\NetBeans 7.0.1\java\maven\bin

cmd /c mvn clean
cmd /c mvn package
rem cmd /c mvn site
rem mvn surefire-report:report

pause