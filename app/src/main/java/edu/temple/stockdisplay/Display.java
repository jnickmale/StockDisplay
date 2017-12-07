package edu.temple.stockdisplay;

import android.app.Fragment;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;


public class Display extends AppCompatActivity implements Portfolio.OnStockSelected, StockDetails.OnFileRetrievalRequested{

    protected final String STOCK_FILE_NAME = "stockData";
    private final int BLOCK_SIZE = 1024;
    private File myFile;
    private HashMap<String, Integer> fileMap = new HashMap<String, Integer>();
    private String stockName;
    private String stockPrice;
    private String addingStockSymbol;
    private Service theService;

    private ServiceConnection serviceConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //set up the action bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //create the file if it does not exist
        myFile = new File(getFilesDir(), STOCK_FILE_NAME);
        try{
            myFile.createNewFile();
            myFile.setWritable(true);
        }catch(java.io.IOException e) {

        }

        serviceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                theService = (((StockUpdate.LocalBinder)iBinder).getService());
                ((StockUpdate)theService).setFile(myFile, fileMap);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, StockUpdate.class);

        bindService(serviceIntent, serviceConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_add:
                addStock();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addStock(){
        final Handler handler = new stockAddingHandler(getFragmentManager().findFragmentById(R.id.portfolioFrag));

        //request a stock to lookup and add to the list
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a stock symbol");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //make call to the service to add the requested stock
                addingStockSymbol = input.getText().toString();
                ((StockUpdate)theService).addStock(addingStockSymbol, handler);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onStockSelected(String symbol) {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE)){
            //if landscape or large screen
            ((StockDetails)getFragmentManager().findFragmentById(R.id.detailsFrag)).setSymbol(symbol);
        }else{
            getFragmentManager().beginTransaction().replace(R.id.fragHolder, StockDetails.newInstance(symbol)).addToBackStack(null).commit();
        }
    }

    public void loadFromFile(ArrayList<String> stockSymbols){
        int blockIndex;
        byte[] b = new byte[1024];
        int numSyms = (int)(myFile.length()/1024);
        for(int k = 0; k < numSyms; k++){
            JSONObject jsonObject;

                RandomAccessFile readingFile;
                try {
                    readingFile = new RandomAccessFile(myFile, "r");
                    readingFile.seek(BLOCK_SIZE*k);
                    readingFile.read(b);
                    readingFile.close();
                    jsonObject = new JSONObject(new String(b));
                    stockSymbols.add(jsonObject.get("Symbol").toString());
                    fileMap.put(jsonObject.get("Symbol").toString(), k);
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }

    }

    public void loadInfo(String symbol){
        int blockIndex;
        byte[] b = new byte[1024];
        JSONObject jsonObject;
        if((fileMap.get(symbol)) != null){
            blockIndex = fileMap.get(symbol);
            RandomAccessFile readingFile;
            try {
                readingFile = new RandomAccessFile(myFile, "r");
                readingFile.seek(BLOCK_SIZE*blockIndex);
                readingFile.read(b);
                readingFile.close();
                jsonObject = new JSONObject(new String(b));
                stockName = jsonObject.get("Name").toString();
                stockPrice = jsonObject.get("LastPrice").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String getName() {
        return stockName;
    }

    @Override
    public String getPrice() {
        return stockPrice;
    }
}
