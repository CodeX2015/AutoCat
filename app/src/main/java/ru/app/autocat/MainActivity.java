package ru.app.autocat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ListView mDrawerList;
    private Spinner spnTBCat;
    private MorphButton btnChangeView;
    private Handler mHandler;

    public static final String[] DATA = {"Все", "Audi", "BMV", "Ford", "Toyota", "Mercedes", "Nissan", "Mitsubishi", "VW", "Reno"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnChangeView = (MorphButton) findViewById(R.id.stopBtn);
        spnTBCat = (Spinner) findViewById(R.id.toolbar_spinner_cat);
        btnChangeView.setOnStateChangedListener(new MorphButton.OnStateChangedListener() {
            @Override
            public void onStateChanged(MorphButton.MorphState changedTo, boolean isAnimating) {
                // Do something here
                Toast.makeText(MainActivity.this, "Changed to: " + changedTo, Toast.LENGTH_SHORT).show();
            }
        });
        setSupportActionBar(toolbar);

        ArrayAdapter<String> SpinnerAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"Каталог", "Гараж", "Резерв"});
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTBCat.setAdapter(SpinnerAdapter);
        spnTBCat.setVisibility(View.VISIBLE);

        spnTBCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Fragment newFragment = null;
                switch (position) {
                    case 0:
                        newFragment = new FragmentCatalogGrid();
                        Toast.makeText(getBaseContext(), "Catalog", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        newFragment = new FragmentGarage();
                        Toast.makeText(getBaseContext(), "Garage", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //newFragment = new FragmentReserve();
                        Toast.makeText(getBaseContext(), "Reserve", Toast.LENGTH_SHORT).show();
                        break;
                }
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                if (newFragment != null) {
                    changeFragment(newFragment);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList = (ListView) findViewById(R.id.lv_fragment_drawer);
        setAdapter();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mHandler = new Handler();


        XmlParser.parseXML(new XmlParser.LoadListener() {
            @Override
            public void OnParseComplete(Object result) {
                Toast.makeText(MainActivity.this, "OnParseComplete", Toast.LENGTH_LONG).show();;
            }

            @Override
            public void OnParseError(Exception error) {
                Toast.makeText(MainActivity.this, "OnParseError", Toast.LENGTH_LONG).show();;
            }
        }, getResources().getXml(R.xml.test));

    }

    void addButton() {
        MorphButton mb = new MorphButton(this);

    }

    public void savePref() {

        ArrayList<String> nnn = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            nnn.add("sjdhfjkhsdjklfkl");
        }

        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(nnn);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();

    }

    public ArrayList<String> loadPref() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        ArrayList<String> obj = gson.fromJson(json, new TypeToken<ArrayList<String>>() {}.getType());
        return obj;
    }


    private void setAdapter() {
        mDrawerList.setAdapter(new AppSectionAdapter());
    }

    class AppSectionAdapter extends BaseAdapter {
        private class MyRow {
            TextView tvMenuItem;
            LinearLayout llRowDrawer;
        }

        public String[] menu = DATA;

        @Override
        public int getCount() {
            return menu.length;
        }

        @Override
        public Object getItem(int i) {
            return menu[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyRow myRow;
            if (view == null) {
                myRow = new MyRow();
                view = getLayoutInflater().inflate(R.layout.row_fragment_drawer, viewGroup, false);
                myRow.tvMenuItem = (TextView) view.findViewById(R.id.tv_menu_item);
                myRow.llRowDrawer = (LinearLayout) view.findViewById(R.id.ll_row_drawer);
                view.setTag(myRow);
            } else {
                myRow = (MyRow) view.getTag();
            }
            myRow.tvMenuItem.setText(menu[i]);
            return view;
        }

    }

    public void changeFragmentBack(Fragment fragment) {
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.content_frame, fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);
        trans.commit();
    }

    public void changeFragment(Fragment fragment) {
        mHandler.post(new CommitFragmentRunnable(fragment));
    }

    private class CommitFragmentRunnable implements Runnable {

        private Fragment fragment;

        public CommitFragmentRunnable(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void run() {
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0 && !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            fm.popBackStack();
        } else {
            //todo not work yet
            //mDrawerLayout.openDrawer(mDrawerList);
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                appExit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void appExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.app_exit_dialog)).setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    finish();
                    //System.exit(0);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };
}
