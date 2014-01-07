package de.strullerbaumann.telemeejavaclient.boundary;

/*
 * #%L
 * TelemeeJavaClient
 * %%
 * Copyright (C) 2013 - 2014 Thomas Struller-Baumann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import de.strullerbaumann.telemeejavaclient.entity.Channel;
import de.strullerbaumann.telemeejavaclient.entity.ChannelAttribute;
import de.strullerbaumann.telemeejavaclient.entity.LogEntry;
import de.strullerbaumann.telemeejavaclient.entity.TelemeeApp;
import de.strullerbaumann.telemeejavaclient.rest.RestClient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TelemeeJavaClient is the client to communicate from a java-application to the
 telemee-server.
 * <p>
 * Example usage:
 * <p>
 * <code>
 TelemeeJavaClient tj = new TelemeeJavaClient(); <br/>
 * tj.init(); <br/>
 tj.setLogLevel(TelemeeJavaClient.INFO); <br/>
 * <br/>
 * TelemeeApp TEST_APP_01 = new TelemeeApp("Testapp 01"); <br/>
 * Channel TEST_CHANNEL_01 = new Channel("Testchannel 01"); <br/>
 * ChannelAttribute TEST_CHANNELATTRIBUTE_X = new ChannelAttribute("X"); <br/>
 * ChannelAttribute TEST_CHANNELATTRIBUTE_Y = new ChannelAttribute("Y"); <br/>
 * <br/>
 * tj.forTelemeeApp(TEST_APP_01) <br/>
 * .forChannel(TEST_CHANNEL_01) <br/>
 * .forChannelAttribute(TEST_CHANNELATTRIBUTE_X) <br/>
 * .log(123) <br/>
 * .forChannelAttribute(TEST_CHANNELATTRIBUTE_Y) <br/>
 * .log(456) <br/>
 * .endLogEntry() <br/>
 * .send(); <br/>
 * </code>
 * <p>
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 * @version 0.01
 * @since 0.01
 */
//Serializable bacause of using in EE (enabel passivating EJB)
public class TelemeeJavaClient implements Serializable {

   private static final Logger LOGGER = Logger.getLogger(TelemeeJavaClient.class.getName());
   private final RestClient restClient = new RestClient();

   private int logLevel;
   public static final int OFF = 999;
   public static final int SEVERE = 600;
   public static final int WARNING = 500;
   public static final int INFO = 400;
   public static final int FINE = 300;
   public static final int FINER = 200;
   public static final int FINEST = 100;
   public static final int ALL = 0;

   private TelemeeApp currentApp;
   private Channel currentChannel;
   private ChannelAttribute currentChannelAttribute;
   private LogEntry currentLogEntry;
   private final Map<String, TelemeeApp> telemeeApps = new ConcurrentHashMap<>();
   private final Map<String, Channel> channels = new ConcurrentHashMap<>();
   private final Map<String, ChannelAttribute> channelAttributes = new ConcurrentHashMap<>();
   private final List<LogEntry> logEntries = new ArrayList<>();

   /**
    * Initializes TelemeeJavaClient with the default base-URI http://localhost:8080.
    *
    */
   public void init() {
      init(RestClient.DEFAULT_BASE_URI);
   }

   /**
    * Initializes TelemeeJavaClient with the given base-URI.
    *
    * @param baseURI
    */
   public void init(String baseURI) {
      restClient.init(baseURI);
      for (TelemeeApp appFromServer : restClient.getTelemeeApps()) {
         this.telemeeApps.put(appFromServer.getName(), appFromServer);
      }
      for (Channel channelFromServer : restClient.getChannels()) {
         this.channels.put(channelFromServer.getName(), channelFromServer);
      }
      for (ChannelAttribute channelAttributeFromServer : restClient.getChannelAttributes()) {
         this.channelAttributes.put(channelAttributeFromServer.getName(), channelAttributeFromServer);
      }
   }

   /**
    * Get the threadpoolsize in the restclient (for asynchronous creation of
    * logEntries). Defaultvalue is 5.
    *
    * @return int
    */
   public int getThreadPoolSize() {
      return this.restClient.getThreadPoolSize();
   }

   /**
    * Set the threadpoolsize in the restclient (for asynchronous creation of
    * logEntries). Defaultvalue is 5.
    *
    * @param threadPoolSize
    */
   public void setThreadPoolSize(int threadPoolSize) {
      this.restClient.setThreadPoolSize(threadPoolSize);
   }

   /**
    * Destroy client for cleanup.
    *
    */
   public void destroy() {
      this.restClient.destroy();
   }

   /**
    * Get base URI to server.
    *
    * @return String
    */
   public String getBaseURI() {
      return restClient.getBaseURI();
   }

   /**
    * Set base URI to server.
    *
    * @param baseURI
    */
   public void setBaseURI(String baseURI) {
      this.restClient.setBaseURI(baseURI);
   }

   /**
    * Get logLevel.
    *
    * @return int
    */
   public int getLogLevel() {
      return logLevel;
   }

   /**
    * Set logLevel.
    *
    * @param logLevel
    */
   public void setLogLevel(int logLevel) {
      this.logLevel = logLevel;
   }

