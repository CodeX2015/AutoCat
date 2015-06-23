package ru.app.autocat.fragments;

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
import ru.app.autocat.adapters.StickyGridHeadersSimpleArrayAdapter;

/**
 * Created by Yakovlev on 23.06.2015.
 */

public class GridViewSample extends Fragment{
    private GridView mGridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gridview, container, false);
        Button btnParse = (Button) view.findViewById(R.id.btnParse);
        mGridView = (GridView) view.findViewById(R.id.asset_grid);
        seplisttest();
        adjustGridView();


        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentCatalogListGroup newFragment = new FragmentCatalogListGroup();
                ((MainActivity) getActivity()).changeFragmentBack(newFragment);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentDetails fragmentDetails = new FragmentDetails();
                        Bundle carsArgs = new Bundle();
                        Gson gson = new Gson();
                        String json = gson.toJson(parent.getAdapter().getItem(position));
                        carsArgs.putString("CarDetails", json);
                        fragmentDetails.setArguments(carsArgs);
                        ((MainActivity) getActivity()).changeFragmentBack(fragmentDetails);
                    }
                });
            }
        });

        return view;
    }

    void seplisttest () {
        // 1. Your data source
        ArrayList<Car> cars = ((MainActivity) getActivity()).getCarsDBG();

        // 2. Sort them using the distance from the current car
        MarkComparator markComparator = new MarkComparator();
        Collections.sort(cars, markComparator);

        //sample grid adapter
        //todo https://github.com/TonicArtos/StickyGridHeaders

        // 3. Set adapter
        mGridView.setAdapter(new StickyGridHeadersSimpleArrayAdapter<Car>(getActivity()
                .getApplicationContext(), cars, R.layout.grid_header, R.layout.grid_data_item));

    }

    private class MarkComparator implements Comparator<Car> {
        @Override
        public int compare(Car car1, Car car2) {
            return car1.getMark().compareTo(car2.getMark());
        }
    }

    private void adjustGridView() {
        mGridView.setNumColumns(GridView.AUTO_FIT);
    }

}
