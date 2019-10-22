package com.example.activitymusic.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitymusic.Adapter.NotificationAdapter;
import com.example.activitymusic.Model.Notification;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private RecyclerView mRecyclerViewNotification;
    private ArrayList<Notification> mListNotification;
    private NotificationAdapter mNotificationAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Toast.makeText(getContext(), "Notification", Toast.LENGTH_SHORT).show();
     View view = inflater.inflate(R.layout.notification_fragment, container, false);
        mRecyclerViewNotification= view.findViewById(R.id.recycler_view_notification);
        mListNotification =new ArrayList<>();
        mNotificationAdapter=new NotificationAdapter(mListNotification,getContext());
        GetData();
        return view;
    }

    private void GetData(){
        DataServer dataServer = APIServer.getServer();
        Call<List<Notification>> listCall= dataServer.getDataNotification();
        listCall.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
            ArrayList<Notification> notifications=(ArrayList<Notification>) response.body();
                mNotificationAdapter=new NotificationAdapter(notifications,getActivity());
                mRecyclerViewNotification.setAdapter(mNotificationAdapter);
                mRecyclerViewNotification.addItemDecoration(new DividerItemDecoration(mRecyclerViewNotification.getContext(), DividerItemDecoration.VERTICAL));
                mRecyclerViewNotification.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {

            }
        });

    }
}
