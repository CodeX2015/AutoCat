package ru.app.autocat.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.app.autocat.Car;
import ru.app.autocat.R;

public class StickyGridAdapter extends BaseAdapter implements
		StickyGridHeadersSimpleAdapter {

	private ArrayList<Car> list;
	private LayoutInflater mInflater;
	private GridView mGridView;

	boolean isScrollStop = true;

	public StickyGridAdapter(Context context, ArrayList<Car> list,
							 GridView mGridView) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
		this.mGridView = mGridView;
		this.mGridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.v("onScrollStateChanged", "onScrollStateChanged");
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					isScrollStop = true;
					/** - - **/
					notifyDataSetChanged();
				} else {
					isScrollStop = false;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Log.v("onScroll", "onScroll");
			}
		});
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder;
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.grid_data_item, parent, false);
			mViewHolder.mImageView = (ImageView) convertView
					.findViewById(R.id.grid_item);
			mViewHolder.mTextView = (TextView) convertView
					.findViewById(R.id.data_item_text);
			convertView.setTag(mViewHolder);

		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}


		//Todo set data item
		mViewHolder.mTextView.setText(list.get(position).getMark());

		//String path = list.get(position).getPath();
		//disPlayImage("file://" + path, mViewHolder.mImageView);

		return convertView;
	}

	private void disPlayImage(String path, ImageView imageView) {
		Log.v("isScroll", isScrollStop + "");
		imageView.setTag(path);
		/**
		 * set i strange chines method
		 */
		imageView.setImageResource(R.drawable.car_example);
		//imageView.setBackgroundDrawable(new BitmapDrawable());

		if (isScrollStop) {
			//imageLoader.displayImage(path, imageView, options);
		}
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder mHeaderHolder;
		if (convertView == null) {
			mHeaderHolder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.grid_header, parent, false);
			mHeaderHolder.mHeaderTextView = (TextView) convertView
					.findViewById(R.id.grid_header);
			convertView.setTag(mHeaderHolder);
		} else {
			mHeaderHolder = (HeaderViewHolder) convertView.getTag();
		}

		mHeaderHolder.mHeaderTextView.setText(list.get(position).getMark());

		return convertView;
	}

	public static class ViewHolder {
		public ImageView mImageView;
		public TextView mTextView;
	}

	public static class HeaderViewHolder {
		public TextView mHeaderTextView;
	}

	@Override
	public long getHeaderId(int position) {
		return position;
	}

}
