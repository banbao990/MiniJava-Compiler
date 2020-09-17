@echo off
echo 注意要将本文件、待测试的 ".*pg" 文件、"pgi.jar" 文件放在同一文件夹下
echo Please put this file, pgi.jar and "*.pg" files for test in the same folder
pause
for %%i in (*.pg) do java -jar pgi.jar < %%i
pause