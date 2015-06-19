package ru.app.autocat;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by CodeX on 19.06.2015.
 */

public class XmlParserHelper {
    private static ExecutorService mExecService = Executors.newCachedThreadPool();
    static String LOG_TAG = "AutoCat_log";
    static String tmp;

    public static void parseXMLbyStack(final LoadListener listener, final XmlPullParser xpp) {
        mExecService.submit(new Runnable() {

            @Override
            public void run() {
                ArrayList<Car> cars = new ArrayList<Car>();
                try {
                    //xpp.setInput(new FileReader(XMLFILEPATH));
                    int eventType = xpp.getEventType();
                    String tagName = "";
                    String closeTag;
                    Car car = null;
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        closeTag = xpp.getName();
                /*
                 * Opening tag
                */
                        if (eventType == XmlPullParser.START_TAG) {
                    /*
                    * The name of the tag like: <car> --> car
                    */
                            tagName = xpp.getName();
                            //Log.d(LOG_TAG, "START.tagName = " + xpp.getName());
                            if (tagName.equals("car_database")) {
                                // init your ArrayList
                                Log.d(LOG_TAG, "Clear Array");
                                cars.clear();
                            } else if (tagName.equals("car")) {
                                // new car tag opened
                                car = new Car();
                                Log.d(LOG_TAG, "Create Car");
                            }
                        } else if (eventType == XmlPullParser.TEXT) {
                            //Log.d(LOG_TAG, tagName + " ?= " + xpp.getText());
                            switch (tagName) {
                                // fill CarStruct
                                // do so for the other tags as well
                                case "id":
                                    if (car != null) {
                                        car.setId(xpp.getText());
                                    }
                                    break;
                                case "model":
                                    if (car != null) {
                                        car.setModel(xpp.getText());
                                    }
                                    break;
                                case "title":
                                    if (car != null) {
                                        car.setTitle(xpp.getText());
                                    }
                                    break;
                                case "mark":
                                    if (car != null) {
                                        car.setMark(xpp.getText());
                                    }
                                    break;
                                case "imageName1":
                                    if (car != null) {
                                        car.setImageName(xpp.getText());
                                    }
                                    break;
                                case "created":
                                    if (car != null) {
                                        car.setCreated(xpp.getText());
                                    }
                                    break;
                                case "kpp":
                                    if (car != null) {
                                        car.setKpp(xpp.getText());
                                    }
                                    break;
                                case "country":
                                    if (car != null) {
                                        car.setCountry(xpp.getText());
                                    }
                                    break;
                                default:
                                    //Log.d(LOG_TAG, tagName + " d= " + xpp.getText());
                                    break;
                            }
                        /*
                        * Closing tag
                        */
                        } else if (eventType == XmlPullParser.END_TAG) {
                            //Log.d(LOG_TAG, "END.tagName = " + closeTag);
                            if (closeTag.equals("car")) {
                                cars.add(car);
                                //Log.d(LOG_TAG, "Add Car to Array");
                            }
                        }
                        eventType = xpp.next();
                    }
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                    listener.OnParseError(e);
                }
                listener.OnParseComplete(cars);
            }
        });
    }


    public interface LoadListener {
        void OnParseComplete(Object result);

        void OnParseError(Exception error);
    }
}
