package ru.app.autocat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Yakovlev on 19.06.2015.
 */
public class FragmentCatalogList extends Fragment {

    HeaderListView mListview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);
        mListview = (HeaderListView) view.findViewById(R.id.lvMain);
        //mListview.setAdapter(SectionAdapter adapter)

        return view;
    }
}
