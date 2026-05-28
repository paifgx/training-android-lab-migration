package de.garten.training.depotflow.ui.detail;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.garten.training.depotflow.R;
import de.garten.training.depotflow.core.ResultCallback;
import de.garten.training.depotflow.core.ServiceLocator;
import de.garten.training.depotflow.data.db.green.ChecklistItem;
import de.garten.training.depotflow.data.db.green.Stop;
import de.garten.training.depotflow.data.db.green.WorkOrder;
import de.garten.training.depotflow.data.repository.WorkOrderDetails;
import de.garten.training.depotflow.data.repository.WorkOrderRepository;
import de.garten.training.depotflow.ui.LegacyUiFormatter;

public class WorkOrderDetailActivity extends Activity {

    public static final String EXTRA_LOCAL_ID = "localId";

    private final LegacyUiFormatter formatter = new LegacyUiFormatter();
    private WorkOrderRepository repository;
    private TextView titleText;
    private TextView metaText;
    private TextView stopsText;
    private TextView checklistText;
    private TextView errorText;
    private Button completeButton;
    private long localId;
    private WorkOrder currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_detail);

        repository = ServiceLocator.get().workOrderRepository();
        localId = getIntent().getLongExtra(EXTRA_LOCAL_ID, -1L);

        titleText = findViewById(R.id.detailTitle);
        metaText = findViewById(R.id.detailMeta);
        stopsText = findViewById(R.id.detailStops);
        checklistText = findViewById(R.id.detailChecklist);
        errorText = findViewById(R.id.detailError);
        completeButton = findViewById(R.id.completeButton);

        completeButton.setOnClickListener(view -> completeOffline());
        loadDetails();
    }

    private void loadDetails() {
        repository.loadDetails(localId, new ResultCallback<WorkOrderDetails>() {
            @Override
            public void onSuccess(WorkOrderDetails value) {
                errorText.setVisibility(View.GONE);
                currentOrder = value.getWorkOrder();
                render(value);
            }

            @Override
            public void onError(Throwable error) {
                errorText.setVisibility(View.VISIBLE);
                errorText.setText("Details konnten nicht geladen werden: " + error.getMessage());
            }
        });
    }

    private void render(WorkOrderDetails details) {
        WorkOrder order = details.getWorkOrder();
        titleText.setText(order.getExternalNumber() + "\n" + order.getTitle());
        metaText.setText(formatter.formatOrderSubtitle(order) + "\n" + formatter.formatOrderStatus(order));

        StringBuilder stops = new StringBuilder("Stopps\n\n");
        for (Stop stop : details.getStops()) {
            stops.append(formatter.formatStop(stop)).append("\n\n");
        }
        stopsText.setText(stops.toString().trim());

        StringBuilder checklist = new StringBuilder("Checkliste\n\n");
        for (ChecklistItem item : details.getChecklistItems()) {
            checklist.append(formatter.formatChecklistItem(item)).append("\n");
        }
        checklistText.setText(checklist.toString().trim());
    }

    private void completeOffline() {
        if (currentOrder == null) {
            return;
        }
        completeButton.setEnabled(false);
        repository.completeOffline(currentOrder.getServerId(), new ResultCallback<WorkOrder>() {
            @Override
            public void onSuccess(WorkOrder value) {
                Toast.makeText(WorkOrderDetailActivity.this, "Lokal erledigt und Outbox-Eintrag erzeugt", Toast.LENGTH_LONG).show();
                completeButton.setEnabled(true);
                loadDetails();
            }

            @Override
            public void onError(Throwable error) {
                completeButton.setEnabled(true);
                errorText.setVisibility(View.VISIBLE);
                errorText.setText("Lokale Änderung fehlgeschlagen: " + error.getMessage());
            }
        });
    }
}