   /**
    * Deletes a TelemeeApp on the server.
    *
    * @param telemeeAppToDelete
    */
   public void deleteTelemeeApp(TelemeeApp telemeeAppToDelete) {
      TelemeeApp telemeeApp = this.telemeeApps.get(telemeeAppToDelete.getName());
      if (telemeeApp != null) {
         restClient.deleteTelemeeApp(telemeeApp);
         this.telemeeApps.remove(telemeeApp.getName());
         telemeeApp.setId(Long.MIN_VALUE);
      }
   }

   /**
    * Deletes a Channel on the server.
    *
    * @param channelToDelete
    */
   public void deleteChannel(Channel channelToDelete) {
      Channel channel = this.channels.get(channelToDelete.getName());
      if (channel != null) {
         for (TelemeeApp telemeeApp : this.telemeeApps.values()) {
            if (telemeeApp.getChannels().contains(channel)) {
               telemeeApp.getChannels().remove(channel);
            }
         }
         this.channels.remove(channel.getName());
         restClient.deleteChannel(channel);
         channel.setId(Long.MIN_VALUE);
      }
   }

   /**
    * Deletes a ChannelAttribute on the server.
    *
    * @param channelAttributeToDelete
    */
   public void deleteChannelAttribute(ChannelAttribute channelAttributeToDelete) {
      ChannelAttribute channelAttribute = this.channelAttributes.get(channelAttributeToDelete.getName());
      if (channelAttribute != null) {
         for (Channel channel : this.channels.values()) {
            if (channel.getChannelAttributes().contains(channelAttributeToDelete)) {
               channel.getChannelAttributes().remove(channelAttributeToDelete);
            }
         }
         restClient.deleteChannelAttribute(channelAttribute);
         this.channelAttributes.remove(channelAttribute.getName());
         channelAttribute.setId(Long.MIN_VALUE);
      }
   }

   /**
    * Deletes a LogEntries of a given channel on the server.
    *
    * @param channel
    */
   public void deleteLogEntries(Channel channel) {
      if (channel != null) {
         restClient.deleteLogEntries(channel);
      }
   }

   /**
    * Get all TelemeeApps from the TelemeeJavaClient-Chache. TelemeeApps from other
 applications could be not in this collection, because it is only
 synchronized with the server on the instantiation of TelemeeJavaClient.
    *
    * @return Collection<TelemeeApp>
    */
   public Collection<TelemeeApp> getTelemeeApps() {
      return telemeeApps.values();
   }

   /**
    * Get all Channels. Channels from other applications could be not in this
 collection, because it is only synchronized with the server on the
 instantiation of TelemeeJavaClient.
    *
    * @return Collection<Channel>
    */
   public Collection<Channel> getChannels() {
      return channels.values();
   }

   /**
    * Get all ChannelAttributes. ChannelAttributes from other applications could
 be not in this collection, because it is only synchronized with the server
 on the instantiation of TelemeeJavaClient.
    *
    * @return Collection<ChannelAttribute>
    */
   public Collection<ChannelAttribute> getChannelAttributes() {
      return channelAttributes.values();
   }

   /**
    * Define the current telemeeApp.
    *
    * @param telemeeApp the telemeeApp
    *
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient forTelemeeApp(TelemeeApp telemeeApp) {
      if (this.telemeeApps.get(telemeeApp.getName()) == null) {
         this.telemeeApps.put(telemeeApp.getName(), telemeeApp);
      } else {
         telemeeApp = this.telemeeApps.get(telemeeApp.getName());
      }
      this.currentApp = telemeeApp;
      return this;
   }

   /**
    * Define the current channel.
    *
    * @param channel
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient forChannel(Channel channel) {
      if (this.channels.get(channel.getName()) == null) {
         this.channels.put(channel.getName(), channel);
      } else {
         channel = this.channels.get(channel.getName());
      }
      if (this.currentApp == null) {
         throw new IllegalStateException("Please define a telemeeApp for channel " + channel);
      }
      this.currentApp.addChannel(channel);
      this.currentChannel = channel;
      return this;
   }

   /**
    * Define the current channelAttribute.
    *
    * @param channelAttribute
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient forChannelAttribute(ChannelAttribute channelAttribute) {
      if (this.channelAttributes.get(channelAttribute.getName()) == null) {
         this.channelAttributes.put(channelAttribute.getName(), channelAttribute);
      } else {
         channelAttribute = this.channelAttributes.get(channelAttribute.getName());
      }
      if (this.currentChannel == null) {
         throw new IllegalStateException("Please define a channel for channelattribute " + channelAttribute);
      }
      this.currentChannel.addChannelAttribute(channelAttribute);
      this.currentChannelAttribute = channelAttribute;
      return this;
   }

   /**
    * Get count of logEntries of a channel.
    *
    * @param channel
    * @return Long
    */
   public Long getLogEntriesCount(Channel channel) {
      return this.restClient.getLogEntriesCount(channel);
   }

