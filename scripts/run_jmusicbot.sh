#!/bin/sh

# This will have this script check for a new version of KMusicBot every
# startup (and download it if the latest version isn't currently downloaded)
DOWNLOAD=true

# This will cause the script to run in a loop so that the bot auto-restarts
# when you use the shutdown command
LOOP=true

download() {
    if [ $DOWNLOAD == true ]; then
        URL=$(curl -s https://api.github.com/repos/d1m0s23/KMusicBot-Fork/releases/latest \
           | grep -i browser_download_url.*\.jar \
           | sed 's/.*\(http.*\)"/\1/')
        FILENAME=$(echo $URL | sed 's/.*\/\([^\/]*\)/\1/')
        if [ -f $FILENAME ]; then
            echo "Latest version already downloaded (${FILENAME})"
        else
            curl -L $URL -o $FILENAME
        fi
    fi
}

run() {
    java -Dnogui=true -jar $(ls -t KMusicBot* | head -1)
}

while
    download
    run
    $LOOP
do
    continue
done 
