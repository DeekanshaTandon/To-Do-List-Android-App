package example.com.inclass11_group12;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TaskAdapter extends ArrayAdapter<Task> {

    ViewHolder viewHolder;

    ITask iTask;
    public TaskAdapter(Context context, int resource, List<Task> objects, ITask iTask) {
        super(context, resource, objects);
        this.iTask=iTask;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos=position;
        final Task task=getItem(position);
        viewHolder=new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_display, parent, false);
            viewHolder.todo = (TextView) convertView.findViewById(R.id.listTODOtext);
            viewHolder.list_time = (TextView) convertView.findViewById(R.id.listTime);
            viewHolder.priority = (TextView) convertView.findViewById(R.id.listTodopriority);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.listcheckbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.todo.setText(task.note.toString());
        //viewHolder.list_time.setText(task.time.toString());
        viewHolder.priority.setText(task.priority.toString());
       // viewHolder.checkBox.setVisibility(View.INVISIBLE);
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        if(task.status.equals("checked"))
        {
            viewHolder.checkBox.setChecked(true);
        }
        else
        {
            viewHolder.checkBox.setChecked(false);
        }
        viewHolder.checkBox.setOnCheckedChangeListener(null);

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked==true)
                {
                    iTask.checkbox_true(task.id,task);

                    Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                }
                else if(isChecked==false)
                {
                    iTask.checkbox_false(task.id,task);
                    Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                }
            }
        });


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date date=dateFormat.parse(task.time.toString().trim());
            PrettyTime pt=new PrettyTime();
            //Log.d("msgtime","prettytime final____"+pt.format(date));

            viewHolder.list_time.setText(pt.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }
    public static class ViewHolder
    {
        TextView todo;
        TextView list_time;
        TextView priority;
        CheckBox checkBox;
    }
}

