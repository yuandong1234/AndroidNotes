package com.yuong.notes;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.loadmore.RecyclerViewPageHelper;
import com.app.loadmore.WapRecyclerViewAdapter;
import com.app.loadmore.WarpRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LoadMoreActivity extends AppCompatActivity implements WapRecyclerViewAdapter.LoadMoreListener, View.OnClickListener {
    private static final String TAG = LoadMoreActivity.class.getSimpleName();
    private WarpRecyclerView recyclerView;
    private WapRecyclerViewAdapter wapRecyclerViewAdapter;
    private MyAdapter adapter;
    private RecyclerViewPageHelper pageHelper;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_more);
        initView();
        initData(page);
    }

    private void initView() {
        findViewById(R.id.btn).setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MyAdapter(this);

        wapRecyclerViewAdapter = new WapRecyclerViewAdapter(adapter);
        wapRecyclerViewAdapter.setLoadMoreListener(recyclerView, this);
        pageHelper = new RecyclerViewPageHelper(this, wapRecyclerViewAdapter);

        recyclerView.setAdapter(wapRecyclerViewAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mDataList = new ArrayList<>();
        private Context mContext;
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            this.mContext = context;
            this.inflater = LayoutInflater.from(context);
        }

        public void setData(List<String> data) {
            if (data != null) {
                mDataList = data;
                notifyDataSetChanged();
            }
        }

        public void addData(List<String> data) {
            if (data != null && data.size() > 0) {
                mDataList.addAll(data);
                // notifyDataSetChanged();
                notifyItemRangeInserted(mDataList.size(), data.size());
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = inflater.inflate(R.layout.item_list_load_more, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            viewHolder.textView.setText(mDataList.get(position));
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.text);
            }
        }


    }

    private void initData(int page) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String temp = page + "-" + i;
            data.add(temp);
        }
        if (page == 0) {
            setData(data);
        } else {
            setMoreData(data);
        }

        Log.e(TAG, "size : " + wapRecyclerViewAdapter.getItemCount());
    }

    private void setData(List<String> data) {
        adapter.setData(data);
        pageHelper.setLoadMoreEnable(true);
    }

    private void setMoreData(List<String> data) {
        adapter.addData(data);
    }

    @Override
    public void loadMore() {
        Log.e(TAG, "loadMore......................");
        if (page > 2) return;
        page++;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData(page);
            }
        }, 1000);
    }


}
