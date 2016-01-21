package com.scadroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.ViewGroup.LayoutParams;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.TimePickerDialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.ImageButton;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.Switch;
import com.scadroid.R;
import com.scadroid.database.DataBase;
import com.scadroid.domain.Point;
import com.scadroid.interfaces.UpdatePointInterface;
import com.scadroid.webservice.request.IServiceEvents;
import com.scadroid.webservice.request.ItemStringValue;
import com.scadroid.webservice.request.OperationResult;
import com.scadroid.webservice.tasks.AsyncTasks;
import com.scadroid.webservice.tasks.UpdatePoint;
import com.xgc1986.ripplebutton.utils.RippleDrawableHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by joe on 30/06/15.
 */

public class PointConfigActivity extends ActionBarActivity implements UpdatePointInterface, IServiceEvents, View.OnClickListener {

    private Point point;
    private IServiceEvents service = this;
    private Switch switch_schedule;
    private TextView tv_value;
    private UpdatePoint update;
    private ImageButton iv_point_config;

    // valores utilizados no dialog
    private TextView tv_hour;
    private TextView tv_minute_1;
    private TextView tv_minute_2;
    private TextView tv_second_1;
    private TextView tv_second_2;

    private int[] icons = { R.drawable.coffee_maker, R.drawable.cooker,
                            R.drawable.cooker_hood, R.drawable.desk_lamp,
                            R.drawable.electrical_filled, R.drawable.fan,
                            R.drawable.fax, R.drawable.fridge,
                            R.drawable.gas_industry, R.drawable.humidity,
                            R.drawable.iron, R.drawable.lamp,
                            R.drawable.laptop, R.drawable.light,
                            R.drawable.light_automation, R.drawable.microwave,
                            R.drawable.office_phone, R.drawable.plumbing,
                            R.drawable.shower, R.drawable.shower_tub,
                            R.drawable.thermometer_automation, R.drawable.toilet_bowl,
                            R.drawable.water, R.drawable.widescreen,
                            R.drawable.workstation };

