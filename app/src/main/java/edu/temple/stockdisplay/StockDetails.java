package edu.temple.stockdisplay;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StockDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockDetails extends Fragment {
    private String symbol;


    public StockDetails() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StockDetails.
     */
    public static StockDetails newInstance(String symbol) {
        StockDetails fragment = new StockDetails();
        Bundle args = new Bundle();
        args.putString("symbol", symbol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.symbol = getArguments().getString("symbol");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stock_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Picasso.with(this.getContext()).load("https://finance.google.com/finance/getchart?p=5d&q=" + symbol).into((ImageView) (getView().findViewById(R.id.stockChart)));

        OnFileRetrievalRequested fileInfo = ((OnFileRetrievalRequested)getActivity());
        fileInfo.loadInfo(symbol);
        ((TextView)getView().findViewById(R.id.name)).setText(fileInfo.getName());
        ((TextView)getView().findViewById(R.id.price)).setText(fileInfo.getPrice());
    }

    public void setSymbol(String symbol){
        this.symbol = symbol;
        Picasso.with(this.getContext()).load("https://finance.google.com/finance/getchart?p=5d&q=" + symbol).into((ImageView) (getView().findViewById(R.id.stockChart)));

        OnFileRetrievalRequested fileInfo = ((OnFileRetrievalRequested)getActivity());
        fileInfo.loadInfo(symbol);
        ((TextView)getView().findViewById(R.id.name)).setText(fileInfo.getName());
        ((TextView)getView().findViewById(R.id.price)).setText(fileInfo.getPrice());
    }

    public interface OnFileRetrievalRequested {
        public void loadInfo(String symbol);
        public String getName();
        public String getPrice();
    }
}
