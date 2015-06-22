package ru.app.autocat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ru.app.autocat.Car;
import ru.app.autocat.MainActivity;
import ru.app.autocat.R;

/**
 * Created by CodeX on 18.06.2015.
 */
public class FragmentDetailsGarage extends Fragment {

    Button mMinusBtnMT;
    Button mPlusBtnMT;
    TextView mAmountMT;
    TextView mKppMT;
    Button mMinusBtnAT;
    Button mPlusBtnAT;
    TextView mAmountAT;
    TextView mKppAT;
    TextView mModel;
    TextView mDescription;
    TextView mCreated;
    ImageView mCarPic;
    Button mRemoveBtn;
    Car carDetails;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_garage, container, false);

        mModel = (TextView) view.findViewById(R.id.tvModel);
        mCarPic = (ImageView) view.findViewById(R.id.ivCarPic);
        mDescription = (TextView) view.findViewById(R.id.tvDescripion);
        mCreated = (TextView) view.findViewById(R.id.tv_create);
        mRemoveBtn = (Button) view.findViewById(R.id.btn_remove_car);
        mMinusBtnMT = (Button) view.findViewById(R.id.btn_minus_mt);
        mPlusBtnMT = (Button) view.findViewById(R.id.btn_plus_mt);
        mAmountMT = (TextView) view.findViewById((R.id.tvAmount_mt));
        mKppMT = (TextView) view.findViewById(R.id.tv_mt);
        mMinusBtnAT = (Button) view.findViewById(R.id.btn_minus_at);
        mPlusBtnAT = (Button) view.findViewById(R.id.btn_plus_at);
        mAmountAT = (TextView) view.findViewById((R.id.tvAmount_at));
        mKppAT = (TextView) view.findViewById(R.id.tv_at);

        carDetails = getData();

        mMinusBtnMT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.valueOf((String) mAmountMT.getText()) > 0) {
                    mAmountMT.setText(String.valueOf(Integer.valueOf((String) mAmountMT.getText()) - 1));
                }
            }
        });

        mPlusBtnMT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAmountMT.setText(String.valueOf(Integer.valueOf((String) mAmountMT.getText()) + 1));
            }
        });

        mMinusBtnAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.valueOf((String) mAmountAT.getText()) > 0) {
                    mAmountAT.setText(String.valueOf(Integer.valueOf((String) mAmountAT.getText()) - 1));
                }
            }
        });

        mPlusBtnAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAmountAT.setText(String.valueOf(Integer.valueOf((String) mAmountAT.getText()) + 1));
            }
        });

        mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) getActivity()).deleteSelectPref(carDetails);
                Toast.makeText(getActivity(), "Delete car from garage", Toast.LENGTH_LONG).show();
            }
        });
        if (carDetails != null) {
            mModel.setText(carDetails.getModel());
            mDescription.setText(carDetails.getTitle());
            mCreated.setText(carDetails.getCreated());
            mKppMT.setText(carDetails.getKppMT());
            mKppAT.setText(carDetails.getKppAT());
            mAmountMT.setText(String.valueOf(carDetails.getAmountKppMt()));
            mAmountAT.setText(String.valueOf(carDetails.getAmountKppAt()));
        }

        return view;
    }

    private Car getData() {
        if (getArguments() != null) {
            Gson gson = new Gson();
            String json = getArguments().getString("CarDetails");
            return gson.fromJson(json, new TypeToken<Car>() {
            }.getType());
        } else {
            return null;
        }
    }
}
