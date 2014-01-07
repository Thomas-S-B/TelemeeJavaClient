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

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class LogValue {

   private ChannelAttribute channelAttribute;
   private String value;

   public LogValue(String value, ChannelAttribute channelAttribute) {
      this.channelAttribute = channelAttribute;
      this.value = value;
   }

   public ChannelAttribute getChannelAttribute() {
      return channelAttribute;
   }

   public void setChannelAttribute(ChannelAttribute channelAttribute) {
      this.channelAttribute = channelAttribute;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

}
