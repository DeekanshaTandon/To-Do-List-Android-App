package example.com.inclass11_group12;


import io.realm.RealmObject;

public class Task extends RealmObject {

    int id;
    String note,priority,time,status;

    public Task()
    {

    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", note='" + note + '\'' +
                ", priority='" + priority + '\'' +
                ", time='" + time + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}