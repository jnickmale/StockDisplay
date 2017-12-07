package edu.temple.stockdisplay;


import android.content.ClipData;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Portfolio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Portfolio extends Fragment {

    protected final String STOCK_FILE_NAME = "stockData";
    private final int BLOCK_SIZE = 1024;
    private ArrayList<String> stockSymbols;
    private ArrayAdapter<String> adapter;


    public Portfolio() {
        // Required empty public constructor
        stockSymbols = new ArrayList<String>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Portfolio.
     */
    // TODO: Rename and change types and number of parameters
    public static Portfolio newInstance(String param1, String param2) {
        Portfolio fragment = new Portfolio();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stockSymbols = getArguments().getStringArrayList("symbols");
        }else{
            stockSymbols = new ArrayList<String>();
            stockSymbols.add(getString(R.string.PleaseAddItem));
        }

    }


    public void addToList(String symbol){
        stockSymbols.add(symbol);
        adapter.notifyDataSetChanged();
    }

    public void populateList(ArrayList<String> stockSymbols){

        ((OnStockSelected)getActivity()).loadFromFile(stockSymbols);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //populateList(stockSymbols);
        adapter=new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_list_item_1,
                stockSymbols);
        ((ListView)(getView().findViewById(R.id.stockList))).setAdapter(adapter);

        ((ListView)(getView().findViewById(R.id.stockList))).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String symbol = (String)adapterView.getItemAtPosition(i);
                ((OnStockSelected)getActivity()).onStockSelected(symbol);
            }
        });

    }

    public interface OnStockSelected {
        public void onStockSelected(String symbol);
        public void loadFromFile(ArrayList<String> symbols);
    }
}
