package ru.app.autocat.fragments;

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
import ru.app.autocat.adapters.SeparatedListAdapter;
import ru.app.autocat.helpers.XmlParserHelper;

/**
 * Created by Yakovlev on 19.06.2015.
 */
public class FragmentCatalogList extends Fragment {

    ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);
        mListView = (ListView) view.findViewById(R.id.lvMain);
        parseXML();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentDetails fragmentDetails = new FragmentDetails();
                Bundle carsArgs = new Bundle();
                Gson gson = new Gson();
                String json = gson.toJson(((MyListAdapter) parent.getAdapter()).getItem(position));
                carsArgs.putString("CarDetails", json);
                fragmentDetails.setArguments(carsArgs);
                ((MainActivity) getActivity()).changeFragmentBack(fragmentDetails);
            }
        });

        return view;
    }

    private void parseXML() {
        XmlParserHelper.parseXMLbyStack(new XmlParserHelper.LoadListener() {
            @Override
            public void OnParseComplete(final Object result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SeparatedListAdapter adapter = new SeparatedListAdapter(getActivity());
                        adapter.addSection("All", new MyListAdapter((ArrayList<Car>) result));


                        mListView.setAdapter(adapter);

                    }
                });
            }

            @Override
            public void OnParseError(final Exception error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "OnParseError: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }, getActivity().getResources().getXml(R.xml.test));
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
                myRow.tvMT = (TextView) convertView.findViewById(R.id.tv_mt);
                myRow.tvAT = (TextView) convertView.findViewById(R.id.tv_at);
                myRow.ivCarPic = (ImageView) convertView.findViewById(R.id.ivCarPic);
                convertView.setTag(myRow);
            } else {
                myRow = (MyRow) convertView.getTag();
            }
            myRow.tvModel.setText(getItem(position).getModel());
            myRow.tvCreate.setText(getItem(position).getCreated());
            //myRow.tvMT.setText(getItem(position).getMT);
            //myRow.tvAT..setText(getItem(position).getAT);
            return convertView;
        }

        private class MyRow {
            TextView tvModel;
            TextView tvCreate;
            TextView tvMT;
            TextView tvAT;
            ImageView ivCarPic;
        }
    }
}
