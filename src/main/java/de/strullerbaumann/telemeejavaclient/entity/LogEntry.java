package de.strullerbaumann.telemeejavaclient.entity;

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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class LogEntry {

   private Channel channel;
   private String description;
   private int logLevel;
   private List<LogValue> logValues = new ArrayList<>();

   public LogEntry() {
   }

   public LogEntry(Channel channel, String description, int logLevel) {
      this.channel = channel;
      this.description = description;
      this.logLevel = logLevel;
   }

   public Channel getChannel() {
      return channel;
   }

   public void setChannel(Channel channel) {
      this.channel = channel;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public int getLogLevel() {
      return logLevel;
   }

   public void setLogLevel(int logLevel) {
      this.logLevel = logLevel;
   }

   public List<LogValue> getLogValues() {
      return logValues;
   }

   public void setLogValues(List<LogValue> logValues) {
      this.logValues = logValues;
   }

   @Override
   public String toString() {
      return "LogEntry{" + "channel=" + channel + ", description=" + description + ", logLevel=" + logLevel + ", logValues=" + logValues + '}';
   }

   public void addLogValue(String value, ChannelAttribute channelAttribute) {
      this.logValues.add(new LogValue(value, channelAttribute));
   }

}
