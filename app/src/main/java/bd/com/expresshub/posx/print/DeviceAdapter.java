package bd.com.expresshub.posx.print;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bd.com.expresshub.posx.R;

public class DeviceAdapter extends ArrayAdapter<DeviceModel> implements View.OnClickListener{

    private ArrayList<DeviceModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;

    }

    public DeviceAdapter(ArrayList<DeviceModel> data, Context context) {
        super(context, R.layout.adapter_bluetooth_device, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DeviceModel dataModel=(DeviceModel) object;

        switch (v.getId())
        {
            case R.id.tvName:
                v.setBackgroundResource(R.color.md_light_blue_800);
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DeviceModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_bluetooth_device, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.tvName);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

           /* Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);*/
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getName());

        // Return the completed view to render on screen
        return convertView;
    }
}
