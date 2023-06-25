REM
REM This script creates and run the pf4j demo.
REM

REM create artifacts using maven
call mvn clean package -DskipTests

REM create demo-dist folder
rmdir demo-dist /s /q
mkdir demo-dist
mkdir demo-dist\plugins

REM copy artifacts to demo-dist folder
xcopy demo\app\target\pf4j-demo-app-*.zip demo-dist /s /i
xcopy demo\plugins\plugin1\target\pf4j-demo-plugin1-*-all.jar demo-dist\plugins /s
xcopy demo\plugins\plugin2\target\pf4j-demo-plugin2-*-all.jar demo-dist\plugins /s
xcopy demo\plugins\enabled.txt demo-dist\plugins /s
xcopy demo\plugins\disabled.txt demo-dist\plugins /s

cd demo-dist

REM unzip app
jar xf pf4j-demo-app-*.zip
del pf4j-demo-app-*.zip

REM run demo
rename pf4j-demo-app-*-SNAPSHOT.jar pf4j-demo.jar
java -Xbootclasspath/a:/opt/jdk1.8/lib/tools.jar -jar pf4j-demo.jar

cd ..
