package com.example.activitymusic.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitymusic.Activity.MainActivityMusic;
import com.example.activitymusic.Adapter.SearchOnlineAdapter;
import com.example.activitymusic.R;

public class SearchOnlineFragment extends Fragment implements SearchOnlineAdapter.ISearchOnlineAdapterClickListener {

    RecyclerView mRecyclerViewSearchOnline;
    public SearchOnlineAdapter mSearchOnlineAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_online_fragment, container,false);

        mRecyclerViewSearchOnline = view.findViewById(R.id.list_search_onlilne);
        mSearchOnlineAdapter = new SearchOnlineAdapter(getContext());
        mRecyclerViewSearchOnline.setAdapter(mSearchOnlineAdapter);
        mRecyclerViewSearchOnline.addItemDecoration(new DividerItemDecoration(mRecyclerViewSearchOnline.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerViewSearchOnline.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchOnlineAdapter.setOnClickListenner(this);

        return view;
    }

    @Override
    public void onItemClick(int position) {
        ((MainActivityMusic)getActivity()).mMediaPlaybackService.playSongOnline(mSearchOnlineAdapter.mSearchOnlineList.get(position), mSearchOnlineAdapter.mSearchOnlineList);
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.sub_fragment_a, ((MainActivityMusic)getActivity()).mMediaPlaybackFragment).commit();
    }
}
