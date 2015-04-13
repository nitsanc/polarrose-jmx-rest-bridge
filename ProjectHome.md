# JMX/REST Bridge #

Simple Java Web Application that exposes JMX servers through HTTP. This was written so that external tools can easily query JMX attributes of Java applications. More specifically, this was written to allow Cacti to generate fancy graphs of ActiveMQ instances.

Right now this web application exposes just one controller `/retrieveAttribute` that takes a `service`, `object` and `attribute` parameter. These are equivalent to JMXServiceURL, ObjectName and AttributeName in the JMX world.

The plan is to extend this web app to support a real REST like interface to make it easy to query whole sets of attributes or objects as a whole. That will come later.

## Example: Querying a remote JVM ##

You can call it like this to retrieve the VmName attribute of the java.lang:type=Runtime object running on a remote machine:

```
http://localhost:8080/retrieveAttribute
   ?service=service:jmx:rmi:///jndi/rmi://10.0.3.1:1099/jmxrmi
   &object=java.lang:type=Runtime
   &attribute=VmName
```

Which will return a simple `text/plain` response containing just the attribute value. In this case something like `Java HotSpot(TM) 64-Bit Server VM`.

## Example: Querying the number of items in an ActiveMQ Queue ##

The following requests returns the number of messages in the queue named MyQueue on the broker named MyBroker.

```
http://localhost:8080/retrieveAttribute
   ?service=service:jmx:rmi:///jndi/rmi://10.10.10.10:1099/jmxrmi
   &object=org.apache.activemq:Type=Queue,Destination=MyQueue,BrokerName=MyBroker
   &attribute=QueueSize
```

Which will return a simple `text/plain` response containing just the attribute value. In this case some number.
