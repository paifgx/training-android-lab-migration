package de.garten.training.depotflow.ui.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.garten.training.depotflow.R;
import de.garten.training.depotflow.data.db.green.WorkOrder;
import de.garten.training.depotflow.ui.LegacyUiFormatter;

public class WorkOrderListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final LegacyUiFormatter formatter = new LegacyUiFormatter();
    private final List<WorkOrder> items = new ArrayList<>();

    public WorkOrderListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void replaceAll(List<WorkOrder> orders) {
        items.clear();
        if (orders != null) {
            items.addAll(orders);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public WorkOrder getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        WorkOrder order = getItem(position);
        return order.getId() == null ? position : order.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_work_order, parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.orderTitle);
            holder.subtitle = convertView.findViewById(R.id.orderSubtitle);
            holder.status = convertView.findViewById(R.id.orderStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WorkOrder order = getItem(position);
        holder.title.setText(order.getExternalNumber() + " · " + order.getTitle());
        holder.subtitle.setText(formatter.formatOrderSubtitle(order));
        holder.status.setText(formatter.formatOrderStatus(order));
        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView subtitle;
        TextView status;
    }
}
