package de.garten.training.depotflow

import android.app.Application
import de.garten.training.depotflow.core.ServiceLocator

class DepotFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
