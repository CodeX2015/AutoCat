package ru.app.autocat;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by CodeX on 19.06.2015.
 */

public class XmlParser {
    private static ExecutorService mExecService = Executors.newCachedThreadPool();
    private static Context mContext;
    static String LOG_TAG = "AutoCat_log";
    static String tmp;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static void parseXML(final LoadListener listener, final XmlPullParser xpp) {
        mExecService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                        switch (xpp.getEventType()) {
                            // начало документа
                            case XmlPullParser.START_DOCUMENT:
                                Log.d(LOG_TAG, "START_DOCUMENT");
                                break;
                            // начало тэга
                            case XmlPullParser.START_TAG:
                                Log.d(LOG_TAG, "START_TAG: name = " + xpp.getName()
                                        + ", depth = " + xpp.getDepth() + ", attrCount = "
                                        + xpp.getAttributeCount());
                                tmp = "";
                                for (int i = 0; i < xpp.getAttributeCount(); i++) {
                                    tmp = tmp + xpp.getAttributeName(i) + " = "
                                            + xpp.getAttributeValue(i) + ", ";
                                }
                                if (!TextUtils.isEmpty(tmp))
                                    Log.d(LOG_TAG, "Attributes: " + tmp);
                                break;
                            // конец тэга
                            case XmlPullParser.END_TAG:
                                Log.d(LOG_TAG, "END_TAG: name = " + xpp.getName());
                                break;
                            // содержимое тэга
                            case XmlPullParser.TEXT:
                                Log.d(LOG_TAG, "text = " + xpp.getText());
                                break;

                            default:
                                break;
                        }
                        // следующий элемент
                        xpp.next();
                    }
                    Log.d(LOG_TAG, "END_DOCUMENT");
                    listener.OnParseComplete(tmp);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    listener.OnParseError(e);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public static void parseXMLbyStack(String XMLFILEPATH) {
        ArrayList<Car> cars = new ArrayList<Car>();
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();

        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new FileReader(XMLFILEPATH));
        int eventType = xpp.getEventType();
        String lastTag;
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            Car car;

        /*
         * The name of the tag like: <foo> --> foo
         */
            String tagName = xpp.getName();
        /*
         * Opening tag
         */
            if (eventType == XmlPullParser.START_TAG)
            {
                if (tagName.equals("car_database"))
                {
                    // init your ArrayList

                    cars.clear();
                } else if (tagName.equals("car"))
                {
                    // new user tag opened
                    car = new Car();
                } else if(tagName.equals("id")) {
                    lastTag = "id";
                }

            /*
             * Closing tag
             */
            } else if (eventType == XmlPullParser.END_TAG)
            {
                if (tagName.equals("user"))
                {
                    urArrayList.add(user);
                }
            } else if (eventType == XmlPullParser.TEXT)
            {
                if (lastTag.equals("id"))
                {
                    // fill UserSTruct
                    // do so for the other tags as well
                }
            }

            eventType = xpp.next();
        }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public interface LoadListener {
        void OnParseComplete(Object result);

        void OnParseError(Exception error);
    }
}
