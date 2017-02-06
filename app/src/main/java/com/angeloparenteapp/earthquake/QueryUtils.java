package com.angeloparenteapp.earthquake;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by angel on 06/02/2017.
 */

public class QueryUtils {

    private static final String SAMPLE_JSON_RESPONSE = "{\"type\":\"FeatureCollection\",\"metadata\":{\"generated\":1486383751000,\"url\":\"http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2017-01-01&endtime=2017-01-31&minmag=2&limit=10\",\"title\":\"USGS Earthquakes\",\"status\":200,\"api\":\"1.5.4\",\"limit\":10,\"offset\":1,\"count\":10},\"features\":[{\"type\":\"Feature\",\"properties\":{\"mag\":5.9,\"place\":\"33km NNE of Port-Olry, Vanuatu\",\"time\":1485819324000,\"updated\":1486171125001,\"tz\":660,\"url\":\"http://earthquake.usgs.gov/earthquakes/eventpage/us10007wbk\",\"detail\":\"http://earthquake.usgs.gov/fdsnws/event/1/query?eventid=us10007wbk&format=geojson\",\"felt\":1,\"cdi\":3.8,\"mmi\":4.16,\"alert\":\"green\",\"status\":\"reviewed\",\"tsunami\":0,\"sig\":536,\"net\":\"us\",\"code\":\"10007wbk\",\"ids\":\",us10007wbk,\",\"sources\":\",us,\",\"types\":\",dyfi,geoserve,losspager,moment-tensor,origin,phase-data,shakemap,\",\"nst\":null,\"dmin\":5.971,\"rms\":0.81,\"gap\":21,\"magType\":\"mww\",\"type\":\"earthquake\",\"title\":\"M 5.9 - 33km NNE of Port-Olry, Vanuatu\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[167.171,-14.7729,79]},\"id\":\"us10007wbk\"},\n" +
            "{\"type\":\"Feature\",\"properties\":{\"mag\":4.3,\"place\":\"18km WNW of Ashkasham, Afghanistan\",\"time\":1485818907670,\"updated\":1486248130040,\"tz\":270,\"url\":\"http://earthquake.usgs.gov/earthquakes/eventpage/us10007wbi\",\"detail\":\"http://earthquake.usgs.gov/fdsnws/event/1/query?eventid=us10007wbi&format=geojson\",\"felt\":null,\"cdi\":null,\"mmi\":null,\"alert\":null,\"status\":\"reviewed\",\"tsunami\":0,\"sig\":284,\"net\":\"us\",\"code\":\"10007wbi\",\"ids\":\",us10007wbi,\",\"sources\":\",us,\",\"types\":\",geoserve,origin,phase-data,\",\"nst\":null,\"dmin\":0.716,\"rms\":1.02,\"gap\":68,\"magType\":\"mb\",\"type\":\"earthquake\",\"title\":\"M 4.3 - 18km WNW of Ashkasham, Afghanistan\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[71.3426,36.7554,165.25]},\"id\":\"us10007wbi\"},\n" +
            "{\"type\":\"Feature\",\"properties\":{\"mag\":3.3,\"place\":\"55km NNE of Road Town, British Virgin Islands\",\"time\":1485818531600,\"updated\":1486336329192,\"tz\":-240,\"url\":\"http://earthquake.usgs.gov/earthquakes/eventpage/pr17030005\",\"detail\":\"http://earthquake.usgs.gov/fdsnws/event/1/query?eventid=pr17030005&format=geojson\",\"felt\":0,\"cdi\":1,\"mmi\":null,\"alert\":null,\"status\":\"REVIEWED\",\"tsunami\":0,\"sig\":168,\"net\":\"pr\",\"code\":\"17030005\",\"ids\":\",pr17030005,\",\"sources\":\",pr,\",\"types\":\",dyfi,geoserve,origin,\",\"nst\":3,\"dmin\":1.00162154,\"rms\":0.1,\"gap\":345.6,\"magType\":\"Md\",\"type\":\"earthquake\",\"title\":\"M 3.3 - 55km NNE of Road Town, British Virgin Islands\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-64.4125,18.8812,45]},\"id\":\"pr17030005\"},\n" +
            "{\"type\":\"Feature\",\"properties\":{\"mag\":3.2,\"place\":\"59km SE of Boca de Yuma, Dominican Republic\",\"time\":1485817730600,\"updated\":1486171400040,\"tz\":-300,\"url\":\"http://earthquake.usgs.gov/earthquakes/eventpage/pr17030004\",\"detail\":\"http://earthquake.usgs.gov/fdsnws/event/1/query?eventid=pr17030004&format=geojson\",\"felt\":null,\"cdi\":null,\"mmi\":null,\"alert\":null,\"status\":\"REVIEWED\",\"tsunami\":0,\"sig\":158,\"net\":\"pr\",\"code\":\"17030004\",\"ids\":\",pr17030004,us10007wdg,\",\"sources\":\",pr,us,\",\"types\":\",geoserve,origin,phase-data,\",\"nst\":10,\"dmin\":0.71236402,\"rms\":0.25,\"gap\":327.6,\"magType\":\"Md\",\"type\":\"earthquake\",\"title\":\"M 3.2 - 59km SE of Boca de Yuma, Dominican Republic\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-68.1585,18.0802,28]},\"id\":\"pr17030004\"},\n" +
            "{\"type\":\"Feature\",\"properties\":{\"mag\":2.62,\"place\":\"10km NE of Indio, CA\",\"time\":1485817350640,\"updated\":1485903964205,\"tz\":-480,\"url\":\"http://earthquake.usgs.gov/earthquakes/eventpage/ci37575823\",\"detail\":\"http://earthquake.usgs.gov/fdsnws/event/1/query?eventid=ci37575823&format=geojson\",\"felt\":9,\"cdi\":2.2,\"mmi\":null,\"alert\":null,\"status\":\"reviewed\",\"tsunami\":0,\"sig\":108,\"net\":\"ci\",\"code\":\"37575823\",\"ids\":\",ci37575823,\",\"sources\":\",ci,\",\"types\":\",dyfi,focal-mechanism,geoserve,nearby-cities,origin,phase-data,scitech-link,\",\"nst\":88,\"dmin\":0.05925,\"rms\":0.16,\"gap\":36,\"magType\":\"ml\",\"type\":\"earthquake\",\"title\":\"M 2.6 - 10km NE of Indio, CA\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-116.1506667,33.7913333,7.39]},\"id\":\"ci37575823\"},\n" +
            "{\"type\":\"Feature\",\"properties\":{\"mag\":2.8,\"place\":\"4km S of Maricao, Puerto Rico\",\"time\":1485817218200,\"updated\":1486190877040,\"tz\":-240,\"url\":\"http://earthquake.usgs.gov/earthquakes/eventpage/pr17030003\",\"detail\":\"http://earthquake.usgs.gov/fdsnws/event/1/query?eventid=pr17030003&format=geojson\",\"felt\":null,\"cdi\":null,\"mmi\":null,\"alert\":null,\"status\":\"REVIEWED\",\"tsunami\":0,\"sig\":121,\"net\":\"pr\",\"code\":\"17030003\",\"ids\":\",pr17030003,us10007wc1,\",\"sources\":\",pr,us,\",\"types\":\",geoserve,origin,phase-data,\",\"nst\":11,\"dmin\":0.21559567,\"rms\":0.31,\"gap\":100.8,\"magType\":\"Md\",\"type\":\"earthquake\",\"title\":\"M 2.8 - 4km S of Maricao, Puerto Rico\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-66.9767,18.1821,13]},\"id\":\"pr17030003\"},\n" +
            "{\"type\":\"Feature\",\"properties\":{\"mag\":2.1,\"place\":\"29km WSW of Hawthorne, Nevada\",\"time\":1485816650637,\"updated\":1485894385782,\"tz\":-480,\"url\":\"http://earthquake.usgs.gov/earthquakes/eventpage/nn00576317\",\"detail\":\"http://earthquake.usgs.gov/fdsnws/event/1/query?eventid=nn00576317&format=geojson\",\"felt\":null,\"cdi\":null,\"mmi\":null,\"alert\":null,\"status\":\"reviewed\",\"tsunami\":0,\"sig\":68,\"net\":\"nn\",\"code\":\"00576317\",\"ids\":\",nn00576317,\",\"sources\":\",nn,\",\"types\":\",focal-mechanism,geoserve,origin,phase-data,\",\"nst\":24,\"dmin\":0.139,\"rms\":0.163,\"gap\":56.78,\"magType\":\"ml\",\"type\":\"earthquake\",\"title\":\"M 2.1 - 29km WSW of Hawthorne, Nevada\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-118.9237,38.3951,7.8]},\"id\":\"nn00576317\"},\n" +
            "{\"type\":\"Feature\",\"properties\":{\"mag\":4.5,\"place\":\"137km WSW of Banda Aceh, Indonesia\",\"time\":1485815133830,\"updated\":1485819197040,\"tz\":360,\"url\":\"http://earthquake.usgs.gov/earthquakes/eventpage/us10007wb8\",\"detail\":\"http://earthquake.usgs.gov/fdsnws/event/1/query?eventid=us10007wb8&format=geojson\",\"felt\":null,\"cdi\":null,\"mmi\":null,\"alert\":null,\"status\":\"reviewed\",\"tsunami\":0,\"sig\":312,\"net\":\"us\",\"code\":\"10007wb8\",\"ids\":\",us10007wb8,\",\"sources\":\",us,\",\"types\":\",geoserve,origin,phase-data,\",\"nst\":null,\"dmin\":2.759,\"rms\":0.75,\"gap\":159,\"magType\":\"mb\",\"type\":\"earthquake\",\"title\":\"M 4.5 - 137km WSW of Banda Aceh, Indonesia\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[94.1802,5.0823,44.78]},\"id\":\"us10007wb8\"},\n" +
            "{\"type\":\"Feature\",\"properties\":{\"mag\":2.5,\"place\":\"12km ESE of Hennessey, Oklahoma\",\"time\":1485814863600,\"updated\":1485911628040,\"tz\":-360,\"url\":\"http://earthquake.usgs.gov/earthquakes/eventpage/us10007wb0\",\"detail\":\"http://earthquake.usgs.gov/fdsnws/event/1/query?eventid=us10007wb0&format=geojson\",\"felt\":null,\"cdi\":null,\"mmi\":null,\"alert\":null,\"status\":\"reviewed\",\"tsunami\":0,\"sig\":96,\"net\":\"us\",\"code\":\"10007wb0\",\"ids\":\",us10007wb0,\",\"sources\":\",us,\",\"types\":\",geoserve,origin,phase-data,\",\"nst\":null,\"dmin\":null,\"rms\":0.17,\"gap\":26,\"magType\":\"ml\",\"type\":\"earthquake\",\"title\":\"M 2.5 - 12km ESE of Hennessey, Oklahoma\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[-97.7663,36.0736,5]},\"id\":\"us10007wb0\"},\n" +
            "{\"type\":\"Feature\",\"properties\":{\"mag\":4.5,\"place\":\"Indian Ocean Triple Junction\",\"time\":1485814404170,\"updated\":1485818145040,\"tz\":240,\"url\":\"http://earthquake.usgs.gov/earthquakes/eventpage/us10007wb4\",\"detail\":\"http://earthquake.usgs.gov/fdsnws/event/1/query?eventid=us10007wb4&format=geojson\",\"felt\":null,\"cdi\":null,\"mmi\":null,\"alert\":null,\"status\":\"reviewed\",\"tsunami\":0,\"sig\":312,\"net\":\"us\",\"code\":\"10007wb4\",\"ids\":\",us10007wb4,\",\"sources\":\",us,\",\"types\":\",geoserve,origin,phase-data,\",\"nst\":null,\"dmin\":7.929,\"rms\":0.67,\"gap\":117,\"magType\":\"mb\",\"type\":\"earthquake\",\"title\":\"M 4.5 - Indian Ocean Triple Junction\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[65.5125,-27.4728,10]},\"id\":\"us10007wb4\"}],\"bbox\":[-118.9237,-27.4728,5,167.171,38.3951,165.25]}";

    private QueryUtils() {
    }

    public static ArrayList<EarthQuake> extractEarthquakes() {

        ArrayList<EarthQuake> earthquakes = new ArrayList<>();

        try {

            JSONObject root = new JSONObject(SAMPLE_JSON_RESPONSE);
            JSONArray features = root.getJSONArray("features");

            for (int i = 0; i < features.length(); i++){
                JSONObject current = features.getJSONObject(i);
                JSONObject properties = current.getJSONObject("properties");

                Double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");

                earthquakes.add(new EarthQuake(mag, place, time, url));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return earthquakes;
    }

}
