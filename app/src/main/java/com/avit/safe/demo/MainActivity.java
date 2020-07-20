package com.avit.safe.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.avit.safe.sqlite.AsyncDatabaseOperator;
import com.avit.safe.sqlite.IDatabaseOperation;
import com.avit.safe.sqlite.SafeDatabaseOperator;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IDatabaseOperation<TestBean> normal = new Normal();
        IDatabaseOperation<TestBean> safe = SafeDatabaseOperator.safe(normal);
        safe.read();


        AsyncDatabaseOperator<TestBean> async = AsyncDatabaseOperator.async(safe);
        async.onListener(new AsyncDatabaseOperator.AsyncListener() {
            @Override
            public void onDone(int count, Object src) {

            }
        }).count();

        async.onReadListener(new AsyncDatabaseOperator.AsyncReadListener() {
            @Override
            public void onRead(int count, Object object, List rets) {

            }

            @Override
            public void onDone(int count, Object src) {

            }
        }).read();

    }
}