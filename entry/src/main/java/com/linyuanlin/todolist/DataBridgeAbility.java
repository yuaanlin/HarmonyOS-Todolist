package com.linyuanlin.todolist;

import com.linyuanlin.todolist.model.Todo;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.*;
import ohos.utils.net.Uri;
import ohos.utils.zson.ZSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBridgeAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");

    private static final int GET_ALL_TODO = 1001;
    private static final int UPDATE_TODO = 1002;
    private static final int DELETE_TODO = 1003;

    private static final String BASE_URI = "dataability:///com.linyuanlin.todolist.TodoDataAbility";
    private static final String DATA_PATH = "/todo";

    private static final String DB_COLUMN_TODO_ID = "id";
    private static final String DB_COLUMN_TITLE = "title";
    private static final String DB_COLUMN_DESCRIPTION = "description";
    private static final String DB_COLUMN_DEADLINE = "deadline";
    private static final String DB_COLUMN_DONE = "done";

    private final String[] columns = new String[]{DB_COLUMN_TODO_ID, DB_COLUMN_TITLE, DB_COLUMN_DESCRIPTION, DB_COLUMN_DEADLINE, DB_COLUMN_DONE};
    private final MyRemote remote = new MyRemote();
    private DataAbilityHelper databaseHelper;

    @Override
    public void onStart(Intent intent) {
        HiLog.error(LABEL_LOG, "DataBridgeAbility::onStart");
        super.onStart(intent);
        this.databaseHelper = DataAbilityHelper.creator(this);

        try {
            insert(1, "买菜", "买煮晚餐要用的食材", "2021-08-01", false);
            insert(2, "洗衣服", "把衣服拿去洗", "2021-09-01", false);
        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackground() {
        super.onBackground();
        HiLog.info(LABEL_LOG, "DataBridgeAbility::onBackground");
    }

    @Override
    public void onStop() {
        super.onStop();
        HiLog.info(LABEL_LOG, "DataBridgeAbility::onStop");
    }

    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
    }

    @Override
    public IRemoteObject onConnect(Intent intent) {
        super.onConnect(intent);
        return remote.asObject();
    }

    @Override
    public void onDisconnect(Intent intent) {
    }

    private List<Todo> queryAllTodos() throws DataAbilityRemoteException {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH), columns, predicates);

        if (resultSet == null || resultSet.getRowCount() == 0) {
            return new ArrayList<>();
        }

        resultSet.goToFirstRow();

        List<Todo> results = new ArrayList<>();

        do {
            int id = resultSet.getInt(resultSet.getColumnIndexForName(DB_COLUMN_TODO_ID));
            String title = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_TITLE));
            String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
            String deadline = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DEADLINE));
            int done = resultSet.getInt(resultSet.getColumnIndexForName(DB_COLUMN_DONE));
            results.add(new Todo(id, title, description, deadline, done != 0));
        } while (resultSet.goToNextRow());

        return results;

    }

    private void insert(int id, String title, String description, String deadline, boolean done) throws DataAbilityRemoteException {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putInteger(DB_COLUMN_TODO_ID, id);
        valuesBucket.putString(DB_COLUMN_TITLE, title);
        valuesBucket.putString(DB_COLUMN_DESCRIPTION, description);
        valuesBucket.putString(DB_COLUMN_DEADLINE, deadline);
        valuesBucket.putBoolean(DB_COLUMN_DONE, done);
        databaseHelper.insert(Uri.parse(BASE_URI + DATA_PATH), valuesBucket);
    }

    private void deleteTodo(int id) throws DataAbilityRemoteException {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo("id", id);
        databaseHelper.delete(Uri.parse(BASE_URI + DATA_PATH), predicates);
    }

    class MyRemote extends RemoteObject implements IRemoteBroker {

        MyRemote() {
            super("MyService_MyRemote");
        }

        @Override
        public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) {

            switch (code) {
                case GET_ALL_TODO: {
                    try {
                        List<Todo> r = queryAllTodos();
                        reply.writeString(ZSONObject.toZSONString(r));
                        return true;
                    } catch (DataAbilityRemoteException e) {
                        e.printStackTrace();
                    }
                }
                case UPDATE_TODO: {
                    try {
                        ZSONObject todo = ZSONObject.stringToZSON(data.readString());

                        int id = todo.getInteger("id");
                        String title = todo.getString("title");
                        String description = todo.getString("description");
                        String deadline = todo.getString("deadline");
                        boolean done = todo.getBoolean("done");

                        DataAbilityPredicates predicates = new DataAbilityPredicates();
                        predicates.equalTo("id", id);

                        ResultSet resultSet = null;
                        resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH), columns, predicates);

                        if (resultSet == null || resultSet.getRowCount() == 0) {
                            try {
                                insert(id, title, description, deadline, done);
                                List<Todo> r = queryAllTodos();
                                reply.writeString(ZSONObject.toZSONString(r));
                                return true;
                            } catch (DataAbilityRemoteException e) {
                                e.printStackTrace();
                            }
                        }

                        ValuesBucket valuesBucket = new ValuesBucket();
                        valuesBucket.putString("title", title);
                        valuesBucket.putString("description", description);
                        valuesBucket.putString("deadline", deadline);
                        valuesBucket.putBoolean("done", done);

                        databaseHelper.update(Uri.parse(BASE_URI + DATA_PATH), valuesBucket, predicates);
                        List<Todo> r = queryAllTodos();
                        reply.writeString(ZSONObject.toZSONString(r));
                        return true;
                    } catch (DataAbilityRemoteException e) {
                        e.printStackTrace();
                        return false;
                    }

                }
                case DELETE_TODO: {
                    try {
                        ZSONObject todo = ZSONObject.stringToZSON(data.readString());
                        int id = todo.getInteger("id");
                        deleteTodo(id);
                        return true;
                    } catch (DataAbilityRemoteException e) {
                        e.printStackTrace();
                    }
                }
                default: {
                    Map<String, Object> result = new HashMap<>(5);
                    result.put("code", 500);
                    result.put("msg", "invalid code");
                    reply.writeString(ZSONObject.toZSONString(result));
                    return false;
                }
            }
        }

        @Override
        public IRemoteObject asObject() {
            return this;
        }

    }
}