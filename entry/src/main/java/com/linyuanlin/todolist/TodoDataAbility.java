package com.linyuanlin.todolist;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.data.DatabaseHelper;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.dataability.DataAbilityUtils;
import ohos.data.rdb.*;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.PacMap;
import ohos.utils.net.Uri;

import java.io.FileDescriptor;

public class TodoDataAbility extends Ability {
    private static final String DB_NAME = "tododataability.db";

    private static final String DB_TAB_NAME = "todo";
    private static final String DB_COLUMN_TODO_ID = "id";
    private static final String DB_COLUMN_TITLE = "title";
    private static final String DB_COLUMN_DESCRIPTION = "description";
    private static final String DB_COLUMN_DEADLINE = "deadline";
    private static final String DB_COLUMN_DONE = "done";

    private static final int DB_VERSION = 1;

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");
    private final StoreConfig config = StoreConfig.newDefaultConfig(DB_NAME);
    private final RdbOpenCallback rdbOpenCallback = new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore store) {
            store.executeSql("create table if not exists "
                    + DB_TAB_NAME + " ("
                    + DB_COLUMN_TODO_ID + " integer primary key, "
                    + DB_COLUMN_TITLE + " text not null, "
                    + DB_COLUMN_DESCRIPTION + " text not null, "
                    + DB_COLUMN_DEADLINE + " text not null, "
                    + DB_COLUMN_DONE + " bool not null)");
        }

        @Override
        public void onUpgrade(RdbStore store, int oldVersion, int newVersion) {
        }
    };
    private RdbStore rdbStore;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        HiLog.info(LABEL_LOG, "PersonDataAbility onStart");
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        rdbStore = databaseHelper.getRdbStore(config, DB_VERSION, rdbOpenCallback, null);
    }

    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        ResultSet resultSet = rdbStore.query(rdbPredicates, columns);
        if (resultSet == null) {
            HiLog.info(LABEL_LOG, "resultSet is null");
        }
        return resultSet;
    }

    @Override
    public int insert(Uri uri, ValuesBucket value) {
        ValuesBucket values = new ValuesBucket();
        values.putInteger(DB_COLUMN_TODO_ID, value.getInteger(DB_COLUMN_TODO_ID));
        values.putString(DB_COLUMN_TITLE, value.getString(DB_COLUMN_TITLE));
        values.putString(DB_COLUMN_DESCRIPTION, value.getString(DB_COLUMN_DESCRIPTION));
        values.putString(DB_COLUMN_DEADLINE, value.getString(DB_COLUMN_DEADLINE));
        values.putBoolean(DB_COLUMN_DONE, value.getBoolean(DB_COLUMN_DONE));
        int index = (int) rdbStore.insert(DB_TAB_NAME, values);
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        int index = rdbStore.delete(rdbPredicates);
        HiLog.info(LABEL_LOG, "delete: " + index);
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        int index = rdbStore.update(value, rdbPredicates);
        HiLog.info(LABEL_LOG, "update: " + index);
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public FileDescriptor openFile(Uri uri, String mode) {
        return null;
    }

    @Override
    public String[] getFileTypes(Uri uri, String mimeTypeFilter) {
        return new String[0];
    }

    @Override
    public PacMap call(String method, String arg, PacMap extras) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

}