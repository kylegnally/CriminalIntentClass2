package edu.kvcc.cis298.cis298inclass3;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by cisco on 11/27/2017.
 */

public class CrimeFetcher {

    //TAG for the logcat
    private static final String TAG = "CrimeFetcher";

    //Method to get the raw bytes from the web source. Conversion from bytes
    //to something more meaningful wil happen in a different method.
    //The method has one parameter which is the url that we want to connect
    //to.
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        //Create a new URL object from the url string that was passed in
        URL url = new URL(urlSpec);
        //Create a new HTTP connection to the specified url.
        //If we were to load data from a secure site, it would need
        //to use HttpsURLConnection.
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            //Create a output stream to hold the data that is read from
            //the url source
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //Create an input stream from the http connection
            InputStream in = connection.getInputStream();

            //Check to see that the response from the http request is
            //200, which is the same as http_ok. Every web request will
            //return some sort of response code.
            //Typically the following is true:
            //200 = good, 300 = cache, 400 = error, 500 = server error
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //Throw a new IOException
                throw new IOException(connection.getResponseMessage() +
                    ": with " +
                urlSpec);
            }

            //Create an int to hold how many bytes were read in.
            int bytesRead = 0;
            //Create a byte array to act as a bufer that will read
            //in up to 1024 bytes at a time
            byte[] buffer = new byte[1024];
            //while we can read bytes from the input stream
            while ((bytesRead = in.read(buffer)) > 0) {
                //write the bytes out to the output stream
                out.write(buffer, 0, bytesRead);
            }
            //Once everything is read and written, close the connection
            out.close();
            //Return the output stream converted to a byte array
            return out.toByteArray();

        //Finally close the connection to the url
        } finally {
            connection.disconnect();
        }
    }

    //Method to take in a url and convert the response to a string
    //of data.
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<Crime> fetchCrimes() {

        //Make a local list of crimes to hold what we parse
        //from the returned JSON string
        List<Crime> crimes = new ArrayList<>();

        try {
            String url = Uri.parse("http://barnesbrothers.homeserver.com/crimeapi")
                    .buildUpon()
                    //Add extra parameters here with the method
                    //.appendQueryParameter("param", "value")
                    .build()
                    .toString();

            String jsonString = getUrlString(url);

            Log.i(TAG, "Received JSON: " + jsonString);

            //The root element that we are getting back is an Array
            //so we will convert this string into a JSONArray
            JSONArray jsonBody = new JSONArray(jsonString);
            //Call parse crimes method defined below and
            //send in the crimes list defined at the top
            //of this method
            parseCrimes(crimes, jsonBody);

        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        //Return the list of crimes now that they have been
        //populated by the parsing
        return crimes;
    }

    private void parseCrimes(List<Crime> crimes, JSONArray jsonArray)
        throws IOException, JSONException {

        //Loop through all of the elements in the JSONArray that was
        //sent into this method
        for (int i=0; i < jsonArray.length(); i++) {

            //Fetch a single JSONObject out from the JSONArray based on
            //the current index that we are on.
            JSONObject crimeJsonObject = jsonArray.getJSONObject(i);

            //Pull the value from the JSONObject for the Key of "uuid"
            String uuidString = crimeJsonObject.getString("uuid");
            //Use the Value to create a new UUID from that string
            UUID uuidForNewCrime = UUID.fromString(uuidString);

            //Create a new Crime passing in the newly created UUID.
            Crime crime = new Crime(uuidForNewCrime);

            //Set the title on the crime by retrieving it from the
            //JSONObject
            crime.setTitle(crimeJsonObject.getString("title"));

            //Next let's parse out the date.
            try {
                //Declare a date formatter that can be used to parse
                //the date from a string into an actual date object.
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd",
                        Locale.ENGLISH);
                //Use the format to parse the string that we get from
                //the JSONObject
                Date date = format.parse(crimeJsonObject.getString("incident_date"));

                //Set the date on the crime to the parsed date
                crime.setDate(date);
            } catch (ParseException e) {
                //If there is an exception, just set the date to today.
                crime.setDate(new Date());
            }

            //Evaluate the is_solved value from the JSONObject to see if
            //it is equal to "1". If the expression is true, then the
            //is_solved for the crime is true.
            crime.setSolved(crimeJsonObject.getString("is_solved").equals("1"));

            //Add the finished crime to the list of crimes that
            //was passed in to this method.
            crimes.add(crime);
        }
    }

}
