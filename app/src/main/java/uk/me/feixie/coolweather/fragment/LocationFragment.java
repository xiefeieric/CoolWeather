package uk.me.feixie.coolweather.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {

    private RecyclerView rvLocation;
    private List<City> mCityList;


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

    private void initData() {
        CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(getActivity());
        mCityList = coolWeatherDB.queryAllCity();
    }

    private void initViews(View view) {
        rvLocation = (RecyclerView) view.findViewById(R.id.rvLocation);
        rvLocation.setLayoutManager(new LinearLayoutManager(getActivity()));
        LocationAdapter adapter = new LocationAdapter();
        rvLocation.setAdapter(adapter);
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
            ivLocationDelete = (ImageView) itemView.findViewById(R.id.ivLocationDelete);

            llLocation = (LinearLayout) itemView.findViewById(R.id.llLocation);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            llLocation.setLayoutParams(params);
        }
    }
}
