@echo off
for %%i in (*.pg) do java -jar pgi.jar < %%i
pause