   /**
    * Define the start of an new logEntry.
    *
    * @param description
    * @param logLevel
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient startLogEntry(String description, int logLevel) {
      if (currentChannel == null) {
         throw new IllegalStateException("Please define a channel for logEntry " + description);
      }
      LogEntry newLogEntry = new LogEntry(currentChannel, description, logLevel);
      currentLogEntry = newLogEntry;
      return this;
   }

   /**
    * Create a logValue of type String for a started logEntry.
    *
    * @param value
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient log(String value) {
      if (currentLogEntry == null) {
         throw new IllegalStateException("Please define a LogEntry with 'startLogEntry()' before using log()");
      }
      currentLogEntry.addLogValue(value, currentChannelAttribute);
      return this;
   }

   /**
    * Create a logValue of type String for a started logEntry.
    *
    * @param value
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient log(Object value) {
      return log(String.valueOf(value));
   }

   /**
    * Create a logValue of type boolean for a started logEntry.
    *
    * @param value
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient log(boolean value) {
      return log(String.valueOf(value));
   }

   /**
    * Create a logValue of type char for a started logEntry.
    *
    * @param value
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient log(char value) {
      return log(String.valueOf(value));
   }

   /**
    * Create a logValue of type char[] for a started logEntry.
    *
    * @param value
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient log(char[] value) {
      return log(String.valueOf(value));
   }

   /**
    * Create a logValue of type double for a started logEntry.
    *
    * @param value
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient log(double value) {
      return log(String.valueOf(value));
   }

   /**
    * Create a logValue of type float for a started logEntry.
    *
    * @param value
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient log(float value) {
      return log(String.valueOf(value));
   }

   /**
    * Create a logValue of type int for a started logEntry.
    *
    * @param value
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient log(int value) {
      return log(String.valueOf(value));
   }

   /**
    * Create a logValue of type long for a started logEntry.
    *
    * @param value
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient log(long value) {
      return log(String.valueOf(value));
   }

   /**
    * Define the end of a logEntry.
    *
    * @return TelemeeJavaClient for fluent-API
    */
   public TelemeeJavaClient endLogEntry() {
      logEntries.add(currentLogEntry);
      return this;
   }

   /**
    * Get Channel by name.
    *
    * @param name
    * @return Channel
    */
   public Channel getChannel(String name) {
      return this.channels.get(name);
   }

   /**
    * Clear internal cache of TelemeeJavaClient.
    *
    */
   public void clearCache() {
      currentApp = null;
      currentChannel = null;
      currentChannelAttribute = null;
      currentLogEntry = null;
      telemeeApps.clear();
      channels.clear();
      channelAttributes.clear();
      logEntries.clear();
   }

   /**
    * Wait until all asynchronous sended logEntries are done.
    *
    */
   public void waitTillAllIsDone() {
      this.restClient.waitTillAllIsDone();
   }

   /**
    * Send all TelemeeApps, Channels, ChannelAttributes, Bindings and LogEntries
    * to the Server. LogEntries are sended synchronously.
    *
    */
   public void send() {
      send(false);
   }

   /**
    * Send all TelemeeApps, Channels, ChannelAttributes, Bindings and LogEntries
    * to the Server. LogEntries are sended asynchronously.
    *
    */
   public void sendAsync() {
      send(true);
   }

   private void send(boolean async) {
      sendTelemeeApps();
      sendChannels();
      sendBindChannelsToApps();
      sendChannelAttributes();
      sendBindChannelAttributesToChannels();
      if (async) {
         sendLogEntriesAsync();
      } else {
         sendLogEntries();
      }
      clearLogEntries();
   }

   private void sendTelemeeApps() {
      for (TelemeeApp telemeeApp : telemeeApps.values()) {
         restClient.createTelemeeApp(telemeeApp);
      }
   }

   private void sendChannels() {
      for (Channel channel : channels.values()) {
         restClient.createChannel(channel);
      }
   }

   private void sendBindChannelsToApps() {
      for (TelemeeApp telemeeApp : telemeeApps.values()) {
         for (Channel channel : telemeeApp.getChannels()) {
            restClient.bindChannelToApp(channel, telemeeApp);
         }
      }
   }

   private void sendChannelAttributes() {
      for (ChannelAttribute channelAttribute : channelAttributes.values()) {
         restClient.createChannelAttribute(channelAttribute);
      }
   }

   private void sendBindChannelAttributesToChannels() {
      for (Channel channel : channels.values()) {
         for (ChannelAttribute channelAttribute : channel.getChannelAttributes()) {
            restClient.bindChannelAttributeToChannel(channelAttribute, channel);
         }
      }
   }

   private void sendLogEntries() {
      for (LogEntry logEntry : logEntries) {
         if (logEntry.getLogLevel() >= this.logLevel) {
            restClient.createLogEntry(logEntry);
         }
      }
   }

   private void sendLogEntriesAsync() {
      for (LogEntry logEntry : logEntries) {
         if (logEntry.getLogLevel() >= this.logLevel) {
            try {
               restClient.createLogEntryAsync(logEntry);
            } catch (InterruptedException ex) {
               LOGGER.log(Level.SEVERE, null, ex);
            }
         }
      }
   }

   private void clearLogEntries() {
      this.logEntries.clear();
   }

}
