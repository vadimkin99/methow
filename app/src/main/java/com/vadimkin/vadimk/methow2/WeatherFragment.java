package com.vadimkin.vadimk.methow2;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    TextView tvMazamaDescr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        tvMazamaDescr = (TextView) view.findViewById(R.id.mazamaDescr);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new setWeatherFromUrl(tvMazamaDescr).execute(getMazamaUrl());
    }

    class setWeatherFromUrl extends AsyncTask<String, Void, String> {

        private TextView tvDescr;
        setWeatherFromUrl(TextView tvDescr) {
            this.tvDescr = tvDescr;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";
                return result;
            } catch (MalformedURLException ex) {
                return null;
            } catch (IOException ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String weather) {
            tvDescr.setText(getDescrFromWeather(weather));
        }
    }

    String getWeatherUrl(double lat, double lon) {
        return String.format("http://forecast.weather.gov/MapClick.php?lat=%f&lon=%f&FcstType=json", lat, lon);
    }

    private final double MAZAMA_LAT = 48.5921;
    private final double MAZAMA_LON = -120.4040;

    String getMazamaUrl() {
        return getWeatherUrl(MAZAMA_LAT, MAZAMA_LON);
    }

    String getDescrFromWeather(String weaterJson) {
        try {
            JSONObject jsonObject = new JSONObject(weaterJson);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray jArray = data.getJSONArray("text");
            String descr = jArray.getString(0);
            return descr;

        } catch (JSONException ex) {
            //TODO
            return null;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
