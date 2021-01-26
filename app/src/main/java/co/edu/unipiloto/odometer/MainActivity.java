package co.edu.unipiloto.odometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import java.util.Locale;


public class MainActivity extends Activity {
    private OdometerService odometer;
    private boolean bound=false;
    private int tiempoActu=1000;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder=
                    (OdometerService.OdometerBinder)binder;
            odometer=odometerBinder.getOdometer();
            bound=true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound=false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayDistanceMillas();
        displayDistanceMetros();

    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent= new Intent(this, OdometerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    private void displayDistanceMillas(){
        final TextView distanceView=(TextView)findViewById(R.id.distance);
        final Handler handler= new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (bound && odometer !=null){
                    distance=odometer.getDistance();
                }
                String distanceStr = String.format(Locale.getDefault(),"%1$,.2f miles",distance);
                distanceView.setText(distanceStr);
                handler.postDelayed(this,1000);
            }
        });
    }

    private void displayDistanceMetros(){
        final TextView distanceView=(TextView)findViewById(R.id.distanceMe);
        final Handler handler= new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (bound && odometer !=null){
                    distance=odometer.getDistanceMe();
                }
                String distanceStr = String.format(Locale.getDefault(),"%1$,.2f metros",distance);

                distanceView.setText(distanceStr+ "\n" + "con tiempo: " + (tiempoActu/1000) + "segs");
                handler.postDelayed(this,tiempoActu);
            }
        });
    }

    public void aumentar(View view){
        if(tiempoActu<10000)
            tiempoActu+=1000;
    }

    public void disminuir(View view){
        if(tiempoActu>1000)
            tiempoActu-=1000;
    }
}