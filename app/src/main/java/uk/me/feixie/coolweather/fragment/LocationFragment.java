package uk.me.feixie.coolweather.fragment;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import uk.me.feixie.coolweather.R;
import uk.me.feixie.coolweather.db.CoolWeatherDB;
import uk.me.feixie.coolweather.model.City;
import uk.me.feixie.coolweather.util.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {

    private static int clickPosition;

    private RecyclerView rvLocation;
    private List<City> mCityList;
    private LocationAdapter mAdapter;
    private SharedPreferences mSharedPreferences;


    public LocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        initData();
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initData() {
        CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(getActivity());
        mCityList = coolWeatherDB.queryAllCity();
    }

    private void initViews(View view) {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        rvLocation = (RecyclerView) view.findViewById(R.id.rvLocation);
        rvLocation.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new LocationAdapter();
        rvLocation.setAdapter(mAdapter);
    }


    public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> {

        @Override
        public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_rv_location,null);
            LocationViewHolder viewHolder = new LocationViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(LocationViewHolder holder, int position) {

            String location_click_position = mSharedPreferences.getString("location_click_position", "");

            if (position == 0) {
                holder.tvLocationName.setText(mCityList.get(position).getName());
//                holder.ivLocationDelete.setVisibility(View.GONE);
                holder.ivLocationDelete.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_location_white_24dp));
            }

            if (clickPosition == position) {
                holder.ivLocationRadio.setImageResource(R.drawable.ic_radio_button_checked_white_24dp);
                holder.llLocation.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.llLocation.setElevation(30f);
                }
            } else {
                holder.ivLocationRadio.setImageResource(R.drawable.ic_radio_button_unchecked_white_24dp);
                holder.llLocation.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }

            if (!TextUtils.isEmpty(location_click_position)) {
                if (position == Integer.parseInt(location_click_position)) {
                    holder.ivLocationRadio.setImageResource(R.drawable.ic_radio_button_checked_white_24dp);
                    holder.llLocation.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.llLocation.setElevation(30f);
                    }
                } else {
                    holder.ivLocationRadio.setImageResource(R.drawable.ic_radio_button_unchecked_white_24dp);
                    holder.llLocation.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
            }

            holder.tvLocationName.setText(mCityList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            if (mCityList!=null) {
                return mCityList.size();
            }
            return 0;
        }
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivLocationRadio;
        public TextView tvLocationName;
        public ImageView ivLocationDelete;
        public LinearLayout llLocation;

        public LocationViewHolder(View itemView) {
            super(itemView);

            ivLocationRadio = (ImageView) itemView.findViewById(R.id.ivLocationRadio);

            tvLocationName = (TextView) itemView.findViewById(R.id.tvLocationName);
            tvLocationName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    System.out.println(getAdapterPosition());
                    clickPosition = getAdapterPosition();
//                    System.out.println(clickPosition);
                    mAdapter.notifyDataSetChanged();
                    String name = mCityList.get(clickPosition).getName();

                    //delete all space
//                    name = name.replaceAll("\\s+","");

//                    mSharedPreferences.edit().putString("current_city",name).commit();
                    if (clickPosition == 0) {
                        mSharedPreferences.edit().putString("select_city","").apply();
                    } else {
                        mSharedPreferences.edit().putString("select_city",name).apply();
                    }

                    mSharedPreferences.edit().putString("location_click_position", String.valueOf(clickPosition)).apply();
                }
            });

            ivLocationDelete = (ImageView) itemView.findViewById(R.id.ivLocationDelete);
            ivLocationDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getAdapterPosition()!=0) {
                        if (getAdapterPosition()!=clickPosition) {
                            final City city = mCityList.get(getAdapterPosition());
                            mCityList.remove(city);
                            mAdapter.notifyItemRemoved(getAdapterPosition());

                            new Thread(){
                                @Override
                                public void run() {
                                    CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(getContext());
                                    coolWeatherDB.deleteCity(city);
                                }
                            }.start();
                        } else {
                            UIUtils.showToast(getContext(),"Selected city can not be deleted!");
                        }
                    }
                }
            });

            llLocation = (LinearLayout) itemView.findViewById(R.id.llLocation);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            llLocation.setLayoutParams(params);
        }
    }
}
