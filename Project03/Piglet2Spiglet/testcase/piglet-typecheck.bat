@echo off
for %%i in (*.spg) do java -jar spp.jar < %%i
pause