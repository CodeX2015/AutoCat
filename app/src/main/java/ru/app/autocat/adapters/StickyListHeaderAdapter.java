package ru.app.autocat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

import ru.app.autocat.Car;
import ru.app.autocat.R;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class StickyListHeaderAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    private final Context mContext;
    private ArrayList<Car> mCars;
    private int[] mSectionIndices;
    private String[] mSectionNames;
    private LayoutInflater mInflater;
    LinkedHashMap<String, String> mSections;

    public StickyListHeaderAdapter(Context context, ArrayList<Car> cars) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCars = cars;
        MarkComparator markComparator = new MarkComparator();
        Collections.sort(cars, markComparator);
        mSections = findSections();
        mSectionIndices = getSectionIndices();
        mSectionNames = getSectionNames();
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        String lastFirstMark = mCars.get(0).getMark();
        sectionIndices.add(0);
        for (int i = 1; i < mCars.size(); i++) {
            if (mCars.get(i).getMark() != lastFirstMark) {
                lastFirstMark = mCars.get(i).getMark();
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        String[] sectionsStr = mSections.values().toArray(new String[mSections.size() - 1]);
        int[] numbers = new int[sectionsStr.length];
        for(int i = 0;i < sectionsStr.length;i++)
        {
            // Note that this is assuming valid input
            // If you want to check then add a try/catch
            // and another index for the numbers if to continue adding the others
            numbers[i] = Integer.parseInt(sectionsStr[i]);
        }
        return numbers;
    }


    private LinkedHashMap<String, String> findSections() {
        mSections = new LinkedHashMap<String, String>();
        int n = mCars.size();
        int nSections = 0;
        for (int i = 0; i < n; i++) {
            String sectionName = mCars.get(i).getMark();

            if (!mSections.containsKey(sectionName)) {
                mSections.put(sectionName, String.valueOf(nSections));
                nSections++;
            }
        }

        //return mSections.keySet().toArray(new String[mSections.size()]);
        return mSections;
    }

    private String[] getSectionNames() {
        String[] letters = new String[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            letters[i] = mCars.get(mSectionIndices[i]).getMark();
        }
        return mSections.keySet().toArray(new String[mSections.size()-1]);
    }

    @Override
    public int getCount() {
        return mCars.size();
        //return mCountries.length;
    }

    @Override
    public Car getItem(int position) {
        return mCars.get(position);
        //return mCountries[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyRow myRow;
        if (convertView == null) {
            myRow = new MyRow();
            convertView = mInflater.inflate(R.layout.row_listview, parent, false);
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

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.list_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.list_header_title);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        //CharSequence headerChar = mCountries[position].subSequence(0, 1);
        String headerString = mCars.get(position).getMark();
        holder.text.setText(headerString);

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        return mCars.get(position).getId().charAt(0);
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionNames;
    }

    public void clear() {
        mSectionIndices = new int[0];
        mSectionNames = new String[0];
        notifyDataSetChanged();
    }

    public void restore() {
        mSectionIndices = getSectionIndices();
        mSectionNames = getSectionNames();
        notifyDataSetChanged();
    }

    class HeaderViewHolder {
        TextView text;
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

    private class MarkComparator implements Comparator<Car> {
        @Override
        public int compare(Car car1, Car car2) {
            return car1.getMark().compareTo(car2.getMark());
        }
    }

}
