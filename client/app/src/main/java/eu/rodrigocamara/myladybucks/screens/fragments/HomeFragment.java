package eu.rodrigocamara.myladybucks.screens.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.rodrigocamara.myladybucks.R;
import eu.rodrigocamara.myladybucks.adapters.AnnouncementsAdapter;
import eu.rodrigocamara.myladybucks.adapters.OrderAdapter;
import eu.rodrigocamara.myladybucks.adapters.UserOrdersAdapter;
import eu.rodrigocamara.myladybucks.listeners.ClickListeners;
import eu.rodrigocamara.myladybucks.pojos.Announcement;
import eu.rodrigocamara.myladybucks.pojos.Order;
import eu.rodrigocamara.myladybucks.utils.C;
import eu.rodrigocamara.myladybucks.utils.FirebaseHelper;
import eu.rodrigocamara.myladybucks.utils.FragmentHelper;

/**
 * Created by rodri on 30/12/2017.
 */

public class HomeFragment extends Fragment {
    @BindView(R.id.rv_ads)
    RecyclerView mAnnouncementRecyclerView;
    @BindView(R.id.rv_orders)
    RecyclerView mRvOrders;

    @BindView(R.id.fab_home_menu)
    FloatingActionButton mFabMenu;

    private UserOrdersAdapter mOrderAdapter;
    private List<Order> mOrderList;

    private AnnouncementsAdapter mAnnouncementAdapter;
    private List<Announcement> mAnnouncementList;

    private DatabaseReference mDatabaseReference;
    private ChildEventListener mMenuEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,
                container, false);
        ButterKnife.bind(this, view);

        mAnnouncementList = new ArrayList<>();
        mAnnouncementAdapter = new AnnouncementsAdapter(view.getContext(), mAnnouncementList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        mAnnouncementRecyclerView.setLayoutManager(mLayoutManager);
        mAnnouncementRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAnnouncementRecyclerView.setAdapter(mAnnouncementAdapter);

        mFabMenu.setOnClickListener(ClickListeners.goToMenuListener(getContext()));
        mockAds();
        loadOrders();
        FragmentHelper.updateDrawerMenu(this.getActivity(), R.id.action_home);

        return view;
    }

    private void mockAds() {
        int[] images = new int[]{R.drawable.promotion1, R.drawable.promotion2,
                R.drawable.promotion3};

        Announcement announcement = new Announcement("globo.com", images[0]);
        mAnnouncementList.add(announcement);

        announcement = new Announcement("uol.com", images[1]);
        mAnnouncementList.add(announcement);

        announcement = new Announcement("google.com", images[2]);
        mAnnouncementList.add(announcement);

        mAnnouncementAdapter.notifyDataSetChanged();
    }

    private void loadOrders() {

        mOrderList = new ArrayList<>();

        mDatabaseReference = FirebaseHelper.getDatabase().getReference(C.DB_ORDERS_REFERENCE).child(FirebaseAuth.getInstance().getUid());
        mMenuEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                refreshListValues(dataSnapshot.getValue(Order.class), false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                refreshListValues(dataSnapshot.getValue(Order.class), true);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                refreshListValues(dataSnapshot.getValue(Order.class), true);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                refreshListValues(dataSnapshot.getValue(Order.class), true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException();
            }
        };
        mDatabaseReference.addChildEventListener(mMenuEventListener);
        mOrderAdapter = new UserOrdersAdapter(mOrderList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRvOrders.setLayoutManager(mLayoutManager);
        mRvOrders.setItemAnimator(new DefaultItemAnimator());
        mRvOrders.setAdapter(mOrderAdapter);
    }

    private void refreshListValues(Order value, boolean needsClear) {
        if (needsClear) {
            mOrderList.clear();
        }
        mOrderList.add(value);
        mOrderAdapter.notifyDataSetChanged();
    }
}

