@echo off
for %%i in (*.spg) do java -jar pgi.jar < %%i && pause
pause