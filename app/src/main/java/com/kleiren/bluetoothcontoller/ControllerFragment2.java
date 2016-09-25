package com.kleiren.bluetoothcontoller;

import android.app.Fragment;
import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class ControllerFragment2 extends Fragment{

    private Button btn;
    private RelativeLayout.LayoutParams layoutParams;


    public ControllerFragment2() {
    }


    public static ControllerFragment2 newInstance() {
        ControllerFragment2 fragment = new ControllerFragment2();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View controllerView =  inflater.inflate(R.layout.fragment_controller2, container, false);


        return controllerView;
    }




}
