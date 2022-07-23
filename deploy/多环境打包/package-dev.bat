@echo off
rem /**
rem  */
echo.
echo [INFO] Package project, generate [dev] war package file.
echo.
pause
echo.

cd %~dp0
cd..

call mvn clean package -Pdev -Dmaven.test.skip=true

cd bin
pause