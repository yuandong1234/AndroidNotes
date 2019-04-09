package com.yuong.notes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MultipleItemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Bean> list = new ArrayList<>();
    //    private String[] data = {"item1", "item2", "item3", "item4", "item5",
//            "item6", "item7", "item8", "item9", "item10", "item11",
//            "item12", "item13", "item14", "item15", "item16",
//            "item17", "item18", "item19", "item20", "item21",
//            "item22", "item23", "item24", "item25", "item26",
//            "item27", "item28", "item29", "item30", "item31",
//            "item32", "item33", "item34", "item35", "item36",
//            "item37", "item38", "item39", "item40", "item41",
//            "item42", "item43", "item44", "item45", "item46"};
    private String[] data = {"item1", "item2", "item3", "item4"};
    private MyAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_item);
        initView();
        initData();
    }


    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        adapter = new MyAdapter(list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("MultipleItemActivity", "isSlideToBottom : " + isSlideToBottom(recyclerView));
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                Log.e("MultipleItemActivity", "lastVisibleItem : " + lastVisibleItem);
                Log.e("MultipleItemActivity", "is full screen  : " + (lastVisibleItem == list.size() - 1));
            }
        });
    }

    private void initData() {
        for (String temp : data) {
            Bean bean = new Bean();
            bean.type = 1;
            bean.txt = temp;
            list.add(bean);
        }
        Bean bean = new Bean();
        bean.type = 2;
        bean.txt = "加载中...";
        list.add(bean);
        adapter.notifyDataSetChanged();
    }


    private class MyAdapter extends RecyclerView.Adapter {
        private Context context;
        private List<Bean> list;
        private LayoutInflater inflater;

        private static final int TYPE_BODY = 1;
        private static final int TYPE_FOOTER = 2;


        public MyAdapter(List<Bean> list, Context context) {
            this.list = list;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view;
            switch (viewType) {
                case TYPE_BODY:
                    view = inflater.inflate(R.layout.item_list, viewGroup, false);
                    return new BodyHolder(view);
                case TYPE_FOOTER:
                    view = inflater.inflate(R.layout.layout_load_more, viewGroup, false);
                    return new FootHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            Bean bean = list.get(position);
            if (viewHolder instanceof BodyHolder) {
                ((BodyHolder) viewHolder).content.setText(bean.txt);
            } else if (viewHolder instanceof FootHolder) {
                ((FootHolder) viewHolder).content.setText(bean.txt);
            }
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (list.get(position).type == 1) {
                return TYPE_BODY;
            } else if (list.get(position).type == 2) {
                return TYPE_FOOTER;
            } else {
                return super.getItemViewType(position);
            }
        }

        private class BodyHolder extends RecyclerView.ViewHolder {
            TextView content;

            public BodyHolder(@NonNull View itemView) {
                super(itemView);
                content = itemView.findViewById(R.id.title);
            }
        }

        private class FootHolder extends RecyclerView.ViewHolder {
            TextView content;

            public FootHolder(@NonNull View itemView) {
                super(itemView);
                content = itemView.findViewById(R.id.title);
            }
        }

    }

    private class Bean {
        public int type;
        public String txt;
    }

    public static boolean isSlideToBottom(View child) {
        boolean intercept = false;
        RecyclerView recyclerView = (RecyclerView) child;
        if (recyclerView.getChildCount() == 0) {
            return false;
        }
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            intercept = true;

        return intercept;
    }
}
