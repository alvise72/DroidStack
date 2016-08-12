#$JAVA_HOME/bin/keytool -genkey -v -keystore my-release-key.keystore -alias alias_name -keyalg RSA -keysize 2048 -validity 10000
ant release
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore my-release-key-droidstack.keystore  bin/DroidStack-release-unsigned.apk alias_name
mv bin/DroidStack-release-unsigned.apk bin/DroidStack.apk
jarsigner -verify -verbose -certs bin/DroidStack.apk
/Applications/adt/sdk/build-tools/23.0.3/zipalign 4 bin/DroidStack.apk bin/DroidStack-aligned.apk
