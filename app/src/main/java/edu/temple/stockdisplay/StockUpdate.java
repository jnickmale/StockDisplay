package edu.temple.stockdisplay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class StockUpdate extends Service {
    private final IBinder binder = new LocalBinder();
    private final int BLOCK_SIZE = 1024;

    private File myFile;
    private String filename;
    private HashMap<String, Integer> fileMap;
    private String addingSymbol;

    private Handler handler;

    public StockUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }



    public class LocalBinder extends Binder{
        StockUpdate getService(){
            return StockUpdate.this;
        }
    }

    public void updateData(){
        //for()
    }

    //adds the stock with the desired symbol if not already added
    public void addStock(String symbol, Handler handler){
        this.handler = handler;

        if(fileMap.get(symbol)==null){
            addingSymbol = symbol;
            //get the JSON data from the web

            //URL url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/");
            String url = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + symbol;


            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            writeJsonStringToFile(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String check = "failure";
                }
            });
            queue.add(stringRequest);


                //HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

                //InputStream in = new BufferedInputStream(urlConn.getInputStream());
                //read(bytes);
                //String check = bytes.toString();






        }
    }

    //write the data to the file
    public void writeJsonStringToFile(String jsonString){
        int blockIndex = fileMap.size();
        fileMap.put(addingSymbol,blockIndex);
        byte[] allBytes = new byte[BLOCK_SIZE];
        byte[] jsonBytes = jsonString.getBytes();

        System.arraycopy(jsonBytes, 0, allBytes, 0, jsonBytes.length);


        //write the JSON data to the file
        RandomAccessFile writingFile;
        try {
            writingFile = new RandomAccessFile(myFile, "rw");
            writingFile.seek(BLOCK_SIZE*blockIndex);
            writingFile.write(allBytes);
            writingFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((stockAddingHandler)handler).updateList(addingSymbol);
    }

    //updates all stock data
    public void updateStocks(){

    }

    //sets the file which the service will operate on
    public void setFile(File f, HashMap<String, Integer> fMap){
        myFile = f;
        fileMap = fMap;
        filename = f.getName();
    }
}