    // used to put the buttons icons
    private LinearLayout layout;
    private int auxIcon;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_point);

        // recovering point from the intent
        if(savedInstanceState != null){
            point = savedInstanceState.getParcelable("point");
        } else {
            if (this.getIntent() != null && this.getIntent().getExtras() != null && this.getIntent().getExtras().getParcelable("point") != null) {
                point = this.getIntent().getExtras().getParcelable("point");
            } else {
                Toast.makeText(this, "Fail!", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }

        // TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Point settings - "+point.getName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        DataBase db = new DataBase(PointConfigActivity.this);
        for(Point p : db.searchPoints())
            if(p.getName().equals(point.getName()))
                point = p;
        db.closeDataBase();

        // used to change the hierarchy of a point
        final Spinner spn_label = (Spinner) findViewById(R.id.spinner_hierarquia);
            spn_label.setSelection(point.getCategoryNumber());
        spn_label.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner spinner, View view, int i, long l) {
                if(point.getType().equals("BOOLEAN") && i == 3 || point.getType().equals("BOOLEAN") && i == 0 || !point.getType().equals("BOOLEAN") && i != 3){
                    point.setCategory(i);

                    DataBase db = new DataBase(PointConfigActivity.this);
                        db.updatePoint(point);
                        db.closeDataBase();
                } else {
                    spinner.setSelection(point.getCategoryNumber());
                    Toast.makeText(PointConfigActivity.this, "You can't choose that hierarchy.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String[] items = new String[6];
            items[0] = "null";
            items[1] = "water";
            items[2] = "weather";
            items[3] = "light";
            items[4] = "energy";
            items[5] = "luminosity";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_spn, items);
        adapter.setDropDownViewResource(R.layout.row_spn_dropdown);

        spn_label.setAdapter(adapter);

        // used to change the point's update taxe
        FloatingActionButton btn_update = (FloatingActionButton) findViewById(R.id.btn_update);
            btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialog) {

                        @Override
                        protected void onBuildDone(Dialog dialog) {
                            int update = point.getUpdate();
                            int hours = (int) update / 3600;
                            int mins =  (int) (update - 3600*hours)/60 ;
                            int secs = (int) update - (3600 * hours) - (60 * mins);

                            tv_hour = ((TextView) dialog.findViewById(R.id.tv_hour));

                            tv_minute_1 = ((TextView) dialog.findViewById(R.id.tv_minute_1));
                            tv_minute_2 = ((TextView) dialog.findViewById(R.id.tv_minute_2));
                            tv_second_1 = ((TextView) dialog.findViewById(R.id.tv_second_1));
                            tv_second_2 = ((TextView) dialog.findViewById(R.id.tv_second_2));

                            if(hours >= 10){
                                tv_hour.setText("9");
                                mins = 99;
                                secs = 99;
                            } else
                                tv_hour.setText("" + hours);

                            if(mins >= 10){
                                String conv = ""+mins;
                                    tv_minute_2.setText(conv.subSequence(0,1));
                                    tv_minute_1.setText(conv.subSequence(1,2));
                            } else
                                tv_minute_1.setText(""+mins);

                            if(secs >= 10){
                                String conv = ""+secs;
                                tv_second_2.setText(conv.subSequence(0, 1));
                                tv_second_1.setText(conv.subSequence(1, 2));
                            } else
                                tv_second_1.setText("" + secs);

                            ImageButton iv_delete = ((ImageButton) dialog.findViewById(R.id.iv_delete));
                            iv_delete.setOnClickListener(PointConfigActivity.this);
                            iv_delete.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    tv_hour.setText("0");
                                    tv_minute_1.setText("0");
                                    tv_minute_2.setText("0");
                                    tv_second_1.setText("0");
                                    tv_second_2.setText("0");

                                    return false;
                                }
                            });

                            Button iv0 = ((Button) dialog.findViewById(R.id.iv0));
                            iv0.setOnClickListener(PointConfigActivity.this);

                            Button iv1 = ((Button) dialog.findViewById(R.id.iv1));
                            iv1.setOnClickListener(PointConfigActivity.this);

                            Button iv2 = ((Button) dialog.findViewById(R.id.iv2));
                            iv2.setOnClickListener(PointConfigActivity.this);

                            Button iv3 = ((Button) dialog.findViewById(R.id.iv3));
                            iv3.setOnClickListener(PointConfigActivity.this);

                            Button iv4 = ((Button) dialog.findViewById(R.id.iv4));
                            iv4.setOnClickListener(PointConfigActivity.this);

                            Button iv5 = ((Button) dialog.findViewById(R.id.iv5));
                            iv5.setOnClickListener(PointConfigActivity.this);

                            Button iv6 = ((Button) dialog.findViewById(R.id.iv6));
                            iv6.setOnClickListener(PointConfigActivity.this);

                            Button iv7 = ((Button) dialog.findViewById(R.id.iv7));
                            iv7.setOnClickListener(PointConfigActivity.this);

                            Button iv8 = ((Button) dialog.findViewById(R.id.iv8));
                            iv8.setOnClickListener(PointConfigActivity.this);

                            Button iv9 = ((Button) dialog.findViewById(R.id.iv9));
                            iv9.setOnClickListener(PointConfigActivity.this);

                            dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        }

                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            int hour = Integer.parseInt(((TextView) fragment.getDialog().findViewById(R.id.tv_hour)).getText().toString());
                            int minute2 = Integer.parseInt(((TextView) fragment.getDialog().findViewById(R.id.tv_minute_2)).getText().toString());
                            int minute = Integer.parseInt(minute2 + "" + ((TextView) fragment.getDialog().findViewById(R.id.tv_minute_1)).getText().toString());
                            int second2 = Integer.parseInt(((TextView) fragment.getDialog().findViewById(R.id.tv_second_2)).getText().toString());
                            int second = Integer.parseInt(second2 + "" + ((TextView) fragment.getDialog().findViewById(R.id.tv_second_1)).getText().toString());

                            if (hour == 0 && minute == 0 && second == 0) {
                                point.setUpdate(0);
                            } else {
                                int total = 3600 * hour + 60 * minute + second;
                                point.setUpdate(total);

                                DataBase db = new DataBase(PointConfigActivity.this);
                                    db.updatePoint(point);
                                    db.closeDataBase();
                            }
                            super.onPositiveActionClicked(fragment);
                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            Toast.makeText(PointConfigActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    builder.title("Update Period")
                            .positiveAction("CONFIRM")
                            .negativeAction("CANCEL")
                            .style(R.style.SimpleDialog)
                            .contentView(R.layout.update_timer);

                    DialogFragment fragment = DialogFragment.newInstance(builder);
                    fragment.show(PointConfigActivity.this.getSupportFragmentManager(), null);


                }
            });

        LayoutParams params =
                new LinearLayout.LayoutParams(
                        LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT);

        layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);

        iv_point_config = (ImageButton) findViewById(R.id.iv_point_config);
        iv_point_config.setImageResource(Integer.parseInt(point.getIconString()));
        iv_point_config.setOnClickListener(this);

        for (int i = 0; i < icons.length; i++) {
            android.widget.ImageButton btn = new android.widget.ImageButton(this);
                btn.setImageResource(icons[i]);
                btn.setOnClickListener(PointConfigActivity.this);
                btn.setBackgroundColor(Color.TRANSPARENT);
                if(icons[i] == Integer.parseInt(point.getIconString())) {
                    btn.setBackgroundColor(Color.LTGRAY);
                    btn.requestFocus();
                    auxIcon = i;
                }
                btn.setTag("btnImage");
                btn.setId(i);

            //add the button
            layout.addView(btn);
        }

        //create the layout param for the layout
        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);

        HorizontalScrollView horiontal = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
            horiontal.addView(layout, layoutParam);

        EditText tf_id = (com.rey.material.widget.EditText) findViewById(R.id.tf_id);
            tf_id.setEnabled(false);
        EditText tf_name = (EditText) findViewById(R.id.tf_name);
            tf_name.setEnabled(false);
        tv_value = (TextView) findViewById(R.id.tv_value);

        tf_id.setText("Point address - "+point.getId());
        tf_name.setText("Point name - " + point.getName());

        // used to update the value of point
        update = new UpdatePoint(point.getName(), this, this, TimeUnit.SECONDS, 1, false);
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.iv_point_config){
                // the next condition is used to know if the point is boolean or other kind of object. If the first, then the dialog its't going to appear
                if(point.getType().equals("BOOLEAN"))
                    new AsyncTasks(PointConfigActivity.this, point, PointConfigActivity.this, point.getValue()).execute(1); // changing the point value
                else {
                    // dialog used to catch the new point value defined by the user
                    Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialog){

                        @Override
                        protected void onBuildDone(Dialog dialog) {
                            dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        }

                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            new AsyncTasks(PointConfigActivity.this, point, PointConfigActivity.this, ((EditText) fragment.getDialog().findViewById(R.id.custom_et_value)).getText().toString()).execute(1); // changing the point value

                            super.onPositiveActionClicked(fragment);
                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            Toast.makeText(PointConfigActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    builder.title("Set point '"+point.getName()+"' value")
                            .positiveAction("SET")
                            .negativeAction("CANCEL")
                            .style(R.style.SimpleDialog)
                            .contentView(R.layout.set_value_point);

                    DialogFragment fragment = DialogFragment.newInstance(builder);
                        fragment.show(PointConfigActivity.this.getSupportFragmentManager(), null);
                }
        } else if(view.getId() == R.id.iv_delete){
            if(!tv_hour.getText().equals("0")){
                tv_second_1.setText(tv_second_2.getText());
                tv_second_2.setText(tv_minute_1.getText());
                tv_minute_1.setText(tv_minute_2.getText());
                tv_minute_2.setText(tv_hour.getText());
                tv_hour.setText("0");
            } else if(!tv_minute_2.getText().equals("0")) {
                tv_second_1.setText(tv_second_2.getText());
                tv_second_2.setText(tv_minute_1.getText());
                tv_minute_1.setText(tv_minute_2.getText());
                tv_minute_2.setText("0");
            } else if(!tv_minute_1.getText().equals("0")) {
                tv_second_1.setText(tv_second_2.getText());
                tv_second_2.setText(tv_minute_1.getText());
                tv_minute_1.setText("0");
            }else if(!tv_second_2.getText().equals("0")) {
                tv_second_1.setText(tv_second_2.getText());
                tv_second_2.setText("0");
            }else if(!tv_second_1.getText().equals("0"))
                tv_second_1.setText("0");
        } else if(view.getTag() != "btnImage"){
            if(tv_hour.getText().equals("0")) {
                if (!tv_minute_2.getText().equals("0")) {
                    tv_hour.setText(tv_minute_2.getText());
                    tv_minute_2.setText(tv_minute_1.getText());
                    tv_minute_1.setText(tv_second_2.getText());
                    tv_second_2.setText(tv_second_1.getText());
                    tv_second_1.setText(((Button)view).getText());
                } else if (!tv_minute_1.getText().equals("0")) {
                    tv_minute_2.setText(tv_minute_1.getText());
                    tv_minute_1.setText(tv_second_2.getText());
                    tv_second_2.setText(tv_second_1.getText());
                    tv_second_1.setText(((Button)view).getText());
                } else if (!tv_second_2.getText().equals("0")) {
                    tv_minute_1.setText(tv_second_2.getText());
                    tv_second_2.setText(tv_second_1.getText());
                    tv_second_1.setText(((Button)view).getText());
                } else {
                    tv_second_2.setText(tv_second_1.getText());
                    tv_second_1.setText(((Button)view).getText());
                }
            }
        } else if (view.getTag().equals("btnImage")){
            for(int i = 0; i < icons.length; i++){
                if(i != ((android.widget.ImageButton) view.findViewWithTag("btnImage")).getId())
                    layout.findViewById(i).setBackgroundColor(Color.TRANSPARENT);
                else
                    view.findViewById(i).setBackgroundColor(Color.LTGRAY);
            }

            changeIcon(0, view);
        }
    }

    public void changeIcon(int id, View view){
        int icon;
        // if the require comes from directly of iv_point_config, then...
        if(view != null) {
            int i = ((android.widget.ImageButton) view.findViewWithTag("btnImage")).getId();
            icon = icons[i];
            auxIcon = i;
        } else {
            icon = icons[id];
            auxIcon = id;
        }

        point.setIconString(""+icon);

        DataBase db = new DataBase(PointConfigActivity.this);
        db.updatePoint(point);
        db.closeDataBase();

        iv_point_config.setImageResource(icon);

    }

    // updating values of points with UpdatePoint class
    public void updatePoint(final ItemStringValue value, String description) {
        if(this != null && value != null) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    point.setValue(value.getValue());

                    if(point.getValue() != null) {
                        if (point.getType().equals("BOOLEAN")) {
                            if (point.getValue() != null && point.getValue().equals("true"))
                                tv_value.setText("Ativo");
                            else
                                tv_value.setText("Inativo");
                        } else if (point.getCategoryString().equals("Clima") && point.getType().equals("DOUBLE")) {
                            double val = Double.parseDouble(point.getValue());
                            tv_value.setText(String.format("%.2f", val) + " ºC");
                        } else if (point.getType().equals("DOUBLE")) {
                            double val = Double.parseDouble(point.getValue());
                            tv_value.setText("" + String.format("%.2f", val));
                        } else
                            tv_value.setText(point.getValue());
                    } else
                        tv_value.setText("Without connection or update");
                }
            });
        }
    }

    public void Starting(){
        android.util.Log.i("LOG", "Starting");
    }

    public void Completed(@SuppressWarnings("rawtypes") OperationResult result){
        Log.i("LOG", "Complete");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            update.shutdown();
            finish();
        }
        return true;
    }


}
