package com.yuong.notes.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuong.notes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 日期选择控件
 */
public class DatePickerView extends RelativeLayout implements View.OnClickListener {
    public static final String DATE = "yyyy-MM-dd";
    private RelativeLayout rlDatePicker;
    private FrameLayout flLeft, flRight;
    private ImageView ivLeft, ivRight;
    private TextView tvDate;

    private String nowTime;
    private String selectTime;
    private OnDateChangeCallback callback;


    public DatePickerView(Context context) {
        this(context, null);
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_date_picker, this, true);
        rlDatePicker = view.findViewById(R.id.rl_date_picker);
        flLeft = view.findViewById(R.id.fl_left);
        ivLeft = view.findViewById(R.id.iv_left);
        tvDate = view.findViewById(R.id.tv_date);
        flRight = view.findViewById(R.id.fl_right);
        ivRight = view.findViewById(R.id.iv_right);
        flRight.setVisibility(INVISIBLE);
        flLeft.setOnClickListener(this);
        flRight.setOnClickListener(this);

        nowTime = getNowTime(DATE);
        selectTime = nowTime;
        tvDate.setText(selectTime);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
                changeDate(-1);
                break;
            case R.id.fl_right:
                changeDate(1);
                break;
        }
    }

    private void changeDate(int offset) {
        selectTime = addDate(selectTime, offset, DATE);
        if (nowTime.equalsIgnoreCase(selectTime)) {
            flRight.setVisibility(View.INVISIBLE);
        } else {
            flRight.setVisibility(View.VISIBLE);
        }
        tvDate.setText(selectTime);

        if (callback != null) {
            callback.onChange(selectTime);
        }
    }

    public static String getNowTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(new Date());
    }

    private String addDate(String srcDate, int offset, String format) {
        if (TextUtils.isEmpty(srcDate)) {
            return null;
        }

        SimpleDateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
        Date date = null;
        try {
            date = df.parse(srcDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return null;
        }

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(GregorianCalendar.DATE, offset);
        Date newDate = gc.getTime();

        return df.format(newDate);
    }

    public interface OnDateChangeCallback {
        void onChange(String date);
    }

    public void setCallback(OnDateChangeCallback callback) {
        this.callback = callback;
    }
}
