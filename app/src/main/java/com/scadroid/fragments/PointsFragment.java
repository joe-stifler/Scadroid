package com.scadroid.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;

import com.rey.material.widget.SnackBar;
import com.scadroid.R;
import com.scadroid.activities.MainActivity;
import com.scadroid.adapters.PointsAdapter;
import com.scadroid.database.DataBase;
import com.scadroid.domain.Point;
import com.scadroid.interfaces.NewPointInterface;
import com.scadroid.interfaces.RecyclerViewOnClickListenerHack;
import com.scadroid.interfaces.UpdatePointInterface;
import com.scadroid.webservice.request.API;
import com.scadroid.webservice.request.Enums;
import com.scadroid.webservice.request.ItemInfo;
import com.scadroid.webservice.request.ItemStringValue;
import com.scadroid.webservice.tasks.AsyncTasks;
import com.scadroid.webservice.tasks.UpdatePoint;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */

public class PointsFragment extends Fragment implements RecyclerViewOnClickListenerHack, NewPointInterface,
        UpdatePointInterface{

    private RecyclerView mRecyclerView;
    private PointsAdapter pointAdapter;
    private NewPointInterface pI = this;
    private API api;
    private ArrayList<UpdatePoint> pointsSync;
    private SnackBar snackBar;

    // put recyclerViewe on Activity
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_points, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tb_main);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.add_point:
                            new AsyncTasks(getActivity(), pI).execute(0);
                            break;
                        case R.id.update_points:
                            DataBase db = new DataBase(getActivity());
                            // verifying if there's any point in the database to be used
                            if(db.searchPoints().size() > 0){
                                for(Point item : db.searchPoints()){
                                    // method called to update the value of each point once
                                    new UpdatePoint(item.getName(), PointsFragment.this, getActivity(), TimeUnit.SECONDS, 1, true);
                                }
                            }
                            Toast.makeText(getActivity(), "Update request", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    return true;
                }
            });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true); // set a fixed value to be shown on view

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        pointsSync = new ArrayList<UpdatePoint>();

        // when there's some problem, this is gonna appear
        snackBar = (SnackBar) getActivity().findViewById(R.id.snack_main);
            snackBar.applyStyle(R.style.SnackBarSingleLine);
                snackBar.actionClickListener(new SnackBar.OnActionClickListener() {
                    @Override
                    public void onActionClick(SnackBar snackBar, int i) {

                    }
                });

        createDatas();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(((PointsAdapter) mRecyclerView.getAdapter()) != null)
            ((PointsAdapter) mRecyclerView.getAdapter()).updatePoints();
    }

    // used to put the sotoraged points on view
    public void createDatas(){
        DataBase db = new DataBase(getActivity());
        // verifying if there's any point in the database to be added to the interface
        if(db.searchPoints().size() > 0){
            pointAdapter = new PointsAdapter(getActivity());
            mRecyclerView.setAdapter(pointAdapter);

            for(Point item : db.searchPoints()){
                // method used to stay updating the value of the cards
                newUpdatePoint(item.getName(), item.getUpdate());
            }
        }

        db.closeDataBase();
    }

    public void destroyDatas(){
        for(UpdatePoint point : pointsSync)
            point.shutdown();

        pointsSync = new ArrayList<UpdatePoint>();
    }

    @Override
    public void onClickListener(View view, int position){

    }

    public void addNewPoint(Point p) {
        if (mRecyclerView.getAdapter() == null) {
            pointAdapter = new PointsAdapter(getActivity(), p);
            mRecyclerView.setAdapter(pointAdapter);

            newUpdatePoint(p.getName(), p.getUpdate());
        } else {
            pointAdapter = (PointsAdapter) mRecyclerView.getAdapter();
            pointAdapter.addListItem(p);

            newUpdatePoint(p.getName(), p.getUpdate());
        }
    }

    // method used to create a ScheduleThreadPool that will stay updating the value of the points
    public void newUpdatePoint(String name, int update_rate){
        if(update_rate != 0)
            pointsSync.add(new UpdatePoint(name, this, getActivity(), TimeUnit.SECONDS, update_rate, false));
    }

    public List<Point> getPoints(){
        try {
            return pointAdapter.getListItem();
        }catch(Exception e) {
            return null;
        }
    }

    // updating values of points with UpdatePointClass
    public void updatePoint(ItemStringValue value, String description){
        if(value != null)
            pointAdapter.setListItem(value);
        else
            snackBar.text(description).show();
    }

    // Este método é utilizado para receber os pontos vindos da requisição de quando se clica no botão fab(plus) e serão mostrados na tela
    //  através de um MultiChoiceDialog para que o usuário possa escolher qual ponto ele quer adicionar e assim inserir dentro da view
    private ArrayList<ItemInfo> itemList = new ArrayList<ItemInfo>();
    @Override
    public void addPoints(ArrayList<ItemInfo> itemInfoList){
        itemList = new ArrayList<ItemInfo>();
        boolean aux = false;
        PointsAdapter adapter = (PointsAdapter) mRecyclerView.getAdapter();

        if(adapter != null) {
            if(adapter.getListItem().size() > 0) {
                for (ItemInfo itemInfo : itemInfoList) {
                    for (Point item : ((PointsAdapter) mRecyclerView.getAdapter()).getListItem())
                        if (itemInfo.itemName.equals(item.getName()))
                            aux = true;
                    if (!aux)
                        this.itemList.add(itemInfo);

                    aux = false;
                }
            } else
                this.itemList = itemInfoList;
        } else
            this.itemList = itemInfoList;

        if(itemList.size() > 0) {
            String[] itens = new String[itemList.size()];
            for (int i = 0; i < itemList.size(); i++)
                itens[i] = itemList.get(i).itemName;

           Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialog){
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    CharSequence[] values =  getSelectedValues();
                    if(values == null)
                        Toast.makeText(getActivity(), "You didn't select any point.", Toast.LENGTH_SHORT).show();
                    else{
                        for (CharSequence value : values) {
                            // verifying which points were chosen
                            for (ItemInfo items : itemList) {
                                if (items.itemName == value) {
                                    addNewPoint(new Point(null, null , items.itemName, items.dataType.toString(), "null", items.writable, Calendar.getInstance()));
                                }
                            }
                        }
                        itemList = new ArrayList<ItemInfo>();
                    }
                    super.onPositiveActionClicked(fragment);
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    Toast.makeText(getActivity(), "Cancel" , Toast.LENGTH_SHORT).show();
                    super.onNegativeActionClicked(fragment);
                }
            };

            ((SimpleDialog.Builder)builder).multiChoiceItems(itens)
                    .title("Active Points")
                    .positiveAction("OK")
                    .negativeAction("CANCEL");

            final DialogFragment fragment = DialogFragment.newInstance(builder);
            fragment.show(getFragmentManager(), null);

        } else
            Toast.makeText(getActivity(), "All points already were added!", Toast.LENGTH_SHORT).show();
    }

}
