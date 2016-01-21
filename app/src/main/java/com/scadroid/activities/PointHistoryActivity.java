package com.scadroid.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.charts.PieChart;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.rey.material.app.Dialog;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Spinner;

import com.scadroid.R;
import com.scadroid.database.DataBase;
import com.scadroid.domain.Point;
import com.scadroid.domain.Profile;
import com.scadroid.extras.Charts;
import com.scadroid.extras.DateRangePickerFragment;
import com.scadroid.interfaces.HistoryDataInterface;
import com.scadroid.webservice.request.ItemStringValue;
import com.scadroid.webservice.tasks.AsyncTasks;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by joe on 30/06/15.
 */

public class PointHistoryActivity extends ActionBarActivity implements HistoryDataInterface{

    private Point point;
    private ProgressDialog progress;
    private int periodData = 0; // used to define the period of requested datas by the user
    private float media = 0, total = 0, pagar = 0;
    private TextView tvOne, tvTwo, tvThree; // used to show the values on the screen. This value coming from the chart after the user to do the requesting

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_point);

        if(savedInstanceState != null)
            point = savedInstanceState.getParcelable("point");
        else {
            if (this.getIntent() != null && this.getIntent().getExtras() != null && this.getIntent().getExtras().getParcelable("point") != null)
                point = this.getIntent().getExtras().getParcelable("point");
            else {
                Toast.makeText(this, "Fail!", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }

        // TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Chart - "+point.getName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        // this will define the period of your requisition. In the other words, this defines how much datas you want
        Spinner spinner_period = (Spinner) findViewById(R.id.spinner_period);
            String[] items = new String[8];
                items[0] = "Not defined";
                items[1] = "7 days";
                items[2] = "15 days";
                items[3] = "1 month";
                items[4] = "3 months";
                items[5] = "6 months";
                items[6] = "1 year";
                items[7] = "Set time";
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_spn, items);
                adapter.setDropDownViewResource(R.layout.row_spn_dropdown);
        spinner_period.setAdapter(adapter);

        ImageView iv_setting = (ImageView) findViewById(R.id.setting);
            iv_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!point.getCategoryString().equals("weather") && !point.getCategoryString().equals("luminosity")) {
                        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialog) {

                            @Override
                            protected void onBuildDone(Dialog dialog) {
                                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                if(point.getPrice() != 0)
                                    ((EditText) dialog.findViewById(R.id.custom_et_value)).setText(""+point.getPrice());
                            }

                            @Override
                            public void onPositiveActionClicked(com.rey.material.app.DialogFragment fragment) {
                                String value = ((EditText) fragment.getDialog().findViewById(R.id.custom_et_value)).getText().toString();

                                DataBase db = new DataBase(PointHistoryActivity.this);
                                    point.setPrice(Double.parseDouble(value));
                                    db.updatePoint(point);
                                db.closeDataBase();

                                super.onPositiveActionClicked(fragment);
                            }

                            @Override
                            public void onNegativeActionClicked(com.rey.material.app.DialogFragment fragment) {
                                Toast.makeText(PointHistoryActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                super.onNegativeActionClicked(fragment);
                            }
                        };

                        String value = null;

                        if (point.getCategoryString().equals("water"))
                            value = "Price of water (R$/m³)";
                        else if (point.getCategoryString().equals("energy"))
                            value = "Price of energy (R$/KW)";
                        else
                            value = "Consumption of energy (W)";

                        builder.title(value)
                                .positiveAction("OK")
                                .negativeAction("CANCEL")
                                .contentView(R.layout.set_value_point)
                                .style(R.style.SimpleDialog);

                        com.rey.material.app.DialogFragment fragment = com.rey.material.app.DialogFragment.newInstance(builder);
                        fragment.show(PointHistoryActivity.this.getSupportFragmentManager(), null);
                    } else
                        Toast.makeText(PointHistoryActivity.this, "Not allowed to this category of point", Toast.LENGTH_SHORT).show();
                }
            });

        spinner_period.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner spinner, View view, int i, long l) {
                if(i != 0 && !point.getCategoryString().equals("null")) {
                    Calendar cal = Calendar.getInstance();
                    switch (i) {
                        case 1:
                            cal.add(Calendar.DATE, -7);
                            periodData = 7;
                            new AsyncTasks(PointHistoryActivity.this, cal, point, PointHistoryActivity.this).execute(2);
                            progressDialogOn();
                            break;
                        case 2:
                            cal.add(Calendar.DATE, -15);
                            periodData = 15;
                            new AsyncTasks(PointHistoryActivity.this, cal, point, PointHistoryActivity.this).execute(2);
                            progressDialogOn();
                            break;

                        case 3:
                            cal.add(Calendar.DATE, -30);
                            periodData = 30;
                            new AsyncTasks(PointHistoryActivity.this, cal, point, PointHistoryActivity.this).execute(2);
                            progressDialogOn();
                            break;
                        case 4:
                            cal.add(Calendar.MONTH, -3);
                            periodData = 3;
                            new AsyncTasks(PointHistoryActivity.this, cal, point, PointHistoryActivity.this).execute(2);
                            progressDialogOn();
                            break;
                        case 5:
                            cal.add(Calendar.MONTH, -6);
                            periodData = 6;
                            new AsyncTasks(PointHistoryActivity.this, cal, point, PointHistoryActivity.this).execute(2);
                            progressDialogOn();
                            break;
                        case 6:
                            cal.add(Calendar.YEAR, -1);
                            periodData = 12;
                            new AsyncTasks(PointHistoryActivity.this, cal, point, PointHistoryActivity.this).execute(2);
                            progressDialogOn();
                            break;
                        case 7:
                            /*DialogFragment fragment = new DateRangePickerFragment();
                            fragment.show(getSupportFragmentManager(), null);*/
                            break;
                    }
                }else
                    Toast.makeText(PointHistoryActivity.this, "Add any hierarchy to your point. After you can create your chart.", Toast.LENGTH_SHORT).show();
            }
        });

        tvOne = (TextView) findViewById(R.id.tv_value_one);
        tvTwo = (TextView) findViewById(R.id.tv_value_two);
        tvThree = (TextView) findViewById(R.id.tv_value_three);

        if(point.getCategoryString().equals("water") || point.getCategoryString().equals("energy")){
            tvOne.setText("Total:");
            tvTwo.setText("Average:");
            tvThree.setText("Amount payable:");
        } else if(point.getCategoryString().equals("weather")){
            tvOne.setText("Average:");
            tvTwo.setText("Maximum:");
            tvThree.setText("Minimum:");
        } else if(point.getCategoryString().equals("light")){
            tvOne.setText("Time On:");
            tvTwo.setText("Time Off:");
            tvThree.setText("Consumption:");
        }
    }

    public void progressDialogOn(){
        progress = new ProgressDialog(this);
        progress.setMessage("Requesting datas...");
        progress.setCancelable(false);
        progress.show();
    }

    public void progressDialogOff(){
        progress.dismiss();
    }

    private Calendar dt[];
    private float values[];
    private ArrayList<ItemStringValue> itemValues = new ArrayList<ItemStringValue>();

    @Override
    public void dataHistory(ArrayList<ItemStringValue> array, boolean moreDatas){
        if (itemValues.size() != 0)
            array.remove(0);

        for(ItemStringValue item : array)
            itemValues.add(item);

        if(!moreDatas){
            progressDialogOff();
            final Charts chart = new Charts(this, itemValues, point.getType(), point.getCategoryString(), periodData, point.getPrice());
            LinearLayout linear = (LinearLayout) findViewById(R.id.linearLayout);

            switch(chart.getNonNull()){
                case 1:
                    linear.addView((BarChart) chart.getChart());
                    break;

                case 2:
                    linear.addView((LineChart) chart.getChart());
                    break;

                case 3:
                    linear.addView((PieChart) chart.getChart());
                    break;
            }

            ((TextView) findViewById(R.id.tv_value_field_one)).setText(chart.getValueOne());
            ((TextView) findViewById(R.id.tv_value_field_two)).setText(chart.getValueTwo());
            ((TextView) findViewById(R.id.tv_value_field_three)).setText(chart.getValueThree());

            moreDatas = false;
            itemValues = new ArrayList<ItemStringValue>();
        }  else{
            // If there's more values, then request again for datas
            Calendar cal = Calendar.getInstance();
                cal.setTime(itemValues.get(itemValues.size()-1).getTimestamp());
            new AsyncTasks(PointHistoryActivity.this, cal, point, PointHistoryActivity.this).execute(2);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("point", point);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
            finish();
        return true;
    }
}
