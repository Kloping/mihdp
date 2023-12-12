rmdir libs

mvn clean dependency:copy-dependencies -DoutputDirectory=libs compile

java -classpath ".\target\classes;.\libs\*" io.github.kloping.mihdp.MihDpMain -Dfile.encoding=UTF-8
