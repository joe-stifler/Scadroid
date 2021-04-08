package com.scadroid.webservice.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.scadroid.interfaces.UpdatePointInterface;
import com.scadroid.webservice.request.API;
import com.scadroid.webservice.request.APIError;
import com.scadroid.webservice.request.Enums;
import com.scadroid.webservice.request.IServiceEvents;
import com.scadroid.webservice.request.OperationResult;
import com.scadroid.webservice.request.ReadDataOptions;
import com.scadroid.webservice.request.ReadDataResponse;

import org.ksoap2.SoapFault;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by joe on 20/06/15.
 */
// classe criada para permitir que o ponto seja atulizado em uma certa taxa de tempo
public class UpdatePoint implements IServiceEvents{
    private ScheduledExecutorService scheduleTaskExecutor;
    private ReadDataResponse readData;
    private UpdatePointInterface update;
    private IServiceEvents service = this;
    private API api;
    private int updateTax = 1;
    private TimeUnit timeUnit;

    // recebendo parâmetros para que possa ser feito a requisição
    public UpdatePoint(final String name, UpdatePointInterface updatePoint, final Context c, TimeUnit timeUnit, int updateTax, final boolean shut){
        this.update = updatePoint;
        this.updateTax = updateTax;
        this.timeUnit = timeUnit;

        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // This schedule a task to run every 1 second:
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

                api = new API(service, "http://"+prefs.getString("serverIp", "")+":8080/ScadaBR/services/API");
                // aqui eu adiciono minha requisição ao scada
                try {
                    ArrayList<String> array = new ArrayList<String>();
                        array.add(name);

                    // fazendo requisição de apenas um ponto no caso, mas nada descarta a possibilidade de mais do que uma requisição ao mesmo tempo
                    readData = api.readData(array, new ReadDataOptions(0));

                    // update your UI component here.
                    if (readData != null) {
                        ArrayList<APIError> apiErrors = readData.errors;
                        // verifying if there's any error in ScadaBR
                        if (apiErrors.get(0).code != Enums.ErrorCode.OK) {
                            update.updatePoint(null, apiErrors.get(0).description);
                        } else {
                            update.updatePoint(readData.itemsList.get(0), null);
                        }
                    }

                    // if call just once, shutdown
                    if(shut)
                        shutdown();
                } catch (SoapFault e) {
                    update.updatePoint(null, e.getMessage());
                } catch(Exception e){
                    update.updatePoint(null, e.getMessage());
                }
            }
        }, 0, updateTax, timeUnit);
    }

    public void shutdown(){
        scheduleTaskExecutor.shutdown();
    }

    public void Starting(){
        android.util.Log.i("LOG", "Começando");
    }

    public void Completed(@SuppressWarnings("rawtypes") OperationResult result){
        Log.i("LOG", "Completo");
    }
}