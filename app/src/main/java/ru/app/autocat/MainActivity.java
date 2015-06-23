package ru.app.autocat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import ru.app.autocat.fragments.FragmentCatalogGrid;
import ru.app.autocat.fragments.FragmentCatalogList;
import ru.app.autocat.fragments.FragmentCatalogListGroup;
import ru.app.autocat.fragments.FragmentGarageGrid;
import ru.app.autocat.fragments.FragmentGarageList;
import ru.app.autocat.fragments.FragmentReserve;
import ru.app.autocat.helpers.XmlParserHelper;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ListView mDrawerList;
    private Spinner spnTBCat;
    private ImageView ivChangeView;
    private Handler mHandler;
    public ArrayList<Car> carsDB;
    public ArrayList<Car> carsDBG;
    private Button mDrawerBtnDe;
    private Button mDrawerBtnJp;
    private Button mDrawerBtnUs;
    public static boolean mListUserView;
    LinearLayout mDrawerLayoutMain;

    public final static String FRAGMENT_CATALOG = "fragment_catalog";
    public final static String FRAGMENT_GARAGE = "fragment_garage";

    public static final String[] DATA = {"Все", "Audi", "BMW", "Ford", "Toyota"};

    public void setCarsDBG(ArrayList<Car> carsDBG) {
        this.carsDBG = carsDBG;
    }

    public ArrayList<Car> getCarsDBG() {
        return carsDBG;
    }

    public void setCarsDB(ArrayList<Car> carsDB) {
        this.carsDB = carsDB;
    }

    public ArrayList<Car> getCarsDB() {
        return carsDB;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parseXML();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = mDrawerTitle = "";
        spnTBCat = (Spinner) findViewById(R.id.toolbar_spinner_cat);

        ArrayAdapter<String> SpinnerAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"Каталог", "Гараж", "Резерв"});
        SpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnTBCat.setAdapter(SpinnerAdapter);
        spnTBCat.setVisibility(View.VISIBLE);

        spnTBCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Fragment newFragment = null;
                String fragmentTag = "";
                switch (position) {
                    case 0:
                        if (mListUserView) {
                            newFragment = new FragmentCatalogListGroup();
                        } else {
                            newFragment = new FragmentCatalogGrid();
                        }
                        fragmentTag = FRAGMENT_CATALOG;
                        Toast.makeText(getBaseContext(), "Catalog", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        if (mListUserView) {
                            newFragment = new FragmentGarageList();
                        } else {
                            newFragment = new FragmentGarageGrid();
                        }
                        fragmentTag = FRAGMENT_GARAGE;
                        Toast.makeText(getBaseContext(), "Garage", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        newFragment = new FragmentReserve();
                        Toast.makeText(getBaseContext(), "Reserve", Toast.LENGTH_SHORT).show();
                        break;
                }
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.spn_selected_item_text_color));
                if (newFragment != null) {
                    changeFragment(newFragment, fragmentTag);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ivChangeView = (ImageView) findViewById(R.id.iv_change_view);
        //ToDo http://stackoverflow.com/questions/9731602/animated-icon-for-actionitem
        ivChangeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mListUserView) {
                    mListUserView = true;
                    refresh(ivChangeView, getResources().getDrawable(R.drawable.ic_view_module_white_24dp));
                    Toast.makeText(MainActivity.this, "Changed to: List", Toast.LENGTH_SHORT).show();
                    if (checkVisibleFragment(FRAGMENT_CATALOG)) {
                        changeFragment(new FragmentCatalogList(), FRAGMENT_CATALOG);
                    } else {
                        changeFragment(new FragmentGarageList(), FRAGMENT_GARAGE);
                    }
                } else {
                    mListUserView = false;
                    refresh(ivChangeView, getResources().getDrawable(R.drawable.ic_view_list_white_24dp));
                    Toast.makeText(MainActivity.this, "Changed to: Grid", Toast.LENGTH_SHORT).show();
                    if (checkVisibleFragment(FRAGMENT_CATALOG)) {
                        changeFragment(new FragmentCatalogGrid(), FRAGMENT_CATALOG);
                    } else {
                        changeFragment(new FragmentGarageGrid(), FRAGMENT_GARAGE);
                    }
                }
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mTitle);

        mDrawerLayoutMain = (LinearLayout) findViewById(R.id.llDrawerLayout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerBtnDe = (Button) findViewById(R.id.btnDE);
        mDrawerBtnJp = (Button) findViewById(R.id.btnJP);
        mDrawerBtnUs = (Button) findViewById(R.id.btnUS);

        View.OnClickListener oclBtn = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int m = v.getId();
                String pattern = "";
                switch (v.getId()) {
                    case R.id.btnDE:
                        pattern = "Германия";
                        mDrawerBtnDe.setBackground(getResources().getDrawable(R.drawable.btn_bg_color_pressed));
                        mDrawerBtnUs.setBackground(getResources().getDrawable(R.drawable.btn_bg_color));
                        mDrawerBtnJp.setBackground(getResources().getDrawable(R.drawable.btn_bg_color));
                        break;
                    case R.id.btnUS:
                        pattern = "США";
                        mDrawerBtnDe.setBackground(getResources().getDrawable(R.drawable.btn_bg_color));
                        mDrawerBtnUs.setBackground(getResources().getDrawable(R.drawable.btn_bg_color_pressed));
                        mDrawerBtnJp.setBackground(getResources().getDrawable(R.drawable.btn_bg_color));
                        break;
                    case R.id.btnJP:
                        pattern = "Япония";
                        mDrawerBtnDe.setBackground(getResources().getDrawable(R.drawable.btn_bg_color));
                        mDrawerBtnUs.setBackground(getResources().getDrawable(R.drawable.btn_bg_color));
                        mDrawerBtnJp.setBackground(getResources().getDrawable(R.drawable.btn_bg_color_pressed));
                        break;
                }
                //mDrawerLayout.closeDrawer(mDrawerLayoutMain);
                setCarsDBG(getFilteredDataByCountry(pattern));
                if (mListUserView) {
                    changeFragment(new FragmentCatalogList(), FRAGMENT_CATALOG);
                } else {
                    changeFragment(new FragmentCatalogGrid(), FRAGMENT_CATALOG);
                }
            }
        };

        mDrawerBtnDe.setOnClickListener(oclBtn);
        mDrawerBtnJp.setOnClickListener(oclBtn);
        mDrawerBtnUs.setOnClickListener(oclBtn);

        mDrawerList = (ListView) findViewById(R.id.lv_fragment_drawer);
        setAdapter();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getAdapter().getItem(position).toString().equalsIgnoreCase("Все")) {
                    setCarsDBG(
                            getFilteredData(
                                    parent.getAdapter().getItem(position).toString(), getCarsDB()));
                } else {
                    setCarsDBG(getCarsDB());
                }
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerLayoutMain);
                if (mListUserView) {
                    if (checkVisibleFragment(FRAGMENT_CATALOG)) {
                        changeFragment(new FragmentCatalogList(), FRAGMENT_CATALOG);
                    } else {
                        changeFragment(new FragmentGarageList(), FRAGMENT_GARAGE);
                    }
                } else {
                    if (checkVisibleFragment(FRAGMENT_CATALOG)) {
                        changeFragment(new FragmentCatalogGrid(), FRAGMENT_CATALOG);
                    } else {
                        changeFragment(new FragmentGarageGrid(), FRAGMENT_GARAGE);
                    }
                }
            }
        });

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

    }


    public void refresh(final ImageView iv, final Drawable icon) {
     /* Attach a rotating ImageView to the refresh item as an ActionView */
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.icon_changer);
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.setImageDrawable(icon);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rotation.setRepeatCount(Animation.ABSOLUTE);
        iv.startAnimation(rotation);

        //TODO trigger loading
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void parseXML() {
        XmlParserHelper.parseXMLbyStack(new XmlParserHelper.LoadListener() {
            @Override
            public void OnParseComplete(final Object result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCarsDB((ArrayList<Car>) result);
                        setCarsDBG(getCarsDB());
                    }
                });
            }

            @Override
            public void OnParseError(final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "OnParseError: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }, getResources().getXml(R.xml.test));
    }

    public void savePref(Car carDetails) {
        ArrayList<Car> cars = loadPref();
        if (cars == null) {
            cars = new ArrayList<Car>();
        }
        cars.add(carDetails);
        savePrefFull(cars);
    }

    public void savePrefFull(ArrayList<Car> cars) {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cars);
        prefsEditor.clear();
        if (cars != null) {
            prefsEditor.putString("Cars", json);
        }
        prefsEditor.apply();
    }

    public void deleteSelectPref(Car carDetails) {
        ArrayList<Car> cars = loadPref();
        if (cars != null && cars.size() > 1) {

            for (int i = 0; i <= cars.size() - 1; i++) {
                if (cars.get(i).getId().equalsIgnoreCase(carDetails.getId())) {
                    cars.remove(i);
                }
            }
        } else if (cars != null && cars.size() <= 1) {
            cars = null;
        }
        savePrefFull(cars);
    }

    public ArrayList<Car> loadPref() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("Cars", null);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, new TypeToken<ArrayList<Car>>() {
        }.getType());
    }

    private void setFilterPattern(String pattern) {
        FragmentCatalogGrid fragmentCatalogGrid = new FragmentCatalogGrid();
        Bundle patternArgs = new Bundle();
        if (pattern.equalsIgnoreCase("Все")) {
            pattern = "";
        }
        patternArgs.putString("FilterPattern", pattern);
        fragmentCatalogGrid.setArguments(patternArgs);
        changeFragment(fragmentCatalogGrid, FRAGMENT_CATALOG);
    }

    public ArrayList<Car> getFilteredData(String pattern, ArrayList<Car> dataforfilter) {
        ArrayList<Car> filteredData = new ArrayList<Car>();
        if (dataforfilter != null) {
            for (Car car : dataforfilter) {
                if (car.getMark().equalsIgnoreCase(pattern)) {
                    filteredData.add(car);
                }
            }
            return filteredData;
        } else {
            return null;
        }
    }

    public ArrayList<Car> getFilteredDataByCountry(String pattern) {
        ArrayList<Car> filteredData = new ArrayList<Car>();

        for (Car car : getCarsDB()) {
            if (car.getCountry().equalsIgnoreCase(pattern)) {
                filteredData.add(car);
            }
        }
        return filteredData;
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

    public void changeFragment(Fragment fragment, String fragmentTag) {
        mHandler.post(new CommitFragmentRunnable(fragment, fragmentTag));
    }

    private class CommitFragmentRunnable implements Runnable {

        private Fragment fragment;
        private String fragmentTag;

        public CommitFragmentRunnable(Fragment fragment, String fragmentTag) {

            this.fragment = fragment;
            this.fragmentTag = fragmentTag;
        }

        @Override
        public void run() {
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment, fragmentTag)
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    boolean checkVisibleFragment(String tag) {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return false;
        }
        if (getSupportFragmentManager().findFragmentByTag(tag) != null && getSupportFragmentManager().findFragmentByTag(tag).isVisible()) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0 && !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            fm.popBackStack();
        } else {
            mDrawerLayout.openDrawer(mDrawerLayoutMain);
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
        if (id == R.id.action_exit) {
            appExit();
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
