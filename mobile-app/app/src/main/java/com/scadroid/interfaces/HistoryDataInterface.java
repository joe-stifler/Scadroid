package com.scadroid.interfaces;

import com.scadroid.webservice.request.ItemStringValue;

import java.util.ArrayList;

/**
 * Created by joe on 06/07/15.
 */
public interface HistoryDataInterface {
    public void dataHistory(ArrayList<ItemStringValue> array, boolean moreDatas);
}
