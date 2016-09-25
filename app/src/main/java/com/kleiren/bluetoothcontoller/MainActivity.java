/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.kleiren.bluetoothcontoller;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p/>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends AppCompatActivity {

    private android.widget.RelativeLayout.LayoutParams layoutParams;


    // BLUETOOTH COM
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_UNABLE = 6;

    // Key names received from the BluetoothCommandService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String INFO = "dialog";

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for Bluetooth Command Service
    private BluetoothChatService mChatService = null;

    public static final String TAG = "MainActivity";

    /**
     * Bluetooth Handler
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {

            Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
            if (fragment instanceof TerminalFragment) {

                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothChatService.STATE_CONNECTED:
                                // setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                                ((TerminalFragment) fragment).mConversationArrayAdapter.clear();
                                break;
                            case BluetoothChatService.STATE_CONNECTING:
                                //setStatus(R.string.title_connecting);
                                break;
                            case BluetoothChatService.STATE_LISTEN:
                            case BluetoothChatService.STATE_NONE:
                                // setStatus(R.string.title_not_connected);
                                break;
                        }
                        break;
                    case Constants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        String writeMessage = new String(writeBuf);
                        ((TerminalFragment) fragment).mConversationArrayAdapter.add("Me:  " + writeMessage);
                        break;
                    case Constants.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        ((TerminalFragment) fragment).mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                        break;
                    case Constants.MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);

                        Toast.makeText(getApplicationContext(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                        break;
                    case Constants.MESSAGE_TOAST:

                        Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();

                        break;
                }
            }

            Context context = getApplication();


            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            fragment = getFragmentManager().findFragmentById(R.id.container);
                            if (fragment instanceof MainFragment)
                                ((MainFragment) fragment).startAnimations();
                            break;

                        case BluetoothChatService.STATE_CONNECTING:

                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:

                            // When the bt connection ends or is interrupted, the app goes back to the beginning (main fragment)
                            // To avoid crashes, this does not happen when the app is showing the main fragment
//                            if (!mainFragmentIsinForeground && mIsInForegroundMode) {
//                                getFragmentManager().beginTransaction()
//                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                                        .replace(R.id.container, new MainFragment(), "MainFragment")
//                                        .addToBackStack("MainFragment")
//                                        .commit();
//                                getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//
//                            }
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // Save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    if (null != context) {
                     /*   Toast.makeText(context, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();*/
                    }
                    break;

                case MESSAGE_TOAST:
                    if (null != context) {
                      /*  Toast.makeText(context, msg.getData().getString(TOAST),
                                Toast.LENGTH_SHORT).show();*/
                    }
                    break;

                case MESSAGE_UNABLE:

                    // If the connection cant be established, the app restores the main fragment
                    if (null != context) {
                        Toast.makeText(context, msg.getData().getString(TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    fragment = getFragmentManager().findFragmentById(R.id.container);
                    if (fragment instanceof MainFragment)
                        ((MainFragment) fragment).unableConnect();
                    break;


                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    fragment = getFragmentManager().findFragmentById(R.id.container);

                    // If the app is in the logged in activity, sets the corresponding image as ordered from the bluetooth com
//                    if (readMessage.equals("done")) {
//                        if (fragment instanceof LoggedinFragment)
//                            ((LoggedinFragment) fragment).withdrawed();
//                    }

                    //  Toast.makeText(context, readMessage,  Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private String selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




 /*       if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothChatFragment fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }*/

//        if (savedInstanceState == null) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            ControllerFragment fragment = new ControllerFragment();
//            transaction.replace(R.id.sample_content_fragment, fragment);
//            transaction.commit();
//        }


        setContentView(R.layout.activity_main);
//
//        //Status bar tint
//        // create our manager instance after the content view is set
//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//        // enable status bar tint
//        tintManager.setStatusBarTintEnabled(true);
//        // enable navigation bar tint
//        tintManager.setNavigationBarTintEnabled(true);
//        // set a custom tint color for all system bars
//        tintManager.setTintColor(R.color.darkgreen);
//        tintManager.setTintColor(Color.WHITE);

        //preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
//        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
//        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);
//
//        return super.onPrepareOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_toggle_log:
//                mLogShown = !mLogShown;
//                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
//                if (mLogShown) {
//                    output.setDisplayedChild(1);
//                } else {
//                    output.setDisplayedChild(0);
//                }
//                supportInvalidateOptionsMenu();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    /**
//     * Create a chain of targets that will receive log data
//     */
//    @Override
//    public void initializeLogging() {
//        // Wraps Android's native log framework.
//        LogWrapper logWrapper = new LogWrapper();
//        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
//        Log.setLogNode(logWrapper);
//
//        // Filter strips out everything except the message text.
//        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
//        logWrapper.setNext(msgFilter);
//
//        // On screen logging via a fragment with a TextView.
//        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.log_fragment);
//        msgFilter.setNext(logFragment.getLogView());
//
//        Log.i(TAG, "Ready");
//    }


    /**
     * Main Fragment containing the menu
     */
    public class MainFragment extends Fragment {

        private ImageView gutiBank, darkerBackground;
        Animation animationFadeOut, animationMoveOut, animationMoveIn, animationFadeIn;
        private View mLoadingView;
        private Button btnTerminal, btnController;

        public MainFragment newInstance() {
            MainFragment fragment = new MainFragment();
            return fragment;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Initializes all buttons and animations
            btnTerminal = (Button) rootView.findViewById(R.id.main_btnLogin);
            btnTerminal.setVisibility(View.INVISIBLE);
            btnController = (Button) rootView.findViewById(R.id.main_btnController);
            btnController.setVisibility(View.INVISIBLE);
            mLoadingView = rootView.findViewById(R.id.fragment_loading_spinner);
            mLoadingView.setVisibility(View.INVISIBLE);
            gutiBank = (ImageView) rootView.findViewById(R.id.imageView);
            gutiBank.setVisibility(View.INVISIBLE);
            darkerBackground = (ImageView) rootView.findViewById(R.id.imageView2);
            animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
            animationMoveOut = AnimationUtils.loadAnimation(getActivity(), R.anim.moveout);
            animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
            animationMoveIn = AnimationUtils.loadAnimation(getActivity(), R.anim.movein);
            darkerBackground.startAnimation(animationMoveIn);

            btnTerminal.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnTerminal.setVisibility(View.VISIBLE);
                    btnTerminal.startAnimation(animationFadeIn);
                    btnController.setVisibility(View.VISIBLE);
                    btnController.startAnimation(animationFadeIn);
                }
            }, 1000);


            btnTerminal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLoadingView.setVisibility(View.VISIBLE);
                    btnTerminal.setVisibility(View.INVISIBLE);
                    btnController.setVisibility(View.INVISIBLE);

                    Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    selected = "terminal";
                }
            });

            btnController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLoadingView.setVisibility(View.VISIBLE);
                    btnTerminal.setVisibility(View.INVISIBLE);
                    btnController.setVisibility(View.INVISIBLE);
                    Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    selected = "controller";

                }
            });

            if (mChatService != null) {
                mChatService.stop();
            }


            return rootView;
        }

        /**
         * Animations for the start of the mainFragment
         */
        public void startAnimations() {
            mLoadingView.setVisibility(View.INVISIBLE);
            // btnTerminal.startAnimation(animationFadeOut);
            darkerBackground.startAnimation(animationMoveOut);
          /*  btnTerminal.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnTerminal.setVisibility(View.INVISIBLE);
                }
            }, 500);*/
            darkerBackground.postDelayed(new Runnable() {
                @Override
                public void run() {
                    darkerBackground.setVisibility(View.INVISIBLE);
                    if (selected.equals("terminal")) {
                        getFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.container, TerminalFragment.newInstance())
                                .addToBackStack("face_ident_fragment")
                                .commit();
                    } else {
                        getFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.container, ControllerFragment.newInstance())
                                .addToBackStack("face_ident_fragment")
                                .commit();
                    }
                }
            }, 700);


        }


        @Override
        public void onPause() {
            super.onPause();

            mainFragmentIsinForeground = false;

        }


        @Override
        public void onResume() {
            super.onResume();


            mainFragmentIsinForeground = true;
        }


        /**
         * This happens in the case the app is unable to connect to the server
         * Restores ui after transition animations
         */
        public void unableConnect() {
            mLoadingView.setVisibility(View.INVISIBLE);
            btnTerminal.setVisibility(View.VISIBLE);

            darkerBackground.startAnimation(animationMoveIn);

            btnTerminal.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnTerminal.setVisibility(View.VISIBLE);

                    btnTerminal.startAnimation(animationFadeIn);

                }
            }, 1000);
            darkerBackground.setVisibility(View.VISIBLE);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            super.onActivityResult(requestCode, resultCode, intent);
            //Bluetooth
            switch (requestCode) {
                case REQUEST_CONNECT_DEVICE:
                    // When DeviceListActivity returns with a device to connect
                    if (resultCode == Activity.RESULT_OK) {
                        // Get the device MAC address
                        String address = intent.getExtras()
                                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                        // Get the BLuetoothDevice object
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                        // Attempt to connect to the device
                        mChatService.connect(device, true);
                    }
                    break;
                case REQUEST_ENABLE_BT:
                    // When the request to enable Bluetooth returns
                    if (resultCode == Activity.RESULT_OK) {
                        // Bluetooth is now enabled, so set up a chat session
                        setupCommand();
                    } else {
                        // User did not enable Bluetooth or an error occured
                        // Toast.makeText(getParent(), "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
                        //finish();
                    }
            }
        }

    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            //   Toast.makeText(getApplicationContext(), getString(guti.es.bioaccess.R.string.bluetooth_notConnected), Toast.LENGTH_SHORT).show();
            return;
        }
        Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof ControllerFragment)
            ((ControllerFragment)fragment).sentText.setText(message);

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupCommand() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        // otherwise set up the command service
        else {

            if (mChatService == null)
                setupCommand();
        }

    }


    @Override
    public void onPause() {
        super.onPause();

        mIsInForegroundMode = false;

    }


    @Override
    public void onResume() {
        super.onResume();


        mIsInForegroundMode = true;


        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
            if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                mChatService.stop();
            }
        } else {
            setupCommand();
            if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                mChatService.stop();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Bluetooth
        if (mChatService != null)
            mChatService.stop();
    }

    private void setupCommand() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getParent(), mHandler);

    }


}


