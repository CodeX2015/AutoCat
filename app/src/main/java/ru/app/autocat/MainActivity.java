package ru.app.autocat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.ViewFlipper;

import java.util.ArrayList;

import ru.app.autocat.fragments.FragmentCatalogGridGroup;
import ru.app.autocat.fragments.FragmentCatalogListGroup;
import ru.app.autocat.fragments.FragmentGarageGridGroup;
import ru.app.autocat.fragments.FragmentGarageListGroup;
import ru.app.autocat.fragments.FragmentReserve;

public class MainActivity extends AppCompatActivity {
    Drawable mPressed;
    Drawable mUnPressed;
    private ViewFlipper vfSpinnerTitleChng;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ListView mDrawerList;
    private Spinner spnTBCat;
    private ImageView ivChangeView;
    private Handler mHandler;
    private Button mDrawerBtnDe;
    private Button mDrawerBtnJp;
    private Button mDrawerBtnUs;
    public static boolean mListUserView;
    LinearLayout mDrawerLayoutMain;

    public final static String FRAGMENT_CATALOG = "fragment_catalog";
    public final static String FRAGMENT_GARAGE = "fragment_garage";
    public final static String FRAGMENT_RESERVE = "fragment_reserve";

    public ArrayList<String> mDATA = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.parseXML(this);
        mDATA.add("Все");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new);
        mTitle = mDrawerTitle = "";
        vfSpinnerTitleChng = (ViewFlipper) findViewById(R.id.vf_spinner_title);
        spnTBCat = (Spinner) findViewById(R.id.toolbar_spinner_cat);

        ArrayAdapter<String> SpinnerAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_item, new String[]{"Каталог", "Гараж", "Резерв"});
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
                        fragmentTag = FRAGMENT_CATALOG;
                        if (mListUserView) {
                            newFragment = new FragmentCatalogListGroup();
                        } else {
                            newFragment = new FragmentCatalogGridGroup();
                        }
                        //Toast.makeText(getBaseContext(), "Catalog", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        fragmentTag = FRAGMENT_GARAGE;
                        if (mListUserView) {
                            newFragment = new FragmentGarageListGroup();
                        } else {
                            newFragment = new FragmentGarageGridGroup();
                        }
                        //Toast.makeText(getBaseContext(), "Garage", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        fragmentTag = FRAGMENT_RESERVE;
                        newFragment = new FragmentReserve();
                        //Toast.makeText(getBaseContext(), "Reserve", Toast.LENGTH_SHORT).show();
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
        ivChangeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mListUserView) {
                    mListUserView = true;
                    refresh(ivChangeView, getResources().getDrawable(R.drawable.ic_view_module_white_24dp));
                    //Toast.makeText(MainActivity.this, "Changed to: List", Toast.LENGTH_SHORT).show();
                    if (checkVisibleFragment(FRAGMENT_CATALOG)) {
                        changeFragment(new FragmentCatalogListGroup(), FRAGMENT_CATALOG);
                    } else if (checkVisibleFragment(FRAGMENT_GARAGE)) {
                        changeFragment(new FragmentGarageListGroup(), FRAGMENT_GARAGE);
                    } else {
                        changeFragment(new FragmentReserve(), FRAGMENT_RESERVE);
                    }
                } else {
                    mListUserView = false;
                    refresh(ivChangeView, getResources().getDrawable(R.drawable.ic_view_list_white_24dp));
                    //Toast.makeText(MainActivity.this, "Changed to: Grid", Toast.LENGTH_SHORT).show();
                    if (checkVisibleFragment(FRAGMENT_CATALOG)) {
                        changeFragment(new FragmentCatalogGridGroup(), FRAGMENT_CATALOG);
                    } else if (checkVisibleFragment(FRAGMENT_GARAGE)) {
                        changeFragment(new FragmentGarageGridGroup(), FRAGMENT_GARAGE);
                    } else {
                        changeFragment(new FragmentReserve(), FRAGMENT_RESERVE);
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
                String pattern = "Все";
                mPressed = getResources().getDrawable(R.drawable.btn_bg_color_pressed);
                mUnPressed = getResources().getDrawable(R.drawable.btn_bg_color);
                switch (v.getId()) {
                    case R.id.btnDE:
                        pattern = "Германия";
                        mDrawerBtnDe.setBackgroundDrawable(mPressed);
                        mDrawerBtnUs.setBackgroundDrawable(mUnPressed);
                        mDrawerBtnJp.setBackgroundDrawable(mUnPressed);
                        break;
                    case R.id.btnUS:
                        pattern = "США";
                        mDrawerBtnDe.setBackgroundDrawable(mUnPressed);
                        mDrawerBtnUs.setBackgroundDrawable(mPressed);
                        mDrawerBtnJp.setBackgroundDrawable(mUnPressed);
                        break;
                    case R.id.btnJP:
                        pattern = "Япония";
                        mDrawerBtnDe.setBackgroundDrawable(mUnPressed);
                        mDrawerBtnUs.setBackgroundDrawable(mUnPressed);
                        mDrawerBtnJp.setBackgroundDrawable(mPressed);
                        break;
                }
                //mDrawerLayout.closeDrawer(mDrawerLayoutMain);

                mDATA = Utils.getListOfMarkByCountry(pattern);
                setAdapter();

//                if (mListUserView) {
//                    changeFragment(new FragmentCatalogGridGroup(), FRAGMENT_CATALOG);
//                } else {
//                    changeFragment(new FragmentCatalogGridGroup(), FRAGMENT_CATALOG);
//                }
            }
        };

        mDrawerBtnDe.setOnClickListener(oclBtn);
        mDrawerBtnJp.setOnClickListener(oclBtn);
        mDrawerBtnUs.setOnClickListener(oclBtn);

        mDrawerList = (ListView) findViewById(R.id.lv_fragment_drawer);
        //setAdapter();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.setmCarMarkFilter(parent.getAdapter().getItem(position).toString());
                Utils.setCarsDBFiltered(Utils.getCarsDBOrig());
                if (parent.getAdapter().getItem(position).toString().equalsIgnoreCase("Все")) {
                    mDrawerBtnDe.setBackgroundDrawable(mUnPressed);
                    mDrawerBtnUs.setBackgroundDrawable(mUnPressed);
                    mDrawerBtnJp.setBackgroundDrawable(mUnPressed);
                }

                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerLayoutMain);
                if (mListUserView) {
                    if (checkVisibleFragment(FRAGMENT_CATALOG)) {
                        changeFragment(new FragmentCatalogListGroup(), FRAGMENT_CATALOG);
                    } else {
                        changeFragment(new FragmentGarageListGroup(), FRAGMENT_GARAGE);
                    }
                } else {
                    if (checkVisibleFragment(FRAGMENT_CATALOG)) {
                        changeFragment(new FragmentCatalogGridGroup(), FRAGMENT_CATALOG);
                    } else {
                        changeFragment(new FragmentGarageGridGroup(), FRAGMENT_GARAGE);
                    }
                }
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                vfSpinnerTitleChng.setDisplayedChild(0);
                //spnTBCat.setVisibility(View.VISIBLE);
                //getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                vfSpinnerTitleChng.setDisplayedChild(1);
                //spnTBCat.setVisibility(View.INVISIBLE);
                //getSupportActionBar().setTitle(getString(R.string.app_name));
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

    }

