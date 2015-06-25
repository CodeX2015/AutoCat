package ru.app.autocat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.app.autocat.Car;
import ru.app.autocat.MainActivity;
import ru.app.autocat.R;
import ru.app.autocat.Utils;
import ru.app.autocat.activity.ActivityCarDetails;
import ru.app.autocat.adapters.Sectionizer;
import ru.app.autocat.adapters.SimpleSectionAdapter;

/**
 * Created by CodeX on 21.06.2015.
 */

public class FragmentCatalogListGroup extends Fragment {

    private ArrayList<Car> cars;
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);
        mListView = (ListView) view.findViewById(R.id.lvMain);
        separateList();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //convert object to string
                Gson gson = new Gson();
                String json = gson.toJson(cars.get(position));

                Intent myIntent = new Intent(getActivity(), ActivityCarDetails.class);
                myIntent.putExtra("CarDetails", json);
                getActivity().startActivity(myIntent);


                /**
                 Bundle carsArgs = new Bundle();
                 carsArgs.putString("CarDetails", json);
                 FragmentDetails fragmentDetails = new FragmentDetails();
                 fragmentDetails.setArguments(carsArgs);
                 ((MainActivity) getActivity()).changeFragmentBack(fragmentDetails);
                 */
            }
        });

        return view;
    }


    private ArrayList<Car> getData() {
        ArrayList<Car> cars = ((MainActivity) getActivity()).getCarsDBG();
        if (cars != null) {
            ArrayList<Car> carsLoad = Utils.compareData(getActivity(), cars);
            if (carsLoad != null) {
                return carsLoad;
            }
            return cars;
        } else {
            return null;
        }
    }

    void separateList() {
        // 1. Your data source
        ArrayList<Car> cars = getData();
        if (cars == null) {
            return;
        }
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
        mListView.setAdapter(sectionAdapter);
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.row_listview, parent, false);
                myRow.ivCarPic = (ImageView) convertView.findViewById(R.id.ivCarPic);
                myRow.tvModel = (TextView) convertView.findViewById(R.id.tvModel);
                myRow.tvCreate = (TextView) convertView.findViewById(R.id.tv_create);
                myRow.tvMT = (TextView) convertView.findViewById(R.id.tv_mt_header);
                myRow.tvAT = (TextView) convertView.findViewById(R.id.tv_at_header);
                myRow.tvAmountMT = (TextView) convertView.findViewById(R.id.tv_mt);
                myRow.tvAmountAT = (TextView) convertView.findViewById(R.id.tv_at);
                convertView.setTag(myRow);
            } else {
                myRow = (MyRow) convertView.getTag();
            }
            myRow.tvModel.setText(getItem(position).getModel());
            myRow.tvCreate.setText(getItem(position).getCreated());
            myRow.tvMT.setText(getItem(position).getKppMT());
            myRow.tvAT.setText(getItem(position).getKppAT());
            myRow.tvAmountMT.setText(String.valueOf(getItem(position).getAmountKppMt()));
            myRow.tvAmountAT.setText(String.valueOf(getItem(position).getAmountKppAt()));
            return convertView;
        }

        private class MyRow {
            TextView tvModel;
            TextView tvCreate;
            TextView tvMT;
            TextView tvAT;
            TextView tvAmountMT;
            TextView tvAmountAT;
            ImageView ivCarPic;
        }
    }

    private class MarkComparator implements Comparator<Car> {
        @Override
        public int compare(Car car1, Car car2) {
            return car1.getMark().compareTo(car2.getMark());
        }
    }
}
