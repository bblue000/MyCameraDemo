package com.vip.camerademo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

/**
 * {@doc}
 * <p/>
 * Created by Yin Yong on 17/2/6.
 */
public class UseSysIntentDemo extends Activity {

    /**
     * relative classes
     */
    // Intent

    public static final int REQ_IMAGE = 1;
    public static final int REQ_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sysintent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "用户取消", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = data.getExtras();
        if (null != bundle) {
            Bitmap bm = bundle.getParcelable("data");
            Toast.makeText(this, String.valueOf(bm), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, String.valueOf(data.getData()), Toast.LENGTH_SHORT).show();

        switch (requestCode) {
            case REQ_IMAGE:
                break;
            case REQ_VIDEO:
                break;
        }
    }

    public void image(View view) {
//        MediaStore.EXTRA_OUTPUT - 这个关键字用于创建一个Uri对象来指定一个路径和文件名保存照片。
//        当然，这个设置是可选的，不过强烈推荐使用该方法来保存照片。
//        如果你没有在指定该关键字的值，系统的camera应用会将照片以默认的名字保存在一个默认的地方，
//        当你指定了该关键字的值，数据以Intent.getData()方法返回Uri对象。

        // 不提供MediaStore.EXTRA_OUTPUT时, 返回的bitmap, 存储键为"data"
        // camera app 源码
//        if (mSaveUri != null) {
//            OutputStream outputStream = null;
//            try {
//                outputStream = mContentResolver.openOutputStream(mSaveUri);
//                outputStream.write(data);
//                outputStream.close();
//                setResult(RESULT_OK);
//                finish();
//            } catch (IOException ex) {
//                // ignore exception
//            } finally {
//                Util.closeSilently(outputStream);
//            }
//        } else {
//            Bitmap bitmap = createCaptureBitmap(data);
//            setResult(RESULT_OK,
//                    new Intent("inline-data").putExtra("data", bitmap));
//            finish();
//        }
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQ_IMAGE);
//        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "tmp10086.jpeg"))), REQ_IMAGE);
    }

    public void video(View view) {
//        MediaStore.EXTRA_OUTPUT - 该关键字和拍照使用的关键字一样，意思就是制定一个路径和文件名来构建一个Uri对象来保存录像结果，同样录像结果会以Intent.getData()的方法返回Uri对象。
//        MediaStore.EXTRA_VIDEO_QUALITY - 该关键字用于指定拍摄的录像质量，参数0表示低质量，参数1表示高质量。
//        MediaStore.EXTRA_DURATION_LIMIT - 该关键之用于指定拍摄的录像的时间限制，单位是秒。
//        MediaStore.EXTRA_SIZE_LIMIT - 该关键字用于指定拍摄的录像文件大小限制，单位值byte。

        // 不提供MediaStore.EXTRA_OUTPUT时, 返回uri, Intent.getData()
        startActivityForResult(new Intent(MediaStore.ACTION_VIDEO_CAPTURE), REQ_VIDEO);
    }
}
