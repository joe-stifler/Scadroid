package com.scadroid.webservice.tasks;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.SnackBar;
import com.scadroid.R;
import com.scadroid.domain.Point;
import com.scadroid.interfaces.HistoryDataInterface;
import com.scadroid.interfaces.NewPointInterface;
import com.scadroid.interfaces.StatusServerInterface;
import com.scadroid.interfaces.UpdatePointInterface;
import com.scadroid.webservice.request.API;
import com.scadroid.webservice.request.Enums;
import com.scadroid.webservice.request.GetDataHistoryOptions;
import com.scadroid.webservice.request.GetDataHistoryResponse;
import com.scadroid.webservice.request.GetStatusResponse;
import com.scadroid.webservice.request.IServiceEvents;
import com.scadroid.webservice.request.ItemStringValue;
import com.scadroid.webservice.request.OperationResult;
import com.scadroid.webservice.request.BrowseTagsOptions;
import com.scadroid.webservice.request.BrowseTagsResponse;
import com.scadroid.webservice.request.APIError;
import com.scadroid.webservice.request.Enums.ErrorCode;
import com.scadroid.webservice.request.ReadDataOptions;
import com.scadroid.webservice.request.ReadDataResponse;
import com.scadroid.webservice.request.WriteDataOptions;
import com.scadroid.webservice.request.WriteDataResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by joe on 04/06/15.
 */

public class AsyncTasks extends AsyncTask<Integer, Void, Object> implements IServiceEvents {

    private API api;
    private Context c; // used to print Dialog on screen
    private Point point;
    private Calendar calInit; // used to know first point date to be requested
    private Calendar calFinish; // used to know last point date to be requested. This means, the first period to catch the storage datas and the last period. Example: Sunday == first date, friday == last
    private String value; // used to know if the value it's boolean, or other type of oject.
    private UpdatePointInterface update_rate; // utilizado para fazer o update dos ponto quando clicar no botão para alterar seu valor
    private NewPointInterface pu; // used to return values to PointFragment through of interface
    private HistoryDataInterface hi; // usado para enviar os dados que foram requisitados para a classe PointHistoryACtivity
    private StatusServerInterface si; // used to send the data received from scada referent to status of server

    public AsyncTasks(){}

    // used to request historic datas
    public AsyncTasks(Context c, Calendar date, Point p, HistoryDataInterface hi){
        this.c = c;
            calInit = date;
        this.point = p;
        this.hi = hi;
    }

    // used when the called comes from PreferenceActivity
    public AsyncTasks(Context c, StatusServerInterface statusInterface){
        this.c = c;
        this.si = statusInterface;
    }


    // constructor used to set the value of a point
    public AsyncTasks(Context c, Point p){
        this.c = c;
        this.point = p;
    }

    // constructor used to set the value of a point when the called comes from PointAdapter
    public AsyncTasks(Context c, Point p, UpdatePointInterface update_rate, String value){
        this.c = c;
        this.point = p;
        this.update_rate = update_rate;
        this.value = value;
    }

    // constructor used to search by points (BrowseTag)
    public AsyncTasks(Context c, NewPointInterface pu){
        this.c = c;
        this.pu = pu;
    }

    @Override
    public Object doInBackground(Integer... params){
        // capturando o ip armazenado nas configurações
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        // instanciando a classe que será utilizada para fazer todas as requisições, ressaltando a parte onde se tem o endereço ip do servidor
        api = new API(this, "http://"+prefs.getString("serverIp", "")+":8080/ScadaBR/services/API");

        try {
           if(params[0] == 0){ // Request to ScadaBR to search for activate points
                return api.browseTags(null, new BrowseTagsOptions(0));
            } else if(params[0] == 1){ // here it's gonna be changed the point value
               if(point.getType().equals("BOOLEAN"))
                   value = ""+!Boolean.parseBoolean(point.getValue());

               ItemStringValue itemValue = new ItemStringValue(point.getName(), Enums.DataType.BOOLEAN, value, Enums.QualityCode.GOOD, new Date());

               ArrayList<ItemStringValue> array = new ArrayList<ItemStringValue>();
                    array.add(itemValue);

               return api.writeData(array, new WriteDataOptions(true));
           } else if(params[0] == 2) { // here it's gonna be requested historic datas
               if(calFinish == null)
                   calFinish = Calendar.getInstance();

               Date initDate = calInit.getTime();
               Date finishDate = calFinish.getTime();

               GetDataHistoryResponse history = api.getDataHistory(point.getName(), new GetDataHistoryOptions(0, initDate, finishDate));

               return history;
           } else if(params[0] == 3) { // used to request the state of server
               return api.getStatus();
           }
        } catch(Exception e){
            showMessage(e.getMessage());
        }

        return null;
    }

    @Override
    public void onPostExecute(Object params) {
        if (params != null) {
            try {
                if (params.getClass() == BrowseTagsResponse.class) { // used to shown to the user the active points
                    // verificando se há algum erro propriamente do ScadaBR.
                    if (((BrowseTagsResponse) params).errors.get(0).code != ErrorCode.OK) {
                        showMessage("Verifique se tudo esta correto, tal como a conexao, endereço ip, etc.");
                    } else {
                        // passando via interface o object ItemInfo para que seja recebido pelo fragment e assim exibir na tela em forma de Dialog
                        pu.addPoints(((BrowseTagsResponse) params).itemList);
                    }
                } else if (params.getClass() == WriteDataResponse.class) { // used to update the screen after change the value of a point
                    // verificando se há algum erro propriamente do ScadaBR.
                    if (((WriteDataResponse) params).errors.get(0).code != ErrorCode.OK) {
                        update_rate.updatePoint(null, ((WriteDataResponse) params).errors.get(0).description);
                    } else {
                        // passando via interface o object ItemInfo para que seja recebido pelo fragment e assim exibir na tela em forma de Dialog
                        ItemStringValue item = ((WriteDataResponse) params).itemsList.get(0);
                        item.setDataType(Enums.DataType.fromString(point.getType()));
                        update_rate.updatePoint(item, null);
                    }
                } else if (params.getClass() == GetDataHistoryResponse.class) { // used to catch the values through time of a point
                    if (((GetDataHistoryResponse) params).errors.get(0).code != ErrorCode.OK) {
                        update_rate.updatePoint(null, ((GetDataHistoryResponse) params).errors.get(0).description);
                    } else {
                        ArrayList<ItemStringValue> itemsList = ((GetDataHistoryResponse) params).itemsList;
                        if (itemsList.size() > 1) {
                            // This method was created 'cause when we tried to use the boolean 'moreValues' that comes directely from ScadaBR, we found out that it was ever false. So, we weren't able to use
                            //      that variable. With this, we use a 'if' to know if there's more values in requesting. The 5000 refers to the max value that ScadaBR return in each request.
                            if (itemsList.size() == 5000) {
                                hi.dataHistory(itemsList, true);
                            } else {
                                hi.dataHistory(itemsList, false);
                            }
                        }
                    }
                } else if (params.getClass() == GetStatusResponse.class) { // used to catch the status of server
                    si.receiveStatus(((GetStatusResponse) params).serverStatus);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showMessage(String message){
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialog);

        ((SimpleDialog.Builder)builder).message(message);
    }

    public void Starting(){
        android.util.Log.i("LOG", "Começando");
    }

    public void Completed(@SuppressWarnings("rawtypes") OperationResult result){
        Log.i("LOG", "Completo");
    }
}
