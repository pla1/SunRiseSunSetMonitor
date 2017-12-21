#!/bin/bash
#
# Send sunrise or sunset tweet.
#
logger "$0 Started"
if [ "$#" -ne 5 ]
then
  echo "Invalid number of arguments. There should be 5 arguments. They are type, latitude, longitude, location and email address. For example $0 sunrise 32.894205 -80.037054 \"Charleston SC\" joe@domain.com"
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
emailAddress="$5"
echo "Type: $type Latitude: $latitude Longitude: $longitude Location: $location Email address: $emailAddress"
cd ~/projects/SunRiseSunSetMonitor
git pull
rm -rf out/production/SunRiseSunSetMonitor
mkdir -p out/production/SunRiseSunSetMonitor
/usr/bin/javac -cp lib/sikulixapi.jar:lib/gson-2.8.0.jar -d out/production/SunRiseSunSetMonitor src/net/pla1/srssmonitor/*.java
/usr/bin/java -cp out/production/SunRiseSunSetMonitor:lib/* net.pla1.srssmonitor.SrssDAO "$type" "$latitude" "$longitude" "$location" > /tmp/sunriseSunsetTweet.log
status=$?
if [ $status -ne 0 ]
then
  errorDate="$(date)"
  subject="Error occurred attempting to tweet $type quality. - $errorDate" "$emailAddress"
  logger "$0 $subject"
  cat /tmp/sunriseSunsetTweet.log | /usr/bin/mail -s "$subject" "$emailAddress"
  /usr/bin/gnome-screenshot -f /tmp/sunriseSunsetScreenshot.png
  ( /usr/bin/uuencode /tmp/sunriseSunsetScreenshot.png sunriseSunsetScreenshot.png; echo "Screenshot when error occurred - $errorDate" ) | /usr/bin/mail -s "Sunrise sunset tweet error screenshot - $errorDate" "$emailAddress"
fi
sleep 5
killall chromium-browser
logger "$0 Finished"
