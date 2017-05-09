package com.stage3dev.samples.verticalPager;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PlaceholderFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_COLOR = "arg_color";
    private static final String ARG_TEXT = "arg_text";

    @ColorRes
    private int color;

    private String text;

    private TextView fragText;

    public PlaceholderFragment() {
        // Required empty public constructor
    }

    public static PlaceholderFragment newInstance(@ColorRes int color, String text) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLOR, color);
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.color = getArguments().getInt(ARG_COLOR);
            this.text = getArguments().getString(ARG_TEXT, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_placeholder, container, false);
        v.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        ((TextView) v.findViewById(R.id.frag_text)).setText(text);

        return v;
    }
}
