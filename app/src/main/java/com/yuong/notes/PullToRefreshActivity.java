package com.yuong.notes;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.refresh.RefreshLayout;
import com.app.refresh.listener.RefreshListener;

/**
 * 下拉刷新上拉加载测试用例
 */
public class PullToRefreshActivity extends AppCompatActivity implements RefreshListener {
    private static final String TAG=PullToRefreshActivity.class.getSimpleName();
    private RefreshLayout refreshLayout;
    private ListView listView;
    private RecyclerView recyclerView;
    private Handler handler = new Handler();
    private String[] data = {"item1", "item2", "item3", "item4", "item5",
            "item6", "item7", "item8", "item9", "item10", "item11",
            "item12", "item13", "item14", "item15", "item16",
            "item17", "item18", "item19", "item20", "item21",
            "item22", "item23", "item24", "item25", "item26",
            "item27", "item28", "item29", "item30", "item31",
            "item32", "item33", "item34", "item35", "item36",
            "item37", "item38", "item39", "item40", "item41",
            "item42", "item43", "item44", "item45", "item46"};
   // private String[] data = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh);

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setListener(this);
//        findViewById(R.id.container).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Log.e("PullToRefreshActivity", "onTouch......");
//                return false;
//            }
//        });

        //ListView
        listView = findViewById(R.id.listView);
        MyAdapter adapter=new MyAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e(TAG,"listView OnTouch...");
                return false;
            }
        });



        //RecyclerView
//        recyclerView = findViewById(R.id.recyclerView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        layoutManager.setOrientation(OrientationHelper.VERTICAL);
//        My2Adapter adapter=new My2Adapter(this);
//        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        Log.e("PullToRefreshActivity", "onRefresh....");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.onRefreshComplete();
                //refreshLayout.setEnableRefresh(false);
            }
        }, 3000);
    }

    @Override
    public void onLoadMore() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.onLoadMoreComplete();
                //refreshLayout.setEnableLoadMore(false);
            }
        }, 3000);
    }

    private class MyAdapter extends BaseAdapter {
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int i) {
            return data[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.item_list, null);
            TextView title = view.findViewById(R.id.title);
            title.setText(data[i]);
            return view;
        }
    }

    private class My2Adapter extends RecyclerView.Adapter<My2Adapter.ViewHolder> {

        private Context context;

        public My2Adapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_list, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.title.setText(data[i]);
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
            }
        }
    }
}
