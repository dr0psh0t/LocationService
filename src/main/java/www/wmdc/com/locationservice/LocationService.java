package www.wmdc.com.locationservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class LocationService extends Service
{
    public static final String ACTION_LOCATION_BROADCAST =
            LocationService.class.getName()+"LocationBroadcast";

    private LocationTask locationTask;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        locationTask.stop();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        locationTask = new LocationTask(getApplicationContext());

        Thread locationThread = new Thread(locationTask, "ThreadLocation1");
        locationThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    public void sendBroadcastMessage(double lat, double lng)
    {
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    class LocationTask implements Runnable
    {
        private volatile boolean exit = false;
        private GPSTracker gps;

        public LocationTask(Context context) {
            this.gps = new GPSTracker(context);
        }

        public void run()
        {
            while (!exit)
            {
                sendBroadcastMessage(gps.getLatitude(), gps.getLongitude());

                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {

                }
            }
        }

        public void stop() {
            exit = true;
        }
    }
}