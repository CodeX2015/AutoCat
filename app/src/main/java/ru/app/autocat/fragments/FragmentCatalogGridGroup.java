package ru.app.autocat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.app.autocat.Car;
import ru.app.autocat.MainActivity;
import ru.app.autocat.R;
import ru.app.autocat.Utils;
import ru.app.autocat.activity.ActivityCarDetails;
import ru.app.autocat.adapters.StickyGridHeadersSimpleArrayAdapter;

/**
 * Created by CodeX on 23.06.2015.
 */

public class FragmentCatalogGridGroup extends Fragment {
    private GridView mGridView;
    private ArrayList<Car> cars;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gridview, container, false);
        mGridView = (GridView) view.findViewById(R.id.asset_grid);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //convert object to string
                        Gson gson = new Gson();
                        String json = gson.toJson(cars.get(position));

                        Intent myIntent = new Intent(getActivity(), ActivityCarDetails.class);
                        myIntent.putExtra("CarDetails", json);
                        getActivity().startActivity(myIntent);

                        // create Bundle for fragment
                        //Bundle carsArgs = new Bundle();
                        //carsArgs.putString("CarDetails", json);

                        // create fragment
                        //FragmentDetails fragmentDetails = new FragmentDetails();
                        //fragmentDetails.setArguments(carsArgs);
                        //((MainActivity) getActivity()).changeFragmentBack(fragmentDetails);
                    }
                });
            }
        });

        SeparateByMark();
        return view;
    }

    void SeparateByMark() {
        // 1. Your data source
        //cars = Utils.hashById(((MainActivity) getActivity()).getCarsDBG());
        cars = Utils.getCarsDBOrig();

        if (cars != null) {
            // 2. Sort them using the Mark of the current car
            MarkComparator markComparator = new MarkComparator();
            Collections.sort(cars, markComparator);

            // 3. Set adapter
            mGridView.setAdapter(new StickyGridHeadersSimpleArrayAdapter<Car>(getActivity()
                    .getApplicationContext(), cars, R.layout.grid_header, R.layout.grid_data_item));

        }
    }

    private class MarkComparator implements Comparator<Car> {
        @Override
        public int compare(Car car1, Car car2) {
            return car1.getMark().compareTo(car2.getMark());
        }
    }

}
