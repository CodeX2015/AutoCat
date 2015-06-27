package ru.app.autocat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.app.autocat.helpers.XmlParserHelper;

/**
 * Created by CodeX on 22.06.2015.
 */
public class Utils {
    private static ExecutorService mExecService = Executors.newCachedThreadPool();
    private static Context context;
    private static SharedPreferences mPrefs = null;
    private static ArrayList<Car> carsDBOrig;
    private static ArrayList<Car> carsDBFiltered;
    private static ArrayList<Car> carsDBPrefs;
    private static String mCarMarkFilter = "Все"; //

    public static void setCarsDBOrig(ArrayList<Car> cars) {
        carsDBOrig = cars;
    }

    public static ArrayList<Car> getCarsDBOrig() {
        return hashById(carsDBOrig);
    }

    public static void setCarsDBFiltered(ArrayList<Car> cars) {
        carsDBFiltered = getFilteredDataByMark(mCarMarkFilter, cars);
    }

    public static ArrayList<Car> getCarsDBFiltered() {
        return getFilteredDataByMark(mCarMarkFilter, getCarsDBOrig());
    }

    public static ArrayList<Car> getCarsDBPrefs() {
        return carsDBPrefs;
    }

    public static void setCarsDBPrefs(ArrayList<Car> carsDBPrefs) {
        Utils.carsDBPrefs = carsDBPrefs;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        Utils.context = context;
    }

    public static String getmCarMarkFilter() {
        return mCarMarkFilter;
    }

    public static void setmCarMarkFilter(String mCarMarkFilter) {
        Utils.mCarMarkFilter = mCarMarkFilter;
    }

    public Utils(Context context) {
        Utils.context = context;
        SharedPreferences mPrefs = context.getSharedPreferences("Cars", 0);
    }

