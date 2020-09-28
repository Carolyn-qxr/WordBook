package com.example.wordbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.wordbook.R;

/*
created by qxr
 */
public class Left_fragment extends Fragment {
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     //通过参数中的布局填充获取对应布局
         View view =inflater.inflate(R.layout.left_fragment,container,false);
        return view;
         }
    @Override
    public void onPause() {
        super.onPause();
    }

}
