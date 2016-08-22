#!/usr/bin/env bash

echo "Downloading..."
sleep 2
java -jar vkaudiosaver-1.1.jar -type scraper -u $1 -p $2 --pool-size 5 /home/ruslanys/vkaudiosaver/Music

echo "Updating mp3 tags"
sleep 2
find . -iname "*.mp3" -execdir mid3iconv -e "windows-1251" {} \;