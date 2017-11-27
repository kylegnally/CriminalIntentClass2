package edu.kvcc.cis298.cis298inclass3;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public void fetchCrimes() {
        try {
            String url = Uri.parse("http://barnesbrothers.homeserver.com/crimeapi")
                    .buildUpon()
                    //Add extra parameters here with the method
                    //.appendQueryParameter("param", "value")
                    .build()
                    .toString();

            String jsonString = getUrlString(url);

            Log.i(TAG, "Received JSON: " + jsonString);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
    }
}
