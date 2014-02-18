/*
 * SASAbusWebMap - HTML5 Map for SASAbus
 *
 * SASAbusWebMapMainEntryPoint.java
 *
 * Created: Jan 3, 2014 11:29:26 AM
 *
 * Copyright (C) 2011-2014 Paolo Dongilli, Markus Windegger, Davide Montesin
 *
 * This file is part of SASAbus.
 *
 * SASAbus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SASAbus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with SASAbus.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.sasabz.sasabus.webmap.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SASAbusWebMapMainEntryPoint implements EntryPoint
{

   /**
    * This is the entry point method.
    */
   @Override
   public void onModuleLoad()
   {

      try
      {
         final GWTSASAbusOpenDataLocalStorage storage = new GWTSASAbusOpenDataLocalStorage();

         Element mapElement = DOM.getElementById("map");
         final SASAbusWebMap map = new SASAbusWebMap(mapElement, storage);

      }
      catch (Exception exxx)
      {
         exxx.printStackTrace();
         Window.alert("exxx " + exxx.getMessage());
      }

   }
}
