/*
 * SASAbusWebMap - HTML5 Map for SASAbus
 *
 * SASAbusWebMap.java
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

import it.bz.tis.sasabus.backend.shared.Area;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.opendata.client.model.BusStop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;

import bz.davide.dmweb.client.leaflet.Circle;
import bz.davide.dmweb.client.leaflet.EventListener;
import bz.davide.dmweb.client.leaflet.LatLng;
import bz.davide.dmweb.client.leaflet.Map;
import bz.davide.dmweb.client.leaflet.OSMLayer;
import bz.davide.dmweb.client.leaflet.Path;
import bz.davide.dmweb.client.leaflet.PathOptions;
import bz.davide.dmweb.client.leaflet.Polygon;
import bz.davide.dmweb.client.leaflet.Polyline;
import bz.davide.dmweb.shared.view.AbstractHtmlElementView;
import bz.davide.dmweb.shared.view.ButtonView;
import bz.davide.dmweb.shared.view.DMClickEvent;
import bz.davide.dmweb.shared.view.DMClickHandler;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class SASAbusWebMap extends Map
{
   final static String[]               COLORS             = new String[]{"#FF0000", "#0000FF", "#FFFF00"};

   Polygon                             AreaBz;
   Polygon                             AreaMe;
   Polygon                             AreaSu;

   EventListener                       zoomPositionChanged;

   IdentityHashMap<BusStation, Path[]> shapesOfBusStation = new IdentityHashMap<BusStation, Path[]>();
   BusStationPopup                     busStationPopup;

   GWTSASAbusOpenDataLocalStorage      storage;

   BusStation[]                        busStations        = null;

   static String[]                     progressChars      = new String[]{"|", "/", "-", "\\"};
   int                                 progressIndex;

   ButtonView                          gps;

   LatLng                              userPosition       = null;

   public SASAbusWebMap(Element element, GWTSASAbusOpenDataLocalStorage storage) throws IOException
   {
      super(element);

      this.storage = storage;

      String[] initialParameters = GWTSASAbusOpenDataLocalStorage.initialParameters().split(",");

      final String selectButtonText = initialParameters[3].trim();

      String language = initialParameters[4].trim();

      this.busStationPopup = new BusStationPopup(new BusStationPopup.InitParameters(selectButtonText,
                                                                                    language));

      String tilesUrl = GWTSASAbusOpenDataLocalStorage.getMapTilesRootUrl() + "/{z}/{x}/{y}.png";

      this.addLayer(new OSMLayer(tilesUrl, 8, 17));
      this.setView(new LatLng(Double.parseDouble(initialParameters[0].trim()),
                              Double.parseDouble(initialParameters[1].trim())),
                   Integer.parseInt(initialParameters[2].trim()));

      final double opacity = 0.25;

      ///////////////////////////////////

      String color = COLORS[0];

      PathOptions polylineOptions = new PathOptions();
      polylineOptions.setFillColor(color);
      polylineOptions.setFillOpacity(opacity);
      //polylineOptions.setColor(color);
      polylineOptions.setStroke(false);

      ArrayList<LatLng> bounds = new ArrayList<LatLng>();

      LatLng[] latLngs = new LatLng[Area.boundLats_bz.length];
      for (int i = 0; i < latLngs.length; i++)
      {
         latLngs[i] = new LatLng(Area.boundLats_bz[i], Area.boundLons_bz[i]);
         bounds.add(latLngs[i]);
      }
      this.AreaBz = new Polygon(latLngs, polylineOptions);

      //this.addLayer(AreaBz);

      ///////////////////////////////////

      color = COLORS[1];

      polylineOptions = new PathOptions();
      polylineOptions.setFillColor(color);
      polylineOptions.setFillOpacity(opacity);
      //polylineOptions.setColor(color);
      polylineOptions.setStroke(false);

      bounds = new ArrayList<LatLng>();

      latLngs = new LatLng[Area.boundLats_me.length];
      for (int i = 0; i < latLngs.length; i++)
      {
         latLngs[i] = new LatLng(Area.boundLats_me[i], Area.boundLons_me[i]);
         bounds.add(latLngs[i]);
      }
      this.AreaMe = new Polygon(latLngs, polylineOptions);

      //this.addLayer(AreaMe);

      ///////////////////////////////////

      color = COLORS[2];

      polylineOptions = new PathOptions();
      polylineOptions.setFillColor(color);
      polylineOptions.setFillOpacity(opacity);
      //polylineOptions.setColor(color);
      polylineOptions.setStroke(false);

      bounds = new ArrayList<LatLng>();

      latLngs = new LatLng[Area.boundLats_su.length];
      for (int i = 0; i < latLngs.length; i++)
      {
         latLngs[i] = new LatLng(Area.boundLats_su[i], Area.boundLons_su[i]);
         bounds.add(latLngs[i]);
      }
      this.AreaSu = new Polygon(latLngs, polylineOptions);

      //this.addLayer(AreaSu);

      /////////////////////////////////////////

      this.zoomPositionChanged = new EventListener()
      {
         @Override
         public void onEvent()
         {
            try
            {
               SASAbusWebMap.this.afterZoomOrDrag();
            }
            catch (IOException e)
            {
               Window.alert("Exception " + e.getMessage());
            }
         }
      };

      this.addZoomEndEventListener(this.zoomPositionChanged);
      this.addDragEndEventListener(this.zoomPositionChanged);

      this.afterZoomOrDrag();

      this.gps = new ButtonView(new ButtonView.InitParameters("GPS"));
      this.gps.setStyleName("map-custom-controls");
      DOM.appendChild(DOM.getElementById("map"), this.gps.getElement());

      this.gps.addClickHandler(new DMClickHandler()
      {
         @Override
         public void onClick(DMClickEvent event)
         {
            SASAbusWebMap.this.setView(SASAbusWebMap.this.userPosition, 16);
            SASAbusWebMap.this.zoomPositionChanged.onEvent();
         }
      });

      this.checkGps();
   }

   void checkGps()
   {
      String result = GWTSASAbusOpenDataLocalStorage.getRequestLocationStatus();
      if (result.equals("searching"))
      {
         this.gps.setLabel("GPS searching " + progressChars[this.progressIndex]);
         this.progressIndex++;
         if (this.progressIndex >= progressChars.length)
         {
            this.progressIndex = 0;
         }
         this.checkGpsTimer();
      }
      else if (!result.equals("stop"))
      {
         String[] parts = result.split(",");
         double lat = Double.parseDouble(parts[0]);
         double lon = Double.parseDouble(parts[1]);
         float accMeter = Float.parseFloat(parts[2]);
         this.userPosition = new LatLng(lat, lon);
         this.gps.setLabel("Show my position!");
         this.checkGpsTimer();
      }
   }

   void checkGpsTimer()
   {
      Timer timer = new Timer()
      {
         @Override
         public void run()
         {
            SASAbusWebMap.this.checkGps();
         }
      };
      timer.schedule(500);
   }

   private void afterZoomOrDrag() throws IOException
   {

      int zoom = this.getZoom();
      if (zoom > 14)
      {
         SASAbusWebMap.this.removeLayer(this.AreaBz);
         SASAbusWebMap.this.removeLayer(this.AreaMe);
         SASAbusWebMap.this.removeLayer(this.AreaSu);

         this.createBusStationsAndShapes();

         LatLng center = this.getCenter();

         for (BusStation busStation : this.busStations)
         {
            double radius1 = busStation.getBusStops()[0].getLat() - center.getLat();
            double radius2 = busStation.getBusStops()[0].getLon() - center.getLng();
            double radius = radius1 * radius1 + radius2 * radius2;

            if (radius < 0.0005d)
            {
               for (Path path : this.shapesOfBusStation.get(busStation))
               {
                  this.addLayer(path);
               }
            }
         }
      }
      else
      {
         SASAbusWebMap.this.addLayer(this.AreaBz);
         SASAbusWebMap.this.addLayer(this.AreaMe);
         SASAbusWebMap.this.addLayer(this.AreaSu);

         for (Path[] paths : this.shapesOfBusStation.values())
         {
            for (Path path : paths)
            {
               this.removeLayer(path);
            }
         }
      }
   }

   private void createBusStationsAndShapes() throws IOException
   {
      if (this.busStations != null)
      {
         return;
      }

      PathOptions pathOptions = new PathOptions();
      pathOptions.setFillColor("red");
      pathOptions.setColor("#555555");
      pathOptions.setFillOpacity(.8);

      PathOptions pathOptions2 = new PathOptions();
      pathOptions2.setFillColor("yellow");
      pathOptions2.setColor("red");
      pathOptions2.setFillOpacity(.8);

      this.busStations = this.storage.getBusStations().getList();

      for (final BusStation busStation : this.busStations)
      {
         ArrayList<Path> shapes = new ArrayList<Path>();

         EventListener busStationClickListener = new EventListener()
         {
            @Override
            public void onEvent()
            {
               try
               {
                  LatLng latLng = new LatLng(busStation.getBusStops()[0].getLat(),
                                             busStation.getBusStops()[0].getLon());
                  SASAbusWebMap.this.busStationPopup.setBusStation(busStation);
                  SASAbusWebMap.this.openPopup(SASAbusWebMap.this.busStationPopup.getElement(), latLng);
                  AbstractHtmlElementView.notifyAttachRecursive(SASAbusWebMap.this.busStationPopup);
               }
               catch (Exception exxx)
               {
                  exxx.printStackTrace();
                  Window.alert("exxx " + exxx.getMessage());
               }
            }
         };

         BusStop[] busStops = busStation.getBusStops();
         LatLng lastLatLng = null;
         for (int i = 0; i < busStops.length; i++)
         {
            BusStop busStop = busStops[i];

            LatLng latLng = new LatLng(busStop.getLat(), busStop.getLon());

            Circle circle = new Circle(latLng, 20, pathOptions);
            shapes.add(circle);

            circle.addClickEventListener(busStationClickListener);

            if (i > 0)
            {
               LatLng[] vertexs = new LatLng[2];
               vertexs[0] = lastLatLng;
               vertexs[1] = latLng;
               Polyline polyline = new Polyline(vertexs, pathOptions);
               polyline.addClickEventListener(busStationClickListener);
               shapes.add(polyline);

            }
            lastLatLng = latLng;
         }

         this.shapesOfBusStation.put(busStation, shapes.toArray(new Path[0]));
      }

   }
}
