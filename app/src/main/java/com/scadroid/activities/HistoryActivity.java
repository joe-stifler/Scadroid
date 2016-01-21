package com.scadroid.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.os.AsyncTask;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.OnCheckedChangeListener;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.scadroid.R;
import com.scadroid.adapters.PointsAdapter;
import com.scadroid.database.DataBase;
import com.scadroid.domain.Point;
import com.scadroid.domain.Profile;
import com.scadroid.webservice.request.API;
import com.scadroid.webservice.request.GetDataHistoryResponse;
import com.scadroid.webservice.request.IServiceEvents;
import com.scadroid.webservice.request.ItemStringValue;
import com.scadroid.webservice.request.OperationResult;
import com.scadroid.webservice.request.APIError;
import com.scadroid.webservice.request.Enums.ErrorCode;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends ActionBarActivity{

    private Calendar[][] dt;
    private float[][] values;
    private int aux = 0;
    private PieChart mChart;
    private String xData[], hierarquia, descricao, sufixo;
    private float yData[], valor[], valorTotal=0;
    private Toolbar mToolbar;
    private String[] names;
    private ArrayList<ItemStringValue> itemString = new ArrayList<ItemStringValue>();
    private Date finishDate = new Date();
    private Date initDate = new Date();
    private long timeref = 0, timetotal = 0;
    private long[] time;
    private ProgressDialog progress;

    private List<Profile> listProfile;
    private int mItemDrawerSelected;
    private int mProfileDrawerSelected;
    private SharedPreferences preferences;
    private Drawer.Result navigationDrawer;
    private AccountHeader.Result headerNavigationLeft;

    private OnCheckedChangeListener aOnCheckedChaangeListener = new OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(IDrawerItem iDrawerItem, CompoundButton compoundButton, boolean b) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initDate.setDate(initDate.getDay() - 1);
        // creating Toolbar where it will stay the icon ic_launcher and navigation bar
        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.app_name);
        mToolbar.setLogo(R.drawable.logo_50);

        setSupportActionBar(mToolbar);

        // Navigation Drawer
        // HEADER
        headerNavigationLeft = new AccountHeader()
                .withActivity(this)
                .withCompactStyle(false)
                .withSavedInstance(savedInstanceState)
                .withThreeSmallProfileImages(true)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        Profile aux = getProfile(listProfile, (ProfileDrawerItem) iProfile);
                        mProfileDrawerSelected = getPersonPositionProfile(listProfile, (ProfileDrawerItem) iProfile);
                        headerNavigationLeft.setBackgroundRes(aux.getBackground());

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("serverIp", aux.getServerId());
                        editor.commit();

                        return true;
                    }
                })
                .build();

        // instantiating the object
        listProfile = new ArrayList<Profile>();

        // capturando o ip armazenado nas configurações
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // auxiliary variable
        int count = 0;

        DataBase db = new DataBase(this);
        for(Profile p : db.searchDataBases()) {
            listProfile.add(p);

            if(preferences.getString("serverIp", "").equals(p.getServerId()))
                mProfileDrawerSelected = count;

            count++;
        }
        db.closeDataBase();

        // set the background from the profiles
        headerNavigationLeft.setBackgroundRes(R.drawable.wallpaper);

        navigationDrawer = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withDisplayBelowToolbar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.START)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(headerNavigationLeft)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        return true;
                    }
                })
                .withSelectedItem(1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        if (i == 0) {
                            Intent in = new Intent(HistoryActivity.this, MainActivity.class);
                            startActivity(in);
                            finish();
                        } else if (i == 3) {
                            getIp();
                            navigationDrawer.setSelection(1);
                        } else if (i == 5) {
                            Intent in = new Intent(HistoryActivity.this, PreferenceActivity.class);
                            navigationDrawer.setSelection(1);
                            startActivity(in);
                        }
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        Toast.makeText(HistoryActivity.this, "onItemLongClick: " + i, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .build();

        navigationDrawer.addItem(new PrimaryDrawerItem().withName("Points").withIcon(getResources().getDrawable(R.drawable.ic_home_black_24dp)));
        navigationDrawer.addItem(new PrimaryDrawerItem().withName("History").withIcon(getResources().getDrawable(R.drawable.ic_history_black_24dp)));
        navigationDrawer.addItem(new DividerDrawerItem());
        navigationDrawer.addItem(new PrimaryDrawerItem().withName("Add Host").withIcon(getResources().getDrawable(R.drawable.add_database)));
        navigationDrawer.addItem(new DividerDrawerItem());
        navigationDrawer.addItem(new PrimaryDrawerItem().withName("Settings"));
        navigationDrawer.addItem(new PrimaryDrawerItem().withName("About"));

        if(preferences.getString("serverIp", "") == "")
            getIp();

        if(mProfileDrawerSelected != 0){
            Profile aux = listProfile.get(mProfileDrawerSelected);
            listProfile.set(mProfileDrawerSelected, listProfile.get(0));
            listProfile.set(0, aux);
        }

        for(int i = 0; i < listProfile.size(); i++)
            headerNavigationLeft.addProfile(listProfile.get(i).getProfile(), i);

        db = new DataBase(this);

        ArrayList<String> array = new ArrayList<String>();

        // verificando se há pontos no banco de dados para que sejam adicionados a view
        if(db.searchPoints().size() > 0){
            for(Point p : db.searchPoints()){
                if (!p.getCategoryString().equals("null")) {
                    if (p.getCategoryString().equals("water") && !array.contains("water"))
                        array.add("water");
                    else if (p.getCategoryString().equals("weather") && !array.contains("weather"))
                        array.add("weather");
                    else if (p.getCategoryString().equals("light") && !array.contains("light")) {
                        array.add("light");
                        android.util.Log.v("LOG", "Entrou em lights");
                    }
                    else if (p.getCategoryString().equals("energy") && !array.contains("energy"))
                        array.add("energy");
                }

                android.util.Log.v("LOG", ""+p.getCategoryString());
            }
        }

        db.closeDataBase();

        if(array.size() > 0) {
            String[] items = new String[array.size()];

            for (int x = 0; x < array.size(); x++)
                items[x] = array.get(x);

            choiceDialog(items);
        } else {
            Toast.makeText(HistoryActivity.this, "Add any hierarchy to your point.", Toast.LENGTH_SHORT).show();
        }

        mChart = (PieChart) findViewById(R.id.mChart);
    }

    // catching the ip from server to the program do requests for datas
    public void getIp(){
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialog){

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                String ip = ((EditText) fragment.getDialog().findViewById(R.id.custom_et_server_ip)).getText().toString();
                String name = ((EditText) fragment.getDialog().findViewById(R.id.custom_et_name_server)).getText().toString();

                if(listProfile.isEmpty()) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", ((EditText) fragment.getDialog().findViewById(R.id.custom_et_user)).getText().toString()); // value to store
                    editor.putString("password", ((EditText) fragment.getDialog().findViewById(R.id.custom_et_password)).getText().toString()); // value to store
                    editor.putString("serverName", name); // value to store
                    editor.putString("serverIp", ip); // value to store
                    editor.commit();
                }

                DataBase db = new DataBase(HistoryActivity.this);
                db.insertDataBase(ip, name);
                db.closeDataBase();

                ProfileDrawerItem aux = new ProfileDrawerItem();
                aux.setName(name);
                aux.setIcon(getResources().getDrawable(R.drawable.logo_50));
                aux.setEmail(ip);

                Profile p = new Profile();
                p.setProfile(aux);
                p.setServerId(ip);
                p.setBackground(R.drawable.wallpaper);

                listProfile.add(p);

                headerNavigationLeft.addProfile(p.getProfile(), listProfile.size() - 1);

                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                Toast.makeText(HistoryActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.title("Server settings")
                .positiveAction("CONNECT")
                .negativeAction("CANCEL")
                .style(R.style.SimpleDialog)
                .contentView(R.layout.layout_login);

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);
    }

    public void choiceDialog(String[] items){
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DataBase db = new DataBase(HistoryActivity.this);
                ArrayList<String> arrayAux = new ArrayList<String>();
                for(Point p : db.searchPoints())
                    if(p.getCategoryString().equals(getSelectedValue()))
                        arrayAux.add(p.getName());
                names = new String[arrayAux.size()];
                hierarquia = (String) getSelectedValue();
                if(hierarquia == "light"){
                    descricao = "Porcentagem de tempo que cada luz ficou ligada.";
                    sufixo = "h";
                }else if(hierarquia == "energy"){
                    descricao = "Porcentagem de energia gasta em kWh por cada ponto de energia.";
                    sufixo = "kWh";
                }else if(hierarquia == "water"){
                    descricao = "Porcentagem de litros gastos por cada ponto de água";
                    sufixo = "L";
                }else{
                    Toast.makeText(HistoryActivity.this,"Gráfico não disponível para a hierarquia Clima",Toast.LENGTH_SHORT).show();
                }
                for(int x = 0; x < names.length; x++)
                    names[x] = arrayAux.get(x);

                time = new long[names.length];
                valor = new float[names.length];
                new Async().execute();
                progressDialogOn();

                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                Toast.makeText(HistoryActivity.this, "Cancelado" , Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        };

        ((SimpleDialog.Builder)builder).items(items, 0)
                .title("Hierarquias ativas")
                .positiveAction("OK")
                .negativeAction("CANCELAR");

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);
    }

    public void progressDialogOn(){
        progress = new ProgressDialog(this);
        progress.setMessage("Requesting datas...");
        progress.show();
        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
    }

    public void progressDialogOff(){
        progress.dismiss();
    }

    public void guardarDados(ArrayList<ItemStringValue>  itemValues){
        xData = new String[names.length];
        yData = new float[names.length];
        values = new float[names.length][itemValues.size()];
        dt = new Calendar[names.length][itemValues.size()];
        Log.v("TESTE", "tamanho de dt: "+dt[aux].length+" tamanho de itemValues:"+itemValues.size());
        if (hierarquia == "light") {
            if(itemValues.size() > 0){
                for (int x = 0; x < itemValues.size(); x++){
                    if(itemValues.get(x).getValue() == "true")
                        values[aux][x] = 1;
                    else
                        values[aux][x] = 0;

                    dt[aux][x] = Calendar.getInstance();
                    dt[aux][x].setTime(itemValues.get(x).getTimestamp());
                }
            }
            if (values[aux][0] == 1) {
                timeref = dt[aux][0].getTimeInMillis();
            }

            for (int i = 1; i < dt[aux].length; i++) {
                if (values[aux][i] != values[aux][i-1]) {
                    if (values[aux][i] == 0) {
                        valor[aux] += (dt[aux][i].getTimeInMillis() - timeref) /(1000*60*60);
                    } else if (values[aux][i] == 1) {
                        timeref = dt[aux][i].getTimeInMillis();
                    }
                }
            }
            timetotal += valor[aux];
            timeref = 0;
        }else if(hierarquia == "energy" || hierarquia == "water") {
            if(itemValues.size() > 0){
                for (int x = 0; x < itemValues.size(); x++){
                    values[aux][x] = Float.parseFloat(itemValues.get(x).getValue());
                    dt[aux][x] = Calendar.getInstance();
                    dt[aux][x].setTime(itemValues.get(x).getTimestamp());
                }
            }
            for (int i = 0; i < dt[aux].length; i++) {
                valor[aux] += values[aux][i];
            }
            Log.v("Valor[z]: ",""+valor[0]);
            if(hierarquia == "energy")
                valorTotal += valor[aux]/1000;
        }
        if (aux == names.length-1){
            Grafico();
        }

    }

    public void Grafico(){
        progressDialogOff();

        for (int z = 0; z < names.length; z++) {
            Log.v("ValorTOTAL: ",""+valorTotal);
            Log.v("LOG" ,"Hierarquia2 teste:: "+hierarquia);
            if (hierarquia == "light"){
                yData[z] = (valor[z] / timetotal*100);
            }else {
                yData[z] = (valor[z]/1000 / valorTotal * 100);
            }
            xData[z] = names[z];
        }

        mChart.setUsePercentValues(true);
        mChart.setDescription(descricao);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(9);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int i, Highlight highlight) {
                float valoutempo = 0;
                if (e == null)
                    return;
                Toast.makeText(HistoryActivity.this, xData[e.getXIndex()] + " = " + String.format("%.2f", valor[e.getXIndex()]) + sufixo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for(int i = 0; i < yData.length; i++){
            yVals.add(new Entry(yData[i], i));
        }

        ArrayList<String> xVals = new ArrayList<String>();
        for(int i = 0; i < xData.length; i++){
            xVals.add(xData[i]);
        }

        PieDataSet dataSet = new PieDataSet(yVals, "Pontos da casa");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);

        mChart.highlightValues(null);
        mChart.invalidate();

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }

    // PERSON
    private Profile getProfile( List<Profile> list, ProfileDrawerItem p ){
        Profile aux = null;
        for(int i = 0; i < list.size(); i++){
            if( list.get(i).getProfile().getEmail().equalsIgnoreCase( p.getEmail() ) ){
                aux = list.get(i);
                break;
            }
        }
        return( aux );
    }

    private int getPersonPositionProfile( List<Profile> list, ProfileDrawerItem p ){
        for(int i = 0; i < list.size(); i++){
            if( list.get(i).getProfile().getEmail().equalsIgnoreCase( p.getEmail() ) ){
                return(i);
            }
        }
        return -1;
    }

    class Async extends AsyncTask<Void, Void, GetDataHistoryResponse> implements IServiceEvents {
        private API api;
        private boolean test = true;

        public Async() {
            // capturando o ip armazenado nas configurações
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HistoryActivity.this);
            api = new API(this, "http://"+prefs.getString("serverIp", "")+":8080/ScadaBR/services/API");
        }

        @Override
        public GetDataHistoryResponse doInBackground(Void... params) {
            try {
                Date finishDate = new Date();
                Log.v("LOG", "aqui");
                return api.getDataHistory(names[aux], new com.scadroid.webservice.request.GetDataHistoryOptions(0, initDate, finishDate));
            } catch (Exception e) {
                Log.i("LOG", "doInBackground: " + e.getMessage());
            }
            return null;
        }

        @Override
        public void onPostExecute(GetDataHistoryResponse historyResponse) {
            super.onPostExecute(historyResponse);

            try {
                ArrayList<APIError> apiErrors = historyResponse.errors;

                if (apiErrors.get(0).code != ErrorCode.OK) {
                    Log.i("LOG", "Error: " + apiErrors.get(0).description);
                } else {
                    if (historyResponse.itemsList.size() > 1) {

                        for (ItemStringValue x : historyResponse.itemsList) {
                            itemString.add(x);
                        }
                        if (historyResponse.itemsList.size() == 5000) {
                            initDate = historyResponse.itemsList.get(historyResponse.itemsList.size() - 1).getTimestamp();
                            new Async().execute();
                        } else {
                            Log.v("LOG", "aqui em baixo" + itemString.size());
                            guardarDados(itemString);
                            if(aux < names.length-1) {
                                aux++;
                                itemString.clear();
                                initDate.setDate(initDate.getDay() - 1);
                                new Async().execute();
                            }
                        }
                    }
                }
            } catch (Exception e){
                Log.i("LOG", "Erro: " + e.getMessage());
            }
        }

        public void Starting() {
            Log.i("LOG", "Começando");
        }

        public void Completed(@SuppressWarnings("rawtypes") OperationResult result) {
            Log.i("LOG", "Completo");
        }
    }
}