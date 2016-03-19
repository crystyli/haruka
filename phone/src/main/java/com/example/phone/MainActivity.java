package com.example.phone;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(0);
        for(ApplicationInfo applicationInfo:applicationInfos){
            int uid =applicationInfo.uid;
            long tx = TrafficStats.getUidTxBytes(uid);
            long rx = TrafficStats.getUidRxBytes(uid);
            Log.e("aaa", applicationInfo.packageName+"tx:"+tx+",rx"+rx );
        }
        long g32 = TrafficStats.getMobileTxBytes();
        long r32 =TrafficStats.getMobileRxBytes();
        Log.e("aaa", "g32:"+g32+",r32:"+r32);
        long tal1 = TrafficStats.getTotalTxBytes();
        long ral1 = TrafficStats.getTotalRxBytes();
        Log.e("aaa", "tall:"+tal1+",rall:"+ral1);
    }
}
