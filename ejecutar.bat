@echo off
REM Compila y ejecuta el navegador en Windows.
REM   .\ejecutar.bat             -> interfaz grafica
REM   .\ejecutar.bat --consola   -> version de consola
setlocal
cd /d "%~dp0"
if not exist out mkdir out

javac -encoding UTF-8 -d out src\navegador\*.java src\navegador\modelo\*.java src\navegador\ui\*.java src\navegador\consola\*.java
if errorlevel 1 goto :error

java -cp out navegador.Main %*
goto :fin

:error
echo.
echo No se pudo compilar el proyecto. Verifica que el JDK este instalado (javac -version).
pause

:fin
endlocal
