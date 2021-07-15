package com.linyuanlin.todolist;

import ohos.aafwk.ability.IAbilityContinuation;
import ohos.aafwk.content.Intent;
import ohos.ace.ability.AceAbility;
import ohos.hiviewdfx.HiLogLabel;

public class MainAbility extends AceAbility implements IAbilityContinuation {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Yuanlin");

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        requestPermissionsFromUser(new String[]{"ohos.permission.DISTRIBUTED_DATASYNC"}, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
