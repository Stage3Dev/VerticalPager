package com.stage3dev.samples.verticalPager;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.stage3dev.verticalpager.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private VerticalViewPager pager;

    private List<FragData> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        data.add(new FragData(R.color.primary_dark, "Primary dark"));
        data.add(new FragData(R.color.colorAccent, "Accent"));
        data.add(new FragData(R.color.primary_light, "Primary Light"));
        data.add(new FragData(R.color.primary, "Primary"));


        pager = ((VerticalViewPager) findViewById(R.id.pager));

        pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                FragData fd = data.get(position);
                return PlaceholderFragment.newInstance(fd.color, fd.text);
            }

            @Override
            public int getCount() {
                return data.size();
            }
        });
    }

    private static class FragData {

        @ColorRes
        int color;

        String text;

        public FragData(int color, String text) {
            this.color = color;
            this.text = text;
        }
    }

}
