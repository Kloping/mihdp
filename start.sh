mvn package assembly:single
java -Dfile.encoding=UTF-8 -cp "target/*" io.github.kloping.mihdp.MihDpMain
