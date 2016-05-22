package com.kleiren.bluetoothcontoller;

import android.content.ClipData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class ControllerFragment extends Fragment{

    private Button btn;
    private android.widget.RelativeLayout.LayoutParams layoutParams;


    public ControllerFragment() {
    }


    public static ControllerFragment newInstance(String param1, String param2) {
        ControllerFragment fragment = new ControllerFragment();

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
        View controllerView =  inflater.inflate(R.layout.fragment_controller, container, false);

        btn = (Button) controllerView.findViewById(R.id.btnX);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Im pressed", Toast.LENGTH_SHORT).show();
                BluetoothChatFragment.sendMessage("X");
            }
        });

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(btn);

                btn.startDrag(data, shadowBuilder, btn, 0);
                // btn.setVisibility(View.INVISIBLE);
                return true;
            }
        });

        btn.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();

                        // Do nothing
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        layoutParams.leftMargin = x_cord;
                        layoutParams.topMargin = y_cord;
                        v.setLayoutParams(layoutParams);
                        v.bringToFront();

                        v.invalidate();
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        if (!event.getResult())
                            v.setVisibility(View.VISIBLE);
                        v.bringToFront();

                        // Do nothing
                        break;

                    case DragEvent.ACTION_DROP:

                        // Do nothing
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(btn);

                    btn.startDrag(data, shadowBuilder, btn, 0);
                    btn.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
            }
        });

        return controllerView;
    }




}
