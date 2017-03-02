package com.vadimkin.vadimk.methow2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    TextView tvMazamaPeriod;
    TextView tvMazamaDescr;
    ImageView ivMazamaIcon;

    TextView tvMazamaPeriodNext;
    TextView tvMazamaDescrNext;
    ImageView ivMazamaIconNext;

    TextView tvWinthropPeriod;
    TextView tvWinthropDescr;
    ImageView ivWinthropIcon;

    TextView tvWinthropPeriodNext;
    TextView tvWinthropDescrNext;
    ImageView ivWinthropIconNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        tvMazamaDescr = (TextView) view.findViewById(R.id.mazamaDescr);
        ivMazamaIcon = (ImageView) view.findViewById(R.id.mazamaIcon);
        tvMazamaPeriod = (TextView) view.findViewById(R.id.mazamaPeriod);
        tvMazamaDescrNext = (TextView) view.findViewById(R.id.mazamaDescrNext);
        ivMazamaIconNext = (ImageView) view.findViewById(R.id.mazamaIconNext);
        tvMazamaPeriodNext = (TextView) view.findViewById(R.id.mazamaPeriodNext);
        tvWinthropDescr = (TextView) view.findViewById(R.id.winthropDescr);
        ivWinthropIcon = (ImageView) view.findViewById(R.id.winthropIcon);
        tvWinthropPeriod = (TextView) view.findViewById(R.id.winthropPeriod);
        tvWinthropDescrNext = (TextView) view.findViewById(R.id.winthropDescrNext);
        ivWinthropIconNext = (ImageView) view.findViewById(R.id.winthropIconNext);
        tvWinthropPeriodNext = (TextView) view.findViewById(R.id.winthropPeriodNext);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new setWeatherFromUrl(tvMazamaPeriod, tvMazamaDescr, ivMazamaIcon, tvMazamaPeriodNext, tvMazamaDescrNext, ivMazamaIconNext).execute(getMazamaUrl());
        new setWeatherFromUrl(tvWinthropPeriod, tvWinthropDescr, ivWinthropIcon, tvWinthropPeriodNext, tvWinthropDescrNext, ivWinthropIconNext).execute(getWinthropUrl());
    }

    class setWeatherFromUrl extends AsyncTask<String, Void, String> {

        private TextView tvDescr;
        private TextView tvPeriod;
        private ImageView ivIcon;
        private TextView tvDescrNext;
        private TextView tvPeriodNext;
        private ImageView ivIconNext;
        setWeatherFromUrl(TextView tvPeriod, TextView tvDescr, ImageView ivIcon, TextView tvPeriodNext, TextView tvDescrNext, ImageView ivIconNext) {
            this.tvPeriod = tvPeriod;
            this.tvDescr = tvDescr;
            this.ivIcon = ivIcon;
            this.tvPeriodNext = tvPeriodNext;
            this.tvDescrNext = tvDescrNext;
            this.ivIconNext = ivIconNext;
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
            tvPeriod.setText(getPeriodFromWeather(weather, 0));
            tvDescr.setText(getDescrFromWeather(weather, 0));
            new setImageFromUrl(ivIcon).execute(getIconUrlFromWeather(weather, 0));
            tvPeriodNext.setText(getPeriodFromWeather(weather, 1));
            tvDescrNext.setText(getDescrFromWeather(weather, 1));
            new setImageFromUrl(ivIconNext).execute(getIconUrlFromWeather(weather, 1));

            final MainActivity activity = (MainActivity) getActivity();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    class setImageFromUrl extends AsyncTask<String, Void, Bitmap> {

        private ImageView ivImage;
        setImageFromUrl(ImageView iv) {
            this.ivImage = iv;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException ex) {
                return null;
            } catch (IOException ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ivImage.setImageBitmap(bitmap);
        }
    }

    String getWeatherUrl(double lat, double lon) {
        return String.format("http://forecast.weather.gov/MapClick.php?lat=%f&lon=%f&FcstType=json", lat, lon);
    }

    private final double MAZAMA_LAT = 48.5921;
    private final double MAZAMA_LON = -120.4040;
    private final double WINTHROP_LAT = 48.4779;
    private final double WINTHROP_LON = -120.1862;

    String getMazamaUrl() {
        return getWeatherUrl(MAZAMA_LAT, MAZAMA_LON);
    }
    String getWinthropUrl() {
        return getWeatherUrl(WINTHROP_LAT, WINTHROP_LON);
    }

    String getDescrFromWeather(String weaterJson, int periodIndex) {
        try {
            JSONObject jsonObject = new JSONObject(weaterJson);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray jArray = data.getJSONArray("text");
            String descr = jArray.getString(periodIndex);
            return descr;

        } catch (JSONException ex) {
            //TODO
            return null;
        }
    }

    String getPeriodFromWeather(String weaterJson, int periodIndex) {
        try {
            JSONObject jsonObject = new JSONObject(weaterJson);
            JSONObject time = jsonObject.getJSONObject("time");
            JSONArray start = time.getJSONArray("startPeriodName");
            String period = start.getString(periodIndex);
            return period;

        } catch (JSONException ex) {
            //TODO
            return null;
        }
    }

    String getIconUrlFromWeather(String weatherJson, int periodIndex) {
        try {
            JSONObject jsonObject = new JSONObject(weatherJson);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray jArray = data.getJSONArray("iconLink");
            String icon = jArray.getString(periodIndex);
            return icon;

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