//    private ArrayList<String> getMarkListByCountry() {
//        ArrayList<String> mArrayListMarks = new ArrayList<>();
//        ArrayList<Car> cars = getCarsDBG();
//        HashMap<String, String> marks = new HashMap<String, String>();
//        if (cars != null) {
//            for (Car car : cars) {
//                if (!marks.containsKey(car.getMark())) {
//                    marks.put(car.getMark(), "");
//                }
//            }
//            mArrayListMarks.addAll(marks.keySet());
//        }
//        mArrayListMarks.add(0, "Все");
//        return mArrayListMarks;
//    }
//
//    public static Bitmap drawableToBitmap(Drawable drawable) {
//        if (drawable instanceof BitmapDrawable) {
//            return ((BitmapDrawable) drawable).getBitmap();
//        }
//
//        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        drawable.draw(canvas);
//
//        return bitmap;
//    }
//
//    private void parseXML() {
//        XmlParserHelper.parseXMLbyStack(new XmlParserHelper.LoadListener() {
//            @Override
//            public void OnParseComplete(final Object result) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //setCarsDB((ArrayList<Car>) result);
//                        //setCarsDBG(getCarsDB());
//
//                        Utils.setCarsDBOrig((ArrayList<Car>) result);
//                    }
//                });
//            }
//
//            @Override
//            public void OnParseError(final Exception error) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "OnParseError: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        }, getResources().getXml(R.xml.test));
//    }
//
//    public void savePref(Car carDetails) {
//        ArrayList<Car> cars = loadPref();
//        if (cars == null) {
//            cars = new ArrayList<Car>();
//        }
//        cars.add(carDetails);
//        savePrefFull(cars);
//    }
//
//    public void savePrefFull(ArrayList<Car> cars) {
//        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(cars);
//        prefsEditor.clear();
//        if (cars != null) {
//            prefsEditor.putString("Cars", json);
//        }
//        prefsEditor.apply();
//    }
//
//    public void deleteSelectPref(Car carDetails) {
//        ArrayList<Car> cars = loadPref();
//        if (cars != null && cars.size() > 1) {
//
//            for (int i = 0; i <= cars.size() - 1; i++) {
//                if (cars.get(i).getId().equalsIgnoreCase(carDetails.getId())) {
//                    cars.remove(i);
//                }
//            }
//        } else if (cars != null && cars.size() <= 1) {
//            cars = null;
//        }
//        savePrefFull(cars);
//    }
//
//    public ArrayList<Car> loadPref() {
//        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = mPrefs.getString("Cars", null);
//        if (json == null) {
//            return null;
//        }
//        return gson.fromJson(json, new TypeToken<ArrayList<Car>>() {
//        }.getType());
//    }

