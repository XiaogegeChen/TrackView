package com.github.xiaogegechen.trackview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.xiaogegechen.library.TrackView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private boolean state = false;

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

                TrackView.Position position = trackView.getPosition ();
                Log.d (TAG, "onClick: " + position);

                if(!state){
                    trackView.close ();
                }else{
                    trackView.open ();
                }
                state = !state;
            }
        });

    }
}
