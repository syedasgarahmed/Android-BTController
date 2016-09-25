package com.kleiren.bluetoothcontoller;

import android.content.ClipData;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ControllerFragment extends Fragment implements View.OnClickListener{

    private ImageButton btnSettings;
    private android.widget.RelativeLayout.LayoutParams layoutParams;

    private String btnA = "A", btnB= "B", btnX = "X", btnY = "Y", btnN = "N", btnS = "S", btnE = "E", btnW = "W", btnNE = "NE", btnNW = "NW", btnSE = "SE", btnSW = "SW";

    public TextView sentText;

    public ControllerFragment() {
    }


    public static ControllerFragment newInstance() {
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

        controllerView.findViewById(R.id.btnSettings).setOnClickListener(this);
        controllerView.findViewById(R.id.btnTerminal).setOnClickListener(this);
        controllerView.findViewById(R.id.btnA).setOnClickListener(this);
        controllerView.findViewById(R.id.btnB).setOnClickListener(this);
        controllerView.findViewById(R.id.btnX).setOnClickListener(this);
        controllerView.findViewById(R.id.btnY).setOnClickListener(this);
        controllerView.findViewById(R.id.btnN).setOnClickListener(this);
        controllerView.findViewById(R.id.btnS).setOnClickListener(this);
        controllerView.findViewById(R.id.btnE).setOnClickListener(this);
        controllerView.findViewById(R.id.btnW).setOnClickListener(this);
        controllerView.findViewById(R.id.btnNE).setOnClickListener(this);
        controllerView.findViewById(R.id.btnNW).setOnClickListener(this);
        controllerView.findViewById(R.id.btnSE).setOnClickListener(this);
        controllerView.findViewById(R.id.btnSW).setOnClickListener(this);

        sentText = (TextView) controllerView.findViewById(R.id.txtSent);




        return controllerView;
    }



    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case (R.id.btnSettings):
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, ControllerFragment2.newInstance())
                        .addToBackStack("face_ident_fragment")
                        .commit();
                break;
            case (R.id.btnTerminal):
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, TerminalFragment.newInstance())
                        .addToBackStack("terminal_fragment")
                        .commit();
                break;
            case (R.id.btnA):
                ((MainActivity) getActivity()).sendMessage(btnA);
                break;
            case (R.id.btnB):
                ((MainActivity) getActivity()).sendMessage(btnB);
                break;
            case (R.id.btnX):
                ((MainActivity) getActivity()).sendMessage(btnX);
                break;
            case (R.id.btnY):
                ((MainActivity) getActivity()).sendMessage(btnY);
                break;
            case (R.id.btnN):
                ((MainActivity) getActivity()).sendMessage(btnN);
                break;
            case (R.id.btnW):
                ((MainActivity) getActivity()).sendMessage(btnW);
                break;
            case (R.id.btnS):
                ((MainActivity) getActivity()).sendMessage(btnS);
                break;
            case (R.id.btnNE):
                ((MainActivity) getActivity()).sendMessage(btnNE);
                break;
            case (R.id.btnSE):
                ((MainActivity) getActivity()).sendMessage(btnSE);
                break;
            case (R.id.btnNW):
                ((MainActivity) getActivity()).sendMessage(btnNW);
                break;
            case (R.id.btnSW):
                ((MainActivity) getActivity()).sendMessage(btnSW);
                break;
        }
    }


}
