@echo off
rem Dung: build.bat [build|test|install|clean]
if "%1"=="test" (docker compose --profile test run --rm --build test & goto :eof)
if "%1"=="install" (adb install -r output\app-debug.apk & goto :eof)
if "%1"=="clean" (docker compose down -v & rmdir /s /q output & goto :eof)
docker compose up --build builder
