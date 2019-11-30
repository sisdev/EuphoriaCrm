package in.sisoft.apps.ecrm.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.Objects;

import in.sisoft.apps.ecrm.Adapters.TaskAdapter;
import in.sisoft.apps.ecrm.R;

public class MyTaskActivity extends AppCompatActivity {


    TaskAdapter adapter;
    //widget
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("My Tasks");
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        adapter = new TaskAdapter(MyTaskActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setupCallbackListener(position -> {
            Intent intent = new Intent(MyTaskActivity.this, UpdateMyTaskActivity.class);
            intent.putExtra(getString(R.string.position),position);
            startActivityForResult(intent,0);
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }
}
