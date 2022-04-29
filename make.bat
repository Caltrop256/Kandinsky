@echo off
dir /s /B *.java > sources.txt
javac -d ./out @sources.txt
del sources.txt