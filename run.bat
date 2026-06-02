@echo off
setlocal
cd /d "%~dp0"

set "SQLITE_JAR=lib\sqlite-jdbc-3.49.1.0.jar"
set "LOCAL_JDK=%CD%\jdk"

rem ============================================
rem 1. Check SQLite driver
rem ============================================
if not exist "%SQLITE_JAR%" (
    echo [ERROR] SQLite driver not found: %SQLITE_JAR%
    echo Download from: https://github.com/xerial/sqlite-jdbc/releases
    echo Save as: lib\sqlite-jdbc-3.49.1.0.jar
    pause
    exit /b 1
)

rem ============================================
rem 2. Find or download Java
rem ============================================
set "JAVAC="
set "JAVA="

rem 2a. Check local .\jdk\ folder
if exist "%LOCAL_JDK%\bin\javac.exe" (
    set "JAVAC=%LOCAL_JDK%\bin\javac.exe"
    set "JAVA=%LOCAL_JDK%\bin\java.exe"
    goto :java_found
)

rem 2b. Check system PATH
where javac >nul 2>&1
if %errorlevel% equ 0 (
    set "JAVAC=javac"
    set "JAVA=java"
    goto :java_found
)

rem 2c. Check JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set "JAVAC=%JAVA_HOME%\bin\javac.exe"
        set "JAVA=%JAVA_HOME%\bin\java.exe"
        goto :java_found
    )
)

rem 2d. Auto-download JDK into .\jdk\
echo.
echo ============================================
echo  JDK not found. Downloading automatically...
echo  One-time download (~180MB). Please wait.
echo ============================================
echo.

set "JDK_URL=https://api.adoptium.net/v3/binary/latest/11/ga/windows/x64/jdk/hotspot/normal/eclipse"
set "ZIP=%TEMP%\jdk_freshstore.zip"
set "TMPDIR=%TEMP%\jdk_freshstore_extract"

echo [1/3] Downloading JDK 11...
powershell -Command "try{[System.Net.ServicePointManager]::SecurityProtocol=3072}catch{};$wc=New-Object System.Net.WebClient;$wc.DownloadFile('%JDK_URL%','%ZIP%')"

if not exist "%ZIP%" (
    echo.
    echo ============================================
    echo  [ERROR] Auto-download failed.
    echo.
    echo  If you are on Windows 7, it may lack TLS 1.2
    echo  support. Please download JDK manually:
    echo.
    echo    1. Open https://adoptium.net in your browser
    echo    2. Download JDK 11, Windows x64
    echo    3. Install to: %LOCAL_JDK%
    echo    4. Or set JAVA_HOME and add to PATH
    echo ============================================
    pause
    exit /b 1
)

echo [2/3] Extracting...
if exist "%TMPDIR%" rmdir /s /q "%TMPDIR%"
mkdir "%TMPDIR%"
powershell -Command "$s=New-Object -ComObject Shell.Application; $s.NameSpace('%TMPDIR%').CopyHere($s.NameSpace('%ZIP%').Items(),16); Start-Sleep -Seconds 20"

del "%ZIP%"

echo [3/3] Setting up JDK...
if exist "%LOCAL_JDK%" rmdir /s /q "%LOCAL_JDK%"
for /d %%d in ("%TMPDIR%\*") do (
    move "%%d" "%LOCAL_JDK%" >nul
    goto :jdk_moved
)
:jdk_moved
rmdir /s /q "%TMPDIR%" 2>nul

if exist "%LOCAL_JDK%\bin\javac.exe" (
    set "JAVAC=%LOCAL_JDK%\bin\javac.exe"
    set "JAVA=%LOCAL_JDK%\bin\java.exe"
    echo JDK installed to: %LOCAL_JDK%
    goto :java_found
)

echo [ERROR] JDK extraction failed.
echo Please install JDK manually from: https://adoptium.net
pause
exit /b 1

rem ============================================
rem 3. Compile
rem ============================================
:java_found
echo.
echo Java found: %JAVAC%
echo.

set "SRC=src\util\DBUtil.java src\util\DatabaseInit.java src\util\HashUtil.java src\entity\Category.java src\entity\User.java src\entity\Product.java src\entity\Inventory.java src\entity\Sale.java src\dao\CategoryDAO.java src\dao\UserDAO.java src\dao\ProductDAO.java src\dao\InventoryDAO.java src\dao\SaleDAO.java src\ui\CategoryPanel.java src\ui\ProductPanel.java src\ui\InventoryPanel.java src\ui\SalePanel.java src\ui\UserPanel.java src\ui\MainFrame.java src\ui\LoginFrame.java src\Main.java"

echo Compiling...
"%JAVAC%" -cp "%SQLITE_JAR%" -encoding UTF-8 -d out %SRC%
if %errorlevel% neq 0 (
    echo [FAILED] Compile error.
    pause
    exit /b 1
)
echo Compile OK.

rem ============================================
rem 4. Run
rem ============================================
echo.
echo Starting application...
"%JAVA%" -cp "out;%SQLITE_JAR%" Main
pause
