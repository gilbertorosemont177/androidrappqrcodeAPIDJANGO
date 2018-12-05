package com.example.gilbertitoramos.qrcodeappread;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CnxApiDjangoAsyncTask extends AsyncTask<String, Void, JSONObject> {
    Context contextapp;
    String usernametxt;
    String imgv;
    JSONArray jsonarray = null;
    JSONObject jsonObject=null;
    GetAsyncResponse delegateforAllResponsesfromThisAsyncTask=null;

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    @Override
    protected JSONObject doInBackground(String... strings) {

        URL url = null;
        HttpURLConnection urlConnection = null;
        String result = null;
        try {
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream()); // Stream

            result = readStream(in);
        }
        catch (MalformedURLException e) { e.printStackTrace(); }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }


        if(result!=null){

        try {
            jsonarray = new JSONArray(result);

            for(int i=0; i<jsonarray.length();i++){
                jsonObject=jsonarray.getJSONObject(i);
                String email=jsonObject.getString("email");
                String address=jsonObject.getString("address");
                String telephone=jsonObject.getString("telephone");
                String img=jsonObject.getString("img");


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        }

        return jsonObject; // returns the result
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
       super.onPostExecute(jsonObject);


            delegateforAllResponsesfromThisAsyncTask.resultFromAsyncTask(jsonObject);


    }
}