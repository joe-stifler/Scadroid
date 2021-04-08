package com.scadroid.extras;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.scadroid.webservice.request.ItemStringValue;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by joe on 9/12/15.
 */

public class Charts {
    private LineChart lChart;
    private BarChart bChart;
    private PieChart pChart;
    private float media; // variavel da media
    private float total; // variable that shows the brute value without a format
    private float value_in_cash; // variable that shows how much money the user spent with the chosen point
    private boolean aux = false; // used to know if all the datas were added to the chart
    private String valueOne = "", valueTwo = "", valueThree = "";

    public Charts(Context context, ArrayList<ItemStringValue> itemValues, String typeOfData, String hierarchy, int days, double price){
        if (hierarchy != null) {
            if (hierarchy.equals("weather") || hierarchy.equals("luminosity")) {
                lChart = new LineChart(context);
                lineChart(itemValues, hierarchy);
            } else if (hierarchy.equals("energy") || hierarchy.equals("water")) {
                bChart = new BarChart(context);
                barChart(itemValues, days, price, hierarchy);
            } else if (hierarchy.equals("light")){
                pChart = new PieChart(context);
                pieChart(itemValues, hierarchy, price);
            }
        }
    }

    public Object getChart() {
        if (bChart != null)
            return bChart;
        else if (lChart != null)
            return lChart;
        else if (pChart != null)
            return pChart;
        return null;
    }

    // used to know which object chart is not null
    public int getNonNull(){
        if (bChart != null)
            return 1;
        else if (lChart != null)
            return 2;
        else if (pChart != null)
            return 3;

        return 0;
    }

    public void pieChart(ArrayList<ItemStringValue> itemValues, String hierarchy, double price){
        ArrayList<String> xData = new ArrayList<String>();
        ArrayList<Float> yData = new ArrayList<Float>();

        if(hierarchy.equals("light")) {
            int totalTime = 0; // catch the time between the first value and the last
            int totalOn = 0; // catch the time that the light stayed on
            int totalOff = 0; // catch the time that the light stayed on

            totalTime = (int) itemValues.get(itemValues.size() - 1).getTimestamp().getTime() - (int) itemValues.get(0).getTimestamp().getTime();

            String aux = "";
            int lastIndex = 0;

            for (int x = 0; x < itemValues.size(); x++) {
                if (x != 0)
                    if (!aux.equals(itemValues.get(x).getValue()))
                        if (aux.equals("true")) {
                            totalOn += (int) itemValues.get(x).getTimestamp().getTime() - (int) itemValues.get(lastIndex).getTimestamp().getTime();
                            lastIndex = x;
                        } else {
                            totalOff += (int) itemValues.get(x).getTimestamp().getTime() - (int) itemValues.get(lastIndex).getTimestamp().getTime();
                            lastIndex = x;
                        }
                aux = itemValues.get(x).getValue();
            }

            if(aux.equals("true"))
                totalOn += Calendar.getInstance().getTime().getTime() - itemValues.get(itemValues.size()-1).getTimestamp().getTime();
            else
                totalOff += Calendar.getInstance().getTime().getTime() - itemValues.get(itemValues.size()-1).getTimestamp().getTime();

            xData.add("On");
            xData.add("Off");

            yData.add((float) totalOn/totalTime);
            yData.add((float) totalOff/totalTime);

            double on = (totalOn/36) * 0.00001;
            double off = (totalOff/36) * 0.00001; // time that the point was on or off, in hours

            valueOne = String.format("%.3f", on)+" h";
            valueTwo = String.format("%.3f", off)+" h";
            valueThree = String.format("%.3f", (on * price)) +" Wh";

            on = 0;
            off = 0;
            totalOn = 0;
            totalOff = 0;totalTime = 0;
        } else {
            /*for (int z = 0; z < names.length; z++) {
                Log.v("ValorTOTAL: ",""+valorTotal);
                if (hierarquia == "light"){
                    yData[z] = (valor[z] / timetotal*100);
                }else {
                    yData[z] = (valor[z]/1000 / valorTotal * 100);
                }
                xData[z] = names[z];
            }*/
        }

        pChart.setUsePercentValues(true);
      //  pChart.setDescription(descricao);

        pChart.setDrawHoleEnabled(true);
        pChart.setHoleColorTransparent(true);
        pChart.setHoleRadius(7);
        pChart.setTransparentCircleRadius(9);

        pChart.setRotationAngle(0);
        pChart.setRotationEnabled(true);

        pChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int i, Highlight highlight) {
                //float valoutempo = 0;
                if (e == null)
                    return;
                //Toast.makeText(HistoryActivity.this, xData[e.getXIndex()] + " = " + String.format("%.2f", valor[e.getXIndex()]) + sufixo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for(int i = 0; i < yData.size(); i++){
            yVals.add(new Entry(yData.get(i), i));
        }

