package com.zhaoshouren.android.wallpaper;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.os.Handler;
import android.provider.Settings;
import android.service.wallpaper.WallpaperService;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.SurfaceHolder;

public class ZhaoShourenWallpaper extends WallpaperService {
	public static final String SHARED_PREFS_NAME="ZhaoShourenWallpaperSettings";
	private static final String ZS = "ZhaoShouren";
	
	private static final String TIMEFORMAT_12HOUR = "h:mm";
	private static final String TIMEFORMAT_24HOUR = "k:mm";
	
//	private class WeatherDownloaderTask extends AsyncTask<Location, String, Boolean> {
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//		}
//		
//		@Override
//		protected Boolean doInBackground(Location... params) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		
//		@Override
//		protected void onPostExecute(Boolean result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//		}
//		
//	}
	

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        return new ZhaoShourenEngine();
    }

    class ZhaoShourenEngine extends Engine 
        implements SharedPreferences.OnSharedPreferenceChangeListener {

//    	private final LocationManager mLocationManager;
    	
    	private final Handler mHandler = new Handler();
        private final Paint mPaint = new Paint();
        
        
//        private final LocationListener mLocationListener = new LocationListener() {
//	        public void onLocationChanged(Location location) {
//	              updateZipCode(location);
//	        }
//	
//	        public void onStatusChanged(String provider, int status, Bundle extras) {}
//	
//	        public void onProviderEnabled(String provider) {}
//	
//	        public void onProviderDisabled(String provider) {}
//        };

        private final Runnable mUpdateCanvas = new Runnable() {
            public void run() {
                updateCanvas();
            }
        };
        
//        private final Geocoder mGeocoder = new Geocoder(getBaseContext()); //getApplicationContext()
        
        private boolean mVisible;
        private boolean mBlank = true; 
        private SharedPreferences mPreferences;
        protected int mTimeFormat;
        protected String mAmPmFormat = new String();
        private String mTime = new String();
        
//        protected List<Address> mAddresses;
//        protected String mZipCode;
        

        ZhaoShourenEngine() {
            // Create a Paint to draw the lines for our cube
            final Paint paint = mPaint;
            paint.setColor(Color.DKGRAY);
            paint.setAntiAlias(true);
            paint.setTypeface(Typeface.DEFAULT_BOLD);

            mPreferences = getSharedPreferences(SHARED_PREFS_NAME, 0);
            mPreferences.registerOnSharedPreferenceChangeListener(this);
            onSharedPreferenceChanged(mPreferences, null);
            
//            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // Register the listener with the Location Manager to receive location updates
            //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60 * 60, 10000, mLocationListener);
        }

        public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {	
        	mTimeFormat = Integer.parseInt(preferences.getString("timeFormat", "0"));
        	mAmPmFormat = preferences.getString("amPmFormat", "");
        	Log.i("DateFormat", "mTimeFormat: " + mTimeFormat + "; mAmPmFormat: " + mAmPmFormat);
        	mBlank = true;
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            //setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            clearCanvas();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                updateCanvas();
            } else {
            	clearCanvas();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            updateCanvas();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            clearCanvas();
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
        	updateCanvas();
        }

    
        
        /*
         * Update Wallpaper
         */
        
        void updateCanvas() {
        	final long currentTimeMillis = System.currentTimeMillis();
        	final String currentTime = (String) DateFormat.format("hhmm", currentTimeMillis);
        	
        	if (!mTime.equals(currentTime) || mBlank) {
//            	Log.i(ZS, "mTime: " + mTime + "; currentTime: " + currentTime);
//            	Log.i(ZS, Boolean.toString(!mTime.equals(currentTime) || mBlank));
        		
        		
            	
        		final SurfaceHolder holder = getSurfaceHolder();
        		final Rect frame = holder.getSurfaceFrame();
        		
            	Canvas canvas = null;
        		
	        	try {
	        		canvas = holder.lockCanvas();
	        		if (canvas != null) {
	        			canvas.save();
	        			
	        			canvas.drawColor(Color.BLACK);
	        			drawTime(canvas, currentTimeMillis, frame.right);
	        			drawDate(canvas, currentTimeMillis);
	        			
	        			canvas.restore();
	        			mTime = currentTime;
	            		mBlank = false;
	        		}
	        	} finally {
	        		if (canvas != null) holder.unlockCanvasAndPost(canvas);
	        	}
        	}
        	
        	mHandler.removeCallbacks(mUpdateCanvas);
        	if (mVisible) {
        		mHandler.postDelayed(mUpdateCanvas, 1000 / 25);
        	}
        }
        
        void clearCanvas() {
        	final SurfaceHolder holder = getSurfaceHolder();
        	Canvas canvas = null;
        	
        	try {
        		canvas = holder.lockCanvas();
        		if (canvas != null) {
        			canvas.save();
        			canvas.drawColor(Color.BLACK);
        			canvas.restore();
        			mBlank = true;
        		}
        	} finally {
        		if (canvas != null) holder.unlockCanvasAndPost(canvas);
        	}
        	
        	mHandler.removeCallbacks(mUpdateCanvas);
        }
        
        void drawTime(Canvas canvas, long currentTimeMillis, int right) {
        	final Paint paint = new Paint(mPaint);
        	final String timeFormat = mTimeFormat == DateUtils.FORMAT_12HOUR || mTimeFormat == 0 && !DateFormat.is24HourFormat(getApplicationContext()) ? TIMEFORMAT_12HOUR : TIMEFORMAT_24HOUR;
        	String amPm = new String();
        	float amPmWidth = 0;
        	
        	paint.setTextAlign(Align.RIGHT);
        	
        	if (timeFormat.equals(TIMEFORMAT_12HOUR)) {
        		amPm = (String) DateFormat.format(mAmPmFormat, currentTimeMillis);
        		amPmWidth = paint.measureText(amPm);
        		canvas.drawText(amPm, right, 150, paint);
        	}
        	
        	paint.setTextSize(150);
        	canvas.drawText((String) DateFormat.format(timeFormat, currentTimeMillis), right - amPmWidth, 150, paint);
        }
        
        void drawDate(Canvas canvas, long currentTimeMillis) {
        	final Paint paint = new Paint(mPaint);
        	paint.setTextSize(50);
        	canvas.drawText((String) DateFormat.format("EEEE, MMMM d, yyyy", currentTimeMillis), 0, 200, paint);
        }
        
//        void updateZipCode(Location location) {
//        	try {
//        		mAddresses = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			if (!mAddresses.isEmpty()) mZipCode = mAddresses.get(0).getPostalCode();
//        	
//        	Iterator<Address> locations = mAddresses.iterator();
//        	while (locations.hasNext()) {
//        	
//        	}
//        }
    }
}