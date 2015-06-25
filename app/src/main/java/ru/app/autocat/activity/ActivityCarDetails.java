package ru.app.autocat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ru.app.autocat.Car;
import ru.app.autocat.MainActivity;
import ru.app.autocat.R;
import ru.app.autocat.Utils;

public class ActivityCarDetails extends AppCompatActivity {

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
    Button mSaveBtn;
    Car carDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.str_car_details));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mModel = (TextView) findViewById(R.id.tvModel);
        mCarPic = (ImageView) findViewById(R.id.ivCarPic);
        mDescription = (TextView) findViewById(R.id.tvDescripion);
        mCreated = (TextView) findViewById(R.id.tv_create);
        mSaveBtn = (Button) findViewById(R.id.btn_save_car);
        mSaveBtn.setVisibility(View.INVISIBLE);
        mMinusBtnMT = (Button) findViewById(R.id.btn_minus_mt);
        mPlusBtnMT = (Button) findViewById(R.id.btn_plus_mt);
        mAmountMT = (TextView) findViewById((R.id.tvAmount_mt));
        mKppMT = (TextView) findViewById(R.id.tv_mt);
        mMinusBtnAT = (Button) findViewById(R.id.btn_minus_at);
        mPlusBtnAT = (Button) findViewById(R.id.btn_plus_at);
        mAmountAT = (TextView) findViewById((R.id.tvAmount_at));
        mKppAT = (TextView) findViewById(R.id.tv_at);

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

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carDetails.setAmountKppMt(Integer.parseInt(mAmountMT.getText().toString()));
                carDetails.setAmountKppAt(Integer.parseInt(mAmountAT.getText().toString()));
                //MainActivity.savePref(carDetails);

                Utils.saveData(ActivityCarDetails.this, carDetails);

                Toast.makeText(ActivityCarDetails.this, "Save car in garage", Toast.LENGTH_LONG).show();
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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_car_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Car getData() {
        if (getIntent() != null) {
            Gson gson = new Gson();
            String json = getIntent().getExtras().getString("CarDetails");
            Car carDetails = gson.fromJson(json, new TypeToken<Car>() {
            }.getType());
            if (Utils.loadItem(ActivityCarDetails.this, carDetails) != null) {
                Utils.loadItem(ActivityCarDetails.this, carDetails);
            }
            return carDetails;
        } else {
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "BackArrowPressed", Toast.LENGTH_LONG).show();
        if (Integer.parseInt(mAmountMT.getText().toString()) > 0
                | Integer.parseInt(mAmountAT.getText().toString()) > 0) {
            saveData();
        } else {
            deleteItem();
        }
        //todo save data to preferenses

    }

    private void deleteItem() {
        Utils.deleteItem(ActivityCarDetails.this, carDetails);
    }

    private void saveData() {
        carDetails.setAmountKppMt(Integer.parseInt(mAmountMT.getText().toString()));
        carDetails.setAmountKppAt(Integer.parseInt(mAmountAT.getText().toString()));
        Utils.saveData(ActivityCarDetails.this, carDetails);
        Toast.makeText(ActivityCarDetails.this, "Save car in garage", Toast.LENGTH_LONG).show();
    }
}
