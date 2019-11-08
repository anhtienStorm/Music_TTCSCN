package com.example.activitymusic.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.activitymusic.Activity.MainActivityMusic;
import com.example.activitymusic.Adapter.BannerAdapter;
import com.example.activitymusic.Adapter.SearchOnlineAdapter;
import com.example.activitymusic.Model.Song;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;
import com.example.activitymusic.Server.interfaceRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.activitymusic.Adapter.SongListAdapter.unAccent;

public class HomeOnlineFragment extends Fragment {
    SwipeRefreshLayout mSwipeRefreshLayout;
    BannerFragment bannerFragment;
    SearchOnlineFragment mSearchOnlineFragment;
    connectRefreshLayout connectRefreshLayout;
    ProgressBar mProgressBar;
    PlayListFragment playListFragment;
    ListSongPlayingOnline mListSongPlayingOnline,mListSongPlaySuggestion;
    ListSongTop10 mListSongTop10;
    ArrayList<SongOnline> mSongOnlineList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_online_fragment, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        getActivity().findViewById(R.id.layoutPlayMusic).setVisibility(View.VISIBLE);
        ((MainActivityMusic) getActivity()).update();

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefreshLayout);
        mProgressBar = view.findViewById(R.id.ProgressBar);
        bannerFragment = new BannerFragment();
        playListFragment = new PlayListFragment();
        mListSongPlayingOnline =new ListSongPlayingOnline("Danh sách phát" , "view");
        mListSongPlaySuggestion=new ListSongPlayingOnline("Gợi Ý", "view");
        mListSongTop10 =new ListSongTop10();
        getActivity().getSupportFragmentManager().beginTransaction() .replace(R.id.banner_fragment, bannerFragment).commit();
        getActivity().getSupportFragmentManager().beginTransaction() .replace(R.id.play_list_fragment, playListFragment).commit();
        getActivity().getSupportFragmentManager().beginTransaction() .replace(R.id.playing_list_fragment, mListSongPlayingOnline).commit();
        getActivity().getSupportFragmentManager().beginTransaction() .replace(R.id.top_10_song_fragment, mListSongTop10).commit();
        getActivity().getSupportFragmentManager().beginTransaction() .replace(R.id.goi_y_song_fragment, mListSongPlaySuggestion).commit();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bannerFragment = new BannerFragment();
                playListFragment = new PlayListFragment();
                mListSongPlayingOnline =new ListSongPlayingOnline("Danh sách phát", "view");
                mListSongPlaySuggestion=new ListSongPlayingOnline("Gợi Ý", "view");
                mListSongTop10 =new ListSongTop10();
                getActivity().getSupportFragmentManager().beginTransaction() .replace(R.id.banner_fragment, bannerFragment).commit();
                getActivity().getSupportFragmentManager().beginTransaction() .replace(R.id.play_list_fragment, playListFragment).commit();
                getActivity().getSupportFragmentManager().beginTransaction() .replace(R.id.playing_list_fragment, mListSongPlayingOnline).commit();
                getActivity().getSupportFragmentManager().beginTransaction() .replace(R.id.top_10_song_fragment, mListSongTop10).commit();
                getActivity().getSupportFragmentManager().beginTransaction() .replace(R.id.goi_y_song_fragment, mListSongPlaySuggestion).commit();

                startRefreshing();
            }
        });
        startRefreshing();

        return view;
    }

    void startRefreshing(){
        bannerFragment.setmRefreshLayout(new interfaceRefreshLayout() {
            @Override
            public void refreshLayout() {


            }
        });
        playListFragment.setmRefreshLayout(new interfaceRefreshLayout() {
            @Override
            public void refreshLayout() {
                mProgressBar.setVisibility(View.GONE);
                stopRefreshing();
            }
        });

    }

    void stopRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void setConnectRefreshLayout(HomeOnlineFragment.connectRefreshLayout connectRefreshLayout) {
        this.connectRefreshLayout = connectRefreshLayout;
    }

    interface connectRefreshLayout {
        void refreshLayout();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                mSearchOnlineFragment = new SearchOnlineFragment();
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.search_online_view, mSearchOnlineFragment).commit();
                getActivity().findViewById(R.id.home_online_view).setVisibility(View.GONE);
                getActivity().findViewById(R.id.layoutPlayMusic).setVisibility(View.GONE);
//                getActivity().findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.search_online_view).setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                getFragmentManager().popBackStack();
                Log.d("tiennab", "onMenuItemActionCollapse: ");
                getActivity().findViewById(R.id.empty_view).setVisibility(View.GONE);
                getActivity().findViewById(R.id.layoutPlayMusic).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.home_online_view).setVisibility(View.VISIBLE);
                return true;
            }
        });

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {

            ArrayList<SongOnline> searchSongList = new ArrayList<>();

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                getData();

                ArrayList<SongOnline> list = new ArrayList<>();
                if (s == null || s.length() == 0) {
//                    getActivity().findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                    if (getActivity().findViewById(R.id.search_online_view) != null){
                        getActivity().findViewById(R.id.search_online_view).setVisibility(View.GONE);
                    }
                } else {
                    getActivity().findViewById(R.id.search_online_view).setVisibility(View.VISIBLE);
//                    getActivity().findViewById(R.id.empty_view).setVisibility(View.GONE);
                    String s1 = unAccent(s.toLowerCase().trim());

                    for (SongOnline song : mSongOnlineList) {
                        if (unAccent(song.getNAMESONG().toLowerCase()).contains(s1)) {
                            list.add(song);
                        }
                    }
                    searchSongList.clear();
                    searchSongList.addAll(list);
                    mSearchOnlineFragment.mSearchOnlineAdapter.updateSearchOnlineList(searchSongList);
                }
                return false;
            }
        });

    }

    private void getData() {
        DataServer dataServer = APIServer.getServer();
        Call<List<SongOnline>> callback = dataServer.getDataSongOnline();
        callback.enqueue(new Callback<List<SongOnline>>() {
            @Override
            public void onResponse(Call<List<SongOnline>> call, Response<List<SongOnline>> response) {
                mSongOnlineList = (ArrayList<SongOnline>) response.body();
            }

            @Override
            public void onFailure(Call<List<SongOnline>> call, Throwable t) {

            }
        });
    }
}
