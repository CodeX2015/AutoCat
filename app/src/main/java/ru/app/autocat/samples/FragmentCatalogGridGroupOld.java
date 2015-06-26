package ru.app.autocat.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.app.autocat.Car;
import ru.app.autocat.MainActivity;
import ru.app.autocat.R;
import ru.app.autocat.adapters.Sectionizer;
import ru.app.autocat.adapters.SimpleSectionAdapter;

/**
 * Created by CodeX on 18.06.2015.
 */

public class FragmentCatalogGridGroupOld extends Fragment {

    private GridView mGridView;
    //ArrayAdapter<String> adapter;
    //todo https://github.com/TonicArtos/StickyGridHeaders

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gridview, container, false);
        //Button btnParse = (Button) view.findViewById(R.id.btnParse);
        mGridView = (GridView) view.findViewById(R.id.asset_grid);
        seplisttest();
        adjustGridView();

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

        // 2. Sort them using the distance from the current city
        Car all = new Car("Все");
        MarkComparator markComparator = new MarkComparator();
        Collections.sort(cars, markComparator);

        // 3. Create your custom adapter
        MyListAdapter carAdapter = new MyListAdapter(cars);

        // 4. Create a Sectionizer
        CarSectionizer carSectionizer = new CarSectionizer(all);

        // 5. Wrap your adapter within the SimpleSectionAdapter
        SimpleSectionAdapter<Car> sectionAdapter = new SimpleSectionAdapter<Car>(getActivity(),
                carAdapter, R.layout.list_header, R.id.list_header_title, carSectionizer);

        // 6. Set the adapter to your ListView
        mGridView.setAdapter(sectionAdapter);
    }

    class CarSectionizer implements Sectionizer<Car> {
        private Car car;

        public CarSectionizer(Car car) {
            this.car = car;
        }

        @Override
        public String getSectionTitleForItem(Car car) {
            return car.getMark();
        }
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

    private class MyListAdapter extends BaseAdapter {
        ArrayList<Car> cars;

        public MyListAdapter(ArrayList<Car> cars) {
            this.cars = cars;
        }

        @Override
        public int getCount() {
            return cars.size();
        }

        @Override
        public Car getItem(int position) {
            return cars.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyRow myRow;
            if (convertView == null) {
                myRow = new MyRow();
                convertView = getActivity().getLayoutInflater().inflate(R.layout.row_gridview, parent, false);
                myRow.tvModel = (TextView) convertView.findViewById(R.id.tvModel);
                myRow.ivCarPic = (ImageView) convertView.findViewById(R.id.ivCarPic);
                convertView.setTag(myRow);
            } else {
                myRow = (MyRow) convertView.getTag();
            }
            myRow.tvModel.setText(getItem(position).getModel());
            return convertView;
        }

        private class MyRow {
            TextView tvModel;
            ImageView ivCarPic;
        }
    }
}
