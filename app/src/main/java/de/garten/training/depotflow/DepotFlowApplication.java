package de.garten.training.depotflow;

import android.app.Application;

import de.garten.training.depotflow.core.ServiceLocator;

public class DepotFlowApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceLocator.init(this);
    }
}
