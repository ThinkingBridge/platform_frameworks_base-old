package com.android.systemui.statusbar.toggles;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.util.Log;

import com.android.internal.util.MemInfoReader;
import com.android.systemui.R;

public class MemorySizeToggle extends StatefulToggle {

    long secServerMem;
    long availMem;
    long totalMem;
    ActivityManager mAm;
    ActivityManager.MemoryInfo mMemInfo;
    MemInfoReader mMemInfoReader = new MemInfoReader();

    @Override
    protected void init(Context c, int style) {
        super.init(c, style);
        setIcon(R.drawable.ic_qs_settings);
        mAm = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
        mMemInfo = new ActivityManager.MemoryInfo();
        refresh();
    }

    @Override
    public boolean onLongClick(View v) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(ComponentName
                .unflattenFromString("com.android.settings/.RunningServices"));
        intent.addCategory("android.intent.category.LAUNCHER");
        
        startActivity(intent);
        
        return super.onLongClick(v);
    }

    public void refresh() {
        mAm.getMemoryInfo(mMemInfo);
        secServerMem = mMemInfo.secondaryServerThreshold;
        mMemInfoReader.readMemInfo();
        availMem = (mMemInfoReader.getFreeSize() + mMemInfoReader.getCachedSize() -
                   secServerMem) / 1048576;
        totalMem = (mMemInfoReader.getTotalSize()) / 1048576;
        setLabel(String.valueOf(availMem + "MB" + " / " + totalMem + "MB"));
    }

    @Override
    public void doEnable() {
        refresh();
    }

    @Override
    public void doDisable() {
        refresh();
    }

    @Override
    protected void updateView() {
        super.updateView();
        updateCurrentState(State.DISABLED);
    }
}


