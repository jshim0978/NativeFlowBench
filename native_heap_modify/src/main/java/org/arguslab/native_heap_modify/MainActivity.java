package org.arguslab.native_heap_modify;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * @testcase_name native_heap_modify
 * @author Fengguo Wei
 * @author_mail fgwei521@gmail.com
 *
 * @description The value v of a source is sent to native lib via jni.
 * 				native lib leaks the sensitive data.
 * @dataflow heap_update() -> source -> data -> sink
 * @number_of_leaks 1
 * @challenges The analysis must be able to track data flow in both java and native to capture the data leakage.
 */
public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("heap_modify"); // "heap_modify.dll" in Windows, "libheap_modify.so" in Unixes
    }

    public static native void heapModify(Context myContext, Data data);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }

    private void leakImei() {
        Data data = new Data();
        heapModify(this.getApplicationContext(), data); // source
        Log.i("str", data.str); // sink
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                leakImei();
                return;
            }
        }
    }
}
