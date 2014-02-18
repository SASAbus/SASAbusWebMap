/*
 * SASAbusWebMap - HTML5 Map for SASAbus
 *
 * GWTSASAbusOpenDataLocalStorage.java
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

import it.sasabz.sasabus.opendata.client.SASAbusOpenDataLocalStorage;

import java.io.IOException;

import bz.davide.dmxmljson.unmarshalling.Structure;
import bz.davide.dmxmljson.unmarshalling.json.JSONParser;
import bz.davide.dmxmljson.unmarshalling.json.gwt.GWTStructure;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class GWTSASAbusOpenDataLocalStorage extends SASAbusOpenDataLocalStorage
{

   public GWTSASAbusOpenDataLocalStorage()
   {
      super(new JSONParser()
      {
         @Override
         public Structure parse(String json)
         {
            JSONValue jsonObject = com.google.gwt.json.client.JSONParser.parseStrict(json);
            GWTStructure gwtStructure = new GWTStructure((JSONObject) jsonObject);
            return gwtStructure;
         }
      });
   }

   @Override
   protected void setData(String key, String data) throws IOException
   {
      throw new IOException("setData not implemented!");
   }

   @Override
   protected String getData(String key) throws IOException
   {
      String json = getDataJson(key);
      //Window.alert(json);
      return json;
   }

   static native String getDataJson(String key)/*-{
		return $wnd.it_sasabz_sasabus_webmap_client_GWTSASAbusOpenDataLocalStorage
				.getData(key);
   }-*/;

   static native String getMapTilesRootUrl()/*-{
		return $wnd.it_sasabz_sasabus_webmap_client_GWTSASAbusOpenDataLocalStorage
				.getMapTilesRootUrl();
   }-*/;

   static native void showDepartures(String busStationName)/*-{
		$wnd.it_sasabz_sasabus_webmap_client_GWTSASAbusOpenDataLocalStorage
				.showDepartures(busStationName);
   }-*/;

   static native String getRequestLocationStatus()/*-{
		return $wnd.it_sasabz_sasabus_webmap_client_GWTSASAbusOpenDataLocalStorage
				.getRequestLocationStatus();
   }-*/;

   static native String initialParameters()/*-{
		return $wnd.it_sasabz_sasabus_webmap_client_GWTSASAbusOpenDataLocalStorage
				.initialParameters();
   }-*/;
}
