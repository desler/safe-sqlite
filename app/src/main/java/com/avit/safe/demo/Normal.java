package com.avit.safe.demo;

import com.avit.safe.sqlite.BaseDatabaseOperator;

public class Normal extends BaseDatabaseOperator<String, TestBean> {

    @Override
    protected String dataKey(TestBean testBean) {
        return super.dataKey(testBean);
    }

    @Override
    public String getLogTag() {
        return "Normal";
    }
}