//    private void setFilterPattern(String pattern) {
//        FragmentCatalogGrid fragmentCatalogGrid = new FragmentCatalogGrid();
//        Bundle patternArgs = new Bundle();
//        if (pattern.equalsIgnoreCase("Все")) {
//            pattern = "";
//        }
//        patternArgs.putString("FilterPattern", pattern);
//        fragmentCatalogGrid.setArguments(patternArgs);
//        changeFragment(fragmentCatalogGrid, FRAGMENT_CATALOG);
//    }

//    public ArrayList<Car> getFilteredDataByMark(String pattern, ArrayList<Car> dataforfilter) {
//        ArrayList<Car> filteredData = new ArrayList<Car>();
//        if (dataforfilter != null) {
//            for (Car car : dataforfilter) {
//                if (car.getMark().equalsIgnoreCase(pattern)) {
//                    filteredData.add(car);
//                }
//            }
//            return filteredData;
//        } else {
//            return null;
//        }
//    }
//
//    public ArrayList<Car> getFilteredDataByCountry(String pattern) {
//        ArrayList<Car> filteredData = new ArrayList<Car>();
//
//        for (Car car : getCarsDB()) {
//            if (car.getCountry().equalsIgnoreCase(pattern)) {
//                filteredData.add(car);
//            }
//        }
//        return filteredData;
//    }

    private void setAdapter() {
        mDrawerList.setAdapter(new AppSectionAdapter());
    }

    class AppSectionAdapter extends BaseAdapter {
        private class MyRow {
            TextView tvMenuItem;
            LinearLayout llRowDrawer;
        }

        public ArrayList<String> menu = mDATA;


        @Override
        public int getCount() {
            return menu.size();
        }

        @Override
        public Object getItem(int i) {
            return menu.get(i);
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
            myRow.tvMenuItem.setText(menu.get(i));
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
        ivChangeView.setVisibility(View.INVISIBLE);
    }

    public void changeFragment(Fragment fragment, String fragmentTag) {
        mHandler.post(new CommitFragmentRunnable(fragment, fragmentTag));
        ivChangeView.setVisibility(View.VISIBLE);
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
        Fragment myFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (myFragment != null) {
            boolean isVisible = myFragment.isVisible();
            if (isVisible) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(this, "Return to main", Toast.LENGTH_LONG).show();
        if (mListUserView) {
            if (checkVisibleFragment(FRAGMENT_CATALOG)) {
                changeFragment(new FragmentCatalogListGroup(), FRAGMENT_CATALOG);
            } else {
                changeFragment(new FragmentGarageListGroup(), FRAGMENT_GARAGE);
            }
        } else {
            if (checkVisibleFragment(FRAGMENT_CATALOG)) {
                changeFragment(new FragmentCatalogGridGroup(), FRAGMENT_CATALOG);
            } else {
                changeFragment(new FragmentGarageGridGroup(), FRAGMENT_GARAGE);
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0 && !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            fm.popBackStack();
            ivChangeView.setVisibility(View.VISIBLE);
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
