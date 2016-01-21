package com.scadroid.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.content.Intent;

import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.Drawer.Result;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.OnCheckedChangeListener;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.SnackBar;
import com.scadroid.R;
import com.scadroid.database.DataBase;
import com.scadroid.domain.Profile;
import com.scadroid.fragments.PointsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joe on 16/05/15.
 */

public class MainActivity extends ActionBarActivity{

    private Toolbar mToolbar;
    private Result navigationDrawer;
    private AccountHeader.Result headerNavigationLeft;
    private List<Profile> listProfile;
    private int mItemDrawerSelected;
    private int mProfileDrawerSelected;
    private SharedPreferences preferences;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private OnCheckedChangeListener aOnCheckedChaangeListener = new OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(IDrawerItem iDrawerItem, CompoundButton compoundButton, boolean b) {
            Toast.makeText(MainActivity.this, "onCheckedChanged: "+(b ? "true" : "false"), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // creating Toolbar where it will stay the icon ic_launcher and navigation bar
        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.app_name);
        mToolbar.setLogo(R.drawable.logo_50);
        setSupportActionBar(mToolbar);

        final String tag = "mainFrag";

        // FRAGMENT
        if(savedInstanceState == null){
            PointsFragment frag1 = new PointsFragment();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.rl_fragment_container, frag1, "frag1");
            ft.commit();
        }

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
                .withSelectedItem(0)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        if (i == 1) {
                            Intent in = new Intent(MainActivity.this, HistoryActivity.class);
                            startActivity(in);
                            finish();
                        } else if (i == 3) {
                            getIp();
                            navigationDrawer.setSelection(0);
                        } else if (i == 5) {
                            Intent in = new Intent(MainActivity.this, PreferenceActivity.class);
                            navigationDrawer.setSelection(0);
                            startActivity(in);
                        }
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        Toast.makeText(MainActivity.this, "onItemLongClick: " + i, Toast.LENGTH_SHORT).show();
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

                DataBase db = new DataBase(MainActivity.this);
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
                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
