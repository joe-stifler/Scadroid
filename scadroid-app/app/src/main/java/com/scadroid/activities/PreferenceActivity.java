package com.scadroid.activities;

import com.scadroid.R;
import com.scadroid.interfaces.StatusServerInterface;
import com.scadroid.webservice.request.ServerStatus;
import com.scadroid.webservice.tasks.AsyncTasks;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by joe on 23/06/15.
 */

public class PreferenceActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(com.scadroid.R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MyPreferenceFragment()).commit();

        setTheme(R.style.PreferenceScreen);
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements StatusServerInterface
    {
        private EditTextPreference serverState;
        private EditTextPreference startTime;
        private EditTextPreference productVersion;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            new AsyncTasks(getActivity(), this).execute(3);

            serverState = (EditTextPreference) findPreference("serverState");
            startTime = (EditTextPreference) findPreference("startTime");
            productVersion = (EditTextPreference) findPreference("productVersion");
        }

        @Override
        public void receiveStatus(ServerStatus serverStatus) {
            serverState.setSummary(serverStatus.serverState.toString());
            startTime.setSummary(serverStatus.startTime.toString());
            productVersion.setSummary(serverStatus.productVersion.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return true;
    }


}
