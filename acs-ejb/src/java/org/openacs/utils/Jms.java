/*
 * 
 * Copyright 2007-2012 Audrius Valunas
 * 
 * This file is part of OpenACS.

 * OpenACS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OpenACS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OpenACS.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.openacs.utils;

import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageFormatException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.openacs.HostsLocal;

public class Jms implements ExceptionListener {

    private MessageProducer producer;
    private QueueSession queuesession;
    private javax.jms.Queue queue;
    private QueueConnection conn;
    private boolean clustered = true;

    public Jms() throws NamingException, JMSException {
        try {
            InitialContext ctx = new InitialContext();
            ctx.lookup("/HAPartition");
        } catch (NameNotFoundException e) {
            // We are not clustered
            this.setClustered(false);
        }
        setupJMS();
    }

    public void closeJMS() {
        try {
            queuesession.close();
            conn.close();
        } catch (JMSException ex) {
            ex.printStackTrace();
        }
    }

    public void setupJMS() throws NamingException, JMSException {
        InitialContext iniCtx;
        if (this.isClustered()) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Clustered - Using HA-JMS");
            Properties p = new Properties();
            p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
            p.put(Context.URL_PKG_PREFIXES, "jboss.naming:org.jnp.interfaces");
            p.put(Context.PROVIDER_URL, "localhost:1100"); // HA-JNDI port.
            iniCtx = new InitialContext(p);
        } else {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Not clustered - Using non-HA JMS");
            iniCtx = new InitialContext();
        }

        QueueConnectionFactory qcf = (QueueConnectionFactory) iniCtx.lookup("ConnectionFactory");
        queue = (javax.jms.Queue) iniCtx.lookup("queue/acsQueue");
        conn = qcf.createQueueConnection();
        conn.setExceptionListener(this);
        conn.start();
        queuesession = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);

        producer = queuesession.createProducer(queue);

        clear();
    }

    private void clear() {
        try {
            MessageConsumer cons = queuesession.createConsumer(queue);
            Message msg = null;
            while ((msg = cons.receiveNoWait()) != null) {
                msg.acknowledge();
//                System.out.println ("Cleared corrid="+msg.getJMSCorrelationID()+" id="+msg.getJMSMessageID());
            }
        } catch (JMSException ex) {
            Logger.getLogger(Jms.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public MessageConsumer createConsumer(String filter) throws JMSException {
        return queuesession.createConsumer(queue, filter);
    }

    public void onException(JMSException e) {
        System.out.println("onException " + e);
        closeJMS();
        try {
            setupJMS();
        } catch (NamingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
        } catch (JMSException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
        }

    }

    public void sendResponseMessage(Serializable object, String id) throws JMSException {
        ObjectMessage message = queuesession.createObjectMessage();
        message.setObject(object);
        if (id != null) {
            message.setJMSCorrelationID(id);
        }
        message.setJMSExpiration(3600 * 1000);
        producer.send(queue, message);
    }

    public void sendCallMessage(Serializable object, String id, HostsLocal host/*String oui, String sn*/) throws JMSException {
        ObjectMessage msg = queuesession.createObjectMessage();
        msg.setJMSMessageID(id);
        msg.setJMSExpiration(3600 * 1000);
        msg.setObject(object);

        /*
        msg.setStringProperty("OUI", oui);
        msg.setStringProperty("SN", sn);
         */
        msg.setStringProperty("HWID", host.getHwid().toString());
        msg.setStringProperty("SN", host.getSerialno());

        producer.send(queue, msg);
    }

    public Object Receive(String filter, long timeoutReceive) throws JMSException {
        System.out.println("CLIENT: Creating cosumer: " + filter + "'");
        MessageConsumer consumer = queuesession.createConsumer(queue, filter);
        Message mrcv;
        if ((mrcv = consumer.receive(timeoutReceive)) != null) {
            Object rm1;
            try {
                rm1 = ((ObjectMessage) mrcv).getObject();
                //System.out.println("RCV1: " + mrcv.getJMSCorrelationID() + " req=" + rm1.name);
                return rm1;
            } catch (MessageFormatException e) {
                System.out.println("MessageFormatException: " + e.getMessage());
                mrcv.acknowledge();
            }
        }
        return null;
    }

    /**
     * @return the clustered
     */
    public boolean isClustered() {
        return clustered;
    }

    /**
     * @param clustered the clustered to set
     */
    public void setClustered(boolean clustered) {
        this.clustered = clustered;
    }
}
