package pv.projects.mediasoft.com.pventes.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {

    Authenticator authenticator =  null ;
    public AuthenticatorService() {

        authenticator = new Authenticator(this) ;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return authenticator.getIBinder() ;
    }
}

