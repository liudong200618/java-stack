@echo off

echo.
echo [INFO] Package project, generate [prod] war package file.
echo.
pause
echo.

cd %~dp0
cd..

call mvn clean package -Pprod -Dmaven.test.skip=true

cd bin
pause