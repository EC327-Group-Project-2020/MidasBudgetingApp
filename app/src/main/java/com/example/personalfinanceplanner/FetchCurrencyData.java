package com.example.personalfinanceplanner;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class FetchCurrencyData extends AsyncTask<Void, Void,Void> {

    //debug tag for log
    private static final String TAG_DEBUG = FetchCurrencyData.class.getName();
    ArrayList<String> currNames = new ArrayList<>();
    ArrayList<ArrayList<String>> currNamesRates = new ArrayList<ArrayList<String>>();

    @Override
    protected Void doInBackground(Void... voids) {

        String data = "";
        Log.d(TAG_DEBUG, "Inside background");
        try {
            URL symbolurl = new URL("https://api.exchangeratesapi.io/latest?base=USD");
            HttpsURLConnection HttpsURLConnection = (javax.net.ssl.HttpsURLConnection) symbolurl.openConnection();
            InputStream currencyInput = HttpsURLConnection.getInputStream();
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(currencyInput));
            Log.d(TAG_DEBUG, "Connection established");

            String currLine = "";
            while(currLine!= null){
                currLine = inputReader.readLine();
                data += currLine;
            }
            currencyInput.close();

            Log.d(TAG_DEBUG, "Response from url: " + data);


            JSONObject exChangeJson = new JSONObject(data);
            JSONObject exChangeRates = exChangeJson.getJSONObject("rates");

            JSONArray currNamesJSON = exChangeRates.names();
            Log.d(TAG_DEBUG, "name array created: " + currNamesJSON.toString());

            JSONArray currNamesRatesJSON = new JSONArray();
            for(int i = 0; i < currNamesJSON.length(); i++){
                ArrayList<String> tmp = new ArrayList<String>();
                String key = currNamesJSON.getString(i);
                String value = exChangeRates.get(key).toString();
                tmp.add(key);
                tmp.add(value);
                currNamesRates.add(tmp);
            }

            //check if correct
            String printstr = "";
            for(int i = 0; i < currNamesRates.size(); i++){
                for(int j = 0; j < 2; j++){
                    printstr += currNamesRates.get(i).get(j);
                }
            }
            Log.d(TAG_DEBUG, "name + rate  list of lists created: " + printstr);

            //put the JSON currency name array into transfer array
            int len = currNamesJSON.length();
            for (int i=0;i<len;i++){
                currNames.add(currNamesJSON.get(i).toString());
                }

            //various exception catching
        } catch (MalformedURLException e) {
            Log.d(TAG_DEBUG, "Couldn't find url");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG_DEBUG, "Couldn't connect");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(TAG_DEBUG, "Couldn't convert to JSON");
            e.printStackTrace();
        }
        Log.d(TAG_DEBUG, "At end of async");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        //transfer data to vars in MainActivity
        String prompt = "Add new";

        BudgetDisplayPage.names.add(0,prompt);

        for(int i = 1, j = 0; i < currNames.size(); i++,j++){
            BudgetDisplayPage.names.add(i,currNames.get(i));
        }

        for(int i = 0; i < currNamesRates.size(); i++){
            BudgetDisplayPage.namesRates.add(i,currNamesRates.get(i));
        }

        //notify change to secure update
        BudgetDisplayPage.aa.notifyDataSetChanged();
    }

}
