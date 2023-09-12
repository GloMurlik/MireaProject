package ru.mirea.gribinichenko.mireaproject.ui.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.mirea.gribinichenko.mireaproject.databinding.FragmentNetworkBinding;

public class NetworkFragment extends Fragment {

    private FragmentNetworkBinding binding;

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNetworkBinding.inflate(inflater, container, false);
        mContext = inflater.getContext();
        binding.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadPageTask().execute("https://ipinfo.io/json");
            }
        });
        return binding.getRoot();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadPageTask extends AsyncTask<String, Void, String> {
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.textIp.setText("ip: Загружаем...");
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadIpInfo(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject responseJson = new JSONObject(result);
                String ip = responseJson.getString("ip");
                String city = responseJson.getString("city");
                String hostname = responseJson.getString("hostname");
                String region = responseJson.getString("region");
                String country = responseJson.getString("country");
                String loc = responseJson.getString("loc");
                String org = responseJson.getString("org");
                String postal = responseJson.getString("postal");
                String timezone = responseJson.getString("timezone");
                String readme = responseJson.getString("readme");

                binding.textCity.setText("City: " + city);
                binding.textHost.setText("Hostname: " + hostname);
                binding.textRegion.setText("Region: " + region);
                binding.textIp.setText("IP: " + ip);
                binding.textCountry.setText("Country: " + country);
                binding.textLoc.setText("Loc: " + loc);
                binding.textOrg.setText("Org: " + org);
                binding.textPostal.setText("Postal: " + postal);
                binding.textTimeZone.setText("Time Zone: " + timezone);
                binding.textReadMe.setText("ReadMe: " + readme);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
    private String downloadIpInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read); }
                bos.close();
                data = bos.toString();
            } else {
                data = connection.getResponseMessage()+". Error Code: " + responseCode;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }
}