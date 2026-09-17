@echo off
REM Compila y ejecuta el navegador en Windows.
REM   ejecutar.bat             -> interfaz grafica
REM   ejecutar.bat --consola   -> version de consola
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\*.java > "%TEMP%\fuentes_navegador.txt"
javac -encoding UTF-8 -d out "@%TEMP%\fuentes_navegador.txt"
if errorlevel 1 goto :error
java -cp out navegador.Main %*
goto :eof
:error
echo Error al compilar el proyecto.
pause
