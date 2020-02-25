build:
	mvn -Dtest='!org.aerogear.kryptowire.integration.*Test' clean install

test:
	export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n" && mvn -Dtest='!org.aerogear.kryptowire.integration.*Test' verify

test-integration:
	mvn -Dtest='org.aerogear.kryptowire.integration.*' verify

archive:
	mvn -Dtest='!org.aerogear.kryptowire.integration.*Test' clean package

release:
	mvn -DskipTests -Darguments=-DskipTests clean release:prepare

debug:
	export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n" && mvn hpi:run
