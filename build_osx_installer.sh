echo
echo Building .dmg installer...
echo

cd target

javapackager -deploy -native dmg -srcfiles ImageCompare-1.0-SNAPSHOT-jar-with-dependencies.jar -appclass org.httpeter.ic.ImageCompare -name ImageCompare -outdir deploy -outfile ImageCompare -v
