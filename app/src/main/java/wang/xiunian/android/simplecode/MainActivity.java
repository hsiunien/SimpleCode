package wang.xiunian.android.simplecode;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import wang.xiunian.android.L;
import wang.xiunian.android.simplecode.module.ModuleHandlerStore;
import wang.xiunian.android.simplecode.module.UriDispatcher;
import wang.xiunian.android.simplecode.module.UriWraper;
import wang.xiunian.android.simplecode.urihandler.MethodAStore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        L.init("app", L.LogLevel.DEBUG);
        ModuleHandlerStore.addModule(getApplication(), MethodAStore.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            UriWraper wraper = UriWraper.from("http://methodA");
            UriDispatcher.dispatcher(wraper);
        });
    }

}
