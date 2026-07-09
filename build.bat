@echo off
chcp 65001 >nul
REM =============================================
REM Carp Build Script — 自动构建 + 归档
REM =============================================
REM 用法: build.bat [clean] [version_suffix]
REM 示例: build.bat                    → 构建并归档
REM       build.bat clean              → 清理后构建
REM       build.bat clean beta2        → 构建并添加版本后缀
REM =============================================

setlocal enabledelayedexpansion

:: 设置 JAVA_HOME
set JAVA_HOME=D:\software\java\java21

:: 清理旧构建
if /I "%1"=="clean" (
    echo [Carp] 清理旧构建...
    call .\gradlew.bat clean --no-daemon
    shift
)

:: 设置版本后缀
set SUFFIX=%1
if not "%SUFFIX%"=="" (
    echo [Carp] 版本后缀: %SUFFIX%
)

:: 构建
echo [Carp] 开始构建...
call .\gradlew.bat build --no-daemon
if %ERRORLEVEL% NEQ 0 (
    echo [Carp] 构建失败!
    exit /b 1
)

:: 查找 JAR 产物
for /f "tokens=*" %%f in ('dir /b /o-d build\libs\*.jar 2^>nul') do (
    set JAR_FILE=%%f
    goto :found
)
echo [Carp] 未找到 JAR 产物!
exit /b 1

:found
echo [Carp] 构建成功: build\libs\!JAR_FILE!

:: 归档到历史版本
set VERSION_DIR=历史版本
if not exist %VERSION_DIR% mkdir %VERSION_DIR%

copy /Y "build\libs\!JAR_FILE!" "%VERSION_DIR%\!JAR_FILE!"
echo [Carp] 已归档: %VERSION_DIR%\!JAR_FILE!

echo ============================================
echo  Carp 构建完成
echo  产物: build/libs/!JAR_FILE!
echo ============================================
