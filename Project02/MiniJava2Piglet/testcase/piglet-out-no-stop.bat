@echo off
echo ע��Ҫ�����ļ��������Ե� ".*pg" �ļ���"pgi.jar" �ļ�����ͬһ�ļ�����
echo Please put this file, pgi.jar and "*.pg" files for test in the same folder
pause
for %%i in (*.pg) do java -jar pgi.jar < %%i
pause