package de.garten.training.depotflow.ui.sync;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.garten.training.depotflow.R;
import de.garten.training.depotflow.core.ResultCallback;
import de.garten.training.depotflow.core.ServiceLocator;
import de.garten.training.depotflow.data.repository.SyncRepository;

public class SyncActivity extends Activity {

    private SyncRepository repository;
    private TextView summaryText;
    private TextView errorText;
    private ProgressBar progressBar;
    private Button syncButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        repository = ServiceLocator.get().syncRepository();
        summaryText = findViewById(R.id.syncSummary);
        errorText = findViewById(R.id.syncError);
        progressBar = findViewById(R.id.syncProgress);
        syncButton = findViewById(R.id.startSyncButton);

        syncButton.setOnClickListener(view -> pushPending());
        loadSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSummary();
    }

    private void loadSummary() {
        repository.loadSummary(new ResultCallback<String>() {
            @Override
            public void onSuccess(String value) {
                summaryText.setText(value);
                errorText.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable error) {
                errorText.setVisibility(View.VISIBLE);
                errorText.setText("Summary konnte nicht geladen werden: " + error.getMessage());
            }
        });
    }

    private void pushPending() {
        progressBar.setVisibility(View.VISIBLE);
        syncButton.setEnabled(false);
        repository.pushPending(new ResultCallback<String>() {
            @Override
            public void onSuccess(String value) {
                progressBar.setVisibility(View.GONE);
                syncButton.setEnabled(true);
                errorText.setVisibility(View.GONE);
                summaryText.setText(value);
                loadSummary();
            }

            @Override
            public void onError(Throwable error) {
                progressBar.setVisibility(View.GONE);
                syncButton.setEnabled(true);
                errorText.setVisibility(View.VISIBLE);
                errorText.setText("Sync fehlgeschlagen: " + error.getMessage() + "\nOutbox bleibt erhalten.");
                loadSummary();
            }
        });
    }
}
