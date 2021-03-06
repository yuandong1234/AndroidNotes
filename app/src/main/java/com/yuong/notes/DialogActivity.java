package com.yuong.notes;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.yuong.notes.widget.dialog.CommonDialog;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        showDialog();
    }

    private void showDialog() {
        CommonDialog.Builder builder = new CommonDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_common, null);
        CommonDialog dialog = builder.view(view)
                .height(200)
                .style(R.style.theme_common_dialog)
                .cancelTouchOut(true)
                .build();
        dialog.show();
    }
}