    public static void compareData(final LoadListener listener, Context context) {
        mPrefs = context.getSharedPreferences("Cars", 0);
        mExecService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.OnLoadComplete(compareCars(getCarsDBFiltered()));
                } catch (Exception e) {
                    listener.OnLoadError(e.getMessage());
                }
            }
        });
    }

    public static void loadData(final LoadListener listener, final Context context) {
        mPrefs = context.getSharedPreferences("Cars", 0);
        mExecService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    loadPref();
                    listener.OnLoadComplete(getCarsDBPrefs());
                } catch (Exception e) {
                    listener.OnLoadError(e.getMessage());
                }
            }
        });
    }

    public static void saveItem(final SaveListener listener, Context context, final Car carDetails) {
        mPrefs = context.getSharedPreferences("Cars", 0);
        mExecService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    savePref(carDetails);
                    listener.OnSaveComplete(true);
                } catch (Exception e) {
                    listener.OnSaveError(e.getMessage());
                }
            }
        });
    }

    public static void deleteItem(final DeleteListener listener, Context context, final Car carDetails) {
        mPrefs = context.getSharedPreferences("Cars", 0);
        mExecService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    deleteSelectPref(carDetails);
                    listener.OnDeleteComplete(true);
                } catch (Exception e) {
                    listener.OnDeleteError(e.getMessage());
                }
            }
        });

    }

    public static void loadItem(final LoadListener listener, Context context, final Car carDetails) {
        mPrefs = context.getSharedPreferences("Cars", 0);
        mExecService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.OnLoadComplete(loadSelectPref(carDetails));
                } catch (Exception e) {
                    listener.OnLoadError(e.getMessage());
                }
            }
        });
    }

    public static ArrayList<Car> hashById(ArrayList<Car> cars) {
        if (cars != null) {
            HashMap<String, Car> carsId = new HashMap<String, Car>();
            for (int i = 0; i < cars.size(); i++) {
                carsId.put(cars.get(i).getId(), cars.get(i));
            }
            cars.clear();
            cars.addAll(carsId.values());
        }
        return cars;
    }

    private static ArrayList<Car> compareCars(ArrayList<Car> cars) {
        if (cars == null) {return null;}
        loadPref();
        ArrayList<Car> loadCars = getCarsDBPrefs();
        if (loadCars != null) {
            HashMap<String, Car> carsHash = new HashMap<String, Car>();
            for (int i = 0; i < cars.size(); i++) {
                //if (!carsHash.containsKey(cars.get(i).getId())) {
                carsHash.put(cars.get(i).getId(), cars.get(i));
                //}
            }
            for (int i = 0; i < loadCars.size(); i++) {
                if (carsHash.containsKey(loadCars.get(i).getId())) {
                    carsHash.put(loadCars.get(i).getId(), loadCars.get(i));
                }
            }
            cars.clear();
            cars.addAll(carsHash.values());
        }
        return cars;
    }

    private static Car loadSelectPref(Car carDetails) {
        loadPref();
        ArrayList<Car> cars = getCarsDBPrefs();
        if (cars != null && cars.size() > 1) {
            for (int i = 0; i < cars.size(); i++) {
                if (cars.get(i).getId().equalsIgnoreCase(carDetails.getId())) {
                    return cars.get(i);
                }
            }
        }
        return null;
    }

    private static void deleteSelectPref(Car carDetails) {
        loadPref();
        ArrayList<Car> cars = getCarsDBPrefs();
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

    private static void savePref(Car carDetails) {
        loadPref();
        ArrayList<Car> cars = getCarsDBPrefs();
        if (cars != null) {
            HashMap<String, Car> carsId = new HashMap<String, Car>();
            for (int i = 0; i < cars.size(); i++) {
                if (!carsId.containsKey(cars.get(i).getId())) {
                    carsId.put(cars.get(i).getId(), cars.get(i));
                }
            }
            carsId.put(carDetails.getId(), carDetails);
            cars.clear();
            cars.addAll(carsId.values());
        } else {
            cars = new ArrayList<Car>();
            cars.add(carDetails);
        }
        savePrefFull(cars);
    }

    private static void savePrefFull(ArrayList<Car> cars) {
        if (mPrefs == null) {
            return;
        }
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.clear();
        if (cars != null) {
            Gson gson = new Gson();
            String json = gson.toJson(cars);
            prefsEditor.putString("Cars", json);
        }
        prefsEditor.apply();
        loadPref();
    }

    private static void loadPref() {
        if (mPrefs == null) {
            return;
        }
        Gson gson = new Gson();
        String json = mPrefs.getString("Cars", null);
        setCarsDBPrefs(
                getFilteredDataByMark(mCarMarkFilter,
                        (ArrayList<Car>) gson.fromJson(json,
                                new TypeToken<ArrayList<Car>>() {
                                }.getType())));
    }

    public static ArrayList<Car> getFilteredDataByCountry(String pattern) {
        ArrayList<Car> filteredData = new ArrayList<Car>();
        for (Car car : getCarsDBOrig()) {
            if (car.getCountry().equalsIgnoreCase(pattern)) {
                filteredData.add(car);
            }
        }
        return filteredData;
    }

    public static ArrayList<Car> getFilteredDataByMark(String pattern, ArrayList<Car> carsForFilter) {
        ArrayList<Car> filteredData = new ArrayList<Car>();
        if (carsForFilter != null) {
            for (Car car : carsForFilter) {
                if (pattern.equalsIgnoreCase("Все") || car.getMark().equalsIgnoreCase(pattern)) {
                    filteredData.add(car);
                }
            }
            return filteredData;
        } else {
            return null;
        }
    }

    public static ArrayList<String> getListOfMarkByCountry(String filterCountry) {
        ArrayList<String> mArrayListMarks = new ArrayList<>();
        ArrayList<Car> cars = getFilteredDataByCountry(filterCountry);
        HashMap<String, String> marks = new HashMap<String, String>();
        if (cars != null) {
            for (Car car : cars) {
                if (!marks.containsKey(car.getMark())) {
                    marks.put(car.getMark(), "");
                }
            }
            mArrayListMarks.addAll(marks.keySet());
        }
        mArrayListMarks.add(0, "Все");
        return mArrayListMarks;
    }

    protected static void parseXML(Context context) {
        XmlParserHelper.parseXMLbyStack(new XmlParserHelper.LoadListener() {
            @Override
            public void OnParseComplete(final Object result) {
                setCarsDBOrig((ArrayList<Car>) result);
            }

            @Override
            public void OnParseError(final Exception error) {
                Log.d("OnParseError: ", error.getMessage());
            }
        }, context.getResources().getXml(R.xml.test));
    }

    public interface LoadListener {
        void OnLoadComplete(Object result);
        void OnLoadError(String error);
    }
    public interface SaveListener {
        void OnSaveComplete(boolean result);
        void OnSaveError(String error);
    }
    public interface DeleteListener {
        void OnDeleteComplete(boolean result);
        void OnDeleteError(String error);
    }

    public static void EmptyMessage(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Информация:")
                .setMessage("В гараже нет машин.")
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
