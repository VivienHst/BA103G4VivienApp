package com.beanlife.mem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beanlife.Common;
import com.beanlife.CommonTask;
import com.beanlife.R;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;
import static android.view.View.VISIBLE;

/**
 * Created by Vivien on 2017/10/10.
 * 驗證未完成
 */

public class MemberRegisterFragment extends Fragment {

    private View view;
    private ImageView regMemEdIv;
    private TextView regMemAcTv, regMemLvTv;
    private EditText regMemAcEt, regMemPswEt, regMemPswChkEt, regMemLnameEt, regMemFnameEt, regMemEmailEt,
            regMemPhoneEt, regMemAddEt, regMemRegEt;
    private Spinner regMemProcSp, regMemRoastSp;
    private LinearLayout chkPswLl;
    private Button chkBtn, cancelBtn;
    private MemVO memVO;
    private String mem_ac;
    private CommonTask retrieveMemVO, retrieveMemImg;
    private File file;
    private static final int REQUEST_TAKE_PICTURE = 0;
    private static final int REQUEST_PICK_IMAGE = 1;
    private byte[]image;
    private String memProc;
    private String[] likeSetToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.member_register_fragment, container, false);
        findView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(){
        regMemEdIv = (ImageView) view.findViewById(R.id.reg_mem_ed_img);
        regMemAcEt = (EditText) view.findViewById(R.id.reg_mem_ac_et);
        regMemPswEt = (EditText) view.findViewById(R.id.reg_mem_psw_et);
        regMemPswChkEt = (EditText) view.findViewById(R.id.reg_mem_psw_chk_et);
        regMemLnameEt = (EditText) view.findViewById(R.id.reg_mem_lname_et);
        regMemFnameEt = (EditText) view.findViewById(R.id.reg_mem_fname_et);
        regMemEmailEt = (EditText) view.findViewById(R.id.reg_mem_email_et);
        regMemPhoneEt = (EditText) view.findViewById(R.id.reg_mem_phone_et);
        regMemAddEt = (EditText) view.findViewById(R.id.reg_mem_add_et);
        regMemRegEt = (EditText) view.findViewById(R.id.reg_mem_reg_et);
        chkBtn = (Button) view.findViewById(R.id.mem_ed_chk_btn);
        cancelBtn = (Button) view.findViewById(R.id.mem_ed_cancel_btn);
        regMemProcSp = (Spinner) view.findViewById(R.id.reg_mem_proc_sp);
        regMemRoastSp = (Spinner) view.findViewById(R.id.reg_mem_roast_sp);


        chkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInput()){
                    setMemVO();
                    switchFragment(new MemberCenterFragment());
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(new MemberCenterFragment());
            }
        });

        regMemEdIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picChangeAlert();
            }
        });
    }
//
//    private byte[]getImg(String mem_ac){
//        retrieveMemImg = (CommonTask) new CommonTask().execute(Common.MEM_URL,"getImageNoShrink","mem_ac",mem_ac);
//        String memImgBase64 = "";
//        try {
//            memImgBase64 = retrieveMemImg.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        image = Base64.decode(memImgBase64, Base64.DEFAULT);
//        return image;
//    }

    private void setMemVO(){
        memVO.setMem_ac(regMemAcEt.getText().toString());
        memVO.setMem_pwd(regMemPswEt.getText().toString());
        memVO.setMem_phone(regMemPhoneEt.getText().toString());

        if (regMemLnameEt.getText().toString().trim().length()>0){
            memVO.setMem_lname(regMemLnameEt.getText().toString());
        }
        if (regMemFnameEt.getText().toString().trim().length()>0){
            memVO.setMem_lname(regMemFnameEt.getText().toString());
        }
        if (regMemEmailEt.getText().toString().trim().length()>0){
            memVO.setMem_email(regMemEmailEt.getText().toString());
        }
        if (regMemAddEt.getText().toString().trim().length()>0){
            memVO.setMem_add(regMemAddEt.getText().toString());
        }

//        if (regMemFnameEt.getText().toString().trim().length()>0){
//            memVO.setMem_set(regMemRegEt.getText().toString() + "," + likeSetToken[1] + "," + likeSetToken[2]);
//        }

        String imgToBase64 = Base64.encodeToString(image, Base64.DEFAULT);

        Gson gson = new Gson();
        String memVOString = gson.toJson(memVO);
        retrieveMemVO =(CommonTask) new CommonTask().execute(Common.MEM_URL, "insertMem" , "memVO",
                memVOString, "memPic", imgToBase64);
    }

    private boolean checkInput(){
        boolean checkInput = false;
        if(regMemPswEt.getText().length() < 5){
            Toast.makeText(view.getContext(), "密碼必須大於五碼", Toast.LENGTH_SHORT).show();
        } else if(!regMemPswEt.getText().toString().equals(regMemPswChkEt.getText().toString())){
            Toast.makeText(view.getContext(), "確認密碼不符", Toast.LENGTH_SHORT).show();
        } else if(regMemPhoneEt.getText().toString().trim().length() < 10){
            Toast.makeText(view.getContext(), "電話格式錯誤", Toast.LENGTH_SHORT).show();
        } else if(!regMemEmailEt.getText().toString().trim().isEmpty() &&
                !regMemEmailEt.getText().toString().contains("@")){
            Toast.makeText(view.getContext(), "信箱格式錯誤", Toast.LENGTH_SHORT).show();} else {
            checkInput = true;
        }
        return checkInput;
    }

    private void switchFragment(Fragment fragment) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void picChangeAlert(){
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle("新增頭像");
        myAlertDialog.setMessage("選擇照片來源");

        //選擇相簿照片
        DialogInterface.OnClickListener albumOnClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_IMAGE);
            }
        };

        //使用相機
        DialogInterface.OnClickListener cameraOnClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                Uri contentUri = FileProvider.getUriForFile(getActivity(),
                        getActivity().getPackageName() + ".provider" ,file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                if(isIntentAvailable(getActivity(), intent)){
                    startActivityForResult(intent, REQUEST_TAKE_PICTURE);
                }

            }
        };

        myAlertDialog.setPositiveButton("相簿", albumOnClick);
        myAlertDialog.setNegativeButton("相機", cameraOnClick);
        myAlertDialog.show();
    }

    private boolean isIntentAvailable(Context context, Intent intent){
        PackageManager packageManager= context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE:
                    Bitmap picture = BitmapFactory.decodeFile(file.getPath());
                    regMemEdIv.setImageBitmap(picture);
                    ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                    picture.compress(Bitmap.CompressFormat.JPEG, 100, out1);
                    image = out1.toByteArray();
                    break;
                case REQUEST_PICK_IMAGE:
                    Uri uri = intent.getData();
                    String[] columns = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(uri, columns,
                            null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String imagePath = cursor.getString(0);
                        cursor.close();
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        regMemEdIv.setImageBitmap(bitmap);
                        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out2);
                        image = out2.toByteArray();
                    }
                    break;
            }
        }
    }
}
