package com.example.shimmer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.shimmer.ShimmerFrameLayout;

public class MainActivity extends AppCompatActivity {
    private ShimmerFrameLayout mShimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmer);
        mShimmerFrameLayout.startShimmerAnimation();
    }
}
