package ru.app.autocat;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.app.autocat.activity.ActivityCarDetails;

/**
 * Created by CodeX on 22.06.2015.
 */
public class Utils {
    private static ExecutorService mExecService = Executors.newCachedThreadPool();
    private Context context;
    private static SharedPreferences mPrefs = null;
    private static ArrayList<Car> carsDBOrig;
    private static ArrayList<Car> carsDBFiltered;
    private String mCarMarkFilter = "Все"; //

    public static void setCarsDBOrig(ArrayList<Car> cars) {
        carsDBOrig = cars;
    }

    public static ArrayList<Car> getCarsDBOrig() {
        return hashById(carsDBOrig);
    }

    public static void setCarsDBFiltered(ArrayList<Car> cars) {
        carsDBFiltered = cars;
    }

    public static ArrayList<Car> getCarsDBFiltered() {
        if (carsDBFiltered == null){return getCarsDBOrig();}
        return carsDBFiltered;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Utils(Context context) {
        this.context = context;
        SharedPreferences mPrefs = context.getSharedPreferences("Cars", 0);
    }

    public static ArrayList<Car> compareData(Context context, ArrayList<Car> cars) {
        mPrefs = context.getSharedPreferences("Cars", 0);
        if (cars == null) {
            return null;
        }
        return compareCars(cars);
    }

    public static ArrayList<Car> loadData(Context context) {
        mPrefs = context.getSharedPreferences("Cars", 0);
        return loadPref();
    }

    public static void saveData(Context context, Car carDetails) {
        mPrefs = context.getSharedPreferences("Cars", 0);
        savePref(carDetails);
    }

    public static void deleteItem(Context context, Car carDetails) {
        mPrefs = context.getSharedPreferences("Cars", 0);
        deleteSelectPref(carDetails);
    }

    public static Car loadItem(Context context, Car carDetails) {
        mPrefs = context.getSharedPreferences("Cars", 0);
        return loadSelectPref(carDetails);
    }

    private static ArrayList<Car> compareCars(ArrayList<Car> cars) {
        ArrayList<Car> loadCars = loadPref();
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
        ArrayList<Car> cars = loadPref();
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

    private static void savePref(Car carDetails) {
        ArrayList<Car> cars = loadPref();
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

    private static void savePrefFull(ArrayList<Car> cars) {
        if (mPrefs == null) {
            return;
        }
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cars);
        prefsEditor.clear();
        if (cars != null) {
            prefsEditor.putString("Cars", json);
        }
        prefsEditor.apply();
    }

    private static ArrayList<Car> loadPref() {
        if (mPrefs == null) {
            return null;
        }
        Gson gson = new Gson();
        String json = mPrefs.getString("Cars", null);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, new TypeToken<ArrayList<Car>>() {
        }.getType());
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

    public static ArrayList<Car> getFilteredDataByMark(String pattern) {
        ArrayList<Car> filteredData = new ArrayList<Car>();
        if (getCarsDBOrig() != null) {
            for (Car car : getCarsDBOrig()) {
                if (car.getMark().equalsIgnoreCase(pattern)) {
                    filteredData.add(car);
                }
            }
            return filteredData;
        } else {
            return null;
        }
    }
}
