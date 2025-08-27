@echo off
setlocal EnableExtensions

rem ============================================================================
rem  PostgreSQL DB Setup + SQL UTF8 conversion
rem  Fix: force psql client encoding to UTF-8 to avoid WIN1252 issues
rem  Requires: psql in PATH and PowerShell (Win10/11)
rem ============================================================================

set "ERROR_CODE=0"
set "RELDIR=%~dp0"
set "SQLDIR=%RELDIR%data"

rem ---- FORCE psql client encoding to UTF-8 (key fix)
set "PGCLIENTENCODING=UTF8"

rem ---- optional: console code page (not critical now)
rem chcp 65001 >nul

rem ---- check psql
where psql >nul 2>&1
if errorlevel 1 goto NO_PSQL

echo === PostgreSQL DB Setup ===

rem ---- host
set "HOST="
set /p HOST=Server host [localhost]: 
if "%HOST%"=="" set "HOST=localhost" & echo [INFO] Using default host: localhost

rem ---- port
set "PORT="
set /p PORT=Server port [5432]: 
if "%PORT%"=="" set "PORT=5432" & echo [INFO] Using default port: 5432

rem ---- user
set "PGUSER="
set /p PGUSER=Postgres user [postgres]: 
if "%PGUSER%"=="" set "PGUSER=postgres" & echo [INFO] Using default user: postgres

rem ---- password (plain)
set "PGPASSWORD="
set /p PGPASSWORD=Password for %PGUSER% (visible): 

rem ---- db name
set "DBNAME="
set /p DBNAME=New database name [mydb]: 
if "%DBNAME%"=="" set "DBNAME=mydb" & echo [INFO] Using default db name: mydb

echo ------------------------------------------------------------
echo Host: %HOST%
echo Port: %PORT%
echo User: %PGUSER%
echo DB  : %DBNAME%
echo ------------------------------------------------------------

rem ---- scripts folder
if not exist "%SQLDIR%" goto NO_SQLDIR

rem ---- convert all .sql to UTF-8 without BOM using a temp .ps1
where powershell >nul 2>&1
if errorlevel 1 goto SKIP_CONV

set "PS1=%TEMP%\sql_utf8_convert_%RANDOM%.ps1"
> "%PS1%" echo $dir = '%SQLDIR%';
>>"%PS1%" echo if (Test-Path $dir) {
>>"%PS1%" echo   Get-ChildItem -Path $dir -Filter *.sql -File ^| ForEach-Object {
>>"%PS1%" echo     $p = $_.FullName;
>>"%PS1%" echo     $bytes = [IO.File]::ReadAllBytes($p);
>>"%PS1%" echo     $utf8Strict = New-Object System.Text.UTF8Encoding($false,$true);
>>"%PS1%" echo     $isUtf8 = $true; try { [void]$utf8Strict.GetString($bytes) } catch { $isUtf8 = $false }
>>"%PS1%" echo     if (-not $isUtf8) {
>>"%PS1%" echo       $text = [Text.Encoding]::GetEncoding(1252).GetString($bytes);
>>"%PS1%" echo       [IO.File]::WriteAllText($p, $text, (New-Object System.Text.UTF8Encoding($false)));
>>"%PS1%" echo       Write-Host ('  Converted CP1252 -> UTF8: ' + $p)
>>"%PS1%" echo     } else {
>>"%PS1%" echo       $text = [Text.Encoding]::UTF8.GetString($bytes);
>>"%PS1%" echo       [IO.File]::WriteAllText($p, $text, (New-Object System.Text.UTF8Encoding($false)));
>>"%PS1%" echo       Write-Host ('  Normalized UTF8   : ' + $p)
>>"%PS1%" echo     }
>>"%PS1%" echo   }
>>"%PS1%" echo } else { Write-Host ('  [WARN] Directory not found: ' + $dir) }

echo [INFO] Converting/normalizing .sql files to UTF-8 (no BOM)...
powershell -NoProfile -ExecutionPolicy Bypass -File "%PS1%"
if errorlevel 1 echo [WARN ] Encoding conversion failed or partial. Continuing.
del /q "%PS1%" >nul 2>&1

:SKIP_CONV

