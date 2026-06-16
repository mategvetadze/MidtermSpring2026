@echo off
setlocal
cd /d "%~dp0\.."
if not exist out mkdir out
javac -d out src\Card.java src\Main.java src\GameRules.java src\GameTest.java src\GameState.java src\GameEngine.java src\Deck.java src\ConsoleGame.java src\BotPlayer.java
if errorlevel 1 exit /b 1
echo Running Main --self-test...
java -cp out Main --self-test
if errorlevel 1 exit /b 1
echo Running GameTest with assertions enabled...
java -ea -cp out GameTest
if errorlevel 1 exit /b 1
