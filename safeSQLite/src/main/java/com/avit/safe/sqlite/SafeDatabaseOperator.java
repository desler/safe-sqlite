package com.avit.safe.sqlite;

import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteTableLockedException;
import android.util.Log;

import java.util.List;
import java.util.Map;

public final class SafeDatabaseOperator<DATA> implements IDatabaseOperation<DATA> {

    public static <DATA> SafeDatabaseOperator<DATA> safe(IDatabaseOperation<DATA> operation) {
        return new SafeDatabaseOperator<DATA>(operation);
    }

    private final IDatabaseOperation<DATA> operation;

    private final String TAG;

    private final Object waitLock;

    private SafeDatabaseOperator(IDatabaseOperation<DATA> operation) {
        this.operation = operation;
        this.TAG = operation.getLogTag();
        this.waitLock = new Object();
    }

    private void waitLock(){
        synchronized (waitLock){
            try {
                operation.wait(1);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public String getLogTag() {
        return operation.getLogTag();
    }

    @Override
    public int saveOrUpdate(DATA o) {

        Log.d(TAG, "saveOrUpdate: " + o);

        long b = System.currentTimeMillis();

        int ret = 0;
        while (true) {
            try {
                ret = operation.saveOrUpdate(o);
                Log.d(TAG, "saveOrUpdate: cost = " + (System.currentTimeMillis() - b));
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.w(TAG, "saveOrUpdate: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "saveOrUpdate: ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public int saveOrUpdate(List<DATA> datas) {

        Log.d(TAG, "saveOrUpdate: size = " + datas.size());

        int ret = 0;
        while (true) {
            try {
                ret = operation.saveOrUpdate(datas);
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.w(TAG, "saveOrUpdate: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "saveOrUpdate: ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public DATA read(DATA o) {

        Log.d(TAG, "read: " + o);

        DATA ret = null;
        while (true) {
            try {
                ret = operation.read(o);
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.w(TAG, "read: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "read: ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public List<DATA> read(Map<String, Object> objects) {

        Log.d(TAG, "read: " + objects);

        List<DATA> ret = null;
        while (true) {
            try {
                ret = operation.read(objects);
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.w(TAG, "read: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "read ... : ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public List<DATA> read(int pageSize, int offset, Map<String, Object> object) {

        Log.d(TAG, "read: pageSize = " + pageSize + ", offset = " + offset + ", objects = " + object);

        List<DATA> ret = null;
        while (true) {
            try {
                ret = operation.read(pageSize, offset, object);
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.w(TAG, "read: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "read: ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public List read() {

        Log.d(TAG, "read: all");

        List<DATA> ret = null;
        while (true) {
            try {
                ret = operation.read();
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.w(TAG, "read: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "read: ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public int delete(DATA o) {

        Log.d(TAG, "delete: " + o);

        int ret = 0;
        while (true) {
            try {
                ret = operation.delete(o);
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.w(TAG, "delete: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "delete: ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public int delete(Map<String, Object> objects) {

        Log.d(TAG, "delete: "+ objects);

        int ret = 0;
        while (true) {
            try {
                ret = operation.delete(objects);
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.d(TAG, "delete: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "delete ... : ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public int delete(List<DATA> datas) {

        Log.d(TAG, "delete: size = " + datas.size());

        int ret = 0;
        while (true) {
            try {
                ret = operation.delete(datas);
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.e(TAG, "delete: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "delete: ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public int delete() {

        Log.d(TAG, "delete: all");

        int ret = 0;
        while (true) {
            try {
                ret = operation.delete();
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.e(TAG, "delete: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "delete: ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public int count() {

        Log.d(TAG, "count: all");

        int ret = 0;
        while (true) {
            try {
                ret = operation.count();
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.e(TAG, "count: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "count: ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public int count(DATA data) {

        Log.d(TAG, "count: " + data);

        int ret = 0;
        while (true) {
            try {
                ret = operation.count(data);
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.e(TAG, "count: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "count: ", e);
                break;
            }
        }
        return ret;
    }

    @Override
    public int count(Map<String, Object> objects) {

        Log.d(TAG, "count: " + objects);

        int ret = 0;
        while (true) {
            try {
                ret = operation.count(objects);
                break;
            } catch (SQLiteDatabaseLockedException | SQLiteTableLockedException e) {
                Log.e(TAG, "count: ", e);
                waitLock();
            } catch (Exception e) {
                Log.e(TAG, "count: ", e);
                break;
            }
        }
        return ret;
    }
}
