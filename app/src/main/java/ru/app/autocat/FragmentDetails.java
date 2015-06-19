package ru.app.autocat;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by CodeX on 18.06.2015.
 */
public class FragmentDetails extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        Button mMinusBtn = (Button) view.findViewById(R.id.btn_minus);
        Button mPlusBtn = (Button) view.findViewById(R.id.btn_plus);

        mMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).savePref();
                Toast.makeText(getActivity(), "Save in garage", Toast.LENGTH_LONG).show();
            }
        });

        mPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadPref();
                Toast.makeText(getActivity(),
                        String.valueOf(((MainActivity) getActivity()).loadPref().size()), Toast.LENGTH_LONG).show();
            }
        });



        return view;
    }
}
