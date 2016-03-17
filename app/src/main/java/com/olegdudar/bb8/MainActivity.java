package com.olegdudar.bb8;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.orbotix.ConvenienceRobot;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.robot.DualStackDiscoveryAgent;
import com.robot.RobotBB;

public class MainActivity extends Activity  implements BluetoothAdapter.LeScanCallback, RobotChangedStateListener{
    private static final String TAG = "BluetoothGattActivity";

    private static final String DEVICE_NAME = "BB-";

    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;

    private BluetoothGatt mConnectedGatt;

    private ProgressDialog mProgress;

    private TextView mac_Address;
    private Button connect_button;

    private BluetoothDevice bb_8;


    protected final Handler _mainThreadHandler = new Handler(Looper.getMainLooper());

    private ConvenienceRobot mRobot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        setProgressBarIndeterminate(true);

        mac_Address = (TextView) findViewById(R.id.device_mac);
        mac_Address.setText("Please search for device");

        connect_button = (Button) findViewById(R.id.btn_connect);
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bb_8 != null) {
                    Robot robot = startDiscovery(bb_8);

                    if( robot instanceof RobotBB) {
                        ( (RobotBB) robot ).setDeveloperMode( true );
                    }

                    //Save the robot as a ConvenienceRobot for additional utility methods
                    mRobot = new ConvenienceRobot( robot );


                    mRobot.setLed(0f, 1f, 0f);

                    mRobot.calibrating(true);
                    mRobot.calibrating(false);
                    //Start blinking the robot's LED
                    //blink( false );


                }
                else
                    startDiscovery();
            }
        });

        connect_button.setVisibility(View.INVISIBLE);

        /*
         * Bluetooth in Android 4.3 is accessed via the BluetoothManager, rather than
         * the old static BluetoothAdapter.getInstance()
         */
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();

        mDevices = new SparseArray<BluetoothDevice>();

        DualStackDiscoveryAgent.getInstance().addRobotStateListener( this );

        /*
         * A progress dialog will be needed while the connection process is
         * taking place
         */
        mProgress = new ProgressDialog(this);
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * We need to enforce that Bluetooth is first enabled, and take the
         * user to settings to enable it if they have not done so.
         */
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }

        /*
         * Check for Bluetooth LE Support.  In production, our manifest entry will keep this
         * from installing on these devices, but this will allow test devices or other
         * sideloads to report whether or not the feature exists.
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Make sure dialog is hidden
        mProgress.dismiss();
        //Cancel any scans in progress
        mHandler.removeCallbacks(mStopRunnable);
        mHandler.removeCallbacks(mStartRunnable);
        mBluetoothAdapter.stopLeScan(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Disconnect from any active tag connection
        if (mConnectedGatt != null) {
            mConnectedGatt.disconnect();
            mConnectedGatt = null;
        }
        mRobot.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add the "scan" option to the menu
        getMenuInflater().inflate(R.menu.main, menu);
        //Add any device elements we've discovered to the overflow menu
        for (int i=0; i < mDevices.size(); i++) {
            BluetoothDevice device = mDevices.valueAt(i);
            menu.add(0, mDevices.keyAt(i), 0, device.getName());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                mDevices.clear();
                startScan();
                return true;
            default:
                //Obtain the discovered device to connect with
                BluetoothDevice device = mDevices.get(item.getItemId());
                Log.i(TAG, "Connecting to "+device.getName());
                mac_Address.setText("BB-8 MAC: " + device.getAddress());
                connect_button.setVisibility(View.VISIBLE);
                bb_8 = device;
                return super.onOptionsItemSelected(item);
        }
    }

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    private void startScan() {
        mBluetoothAdapter.startLeScan(this);
        setProgressBarIndeterminateVisibility(true);

        mHandler.postDelayed(mStopRunnable, 3000);
    }

    private void stopScan() {
        mBluetoothAdapter.stopLeScan(this);
        setProgressBarIndeterminateVisibility(false);
    }

    /* BluetoothAdapter.LeScanCallback */

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

        Log.i(TAG, "New LE Device: " + device.getName() + " @ " + rssi);
        /*
         * We are looking for SensorTag devices only, so validate the name
         * that each device reports before adding it to our collection
         */
        if (device.getName() != null && device.getName().startsWith(DEVICE_NAME)) {
            mDevices.put(device.hashCode(), device);
            //Update the overflow menu
            invalidateOptionsMenu();
            mac_Address.setText("BB-8 discovered!");
//            RobotBB bb8 = new RobotBB(device, new RobotRadioDescriptor(), this._mainThreadHandler);
//            ( (RobotBB) bb8 ).setDeveloperMode(true);
//            mRobot = new ConvenienceRobot( bb8 );
//            boolean a = mRobot.isConnected();
        }
    }

    /*
     * In this callback, we've created a bit of a state machine to enforce that only
     * one characteristic be read or written at a time until all of our sensors
     * are enabled and we are registered to get notifications.
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        private String connectionState(int status) {
            switch (status) {
                case BluetoothProfile.STATE_CONNECTED:
                    return "Connected";
                case BluetoothProfile.STATE_DISCONNECTED:
                    return "Disconnected";
                case BluetoothProfile.STATE_CONNECTING:
                    return "Connecting";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "Disconnecting";
                default:
                    return String.valueOf(status);
            }
        }
    };

    /*
     * We have a Handler to process event results on the main thread
     */
    private static final int MSG_PROGRESS = 201;
    private static final int MSG_DISMISS = 202;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS:
                    mProgress.setMessage((String) msg.obj);
                    if (!mProgress.isShowing()) {
                        mProgress.show();
                    }
                    break;
                case MSG_DISMISS:
                    mProgress.hide();
                    break;
            }
        }
    };

    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
        if( robot instanceof RobotBB) {
            ( (RobotBB) robot ).setDeveloperMode( true );
        }

        //Save the robot as a ConvenienceRobot for additional utility methods
        mRobot = new ConvenienceRobot( robot );

        //Start blinking the robot's LED
        blink( false );
