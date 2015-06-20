package ru.app.autocat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by CodeX on 18.06.2015.
 */

public class FragmentCatalogGrid extends Fragment {

    private GridView mGridView;
    //ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gridview, container, false);
        Button btnParse = (Button) view.findViewById(R.id.btnParse);
        mGridView = (GridView) view.findViewById(R.id.gvMain);
        //adapter = new ArrayAdapter<String>(getActivity(), R.layout.row_gridview, R.id.tvText, MainActivity.DATA);
        //mGridView.setAdapter(adapter);
        adjustGridView();
        parseXML();

        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), ListSample.class);
                getActivity().startActivity(myIntent);
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
                        String json = gson.toJson(((MyListAdapter) parent.getAdapter()).getItem(position));
                        carsArgs.putString("CarDetails", json);
                        fragmentDetails.setArguments(carsArgs);
                        ((MainActivity) getActivity()).changeFragmentBack(fragmentDetails);
                    }
                });
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
                        /**
                        if (getArguments() != null && getArguments().getString("FilterPattern","")!="") {
                            mGridView.setAdapter(new MyListAdapter(((MainActivity) getActivity())
                                    .getFilteredData(getArguments().getString("FilterPattern", ""), (ArrayList<Car>) result)));
                        } else {
                            Toast.makeText(getActivity(),
                                    "OnParseComplete, cars = " + String.valueOf(((ArrayList<Car>) result).size()),
                                    Toast.LENGTH_LONG).show();
                            mGridView.setAdapter(new MyListAdapter((ArrayList<Car>) result));
                        }
                        */

                        mGridView.setAdapter(new MyListAdapter(((MainActivity) getActivity()).getCarsDBG()));
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
