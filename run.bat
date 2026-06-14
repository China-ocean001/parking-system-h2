@echo off
setlocal

REM Try to find java
where java >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set JAVA_EXE=java
) else (
    if defined JAVA_HOME (
        set JAVA_EXE="%JAVA_HOME%\bin\java.exe"
    ) else (
        echo Error: Java not found in PATH or JAVA_HOME.
        pause
        exit /b 1
    )
)

REM Run the application
if exist "target\parking-system-0.0.1-SNAPSHOT.jar" (
    echo Starting Parking System...
    %JAVA_EXE% -jar target\parking-system-0.0.1-SNAPSHOT.jar
) else (
    echo Jar file not found. Building project...
    %JAVA_EXE% "-Dmaven.multiModuleProjectDirectory=." -classpath ".mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain clean package -DskipTests
    
    if exist "target\parking-system-0.0.1-SNAPSHOT.jar" (
         echo Build successful. Starting application...
         %JAVA_EXE% -jar target\parking-system-0.0.1-SNAPSHOT.jar
    ) else (
         echo Build failed. Please check the errors above.
         pause
    )
)

endlocal
