SETLOCAL
SET seleniumLibraries=libs\cglib-nodep-3.2.4.jar;libs\client-combined-3.0.0-beta2-nodeps.jar;libs\commons-codec-1.10.jar;libs\commons-exec-1.3.jar;libs\commons-logging-1.2.jar;libs\curvesapi-1.03.jar;libs\gson-2.3.1.jar;libs\guava-19.0.jar;libs\hamcrest-core-1.3.jar;libs\hamcrest-library-1.3.jar;libs\httpclient-4.5.2.jar;libs\httpcore-4.4.4.jar;libs\httpmime-4.5.2.jar;libs\jna-4.1.0.jar;libs\jna-platform-4.1.0.jar;libs\junit-4.12.jar;libs\netty-3.5.7.Final.jar;libs\phantomjsdriver-1.2.1.jar;
SET poiLibraries=libs\poi-3.14-20160307.jar;libs\poi-ooxml-3.14-20160307.jar;libs\poi-ooxml-schemas-3.14-20160307.jar;libs\poi-examples-3.14-20160307.jar;libs\poi-excelant-3.14-20160307.jar;libs\xmlbeans-2.6.0.jar;

javac -cp %poiLibraries%%seleniumLibraries% src\helpEA\*.java
java -cp %poiLibraries%%seleniumLibraries%src\; helpEA.TestAutomation_Runner

del src\helpEA\*.class

ENDLOCAL

pause 

EXIT