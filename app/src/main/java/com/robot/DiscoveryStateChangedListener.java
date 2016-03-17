package com.robot;

/**
 * Created by Oleg_Dudar on 3/17/2016.
 */
public interface DiscoveryStateChangedListener {
    void onDiscoveryDidStart(DiscoveryAgent var1);

    void onDiscoveryDidStop(DiscoveryAgent var1);
}
