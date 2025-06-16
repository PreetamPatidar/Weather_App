//retrieve weather data from API - this backend logic will fetch the latest weather
//data from  the external API and return it. the GUI will
// display this data to the user

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

public class WeatherApp {
    //fetch weather data form given location
    public static JSONObject getWeatherData(String locationName){
        //get location coordinates using the geoLocation API
        JSONArray locationData = getLocationData(locationName);

        //Extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?"+
                "latitude="+ latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m&timezone=auto";

        try{
            //call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check for response status
            //200 - means that the connection was success
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;

            }

            //store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                //read and store into the string builder
                resultJson.append(scanner.nextLine());
            }

            scanner.close();

            conn.disconnect();

            //parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //we want to get the current hour's data
            //so we need to get the index of our current hour

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            //get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            //get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            //get windSpeed
            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windSpeed = (double) windSpeedData.get(index);

            //build the weather json data object that we are going to access in our frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windSpeed);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    // retrieve geographic coordinates for given location name
    public static  JSONArray getLocationData(String locationName){
        //replace any whitespace in location name to + to adhere to API request format
        locationName = locationName.replaceAll(" ", "+");

        //build API url with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="
        + locationName+"&count=10&language=en&format=json";

        try{
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check response status
            //200 means successful connection
            if(conn.getResponseCode() != 200){
                System.out.println("ERROR: Could not connect to API");
                return null;

            }else{
                //store the API result
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                //read and store the resulting json data into our string builder
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                //close scanner
                scanner.close();

                //close url connection
                conn.disconnect();

                //parse the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultJsonObj.get("results");
                return locationData;


            }

        }catch(Exception e) {
            e.printStackTrace();
        }

        //couldn't find location
        return null;

    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set request method to get
            conn.setRequestMethod("GET");

            //connect to our API
            conn.connect();
            return conn;

        }catch (IOException e){
            e.printStackTrace();
        }
        //Could not make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        //iterate through the time list and see which one matches our current time
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                //return index
                return i;
            }
        }

        return 0;

    }

    public static String getCurrentTime(){
        //get current data and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format date to be 2023-09-02T00:00 (this is how is read in the Api)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //format and print the current data and time
        String formattedDateTime = currentDateTime.format(formatter);

        return  formattedDateTime;

    }

    //convert weatherCode to something more readable
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if (weathercode == 0L){
            weatherCondition = "Clear";

        }else if(weathercode > 0L && weathercode <= 3L) {
            weatherCondition = "Cloudy";

        } else if ((weathercode >= 51L && weathercode <= 67L) || (weathercode <= 80L && weathercode <= 99L)){
            weatherCondition = "Rain";


        } else if (weathercode >= 71l && weathercode <= 77L) {
            weatherCondition = "Snow";

        }

        return weatherCondition;
    }
}
