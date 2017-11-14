package com.example.explosiverobot.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dell on 2017/11/14.
 */

public class WriteSd {
    private Context context;
    String filePath = android.os.Environment.getExternalStorageDirectory() + "/aapaibao";

    public WriteSd(Context context) {
        this.context = context;
    }

    public void isExit() {
        if (!isExist()) {
            write();
        }
    }

    private void write() {
        InputStream inputStream;
        try {
            inputStream = context.getResources().getAssets().open("addressId.db");
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath + "/database.db");
            byte[] buffer = new byte[512];
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
            System.out.println("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isExist() {
        File file = new File(filePath + "/database.db");
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
