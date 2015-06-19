package ru.app.autocat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by CodeX on 18.06.2015.
 */
public class FragmentDetails extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        Gson gson = new Gson();
        String json = getArguments().getString("CarDetails");
        final Car carDetails = gson.fromJson(json, new TypeToken<Car>() {
        }.getType());


        Button mMinusBtn = (Button) view.findViewById(R.id.btn_minus);
        Button mPlusBtn = (Button) view.findViewById(R.id.btn_plus);
        Button mSaveBtn = (Button) view.findViewById(R.id.btn_save_car);

        mMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        ((MainActivity) getActivity()).savePref(carDetails);
                Toast.makeText(getActivity(), "Save in garage", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
