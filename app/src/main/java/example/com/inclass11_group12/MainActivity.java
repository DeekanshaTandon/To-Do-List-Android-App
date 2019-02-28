package example.com.inclass11_group12;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements ITask{
    RealmList<Task> taskList;
    RealmList<Task> pendingList;
    RealmList<Task> completedList;
    RealmList<Task> list;
    Realm realm;
    String Menu="";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_showAll:
                Menu="A";
                loadListview();
               // Menu_fetchLISTS();
               // setLIST(taskList);
                break;


            case R.id.menu_showCompleted:
                Menu="C";
                loadListview();
               // Menu_fetchLISTS();
              //  setLIST(completedList);
                break;

            case R.id.menu_showPending:
                Menu="P";
                loadListview();
              //  Menu_fetchLISTS();
               // setLIST(pendingList);
                break;

            default:
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        pendingList=new RealmList<>();
        completedList=new RealmList<>();


        ListView listview=(ListView)findViewById(R.id.listview);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final Task task=list.get(position);
                final int  delete_pos=position;
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm Delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                realm.beginTransaction();
                                RealmResults<Task> reminderObjs = realm.where(Task.class)
                                        .equalTo("id", task.id)
                                        .findAll();
                                boolean isDeleted = reminderObjs.deleteAllFromRealm();
                                realm.commitTransaction();
                                list.remove(position);
                                loadListview();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                final AlertDialog alertDialog=builder.create();
                alertDialog.show();


                return false;
            }
        });

        taskList=new RealmList<>();
        loadListview();
        final Spinner spinner=(Spinner)findViewById(R.id.spinner);
        String[] priority = new String[]{
                "Priority",
                "High",
                "Medium",
                "Low",

        };
        final List<String> priorityList = new ArrayList<>(Arrays.asList(priority));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.support_simple_spinner_dropdown_item,priorityList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){

                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);


        final EditText editTodo=(EditText)findViewById(R.id.editToDO);
        Button btnAdd=(Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(editTodo.getText().toString().length()==0) {
                  Toast.makeText(MainActivity.this, "Please enter Task Name", Toast.LENGTH_SHORT).show();
              }
              else if(spinner.getSelectedItem().toString().equals("Priority"))
              {
                  Toast.makeText(MainActivity.this, "Please select Priority", Toast.LENGTH_SHORT).show();
              }
              else
              {
                  add();
              }

            }
        });
    }
    public void add()
    {
        final Spinner spinner=(Spinner)findViewById(R.id.spinner);
        final ListView listview=(ListView)findViewById(R.id.listview);

        final EditText editTodo=(EditText)findViewById(R.id.editToDO);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date date = Calendar.getInstance().getTime();
        final String mydate = dateFormat.format(date);

        realm.beginTransaction();
        Number curNum=realm.where(Task.class).max("id");
        int intNextID;
        if(curNum == null)
        {
            intNextID=1;
        }
        else
        {
            intNextID= curNum.intValue()+1;
        }
        Task task = realm.createObject(Task.class);
        task.id=intNextID;
        task.note=editTodo.getText().toString();
        task.priority=spinner.getSelectedItem().toString();
        task.status="unchecked";
        task.time=mydate.toString();
        Log.d("demo", task.toString());
        realm.commitTransaction();
        loadListview();
        Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();



    }
    public void loadListview()
    {
        RealmResults<Task> tasks=realm.where(Task.class).findAll();
        list=new RealmList<>();
        taskList.clear();
        pendingList.clear();
        completedList.clear();
        list.clear();

        realm.beginTransaction();
        if(tasks!=null) {
            for (Task task : tasks) {
                Log.d("demo","load"+ task.toString());
                taskList.add(task);
                if(task.status.equals("unchecked"))
                {
                 pendingList.add(task);
                }
                else
                { completedList.add(task);
                }
            }
        }

        switch (Menu)
        {
            case "A":
                list.addAll(taskList);
                break;
            case "P":
                list.addAll(pendingList);
                break;
            case "C":
                list.addAll(completedList);
                break;
                default:
                    list.addAll(taskList);


        }
        Collections.sort(list, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                String p1=o1.status.toString();
                String p2=o2.status.toString();

                if (p1.equals("unchecked") && (p2.equals("checked")))
                    return -1;
                else if(p1.equals("checked") && p2.equals("unchecked"))
                    return 1;
                else if(p1.equals(p2))
                {
                    String m1=o1.priority.toString();
                    String m2=o2.priority.toString();
                    if(m1.equals(m2)) return 0;
                    if(m1.equals("Low") && (m2.equals("Medium") || m2.equals("High")))
                        return 1;
                    if(m1.equals("Medium") && m2.equals("High"))
                        return 1;
                    return -1;


                }
                return 0;
            };
        });
        realm.commitTransaction();
        final ListView listview=(ListView)findViewById(R.id.listview);
        TaskAdapter task_adapter=new TaskAdapter(MainActivity.this,R.layout.listview_display,list,this);
        listview.setAdapter(task_adapter);

    }

    public void Menu_fetchLISTS()
    {
        RealmResults<Task> tasks=realm.where(Task.class).findAll();

        taskList.clear();
        pendingList.clear();
        completedList.clear();
        realm.beginTransaction();
        if(tasks!=null) {
            for (Task task : tasks) {
                Log.d("demo","load"+ task.toString());
                taskList.add(task);
                if(task.status.equals("unchecked"))
                {
                    pendingList.add(task);
                }
                else
                {
                    completedList.add(task);
                }
            }
        }
        realm.commitTransaction();
    }

    public void setLIST(RealmList<Task> list)
    {
        final ListView listview=(ListView)findViewById(R.id.listview);
        TaskAdapter task_adapter=new TaskAdapter(MainActivity.this,R.layout.listview_display,list,this);
        listview.setAdapter(task_adapter);
    }



    @Override
    public void checkbox_true(final int position, Task task) {

        // Updating a boolean field
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                Date date = Calendar.getInstance().getTime();
                String mydate = dateFormat.format(date);
                RealmResults<Task> persons = realm.where(Task.class).equalTo("id", position).findAll();
                persons.setString("status","checked");
                persons.setString("time",mydate);

            }
        });
        loadListview();

    }

    @Override
    public void checkbox_false(final int position, Task task) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                Date date = Calendar.getInstance().getTime();
                String mydate = dateFormat.format(date);
                RealmResults<Task> persons = realm.where(Task.class).equalTo("id", position).findAll();
                persons.setString("status","unchecked");
                persons.setString("time",mydate);

            }
        });
        loadListview();


    }

}
