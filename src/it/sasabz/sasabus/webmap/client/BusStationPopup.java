/*
 * SASAbusWebMap - HTML5 Map for SASAbus
 *
 * BusStationPopup.java
 *
 * Created: Jan 29, 2014 10:07:00 AM
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

import it.sasabz.sasabus.opendata.client.model.BusStation;
import bz.davide.dmweb.shared.view.ButtonView;
import bz.davide.dmweb.shared.view.DMClickEvent;
import bz.davide.dmweb.shared.view.DMClickHandler;
import bz.davide.dmweb.shared.view.DivView;
import bz.davide.dmweb.shared.view.SpanView;

public class BusStationPopup extends DivView
{
   SpanView   busStationName;

   BusStation busStation;

   ButtonView goDepartures;

   String     language;

   public static class InitParameters extends DivView.InitParameters
   {
      String selectButtonText;
      String language;

      public InitParameters(String selectButtonText, String language)
      {
         super();
         this.selectButtonText = selectButtonText;
         this.language = language;
      }

   }

   BusStationPopup(InitParameters initParameters)
   {
      super(initParameters);
      this.language = initParameters.language;
      this.busStationName = new SpanView(new SpanView.InitParameters("Popup"));
      this.appendChild(this.busStationName);

      this.goDepartures = new ButtonView(new ButtonView.InitParameters(initParameters.selectButtonText));

      this.goDepartures.addClickHandler(new DMClickHandler()
      {
         @Override
         public void onClick(DMClickEvent event)
         {
            GWTSASAbusOpenDataLocalStorage.showDepartures(BusStationPopup.this.getBusStationNameUsingAppLanguage(BusStationPopup.this.busStation));
         }
      });

      this.appendChild(this.goDepartures);
   }

   void setBusStation(BusStation busStation)
   {
      this.busStation = busStation;
      this.busStationName.setText(this.getBusStationNameUsingAppLanguage(busStation));
   }

   public String getBusStationNameUsingAppLanguage(BusStation busStation)
   {
      if (this.language.equals("de"))
      {
         return busStation.findName_de();
      }
      else
      {
         return busStation.findName_it();
      }
   }

}
