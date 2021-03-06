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
import java.util.Objects;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ChannelAttribute {

   private long id;
   private String name;

   public ChannelAttribute() {
   }

   public ChannelAttribute(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 89 * hash + Objects.hashCode(this.name);
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final ChannelAttribute other = (ChannelAttribute) obj;
      if (!Objects.equals(this.name, other.name)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "ChannelAttribute{" + "id=" + id + ", name=" + name + '}';
   }

}
