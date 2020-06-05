package com.yuong.notes;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;

public class MarkwonActivity extends AppCompatActivity {
    private TextView textview1;

    private String textHtml = "<p>1. 好好学习</p><p>2. 好好学习</p>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markwon);
        initView();
    }

    private void initView() {
        textview1 = findViewById(R.id.textview1);

        setTextHtml(textview1, textHtml);
    }

    private void setTextHtml(TextView textView, String html) {
        Markwon markwon = Markwon.builder(this)
                .usePlugin(HtmlPlugin.create(new HtmlPlugin.HtmlConfigure() {
                    @Override
                    public void configureHtml(HtmlPlugin plugin) {
                        plugin.excludeDefaults(true);
                    }
                }))
                .build();

        markwon.setMarkdown(textView, html);
    }
}
