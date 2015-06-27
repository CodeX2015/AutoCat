package ru.app.autocat.fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.WeakHashMap;

import ru.app.autocat.Car;
import ru.app.autocat.R;
import ru.app.autocat.Utils;
import ru.app.autocat.activity.ActivityCarDetails;
import ru.app.autocat.adapters.StickyListHeaderAdapter;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by CodeX on 18.06.2015.
 */
public class FragmentGarageListGroup extends Fragment {
    private ArrayList<Car> cars;
    private ExpandableStickyListHeadersListView mListView;
    StickyListHeaderAdapter mStickyListHeaderAdapter;
    WeakHashMap<View, Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview_sticky, container, false);
        mListView = (ExpandableStickyListHeadersListView) view.findViewById(R.id.list);
        mListView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (mListView.isHeaderCollapsed(headerId)) {
                    //mListView.expand(headerId);
                } else {
                    //mListView.collapse(headerId);
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Gson gson = new Gson();
                String json = gson.toJson(cars.get(position));

                Intent myIntent = new Intent(getActivity(), ActivityCarDetails.class);
                myIntent.putExtra("CarDetails", json);
                getActivity().startActivityForResult(myIntent, 1);

                /**
                 Bundle carsArgs = new Bundle();
                 carsArgs.putString("CarDetails", json);
                 FragmentDetailsGarage fragmentDetailsGarage = new FragmentDetailsGarage();
                 fragmentDetailsGarage.setArguments(carsArgs);
                 ((MainActivity) getActivity()).changeFragmentBack(fragmentDetailsGarage);
                 */
            }
        });
        loadData();
        return view;
    }


    //animation executor
    class AnimationExecutor implements ExpandableStickyListHeadersListView.IAnimationExecutor {

        @Override
        public void executeAnim(final View target, final int animType) {
            if (ExpandableStickyListHeadersListView.ANIMATION_EXPAND == animType && target.getVisibility() == View.VISIBLE) {
                return;
            }
            if (ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE == animType && target.getVisibility() != View.VISIBLE) {
                return;
            }
            if (mOriginalViewHeightPool.get(target) == null) {
                mOriginalViewHeightPool.put(target, target.getHeight());
            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofFloat(animStartY, animEndY);
            animator.setDuration(200);
            target.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
                        target.setVisibility(View.VISIBLE);
                    } else {
                        target.setVisibility(View.GONE);
                    }
                    target.getLayoutParams().height = viewHeight;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    lp.height = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    target.setLayoutParams(lp);
                    target.requestLayout();
                }
            });
            animator.start();

        }
    }

    private void loadData() {
        Utils.loadData(new Utils.LoadListener() {
            @Override
            public void OnLoadComplete(Object result) {
                cars = (ArrayList<Car>) result;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter();
                    }
                });
            }

            @Override
            public void OnLoadError(final String error) {
                Log.d("FGLG_loadData ERR", error);
            }
        }, getActivity());
    }

    private void setAdapter() {
        mListView.setAnimExecutor(new AnimationExecutor());
        mStickyListHeaderAdapter = new StickyListHeaderAdapter(getActivity(), cars);
        mListView.setAdapter(mStickyListHeaderAdapter);
    }

}
