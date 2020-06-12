package com.avit.safe.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.avit.safe.sqlite.AsyncDatabaseOperator;
import com.avit.safe.sqlite.IDatabaseOperation;
import com.avit.safe.sqlite.SafeDatabaseOperator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IDatabaseOperation<TestBean> normal = new Normal();
        IDatabaseOperation<TestBean> safe = SafeDatabaseOperator.safe(normal);
        IDatabaseOperation<TestBean> async = AsyncDatabaseOperator.async(safe);

    }
}