@echo off
set DIR="%~dp0"
set JAVA_EXEC="%DIR:"=%\java"



pushd %DIR% & %JAVA_EXEC% %CDS_JVM_OPTS%  -p "%~dp0/../app" -m dev.sbytmacke.tokenhelper/dev.sbytmacke.tokenhelper.AppMain  %* & popd
