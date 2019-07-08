package com.yuong.notes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.loadmore.WapRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class LoadMoreActivity extends AppCompatActivity implements WapRecyclerViewAdapter.LoadMoreListener {
    private static final String TAG = LoadMoreActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private WapRecyclerViewAdapter wapRecyclerViewAdapter;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_more);
        initView();
        initData(0);
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(this);

        wapRecyclerViewAdapter = new WapRecyclerViewAdapter(adapter);
        wapRecyclerViewAdapter.setLoadMoreListener(recyclerView, this);

        //添加尾部
        View footer = LayoutInflater.from(this).inflate(R.layout.layout_load_more, recyclerView, false);
        wapRecyclerViewAdapter.setFooterView(footer);

        recyclerView.setAdapter(wapRecyclerViewAdapter);
    }


    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
            }
            notifyDataSetChanged();
        }

        public void addData(List<String> data) {
            if (data != null && data.size() > 0) {
                mDataList.addAll(data);
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = inflater.inflate(R.layout.item_list_load_more, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            ((ViewHolder)viewHolder).textView.setText(mDataList.get(position));
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
        for (int i = 0; i < 10; i++) {
            String temp = page + "-" + i;
            data.add(temp);
        }
        if (page == 0) {
            adapter.setData(data);
        } else {
            adapter.addData(data);
        }

        Log.e(TAG, "size : "+wapRecyclerViewAdapter.getItemCount());
    }

    @Override
    public void loadMore() {
        Log.e(TAG, "loadMore......................");
    }
}
