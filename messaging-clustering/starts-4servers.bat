set WILDFLY_HOME=D:\RedHat\wildfly-15.0.1.Final

FOR /L %%G IN (100,100,400) DO (
	echo start server %%G
	start cmd /k call %WILDFLY_HOME%\bin\standalone.bat -c=standalone-full-ha.xml -Djboss.socket.binding.port-offset=%%G %WILDFLY_OPTS% -Djboss.server.log.dir=%WILDFLY_HOME%\standalone\log%%G -Djboss.server.temp.dir=%WILDFLY_HOME%\standalone\tmp%%G -Djboss.server.data.dir=%WILDFLY_HOME%\standalone\data%%G
)