@echo off
rem /**
rem  *
rem  */
echo.
echo [INFO] Package project, generate [test] war package file.
echo.
pause
echo.

cd %~dp0
cd..

call mvn clean package -Pdevtest -Dmaven.test.skip=true

cd bin
pause