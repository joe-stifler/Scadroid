package com.scadroid.interfaces;

import com.scadroid.webservice.request.ItemInfo;
import com.scadroid.webservice.request.ItemStringValue;

import java.util.ArrayList;

/**
 * Created by joe on 24/05/15.
 */
public interface NewPointInterface {
    public void addPoints(ArrayList<ItemInfo> itemList);
}
