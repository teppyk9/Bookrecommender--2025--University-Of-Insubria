@echo off
setlocal ENABLEDELAYEDEXPANSION

set "BASE=%~dp0"
set "BASE=%BASE:~0,-1%"

set "DIST_CLI=%BASE%\dist\BookRecommenderCli"
set "JAVA_PREF=%DIST_CLI%\runtime\bin\java.exe"
set "JAR_PREF=%DIST_CLI%\app\client-1.0.0.jar"

set "JAR_FALLBACK=%BASE%\client\target\client-1.0.0.jar"

if exist "%JAVA_PREF%" (
  set "JAVA=%JAVA_PREF%"
) else (
  if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
    set "JAVA=%JAVA_HOME%\bin\java.exe"
  ) else (
    echo [ERRORE] Runtime Java non trovata:
    echo   - "%JAVA_PREF%"
    echo   - e nemmeno %%JAVA_HOME%%\bin\java.exe
    echo Suggerimento: ricrea l'app-image oppure imposta JAVA_HOME.
    exit /b 1
  )
)

if exist "%JAR_PREF%" (
  set "JAR=%JAR_PREF%"
) else if exist "%JAR_FALLBACK%" (
  set "JAR=%JAR_FALLBACK%"
) else (
  echo [ERRORE] JAR del client non trovato:
  echo   - "%JAR_PREF%"
  echo   - "%JAR_FALLBACK%"
  echo Esegui "mvn -DskipTests package" o ricrea l'app-image client.
  exit /b 2
)

"%JAVA%" -jar "%JAR%"
endlocal
