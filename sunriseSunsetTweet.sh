#!/bin/bash
#
# Send sunrise or sunset tweet.
#
if [ "$#" -ne 4 ]
then
  echo "Invalid number of arguments. There should be 4 arguments. They are type, latitude, longitude, and location. For example $0 sunrise 32.894205 -80.037054 \"Charleston SC\""
  exit -1
fi
type="$1"
if [[ "$type" != "sunrise" && "$type" != "sunset" ]]
then
  echo "Pass the type. Either sunrise or sunset."
  exit -1
fi
latitude="$2"
longitude="$3"
location="$4"
echo "Type: $type Latitude: $latitude Longitude: $longitude Location: $location"
cd ~/projects/SunRiseSunSetMonitor
git pull
rm -rf out/production/SunRiseSunSetMonitor
mkdir -p out/production/SunRiseSunSetMonitor
/usr/bin/javac -cp lib/sikulixapi.jar:lib/gson-2.8.0.jar -d out/production/SunRiseSunSetMonitor src/net/pla1/srssmonitor/*.java
/usr/bin/java -cp out/production/SunRiseSunSetMonitor:lib/* net.pla1.srssmonitor.SrssDAO "$type" "$latitude" "$longitude" "$location"
