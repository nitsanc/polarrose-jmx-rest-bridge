package com.polarrose.jrb.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class RetrieveAttributeController implements Controller
{
    private final ConcurrentHashMap<String,JMXConnector> connectors = new ConcurrentHashMap<String, JMXConnector>();

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Object value = null;

        for (int i = 1; i <= 2; i++) {
            try {
                value = retrieveAttribute(
                    request.getParameter("service"),
                    request.getParameter("object"),
                    request.getParameter("attribute")
                );
            } catch (Exception e) {
                if (i == 2) {
                    throw e;
                }
            }
        }

        response.setContentType("text/plain");
        response.getWriter().println(value);
        
        return null;
    }

    private synchronized Object retrieveAttribute(String service, String object, String attribute)
        throws IOException, MalformedObjectNameException, ReflectionException, InstanceNotFoundException, MBeanException, AttributeNotFoundException
    {
        try {
            JMXConnector connector = connectors.get(service);
            if (connector == null) {
                connector = JMXConnectorFactory.connect(new JMXServiceURL(service));
                connectors.put(service, connector);
            }

            MBeanServerConnection connection = connector.getMBeanServerConnection();
            ObjectName objectName = new ObjectName(object);
            return connection.getAttribute(objectName, attribute);
        } catch (IOException e) {
            connectors.remove(service);
            throw e;
        }
    }
}