        ArrayList<String> xVals = new ArrayList<String>();
        for(int i = 0; i < xData.size(); i++){
            xVals.add(xData.get(i));
        }

        PieDataSet dataSet = null;
        if (hierarchy.equals("light"))
            dataSet= new PieDataSet(yVals, "State");
        else
            dataSet= new PieDataSet(yVals, "Home points");

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

        pChart.setData(data);

        pChart.highlightValues(null);
        pChart.invalidate();

        Legend l = pChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7);
            l.setYEntrySpace(5);
    }

    public void lineChart(ArrayList<ItemStringValue> itemValues, String hierarchy){
        LineDataSet dataset = null;
        ArrayList<String> labels = new ArrayList<String>(); // used to give title ( storage date ) to each value
        ArrayList<Entry> entries = new ArrayList<>(); // used to put the values
        if (hierarchy.equals("light")) {
            for (int x = 0; x < itemValues.size(); x++) { // populating the value and dt matrix
                entries.add(new Entry(itemValues.get(x).getValue() == "true" ? 1 : 0, x));

                Calendar cal = Calendar.getInstance();
                    cal.setTime(itemValues.get(x).getTimestamp());
                labels.add("" + cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH));
            }

            dataset = new LineDataSet(entries, "# point's state during the established time");
        } else if (hierarchy.equals("weather")){
            float average = 0, maximum = 0, minimum = 0;
            maximum = Float.parseFloat(itemValues.get(0).getValue());
            minimum = Float.parseFloat(itemValues.get(0).getValue());
            for (int x = 0; x < itemValues.size(); x++) {
                if (Float.parseFloat(itemValues.get(x).getValue()) > maximum)
                    maximum = Float.parseFloat(itemValues.get(x).getValue());
                if (Float.parseFloat(itemValues.get(x).getValue()) < minimum)
                    minimum = Float.parseFloat(itemValues.get(x).getValue());
                average += Float.parseFloat(itemValues.get(x).getValue());
                entries.add(new Entry((float) Float.parseFloat(itemValues.get(x).getValue()), x));
                Calendar cal = Calendar.getInstance();
                cal.setTime(itemValues.get(x).getTimestamp());
                labels.add("" + cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH));
            }

            // no description text
            lChart.setDescription("");

            // enable value highlighting
            lChart.setHighlightEnabled(true);

            // enable touch gestures
            lChart.setTouchEnabled(true);

            // enable scaling and dragging
            lChart.setDragEnabled(true);
            lChart.setScaleEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            lChart.setPinchZoom(false);

            lChart.setDrawGridBackground(false);

            if(hierarchy.equals("weather")) {
                valueOne = "" + String.format("%.2f", average / itemValues.size()) + " ºC";
                valueTwo = "" + String.format("%.2f", maximum) + " ºC";
                valueThree = "" + String.format("%.2f", minimum) + " ºC";
            } else {
                valueOne = "" + String.format("%.2f", average / itemValues.size()) + " lumens";
                valueTwo = "" + String.format("%.2f", maximum) + " lumens";
                valueThree = "" + String.format("%.2f", minimum) + " lumens";
            }

            dataset = new LineDataSet(entries, "# point's value during the established time");
    }
        dataset.setColors(ColorTemplate.PASTEL_COLORS);
        dataset.setDrawValues(true);
        dataset.setDrawCubic(true);

    lChart.animateX(3000);
        LineData data = new LineData(labels, dataset);
            lChart.setData(data);
    }

    public void barChart(ArrayList<ItemStringValue> itemValues, int qtdDM, double price, String hierarchy) {
        //qtdDM is a variabe that defines the quantity of days or months requested by the user

        float[] values = new float[itemValues.size()];
        float total = 0;
        Calendar[] dt = new Calendar[itemValues.size()];

        if(itemValues.size() > 0){
            for (int x = 0; x < itemValues.size(); x++){
                values[x] = Float.parseFloat(itemValues.get(x).getValue());

                Log.v("Valor: ", ""+values[x]);

                dt[x] = Calendar.getInstance();
                dt[x].setTime(itemValues.get(x).getTimestamp());

                Log.v("Data: ", "" +dt[x]);
            }
        }

        int cont = 0;
        float valor = values[0];
        Calendar DM[] = new Calendar[qtdDM];

        // Calendar ultdata = dt[0];
        ArrayList<BarEntry> entries = new ArrayList<>();
        total += values[0];
        for (int i = 1; i < dt.length; i++) {
            Log.v("Valor: ", ""+valor);
            total += values[i];
            if(dt[i].get(Calendar.DATE) != dt[i-1].get(Calendar.DATE)){
                int diasaux = dt[i].get(Calendar.DATE) - dt[i-1].get(Calendar.DATE);
                if (diasaux < 0)
                    diasaux += 30;
                if(diasaux == 1) {
                    entries.add(new BarEntry((float) valor, cont));
                    valor = values[i];
                    DM[cont] = dt[i - 1];
                    cont++;
                }else{
                    int dias = 1;
                    for(int o = 1; o <= diasaux; o++){

                        DM[cont] = Calendar.getInstance();
                        if(dt[i].get(Calendar.MONTH) != dt[i-1].get(Calendar.MONTH)){
                            DM[cont].set(dt[i-1].get(Calendar.YEAR), dt[i-1].get(Calendar.MONTH)+1, dt[i-1].get(Calendar.DATE) + o);
                            dias++;
                        }else{
                            DM[cont].set(dt[i-1].get(Calendar.YEAR), dt[i-1].get(Calendar.MONTH), dt[i-1].get(Calendar.DATE) + o);
                        }
                        entries.add(new BarEntry(0, cont));
                        valor = values[i];
                        cont++;
                    }
                }
            }else{
                valor += values[i];
            }
        }

        BarDataSet dataset = new BarDataSet(entries, "# Quantidade de energia utilizada");
        ArrayList<String> labels = new ArrayList<String>();

        for(int i = 0; i < cont; i++)
            labels.add(""+DM[i].get(Calendar.DATE)+"/"+DM[i].get(Calendar.MONTH));

        dataset.setColors(ColorTemplate.PASTEL_COLORS);
        bChart.animateY(4000);
        bChart.invalidate();
        BarData data = new BarData(labels, dataset);
        bChart.setData(data);

        if (hierarchy.equals("water")) {
            valueOne = "" + String.format("%.2f", total) + " m³";
            valueTwo = "" + String.format("%.2f", (total / cont)) + " m³";
        } else {
            valueOne = ""; //+ String.format("%.2f", total / 1000) + " mA";
            valueTwo = ""; //+ String.format("%.2f", (total / cont) / 1000) + " kWh";
        }

        valueThree = "R$" + String.format("%.2f", (total / 1000) * price);
    }

    public String getValueOne(){
        Log.v("LOG", valueThree);
        return valueOne;
    }

    public String getValueTwo(){
        Log.v("LOG", valueThree);
        return valueTwo;
    }

    public String getValueThree(){
        Log.v("LOG", valueThree);
        return valueThree;
    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

        return format.format(date);
    }
}
