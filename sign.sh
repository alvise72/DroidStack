#$JAVA_HOME/bin/keytool -genkey -v -keystore my-release-key.keystore -alias alias_name -keyalg RSA -keysize 2048 -validity 10000
ant release
$JAVA_HOME/bin/jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore my-release-key.keystore bin/DroidStack-release-unsigned.apk alias_name
mv bin/DroidStack-release-unsigned.apk bin/DroidStack.apk
$JAVA_HOME/bin/jarsigner -verify -verbose -certs bin/DroidStack.apk
/home/adt/sdk/build-tools/19.1.0/zipalign 4 bin/DroidStack.apk bin/DroidStack-aligned.apk
