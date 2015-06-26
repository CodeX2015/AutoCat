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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import ru.app.autocat.Car;
import ru.app.autocat.MainActivity;
import ru.app.autocat.R;
import ru.app.autocat.Utils;
import ru.app.autocat.activity.ActivityCarDetails;

/**
 * Created by CodeX on 18.06.2015.
 */
public class FragmentGarageListGroup extends Fragment {
    private ListView mListView;
    private ArrayList<Car> cars;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);
        mListView = (ListView) view.findViewById(R.id.lvMain);
        cars = loadData();
        if (cars != null) {
            mListView.setAdapter(new MyListAdapter(cars));
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Gson gson = new Gson();
                String json = gson.toJson(cars.get(position));

                Intent myIntent = new Intent(getActivity(), ActivityCarDetails.class);
                myIntent.putExtra("CarDetails", json);
                getActivity().startActivityForResult(myIntent, 1);

                /**
                Bundle carsArgs = new Bundle();
                carsArgs.putString("CarDetails", json);
                FragmentDetailsGarage fragmentDetailsGarage = new FragmentDetailsGarage();
                fragmentDetailsGarage.setArguments(carsArgs);
                ((MainActivity) getActivity()).changeFragmentBack(fragmentDetailsGarage);
                */
            }
        });

        return view;
    }


    private ArrayList<Car> loadData() {
        //ArrayList<Car> result = ((MainActivity) getActivity()).loadPref();

        ArrayList<Car> cars = Utils.loadData(getActivity());
        if (cars == null) {
            Toast.makeText(getActivity(), "No data", Toast.LENGTH_LONG).show();
        }
        return cars;
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
                myRow.tvModel = (TextView) convertView.findViewById(R.id.tvModel);
                myRow.tvCreate = (TextView) convertView.findViewById(R.id.tv_create);
                myRow.tvMT = (TextView) convertView.findViewById(R.id.tv_mt_header);
                myRow.tvAT = (TextView) convertView.findViewById(R.id.tv_at_header);
                myRow.tvAmountMT = (TextView) convertView.findViewById(R.id.tv_mt);
                myRow.tvAmountAT = (TextView) convertView.findViewById(R.id.tv_at);
                myRow.ivCarPic = (ImageView) convertView.findViewById(R.id.ivCarPic);
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
}
