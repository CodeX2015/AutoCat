package ru.app.autocat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by CodeX on 18.06.2015.
 */

public class FragmentCatalogGrid extends Fragment {

    private GridView mGridView;
    ArrayAdapter<String> adapter;
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gridview, container, false);
        Button btnParse = (Button) view.findViewById(R.id.btnParse);
        mGridView = (GridView) view.findViewById(R.id.gvMain);
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.row_gridview, R.id.tvText, MainActivity.DATA);
        mGridView.setAdapter(adapter);
        adjustGridView();


        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parseXML();
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentDetails fragmentDetails = new
                                FragmentDetails();
                        ((MainActivity) getActivity())
                                .changeFragmentBack(fragmentDetails);
                    }
                });
            }
        });

        return view;
    }

    private void parseXML() {
        XmlParser.parseXML(new XmlParser.LoadListener() {
            @Override
            public void OnParseComplete(Object result) {
                Toast.makeText(getActivity(), "OnParseComplete", Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnParseError(Exception error) {
                Toast.makeText(getActivity(), "OnParseError", Toast.LENGTH_LONG).show();
            }
        }, getActivity().getResources().getXml(R.xml.test));
    }


    private void adjustGridView() {
        mGridView.setNumColumns(GridView.AUTO_FIT);
    }

}
