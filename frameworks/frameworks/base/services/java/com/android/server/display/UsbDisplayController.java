/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.display;

import libcore.util.Objects;
import android.content.Context;
import android.hardware.display.WifiDisplay;
import android.media.RemoteDisplay;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;

/**
 * Manages all of the various asynchronous interactions with the
 * {@link WifiP2pManager} on behalf of {@link UsbDisplayAdapter}.
 * <p>
 * This code is isolated from {@link UsbDisplayAdapter} so that we can avoid
 * accidentally introducing any deadlocks due to the display manager calling
 * outside of itself while holding its lock. It's also way easier to write this
 * asynchronous code if we can assume that it is single-threaded.
 * </p>
 * <p>
 * The controller must be instantiated on the handler thread.
 * </p>
 */
public class UsbDisplayController{
	private static final String tag = "UsbDisplayController";

	private static final String DEFAULT_CONTROL_IP = "0.0.0.0";
	private static final int DEFAULT_CONTROL_PORT = 7236;
	
	
	private RemoteDisplay mRemoteDisplay;
	
	private final Context mContext;
	private final Handler mHandler;
	private final Listener mListener;

	public UsbDisplayController(Context context, Handler handler,
			Listener listener) {
		mContext = context;
		mHandler = handler;
		mListener = listener;
		
	}

	public void requestConnect() {
		String iface = DEFAULT_CONTROL_IP+":"+DEFAULT_CONTROL_PORT;
		mRemoteDisplay = RemoteDisplay.listen(iface,
				new RemoteDisplay.Listener() {

					@Override
					public void onDisplayConnected(Surface surface, int width,
							int height, int flags, int session) {
						// TODO Auto-generated method stub
						final WifiDisplay display = createWifiDisplay();
                        advertiseDisplay(display, surface, width, height, flags);
//                        mListener.onDisplayConnected(display, surface, width, height, flags);
					}

					@Override
					public void onDisplayDisconnected() {
						// TODO Auto-generated method stub
						mListener.onDisplayDisconnected();
					}

					@Override
					public void onDisplayError(int error) {
						// TODO Auto-generated method stub
						mListener.onDisplayError();
					}

				}, mHandler);
	}
	
	private static WifiDisplay createWifiDisplay() {
        return new WifiDisplay("00:00:00:00:00:00", "UsbDisplay", null,
                true, true, false);
    }

    // The information we have most recently told WifiDisplayAdapter about.
    private WifiDisplay mAdvertisedDisplay;
    private Surface mAdvertisedDisplaySurface;
    private int mAdvertisedDisplayWidth;
    private int mAdvertisedDisplayHeight;
    private int mAdvertisedDisplayFlags;
    
	private void advertiseDisplay(final WifiDisplay display,
            final Surface surface, final int width, final int height, final int flags) {
        if (!Objects.equal(mAdvertisedDisplay, display)
                || mAdvertisedDisplaySurface != surface
                || mAdvertisedDisplayWidth != width
                || mAdvertisedDisplayHeight != height
                || mAdvertisedDisplayFlags != flags) {
            final WifiDisplay oldDisplay = mAdvertisedDisplay;
            final Surface oldSurface = mAdvertisedDisplaySurface;

            mAdvertisedDisplay = display;
            mAdvertisedDisplaySurface = surface;
            mAdvertisedDisplayWidth = width;
            mAdvertisedDisplayHeight = height;
            mAdvertisedDisplayFlags = flags;

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (oldSurface != null && surface != oldSurface) {
                        mListener.onDisplayDisconnected();
                    } else if (oldDisplay != null && !oldDisplay.hasSameAddress(display)) {
                        mListener.onDisplayConnectionFailed();
                    }

                    if (display != null) {
                        if (!display.hasSameAddress(oldDisplay)) {
                            mListener.onDisplayConnecting(display);
                        } else if (!display.equals(oldDisplay)) {
                            // The address is the same but some other property such as the
                            // name must have changed.
                            mListener.onDisplayChanged(display);
                        }
                        if (surface != null && surface != oldSurface) {
                            mListener.onDisplayConnected(display, surface, width, height, flags);
                        }
                    }
                }
            });
        }
    }
	
	public void requestPause(){
		if(mRemoteDisplay!=null){
			mRemoteDisplay.pause();
		}
	}
	public void requestResume(){
		if(mRemoteDisplay!=null){
			mRemoteDisplay.resume();
		}
	}
	public void requestDisconnect(){
		if(mRemoteDisplay!=null){
			mRemoteDisplay.dispose();
			mRemoteDisplay = null;
		}
	}
	
	//This interface is created by another class
	public interface Listener {
		void onDisplayConnected(WifiDisplay display, Surface surface,
				int width, int height, int flags);
		void onDisplayConnecting(WifiDisplay display);
		void onDisplayChanged(WifiDisplay display);
		void onDisplayDisconnected();
		void onDisplayError();
		void onDisplayConnectionFailed();
	}
}
