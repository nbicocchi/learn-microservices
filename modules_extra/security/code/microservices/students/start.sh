# Start spring boot devtools process in the background.
# This is necessary for hot reload.
(./gradlew -t :bootJar) &
# Next, start the app.
# The "PskipDownload" option ensures dependencies are not downloaded again.
./gradlew bootRun -PskipDownload=true