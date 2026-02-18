@echo off
setlocal

REM ====== CONFIGURATION ======
set "PROJECT_DIR=C:\Users\Duox\Downloads\SDAutofish"
set "MODS_DIR=D:\GameBackup\PrismLauncher-Windows-MSVC-Portable-10.0.1\instances\1.20.1\.minecraft\mods"
set "PRISM_DIR=D:\GameBackup\PrismLauncher-Windows-MSVC-Portable-10.0.1"
set "PRISM_EXE=PrismLauncher.exe"
set "INSTANCE_NAME=1.20.1"

REM ====== BUILD PROJECT ======
echo Running Gradle build...
cd /d "%PROJECT_DIR%"

call gradlew clean build
IF ERRORLEVEL 1 (
    echo Gradle build failed. Abort.
    pause
    exit /b 1
)

echo Gradle build completed.

REM ====== UPDATE MODS ======
echo Deleting old mods in target directory...
if exist "%MODS_DIR%" (
    del /q "%MODS_DIR%\*"
) else (
    echo Mods directory not found: "%MODS_DIR%"
    pause
    exit /b 1
)

echo Copying built mod...
REM Copying the built jar file. Using wildcard to match version.
copy /y "%PROJECT_DIR%\build\libs\autofish-addon-*.jar" "%MODS_DIR%\"

echo Copying library mods...
if exist "%PROJECT_DIR%\libs" (
    copy /y "%PROJECT_DIR%\libs\*" "%MODS_DIR%\"
    echo Library mods copied.
) else (
    echo Libs directory not found, skipping libraries.
)

echo Mods updated.

REM ====== LAUNCH MINECRAFT ======
echo Launching Minecraft...
cd /d "%PRISM_DIR%"
start "" "%PRISM_EXE%" -l "%INSTANCE_NAME%"

echo Done.
exit
