mvn package assembly:single
java -classpath ".\target\*" io.github.kloping.mihdp.MihDpMain -Dfile.encoding=UTF-8