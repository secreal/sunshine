package ptk.com.sunshine;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.id.list;
import static ptk.com.sunshine.R.id.lv;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Fragment fragment = new PlaceholderFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 5;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 6;

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);


            if (ContextCompat.checkSelfPermission(PlaceholderFragment.this.getActivity(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(PlaceholderFragment.this.getActivity(), Manifest.permission.INTERNET)) {
                } else {
                    ActivityCompat.requestPermissions(PlaceholderFragment.this.getActivity(), new String[]{
                            Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
                }
            }
            if (ContextCompat.checkSelfPermission(PlaceholderFragment.this.getActivity(), Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(PlaceholderFragment.this.getActivity(), Manifest.permission.ACCESS_NETWORK_STATE)) {
                } else {
                    ActivityCompat.requestPermissions(PlaceholderFragment.this.getActivity(), new String[]{
                            Manifest.permission.ACCESS_NETWORK_STATE}, MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
                }
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=Jakarta,id&appid=a6c98baae0e401752c6604c9e6cd1f5e");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }







            String[] forecastArray = {
                    forecastJsonStr, "Hari ini - Cerah - 30/32",
                    "Besok - Berawan - 28/30",
                    "Minggu depan - Cerah - 30/32",
                    "Bulan Depan - Cerah - 30/32",
                    "Tahun Depan- Cerah - 30/32",
                    "10 Tahun lagi- Cerah - 30/32"
            };

            JSONArray arr = null;
//            try {
//                arr = new JSONArray(forecastJsonStr);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            List<String> list = new ArrayList<String>();
//            for(int i = 0; i < arr.length(); i++){
//                try {
//                    list.add(arr.getJSONObject(i).getString("name"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

            List<String> weekForecast = new ArrayList<String>(
                    Arrays.asList(forecastArray));
            ArrayAdapter arrayku =  new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);
            ListView lv = (ListView) rootView.findViewById(R.id.lv);
            lv.setDivider(null);
            lv.setAdapter(arrayku);

            }

            return rootView;
        }
    }
}


