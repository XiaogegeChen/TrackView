package com.github.xiaogegechen.trackview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.xiaogegechen.library.TrackView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        final TrackView trackView = findViewById (R.id.track_view);
        final TextView textView = findViewById (R.id.text_view);

        textView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Log.d (TAG, "onClick: text_view");
            }
        });

        trackView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                textView.setText ("WORLD");
                Toast.makeText (MainActivity.this,  "click", Toast.LENGTH_SHORT).show ();
            }
        });

        findViewById (R.id.close).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                trackView.close ();
            }
        });

        findViewById (R.id.open).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                trackView.open ();
            }
        });

        findViewById (R.id.change).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                trackView.setText ("num:"+num);
                num++;
            }
        });

    }
}
