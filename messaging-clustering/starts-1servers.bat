set WILDFLY_HOME=D:\RedHat\wildfly-15.0.1.Final

set WILDFLY_OPTS=-Djboss.server.log.dir=%WILDFLY_HOME%\standalone\log100 -Djboss.server.temp.dir=%WILDFLY_HOME%\standalone\tmp100 -Djboss.server.data.dir=%WILDFLY_HOME%\standalone\data100

call %WILDFLY_HOME%\bin\standalone.bat -c=standalone-full-ha.xml -Djboss.socket.binding.port-offset=100 %WILDFLY_OPTS%
