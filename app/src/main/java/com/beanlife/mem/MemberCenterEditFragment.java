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
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;
import static android.view.View.VISIBLE;

/**
 * Created by vivienhuang on 2017/10/3.
 */

public class MemberCenterEditFragment extends Fragment {

    private View view;
    private ImageView centerMemEdIv;
    private TextView centerMemAcTv, centerMemLvTv;
    private EditText centerMemPswEt, centerMemPswChkEt, centerMemLnameEt, centerMemFnameEt, centerMemEmailEt,
            centerMemPhoneEt, centerMemAddEt, centerMemRegEt;
    private Spinner centerMemProcSp, centerMemRoastSp;
    private LinearLayout chkPswLl;
    private Button chkBtn, cancelBtn;
    private MemVO memVO;
    private String mem_ac;
    private CommonTask retrieveMemVO, retrieveMemImg;
    private File file;
    private static final int REQUEST_TAKE_PICTURE = 0;
    private static final int REQUEST_PICK_IMAGE = 1;
    private byte[]image;
    private String memProc, memGrade;
    private String[] likeSetToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.member_center_edit_fragment, container, false);
        memVO = (MemVO) getArguments().getSerializable("memVO");
        memGrade = (String) getArguments().getSerializable("memGrade");
        findView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findView(){
        centerMemAcTv = (TextView) view.findViewById(R.id.center_mem_ac_ed_tv);
        centerMemLvTv = (TextView) view.findViewById(R.id.center_mem_lv_ed_tv);
        centerMemEdIv = (ImageView) view.findViewById(R.id.center_mem_ed_img);
        centerMemPswEt = (EditText) view.findViewById(R.id.center_mem_psw_et);
        centerMemPswChkEt = (EditText) view.findViewById(R.id.center_mem_psw_chk_et);
        centerMemLnameEt = (EditText) view.findViewById(R.id.center_mem_lname_et);
        centerMemFnameEt = (EditText) view.findViewById(R.id.center_mem_fname_et);
        centerMemEmailEt = (EditText) view.findViewById(R.id.center_mem_email_et);
        centerMemPhoneEt = (EditText) view.findViewById(R.id.center_mem_phone_et);
        centerMemAddEt = (EditText) view.findViewById(R.id.center_mem_add_et);
        centerMemRegEt = (EditText) view.findViewById(R.id.center_mem_reg_et);
        chkPswLl = (LinearLayout) view.findViewById(R.id.center_mem_psw_chk_ll);
        chkBtn = (Button) view.findViewById(R.id.mem_ed_chk_btn);
        cancelBtn = (Button) view.findViewById(R.id.mem_ed_cancel_btn);
        centerMemProcSp = (Spinner) view.findViewById(R.id.center_mem_proc_sp);
        centerMemRoastSp = (Spinner) view.findViewById(R.id.center_mem_roast_sp);

        byte[] memOrgImg = getImg(memVO.getMem_ac());
        Bitmap picture = BitmapFactory.decodeByteArray(memOrgImg, 0, memOrgImg.length);
        centerMemEdIv.setImageBitmap(picture);

        centerMemAcTv.setText(memVO.getMem_ac());
        centerMemLvTv.setText(memVO.getGrade_no().toString());
        centerMemPswEt.setText(memVO.getMem_pwd());
        centerMemPswChkEt.setText(memVO.getMem_pwd());
        centerMemLnameEt.setText(memVO.getMem_lname());
        centerMemFnameEt.setText(memVO.getMem_fname());
        centerMemEmailEt.setText(memVO.getMem_email());
        centerMemPhoneEt.setText(memVO.getMem_phone());
        centerMemAddEt.setText(memVO.getMem_add());
        centerMemLvTv.setText(memGrade);


        String likeSet = memVO.getMem_set();
        likeSetToken = likeSet.split(",");
        centerMemRegEt.setText(likeSetToken[0]);

        Resources res = getResources();

        //選擇處理法
        String[] procAr = res.getStringArray(R.array.prod_proc);

        if(!likeSetToken[1].equals(" ")){
            Integer procIndex = Arrays.asList(procAr).indexOf(likeSetToken[1]);
            centerMemProcSp.setSelection(procIndex, true);
        }else{
            centerMemProcSp.setSelection(0, true);
        }


        centerMemProcSp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                likeSetToken[1] = adapterView.getItemAtPosition(i).toString();
                if(likeSetToken[1].equals("請選擇")){
                    likeSetToken[1] = " ";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //選擇烘焙度
        String[] roastAr = res.getStringArray(R.array.prod_roast);
        if(!likeSetToken[2].equals(" ")){
            Integer roastIndex = Arrays.asList(roastAr).indexOf(likeSetToken[2]);
            centerMemRoastSp.setSelection(roastIndex, true);
        }else{
            centerMemRoastSp.setSelection(0, true);
        }

        centerMemRoastSp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                likeSetToken[2] = adapterView.getItemAtPosition(i).toString();
                if(likeSetToken[2].equals("請選擇")){
                    likeSetToken[2] = " ";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        centerMemPswEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!memVO.getMem_pwd().equals(centerMemPswEt.getText().toString())) {
                    chkPswLl.setVisibility(VISIBLE);
                } else {
                    chkPswLl.setVisibility(View.GONE);
                }
            }
        });

        chkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInput()){
                    setMemVO();
                    ImageView memPicIv = (ImageView) getActivity().findViewById(R.id.memPic_nvHeader);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    memPicIv.setImageBitmap(bitmap);
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

        centerMemEdIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picChangeAlert();
            }
        });
    }

    private byte[]getImg(String mem_ac){
        retrieveMemImg = (CommonTask) new CommonTask().execute(Common.MEM_URL,"getImageNoShrink","mem_ac",mem_ac);
        String memImgBase64 = "";
        try {
            memImgBase64 = retrieveMemImg.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        image = Base64.decode(memImgBase64, Base64.DEFAULT);
        return image;
    }

    private void setMemVO(){

        memVO.setMem_pwd(centerMemPswEt.getText().toString());
        memVO.setMem_lname(centerMemLnameEt.getText().toString());
        memVO.setMem_fname(centerMemFnameEt.getText().toString());
        memVO.setMem_email(centerMemEmailEt.getText().toString());
        memVO.setMem_phone(centerMemPhoneEt.getText().toString());
        memVO.setMem_add(centerMemAddEt.getText().toString());
        memVO.setMem_set(centerMemRegEt.getText().toString() + "," + likeSetToken[1] + ","
                + likeSetToken[2]);
        String imgToBase64 = Base64.encodeToString(image, Base64.DEFAULT);

        Gson gson = new Gson();
        String memVOString = gson.toJson(memVO);
        retrieveMemVO =(CommonTask) new CommonTask().execute(Common.MEM_URL, "updateMem" , "memVO",
                memVOString, "memPic", imgToBase64);
    }

    private boolean checkInput(){
        boolean checkInput = false;
        if(centerMemPswEt.getText().length() < 5){
            Toast.makeText(view.getContext(), "密碼必須大於五碼", Toast.LENGTH_SHORT).show();
        } else if(!centerMemPswEt.getText().toString().equals(centerMemPswChkEt.getText().toString())){
            Toast.makeText(view.getContext(), "確認密碼不符", Toast.LENGTH_SHORT).show();
        } else if(centerMemPhoneEt.getText().toString().trim().length() < 10){
            Toast.makeText(view.getContext(), "電話格式錯誤", Toast.LENGTH_SHORT).show();
        } else if(!centerMemEmailEt.getText().toString().trim().isEmpty() &&
                !centerMemEmailEt.getText().toString().contains("@")){
            Toast.makeText(view.getContext(), "信箱格式錯誤", Toast.LENGTH_SHORT).show();} else {
            checkInput = true;
        }  checkInput = true;

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
        myAlertDialog.setTitle("修改個人照片");
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
                    centerMemEdIv.setImageBitmap(picture);
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
                        centerMemEdIv.setImageBitmap(bitmap);
                        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out2);
                        image = out2.toByteArray();
                    }
                    break;
            }
        }
    }
}
