package de.garten.training.depotflow.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import de.garten.training.depotflow.R;
import de.garten.training.depotflow.core.ResultCallback;
import de.garten.training.depotflow.core.ServiceLocator;
import de.garten.training.depotflow.ui.orders.WorkOrderListActivity;
import de.garten.training.depotflow.ui.sync.SyncActivity;

public class MainActivity extends Activity {

    private TextView summaryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        summaryText = findViewById(R.id.mainSummary);
        Button ordersButton = findViewById(R.id.openOrdersButton);
        Button syncButton = findViewById(R.id.openSyncButton);

        ordersButton.setOnClickListener(view -> startActivity(new Intent(this, WorkOrderListActivity.class)));
        syncButton.setOnClickListener(view -> startActivity(new Intent(this, SyncActivity.class)));

        renderSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderSummary();
    }

    private void renderSummary() {
        ServiceLocator.get().syncRepository().loadSummary(new ResultCallback<String>() {
            @Override
            public void onSuccess(String value) {
                summaryText.setText("Lokaler Zustand\n\n" + value);
            }

            @Override
            public void onError(Throwable error) {
                summaryText.setText("Lokaler Zustand konnte nicht gelesen werden: " + error.getMessage());
            }
        });
    }
}
