package com.avit.safe.sqlite;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class AsyncDatabaseOperator<DATA> implements IDatabaseOperation<DATA> {

    public static <DATA> AsyncDatabaseOperator<DATA> async(IDatabaseOperation<DATA> operation) {
        return new AsyncDatabaseOperator<>(operation);
    }

    private final IDatabaseOperation<DATA> operation;
    private final String TAG;
    private final AsyncExecutor asyncExecutor;

    private AsyncDatabaseOperator(IDatabaseOperation<DATA> operation) {

        this.TAG = operation.getLogTag();

        if (operation instanceof SafeDatabaseOperator) {
            this.operation = operation;
        } else {
            this.operation = SafeDatabaseOperator.safe(operation);
        }

        this.asyncExecutor = new AsyncExecutor(this);
    }

    @Override
    public String getLogTag() {
        return operation.getLogTag();
    }

    @Override
    public int saveOrUpdate(final DATA data) {

        executeOperator(new AsyncTask(data) {
            @Override
            public Object call() {
                sendAsyncMessage(AsyncExecutor.MSG_SAVE, operation.saveOrUpdate(data), this);
                return this;
            }
        });

        return 1;
    }

    @Override
    public int saveOrUpdate(final List<DATA> datas) {

        executeOperator(new AsyncTask(datas) {
            @Override
            public Object call() {
                sendAsyncMessage(AsyncExecutor.MSG_SAVE, operation.saveOrUpdate(datas), this);
                return this;
            }
        });

        return datas.size();
    }

    private void executeOperator(AsyncTask task) {
        AsyncListener listener = onceListener;
        if (listener == null)
            listener = this.listener;

        onceListener = null;

        task.listener = listener;
        asyncExecutor.execute(task);
    }

    @Override
    public DATA read(final DATA data) {

        executeReadOperator(new AsyncTask(data) {
            @Override
            public Object call() {
                rets = operation.read(data);
                sendAsyncMessage(AsyncExecutor.MSG_READ, rets == null ? 0 : 1, this);
                return this;
            }
        });

        return data;
    }

    @Override
    public List<DATA> read(final Map<String, Object> objects) {

        executeReadOperator(new AsyncTask(objects) {
            @Override
            public Object call() {

                List ls = operation.read(objects);
                rets = ls;

                sendAsyncMessage(AsyncExecutor.MSG_READ, ls.size(), this);
                return this;
            }
        });

        return Collections.emptyList();
    }

    @Override
    public List<DATA> read(final int pageSize, final int offset, final Map<String, Object> object) {

        executeReadOperator(new AsyncTask(object) {
            @Override
            public Object call() {
                List ls = operation.read(pageSize, offset, object);
                rets = ls;
                sendAsyncMessage(AsyncExecutor.MSG_READ, ls.size(), this);
                return this;
            }
        });

        return Collections.emptyList();
    }

    @Override
    public List<DATA> read() {

        executeReadOperator(new AsyncTask() {
            @Override
            public Object call() {
                List ls = operation.read();
                rets = ls;
                sendAsyncMessage(AsyncExecutor.MSG_READ, ls.size(), this);
                return this;
            }
        });

        return Collections.emptyList();
    }

    private void executeReadOperator(AsyncTask task) {
        AsyncListener listener = onceListener;
        if (listener == null)
            listener = this.listener;

        onceListener = null;
        task.listener = listener;

        AsyncReadListener readListener = onceReadListener;
        if (readListener == null) {
            readListener = this.readListener;
        }
        onceReadListener = null;
        task.readListener = readListener;

        asyncExecutor.execute(task);
    }

    @Override
    public int delete(final DATA data) {

        executeOperator(new AsyncTask(data) {
            @Override
            public Object call() throws Exception {
                sendAsyncMessage(AsyncExecutor.MSG_DELETE, operation.delete(data), this);
                return this;
            }
        });

        return 0;
    }

    @Override
    public int delete(final Map<String, Object> objects) {
        executeOperator(new AsyncTask(objects) {
            @Override
            public Object call() throws Exception {
                sendAsyncMessage(AsyncExecutor.MSG_DELETE, operation.delete(objects), this);
                return this;
            }
        });
        return 0;
    }

    @Override
    public int delete(final List<DATA> datas) {

        executeOperator(new AsyncTask(datas) {
            @Override
            public Object call() throws Exception {
                sendAsyncMessage(AsyncExecutor.MSG_DELETE, operation.delete(datas), this);
                return this;
            }
        });

        return 0;
    }

    @Override
    public int delete() {
        executeOperator(new AsyncTask() {
            @Override
            public Object call() throws Exception {
                sendAsyncMessage(AsyncExecutor.MSG_DELETE, operation.delete(), this);
                return this;
            }
        });
        return 0;
    }

    @Override
    public int count() {

        executeOperator(new AsyncTask() {
            @Override
            public Object call() throws Exception {
                sendAsyncMessage(AsyncExecutor.MSG_COUNT, operation.count(), this);
                return this;
            }
        });

        return 0;
    }

    @Override
    public int count(final DATA data) {

        executeOperator(new AsyncTask() {
            @Override
            public Object call() throws Exception {
                sendAsyncMessage(AsyncExecutor.MSG_COUNT, operation.count(data), this);
                return this;
            }
        });

        return 0;
    }

    @Override
    public int count(final Map<String, Object> objects) {

        executeOperator(new AsyncTask() {
            @Override
            public Object call() throws Exception {
                sendAsyncMessage(AsyncExecutor.MSG_COUNT, operation.count(objects), this);
                return this;
            }
        });

        return 0;
    }

    private AsyncListener listener;
    private AsyncListener onceListener;

    private AsyncReadListener readListener;
    private AsyncReadListener onceReadListener;


    public void setListener(AsyncListener listener) {
        this.listener = listener;
    }

    public AsyncDatabaseOperator<DATA> onListener(AsyncListener listener) {
        onceListener = listener;
        return this;
    }

    public void setReadListener(AsyncReadListener readListener) {
        this.readListener = readListener;
    }

    public AsyncDatabaseOperator<DATA> onReadListener(AsyncReadListener listener) {
        onceReadListener = listener;
        return this;
    }

    public interface AsyncReadListener<DATA> extends AsyncListener {
        void onRead(int count, Object object, List<DATA> rets);
    }

    public interface AsyncListener {
        void onDone(int count, Object src);
    }

    static class AsyncExecutor extends Handler implements ThreadFactory {

        final static int MSG_SAVE = 100;
        final static int MSG_READ = 101;
        final static int MSG_DELETE = 102;
        final static int MSG_COUNT = 103;

        static Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                Log.e("AsyncDatabaseOperator", "uncaughtException: ", e);
            }
        };

        private final String TAG;
        private final ExecutorService asyncService;
        private final AsyncDatabaseOperator asyncDatabaseOperator;

        public AsyncExecutor(AsyncDatabaseOperator asyncDatabaseOperator) {
            super(Looper.getMainLooper());
            this.TAG = asyncDatabaseOperator.getLogTag();
            this.asyncDatabaseOperator = asyncDatabaseOperator;
            this.asyncService = Executors.newSingleThreadExecutor(this);
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("Async" + asyncDatabaseOperator.getLogTag() + "#" + thread.getId());
            thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            return thread;
        }

        @Override
        public void handleMessage(Message msg) {


            switch (msg.what) {
                case MSG_READ:
                    if (msg.obj != null) {
                        AsyncTask asyncTask = (AsyncTask) msg.obj;
                        if (asyncTask.readListener != null)
                            asyncTask.readListener.onRead(msg.arg1, asyncTask.src, (List) asyncTask.rets);
                    }
                case MSG_SAVE:
                case MSG_DELETE:
                case MSG_COUNT:
                    if (msg.obj != null) {
                        AsyncTask asyncTask = (AsyncTask) msg.obj;
                        if (asyncTask.listener != null)
                            asyncTask.listener.onDone(msg.arg1, asyncTask.src);
                    }
                    break;
                default:
                    Log.d(TAG, "handleMessage: dispatch to custom handleMessage");
                    asyncDatabaseOperator.handleMessage(msg);
            }
        }

        public void execute(AsyncTask command) {
            asyncService.submit(command);
        }

        public void sendAsyncMessage(int msg, int count, AsyncTask objects) {
            Message message = obtainMessage(msg);
            message.arg1 = count;
            message.obj = objects;

            message.sendToTarget();
        }
    }

    static abstract class AsyncTask implements Callable {
        Object src;
        Object rets;

        AsyncListener listener;
        AsyncReadListener readListener;

        AsyncTask(Object data) {
            src = data;
        }

        AsyncTask() {
        }
    }

    public final void execute(final Runnable command, AsyncListener listener) {
        asyncExecutor.execute(new AsyncTask() {
            @Override
            public Object call() {
                try {
                    command.run();
                } catch (Exception e) {
                    Log.e(TAG, "run: ", e);
                }
                return this;
            }
        });
    }

    public void handleMessage(Message msg) {

    }

    public void sendMessage(Message message) {
        Message msg = asyncExecutor.obtainMessage(message.what);
        msg.copyFrom(message);
        msg.sendToTarget();
    }

    private void sendAsyncMessage(int msg, int count, AsyncTask task) {
        asyncExecutor.sendAsyncMessage(msg, count, task);
    }
}
