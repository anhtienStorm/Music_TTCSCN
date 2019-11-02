package com.example.activitymusic.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.activitymusic.Activity.MainActivityMusic;
import com.example.activitymusic.Adapter.ListPlayListAdapter;
import com.example.activitymusic.Adapter.SongOnlineListAdapter;
import com.example.activitymusic.Model.PlayList;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListPlayListFragment extends Fragment {
    Button mCreatPlayList;
    RecyclerView mRecyclerViewListPlayList;
    ListPlayListAdapter mPlayListAdapter;
    ListSongPlayListFragment listSongPlayListFragment;
    ProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String mStatus;

    public ListPlayListFragment(String status) {
        mStatus = status;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_playlist_fragment, container, false);
        mCreatPlayList = view.findViewById(R.id.CreatePlayList);
        mRecyclerViewListPlayList = view.findViewById(R.id.RecyclerViewListPlayList);
        mProgressBar=view.findViewById(R.id.ProgressBarListPlayList);
        mSwipeRefreshLayout=view.findViewById(R.id.SwipeRefreshLayoutListPlayList);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        getData();
        mCreatPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Create", Toast.LENGTH_SHORT).show();
                final Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("Hot thoai");
                dialog.setContentView(R.layout.diglog_create_playlist);
                final EditText inputNamePlayList = dialog.findViewById(R.id.inputNamePlayList);

                Button exit = dialog.findViewById(R.id.btexit);
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                Button create = dialog.findViewById(R.id.btaction);
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Edit", "onClick: " + inputNamePlayList.getText().toString());
                        onCreatePlayList(2, inputNamePlayList.getText().toString());
                        dialog.cancel();
                    }
                });
                dialog.show();

            }
        });


        return view;
    }

    void getData() {
        DataServer dataServer = APIServer.getServer();
        Call<List<PlayList>> callback = dataServer.getDataPlayList();
        callback.enqueue(new Callback<List<PlayList>>() {
            @Override
            public void onResponse(Call<List<PlayList>> call, Response<List<PlayList>> response) {
                final ArrayList<PlayList> lists = (ArrayList<PlayList>) response.body();
                mPlayListAdapter = new ListPlayListAdapter(getActivity(), lists , mStatus);
                mRecyclerViewListPlayList.setAdapter(mPlayListAdapter);
                mRecyclerViewListPlayList.setLayoutManager(new LinearLayoutManager(getContext()));
                mPlayListAdapter.setOnShowPlayList(new ListPlayListAdapter.onShowPlayList() {
                    @Override
                    public void onShow(PlayList playListCurrent) {
                        listSongPlayListFragment = new ListSongPlayListFragment();
                        listSongPlayListFragment.setmPlayList(playListCurrent);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment_a, listSongPlayListFragment).commit();
                    }
                });
                   mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<PlayList>> call, Throwable t) {
                Toast.makeText(getActivity(), "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void onCreatePlayList(int ID_SONG, String NAME_PLAYLIST) {
        DataServer dataServer = APIServer.getServer();
        Call<String> callback = dataServer.InsertPlayList(ID_SONG, NAME_PLAYLIST);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                if (result.equals("true")) {
                    Toast.makeText(getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.VISIBLE);
                    getData();
                } else
                    Toast.makeText(getContext(), "Thất bại", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
