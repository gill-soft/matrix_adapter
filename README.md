# matrix_adapter

Its important to add to tomcat context allowCasualMultipartParsing property

```xml
<Context allowCasualMultipartParsing="true" path="/">
    <Resources cachingAllowed="true" cacheMaxSize="100000" />

    <!-- Default set of monitored resources. If one of these changes, the    -->
    <!-- web application will be reloaded.                                   -->
    <WatchedResource>WEB-INF/web.xml</WatchedResource>
    <WatchedResource>WEB-INF/tomcat-web.xml</WatchedResource>
    <WatchedResource>${catalina.base}/conf/web.xml</WatchedResource>

    <!-- Uncomment this to disable session persistence across Tomcat restarts -->
    <!--
    <Manager pathname="" />
    -->
</Context>
```
