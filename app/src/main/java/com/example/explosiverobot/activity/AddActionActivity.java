package com.example.explosiverobot.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.explosiverobot.R;
import com.example.explosiverobot.base.activity.BaseActivity;
import com.example.explosiverobot.db.manager.ActionItemDbManager;
import com.example.explosiverobot.modle.ActionItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 添加动作数据
 *
 * @author Guanluocang
 *         created at 2017/10/21 11:55
 */
public class AddActionActivity extends Activity implements View.OnClickListener {

    private final static int PICK_PIC = 100;

    private static final int ADD_ACTION_RESUL_TCODE = 2;
    @BindView(R.id.et_actionName)
    EditText etActionName;
    @BindView(R.id.tv_actionPic)
    TextView tvActionPic;
    @BindView(R.id.tv_save)
    TextView tvSave;

    private ActionItemDbManager actionItemDbManager;

    String mActionName = "";//动作名称
    String mPicPath = "";//图片路径
    //选取图片
    public static Uri imageUri1;

    private String tabName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_action);
        ButterKnife.bind(this);
        initView();
        initData();
        setListener();
    }

    protected void initView() {
        tabName = getIntent().getStringExtra("tabName");
        actionItemDbManager = new ActionItemDbManager();
    }

    protected void initData() {

    }

    protected void setListener() {
        tvActionPic.setOnClickListener(this);
        tvSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //提交
            case R.id.tv_save:
                mActionName = etActionName.getText().toString().trim();
                mPicPath = tvActionPic.getText().toString().trim();
                if (!"".equals(mActionName) && !"".equals(mPicPath)) {
                    isSaveAction();
                } else {
                    Toast.makeText(AddActionActivity.this, "请填写动作信息", Toast.LENGTH_SHORT);
                }
                break;
            //添加图片
            case R.id.tv_actionPic:
                doPickPic();
                break;
        }
    }

    /**
     * 判断是否已经存在该Action
     */
    public void isSaveAction() {

        List<ActionItem> userInfos = actionItemDbManager.queryByItemName(mActionName);
        if (!userInfos.isEmpty()) {
            Toast.makeText(AddActionActivity.this, "请勿添加相同的问题！", Toast.LENGTH_SHORT);
        } else {
            doCommit();
        }
    }

    /**
     * 提交
     */
    public void doCommit() {
        actionItemDbManager.insert(new ActionItem(mActionName, mPicPath, "1",tabName));
        Toast.makeText(AddActionActivity.this, "添加成功", Toast.LENGTH_SHORT);
        setResult(ADD_ACTION_RESUL_TCODE);
        finish();
    }

    /**
     * 选择图片
     */
    public void doPickPic() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PIC) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(AddActionActivity.this, "点击取消从相册选择", Toast.LENGTH_SHORT);
                return;
            }

            try {
                imageUri1 = data.getData();
                String ss = imageUri1.getPath();
                String[] pro = {MediaStore.Images.Media.DATA};
                //好像是android多媒体数据库的封装接口，具体的看Android文档
                Cursor cursor = managedQuery(imageUri1, pro, null, null, null);
                Cursor cursor1 = getContentResolver().query(imageUri1, pro, null, null, null);
                //拿到引索
                int index = cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //移动到光标开头
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                String path = cursor.getString(index);
                tvActionPic.setText(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
