package edu.temple.stockdisplay;

import android.app.Fragment;
import android.content.Context;
import android.os.Handler;

/**
 * Created by nmale_000 on 12/7/2017.
 */

public class stockAddingHandler extends Handler {
    Fragment portfolioFrag;
    public stockAddingHandler(Fragment f){
        portfolioFrag = f;
    }

    public void updateList(String symbol){
        ((Portfolio)portfolioFrag).addToList(symbol);
    }
}