rem ---- check required files
set "MISSING="
for %%F in (tablecreation.sql libri.sql utenti.sql librerie.sql libreria_libro.sql valutazioni.sql consigli.sql) do (
  if not exist "%SQLDIR%\%%F" echo [ERROR] Missing file: %SQLDIR%\%%F & set "MISSING=1"
)
if defined MISSING goto MISS_FILES

echo [INFO] Testing connection...
psql -h "%HOST%" -p "%PORT%" -U "%PGUSER%" -d postgres -c "\conninfo"
if errorlevel 1 goto CONN_ERR

echo [INFO] Checking database existence...
set "DBEXISTS="
for /f "tokens=* delims=" %%E in ('psql -h "%HOST%" -p "%PORT%" -U "%PGUSER%" -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='%DBNAME%';"') do set "DBEXISTS=%%E"

if "%DBEXISTS%"=="1" (
  echo [OK   ] Database already exists: %DBNAME%
) else (
  echo [INFO] Creating database: %DBNAME%
  psql -h "%HOST%" -p "%PORT%" -U "%PGUSER%" -d postgres -v ON_ERROR_STOP=1 -c "CREATE DATABASE \"%DBNAME%\" ENCODING 'UTF8' TEMPLATE template0;"
  if errorlevel 1 goto CREATE_ERR
)

echo [INFO] Running scripts in FK-safe order (ON_ERROR_STOP=1)
echo -- 1/7: tablecreation.sql
psql -h "%HOST%" -p "%PORT%" -U "%PGUSER%" -d "%DBNAME%" -v ON_ERROR_STOP=1 -f "%SQLDIR%\tablecreation.sql" || goto SQL_ERR

echo -- 2/7: libri.sql
psql -h "%HOST%" -p "%PORT%" -U "%PGUSER%" -d "%DBNAME%" -v ON_ERROR_STOP=1 -f "%SQLDIR%\libri.sql" || goto SQL_ERR

echo -- 3/7: utenti.sql
psql -h "%HOST%" -p "%PORT%" -U "%PGUSER%" -d "%DBNAME%" -v ON_ERROR_STOP=1 -f "%SQLDIR%\utenti.sql" || goto SQL_ERR

echo -- 4/7: librerie.sql
psql -h "%HOST%" -p "%PORT%" -U "%PGUSER%" -d "%DBNAME%" -v ON_ERROR_STOP=1 -f "%SQLDIR%\librerie.sql" || goto SQL_ERR

echo -- 5/7: libreria_libro.sql
psql -h "%HOST%" -p "%PORT%" -U "%PGUSER%" -d "%DBNAME%" -v ON_ERROR_STOP=1 -f "%SQLDIR%\libreria_libro.sql" || goto SQL_ERR

echo -- 6/7: valutazioni.sql
psql -h "%HOST%" -p "%PORT%" -U "%PGUSER%" -d "%DBNAME%" -v ON_ERROR_STOP=1 -f "%SQLDIR%\valutazioni.sql" || goto SQL_ERR

echo -- 7/7: consigli.sql
psql -h "%HOST%" -p "%PORT%" -U "%PGUSER%" -d "%DBNAME%" -v ON_ERROR_STOP=1 -f "%SQLDIR%\consigli.sql" || goto SQL_ERR

echo.
echo Done. All scripts completed successfully.
goto END

:NO_PSQL
echo [ERROR] psql not found in PATH. Add "C:\Program Files\PostgreSQL\17\bin" to PATH.
set "ERROR_CODE=1"
goto END

:NO_SQLDIR
echo [ERROR] Scripts folder not found: %SQLDIR%
set "ERROR_CODE=4"
goto END

:MISS_FILES
echo [ERROR] One or more .sql files are missing.
set "ERROR_CODE=5"
goto END

:CONN_ERR
echo [ERROR] Connection failed (credentials or server).
set "ERROR_CODE=2"
goto END

:CREATE_ERR
echo [ERROR] Database creation failed.
set "ERROR_CODE=3"
goto END

:SQL_ERR
echo [ERROR] Error while running a SQL script.
set "ERROR_CODE=6"
goto END

:END
echo.
echo Exit code: %ERROR_CODE%
set /p _CLOSE=Press ENTER to close...
endlocal
