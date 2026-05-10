@echo off
echo Cleaning old class files from src...
del /S /Q src\*.class

echo Creating bin directory...
if not exist "bin" mkdir bin

echo Compiling Java files into bin...
javac -d bin src\*.java

echo Compilation complete! You can run the program using:
echo java -cp bin Main
pause