//        switch( type ) {
//            case Online: {
//
//                //If robot uses Bluetooth LE, Developer Mode can be turned on.
//                //This turns off DOS protection. This generally isn't required.
//                if( robot instanceof RobotBB) {
//                    ( (RobotBB) robot ).setDeveloperMode( true );
//                }
//
//                //Save the robot as a ConvenienceRobot for additional utility methods
//                mRobot = new ConvenienceRobot( robot );
//
//                //Start blinking the robot's LED
//                blink( false );
//                break;
//            }
//        }
    }

    //Turn the robot LED on or off every two seconds
    private void blink( final boolean lit ) {
        if( mRobot == null )
            return;

        if( lit ) {
            mRobot.setLed( 0.0f, 0.0f, 0.0f );
        } else {
            mRobot.setLed( 0.0f, 0.0f, 1.0f );
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                blink(!lit);
            }
        }, 2000);
    }

    private void startDiscovery() {
        //If the DiscoveryAgent is not already looking for robots, start discovery.
        if( !DualStackDiscoveryAgent.getInstance().isDiscovering() ) {
            try {
                DualStackDiscoveryAgent.getInstance().startDiscovery(getApplicationContext());
            } catch (DiscoveryException e) {
                Log.e("Sphero", "DiscoveryException: " + e.getMessage());
            }
        }
    }

    private Robot startDiscovery(BluetoothDevice device) {
        //If the DiscoveryAgent is not already looking for robots, start discovery.
        Robot robot = null;
        if( !DualStackDiscoveryAgent.getInstance().isDiscovering() ) {
            try {
                robot = DualStackDiscoveryAgent.getInstance().startDiscovery(getApplicationContext(), device);
            } catch (DiscoveryException e) {
                Log.e("Sphero", "DiscoveryException: " + e.getMessage());
            }
        }
        return robot;
    }
}
