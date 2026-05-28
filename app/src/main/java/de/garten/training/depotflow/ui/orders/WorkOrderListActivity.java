package de.garten.training.depotflow.ui.orders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import de.garten.training.depotflow.R;
import de.garten.training.depotflow.core.ResultCallback;
import de.garten.training.depotflow.core.ServiceLocator;
import de.garten.training.depotflow.data.db.green.WorkOrder;
import de.garten.training.depotflow.data.repository.WorkOrderRepository;
import de.garten.training.depotflow.ui.detail.WorkOrderDetailActivity;

public class WorkOrderListActivity extends Activity {

    private WorkOrderRepository repository;
    private WorkOrderListAdapter adapter;
    private TextView statusText;
    private TextView errorText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_list);

        repository = ServiceLocator.get().workOrderRepository();
        adapter = new WorkOrderListAdapter(this);

        ListView listView = findViewById(R.id.workOrderList);
        Button refreshButton = findViewById(R.id.refreshButton);
        statusText = findViewById(R.id.listStatusText);
        errorText = findViewById(R.id.errorText);
        progressBar = findViewById(R.id.loadingProgress);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, WorkOrderDetailActivity.class);
            intent.putExtra(WorkOrderDetailActivity.EXTRA_LOCAL_ID, id);
            startActivity(intent);
        });

        refreshButton.setOnClickListener(view -> refreshFromServer());
        loadLocal();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocal();
    }

    private void loadLocal() {
        setLoading(true);
        repository.loadLocalOrders(new ResultCallback<List<WorkOrder>>() {
            @Override
            public void onSuccess(List<WorkOrder> value) {
                setLoading(false);
                errorText.setVisibility(View.GONE);
                adapter.replaceAll(value);
                statusText.setText(value.size() + " lokale Work Orders geladen");
            }

            @Override
            public void onError(Throwable error) {
                setLoading(false);
                showError(error);
            }
        });
    }

    private void refreshFromServer() {
        setLoading(true);
        repository.refreshOrders(new ResultCallback<List<WorkOrder>>() {
            @Override
            public void onSuccess(List<WorkOrder> value) {
                setLoading(false);
                errorText.setVisibility(View.GONE);
                adapter.replaceAll(value);
                statusText.setText(value.size() + " Work Orders nach Remote-Refresh");
            }

            @Override
            public void onError(Throwable error) {
                setLoading(false);
                showError(error);
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showError(Throwable error) {
        errorText.setVisibility(View.VISIBLE);
        errorText.setText("Fehler: " + error.getMessage() + "\nFallback: lokale Daten bleiben sichtbar.");
        statusText.setText("Remote-Operation fehlgeschlagen");
    }
}
