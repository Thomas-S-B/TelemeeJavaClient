package de.strullerbaumann.telemeejavaclient.rest;

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
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import de.strullerbaumann.telemeejavaclient.boundary.TelemeeException;
import de.strullerbaumann.telemeejavaclient.entity.Channel;
import de.strullerbaumann.telemeejavaclient.entity.ChannelAttribute;
import de.strullerbaumann.telemeejavaclient.entity.LogEntry;
import de.strullerbaumann.telemeejavaclient.entity.LogValue;
import de.strullerbaumann.telemeejavaclient.entity.TelemeeApp;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class RestClient {

   private Client client = null;
   public static final String DEFAULT_BASE_URI = "http://localhost:8080/";
   private String baseURI = DEFAULT_BASE_URI;
   private static final int DEFAULT_THREADPOOLSIZE = 5;
   private int threadPoolSize = DEFAULT_THREADPOOLSIZE;

   private static final Logger LOGGER = Logger.getLogger(RestClient.class.getName());

   public RestClient() {
      ClientConfig cc = new DefaultClientConfig();
      cc.getProperties().put(ClientConfig.PROPERTY_THREADPOOL_SIZE, threadPoolSize);
      client = Client.create(cc);
   }

   public void init() {
      init(DEFAULT_BASE_URI);
   }

   public void init(String baseURI) {
      this.baseURI = baseURI;
      testConnectionToServer();
   }

   public String getBaseURI() {
      return baseURI;
   }

   public void setBaseURI(String baseURI) {
      this.baseURI = baseURI;
   }

   public int getThreadPoolSize() {
      return threadPoolSize;
   }

   public void setThreadPoolSize(int threadPoolSize) {
      this.threadPoolSize = threadPoolSize;
   }

   public void destroy() {
      client.getExecutorService().shutdown();
//      try {
//         client.getExecutorService().awaitTermination(1, TimeUnit.SECONDS);
//      } catch (InterruptedException ex) {
//         LOGGER.log(Level.SEVERE, null, ex);
//      }
      client.destroy();
   }

   public boolean testConnectionToServer() {
      boolean serverRunning = true;
      try {
         client.resource(baseURI + "telemee/resources/monitor/alive");
      } catch (ClientHandlerException | UniformInterfaceException e) {
         LOGGER.log(Level.SEVERE, "Couldn't connect to telemeeserver(" + baseURI + "telemee)  - is telemeeserver running?", e);
         serverRunning = false;
      }
      return serverRunning;
   }

   public List<TelemeeApp> getTelemeeApps() {
      List<TelemeeApp> telemeeApps = new ArrayList<>();
      try {
         WebResource webResource = client.resource(baseURI + "telemee/resources/telemeeapps");
         String jsonTelemeeApps = webResource
                 .accept(MediaType.APPLICATION_JSON)
                 .get(String.class);
         if (!jsonTelemeeApps.equals("null")) {
            try (JsonReader reader = Json.createReader(new StringReader(jsonTelemeeApps))) {
               JsonObject jsonObject = reader.readObject();
               JsonArray arrayTelemeeApps;
               try {
                  arrayTelemeeApps = jsonObject.getJsonArray("telemeeApp");
               } catch (java.lang.ClassCastException e) {
                  // It's an single JsonObject and not an array => put it in an array
                  arrayTelemeeApps = Json.createArrayBuilder().add(jsonObject.getJsonObject("telemeeApp")).build();
               }
               for (JsonValue value : arrayTelemeeApps) {
                  JsonReader reader2 = Json.createReader(new StringReader(value.toString()));
                  JsonObject jsonObject2 = reader2.readObject();
                  TelemeeApp telemeeAppFromServer = new TelemeeApp();
                  telemeeAppFromServer.setId(Long.valueOf(jsonObject2.getString("id")));
                  telemeeAppFromServer.setName(jsonObject2.getString("name"));
                  telemeeApps.add(telemeeAppFromServer);
               }
            }
         }

      } catch (ClientHandlerException | UniformInterfaceException e) {
         LOGGER.log(Level.SEVERE, "Couldn't get telemeeapps - is telemeeserver running?", e);
      }
      return telemeeApps;
   }

   public List<Channel> getChannels() {
      List<Channel> channels = new ArrayList<>();
      try {
         WebResource webResource = client.resource(baseURI + "telemee/resources/channels");
         String jsonChannels = webResource
                 .accept(MediaType.APPLICATION_JSON)
                 .get(String.class);
         if (!jsonChannels.equals("null")) {
            try (JsonReader reader = Json.createReader(new StringReader(jsonChannels))) {
               JsonObject jsonObject = reader.readObject();
               JsonArray arrayChannels;
               try {
                  arrayChannels = jsonObject.getJsonArray("channel");
               } catch (java.lang.ClassCastException e) {
                  // It's an single JsonObject and not an array => put it in an array
                  arrayChannels = Json.createArrayBuilder().add(jsonObject.getJsonObject("channel")).build();

               }
               for (JsonValue value : arrayChannels) {
                  JsonReader reader2 = Json.createReader(new StringReader(value.toString()));
                  JsonObject jsonObject2 = reader2.readObject();
                  Channel channelFromServer = new Channel();
                  channelFromServer.setId(Long.valueOf(jsonObject2.getString("id")));
                  channelFromServer.setName(jsonObject2.getString("name"));
                  channels.add(channelFromServer);
               }
            }
         }
      } catch (ClientHandlerException | UniformInterfaceException e) {
         LOGGER.log(Level.SEVERE, "Couldn't get channels - is telemeeserver running?", e);
      }
      return channels;
   }

   public List<ChannelAttribute> getChannelAttributes() {
      List<ChannelAttribute> channelAttributes = new ArrayList<>();
      try {
         WebResource webResource = client.resource(baseURI + "telemee/resources/channelattributes");
         String jsonChannelAttributes = webResource
                 .accept(MediaType.APPLICATION_JSON)
                 .get(String.class);
         if (!jsonChannelAttributes.equals("null")) {
            try (JsonReader reader = Json.createReader(new StringReader(jsonChannelAttributes))) {
               JsonObject jsonObject = reader.readObject();
               JsonArray arrayChannelAttributes;
               try {
                  arrayChannelAttributes = jsonObject.getJsonArray("channelAttribute");
               } catch (java.lang.ClassCastException e) {
                  // It's an single JsonObject and not an array => put it in an array
                  arrayChannelAttributes = Json.createArrayBuilder().add(jsonObject.getJsonObject("channelAttribute")).build();
               }
               for (JsonValue value : arrayChannelAttributes) {
                  JsonReader reader2 = Json.createReader(new StringReader(value.toString()));
                  JsonObject jsonObject2 = reader2.readObject();
                  ChannelAttribute channelAttributeFromServer = new ChannelAttribute();
                  channelAttributeFromServer.setId(Long.valueOf(jsonObject2.getString("id")));
                  channelAttributeFromServer.setName(jsonObject2.getString("name"));
                  channelAttributes.add(channelAttributeFromServer);
               }
            }
         }
      } catch (ClientHandlerException | UniformInterfaceException e) {
         LOGGER.log(Level.SEVERE, "Couldn't get channelAttributes - is telemeeserver running?", e);
      }
      return channelAttributes;
   }

   public void createTelemeeApp(TelemeeApp newTelemeeApp) {
      //already persisted (persisted if id > 0)?
      if (newTelemeeApp.getId() < 1) {
         try {
            WebResource webResource = client.resource(baseURI + "telemee/resources/telemeeapps");
            String input = "{\"name\":\"" + newTelemeeApp.getName() + "\"}";
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, input);
            if (response.getStatus() == 201) {
               String newJson = response.getEntity(String.class);
               newTelemeeApp.setId(getIDFromJson(newJson));
            } else {
               throw new TelemeeException("Failed to create app '" + newTelemeeApp.getName() + "' - HTTP error code : " + response.getStatus());
            }
         } catch (TelemeeException e) {
            LOGGER.log(Level.SEVERE, "Couldn't build app(s) - is telemeeserver running?", e);
         }
      }
   }

   public void createChannel(Channel newChannel) {
      //already persisted (persisted if id > 0)?
      if (newChannel.getId() < 1) {
         try {
            WebResource webResource = client.resource(baseURI + "telemee/resources/channels");
            String input = "{\"name\":\"" + newChannel.getName() + "\"}";
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, input);
            if (response.getStatus() != 201) {
               throw new TelemeeException("Failed to create channel '" + newChannel.getName() + "' - HTTP error code : " + response.getStatus());
            }
            String newJson = response.getEntity(String.class);
            newChannel.setId(getIDFromJson(newJson));
         } catch (TelemeeException e) {
            LOGGER.log(Level.SEVERE, "Couldn't build channel(s) - is telemeeserver running?", e);
         }
      }
   }

   public void createChannelAttribute(ChannelAttribute newChannelAttribute) {
      //already persisted (persisted if id > 0)?
      if (newChannelAttribute.getId() < 1) {
         try {
            WebResource webResource = client.resource(baseURI + "telemee/resources/channelattributes");
            String input = "{\"name\":\"" + newChannelAttribute.getName() + "\"}";
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, input);
            if (response.getStatus() != 201) {
               throw new TelemeeException("Failed to create channelattribute '" + newChannelAttribute.getName() + "' - HTTP error code : " + response.getStatus());
            }
            String newJson = response.getEntity(String.class);
            newChannelAttribute.setId(getIDFromJson(newJson));
         } catch (TelemeeException e) {
            LOGGER.log(Level.SEVERE, "Couldn't build channelattribute(s) - is telemeeserver running?", e);
         }
      }
   }

   public void bindChannelToApp(Channel channel, TelemeeApp telemeeApp) {
      //Performance - is channel already bounded to telemeeApp?
      if (!telemeeApp.getBoundedChannels().contains(channel)) {
         try {
            WebResource webResource = client.resource(baseURI + "telemee/resources/telemeeapps/" + telemeeApp.getId() + "/channel/" + channel.getId());
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class);
            if (response.getStatus() != 201) {
               throw new TelemeeException("Failed to bind Channel '" + channel.getName() + "' to app '" + telemeeApp.getName() + "' - HTTP error code : " + response.getStatus());
            }
            telemeeApp.getBoundedChannels().add(channel);
         } catch (TelemeeException e) {
            LOGGER.log(Level.SEVERE, "Couldn't bind channel(s) to app(s) - is telemeeserver running?", e);
         }
      }
   }

   public void bindChannelAttributeToChannel(ChannelAttribute channelAttribute, Channel channel) {
      //Performance - is channelAttribute already bounded to channel?
      if (!channel.getBoundedChannelAttributes().contains(channelAttribute)) {
         try {
            WebResource webResource = client.resource(baseURI + "telemee/resources/channels/" + channel.getId() + "/channelattribute/" + channelAttribute.getId());
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class);
            if (response.getStatus() != 201) {
               throw new TelemeeException("Failed to bind ChannelAttribute '" + channelAttribute.getName() + "' to channel '" + channel.getName() + "' - HTTP error code : " + response.getStatus());
            }
            channel.getBoundedChannelAttributes().add(channelAttribute);
         } catch (TelemeeException e) {
            LOGGER.log(Level.SEVERE, "Couldn't bind ChannelAttribute(s) to channel(s) - is telemeeserver running?", e);
         }
      }
   }

   public void createLogEntry(LogEntry newLogEntry) {
      try {
         WebResource webResource = client.resource(baseURI + "telemee/resources/logentries/");
         JsonObjectBuilder jsonLogEntry = Json.createObjectBuilder();
         jsonLogEntry.add("channelID", newLogEntry.getChannel().getId());
         jsonLogEntry.add("description", newLogEntry.getDescription());
         JsonArrayBuilder jsonLogValues = Json.createArrayBuilder();
         for (LogValue logValue : newLogEntry.getLogValues()) {
            JsonObjectBuilder jsonLogValue = Json.createObjectBuilder();
            jsonLogValue.add("value", logValue.getValue())
                    .add("channelAttributeID", logValue.getChannelAttribute().getId());
            jsonLogValues.add(jsonLogValue);
         }
         jsonLogEntry.add("logValues", jsonLogValues);

         ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonLogEntry.build().toString());
         if (response.getStatus() != 201) {
            throw new TelemeeException("Failed to send " + newLogEntry + " : HTTP error code : " + response.getStatus());
         }
      } catch (TelemeeException e) {
         LOGGER.log(Level.SEVERE, "Couldn't send logentry  - is telemeeserver running?" + newLogEntry, e);
      }
   }

   private final List<Future> futures = new ArrayList<>();

   public void createLogEntryAsync(LogEntry newLogEntry) throws InterruptedException {
      JsonObjectBuilder jsonLogEntry = Json.createObjectBuilder();
      jsonLogEntry.add("channelID", newLogEntry.getChannel().getId());
      jsonLogEntry.add("description", newLogEntry.getDescription());
      JsonArrayBuilder jsonLogValues = Json.createArrayBuilder();
      for (LogValue logValue : newLogEntry.getLogValues()) {
         JsonObjectBuilder jsonLogValue = Json.createObjectBuilder();
         jsonLogValue.add("value", logValue.getValue())
                 .add("channelAttributeID", logValue.getChannelAttribute().getId());
         jsonLogValues.add(jsonLogValue);
      }
      jsonLogEntry.add("logValues", jsonLogValues);

      AsyncWebResource webResource = client.asyncResource(baseURI + "telemee/resources/logentries/");
      Future futureResponse = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonLogEntry.build().toString());
      futures.add(futureResponse);
   }

   public void waitTillAllIsDone() {
      for (Future future : futures) {
         try {
            future.get();
         } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
         }
      }
   }

   public void deleteTelemeeApp(TelemeeApp telemeeApp) {
      try {
         WebResource webResource = client.resource(baseURI + "telemee/resources/telemeeapps/" + telemeeApp.getId());
         webResource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
      } catch (ClientHandlerException | UniformInterfaceException e) {
         LOGGER.log(Level.SEVERE, "Couldn't delete telemeeApp " + telemeeApp + " - is telemeeserver running?", e);
      }
   }

   public void deleteChannel(Channel channel) {
      try {
         WebResource webResource = client.resource(baseURI + "telemee/resources/channels/" + channel.getId());
         webResource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
      } catch (ClientHandlerException | UniformInterfaceException e) {
         LOGGER.log(Level.SEVERE, "Couldn't delete channel " + channel + " - is telemeeserver running?", e);
      }
   }

   public void deleteChannelAttribute(ChannelAttribute channelAttribute) {
      try {
         WebResource webResource = client.resource(baseURI + "telemee/resources/channelattributes/" + channelAttribute.getId());
         webResource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
      } catch (ClientHandlerException | UniformInterfaceException e) {
         LOGGER.log(Level.SEVERE, "Couldn't delete channelAttribute " + channelAttribute + " - is telemeeserver running?", e);
      }
   }

   public void deleteLogEntries(Channel channel) {
      try {
         WebResource webResource = client.resource(baseURI + "telemee/resources/logentries/deleteByChannelId/" + channel.getId());
         webResource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
      } catch (ClientHandlerException | UniformInterfaceException e) {
         LOGGER.log(Level.SEVERE, "Couldn't delete logentries of channel " + channel + " - is telemeeserver running?", e);
      }
   }

   long getIDFromJson(String json) {
      // Input is e.g.: {"id":"8","name":"TelemeeTestClient via JavaClient"}
      long id;
      int start = json.indexOf("\"id\":") + "\"id\":".length() + 1;
      int end = json.indexOf('\"', start);
      id = Long.parseLong(json.substring(start, end));
      return id;
   }

   public long getLogEntriesCount(Channel channel) {
      long count = 0;
      try {
         WebResource webResource = client.resource(baseURI + "telemee/resources/logentries/countByChannelId/" + channel.getId());
         String strCount = webResource
                 .accept(MediaType.TEXT_PLAIN)
                 .get(String.class);
         count = Long.valueOf(strCount);
      } catch (ClientHandlerException | UniformInterfaceException e) {
         LOGGER.log(Level.SEVERE, "Couldn't get logentries count - is telemeeserver running?", e);
      }
      return count;
   }

}
