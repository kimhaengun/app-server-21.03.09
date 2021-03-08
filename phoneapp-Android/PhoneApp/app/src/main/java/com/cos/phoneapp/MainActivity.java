package com.cos.phoneapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity2";
    private RecyclerView rvphone;
    private PhoneAdapter phoneAdapter;
    private FloatingActionButton fabSave;
    private List<Phone> phones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
        Call<CMRespDto<List<Phone>>> call = phoneService.findAll();


        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
            @Override
            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                CMRespDto<List<Phone>> cmRespDto = response.body();
                List<Phone> phones = cmRespDto.getData();
                //어댑터에 넘기기
                LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL,false);
                rvphone = findViewById(R.id.rv_phone);
                rvphone.setLayoutManager(manager);

                phoneAdapter = new PhoneAdapter(phones);
                rvphone.setAdapter(phoneAdapter);

                Log.d(TAG, "onResponse: 응답 받은 데이터 :"+phones);
            }

            @Override
            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                Log.d(TAG, "onFailure: findAll() 실패");
            }
        });
        //데이터 전체 뿌리기 끝


        fabSave=findViewById(R.id.fab_save);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.fab_add, null);
                //Editext
                final EditText etName = dialogView.findViewById(R.id.et_name);
                final EditText etTel = dialogView.findViewById(R.id.et_tel);

                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("연락처 등록");
                dlg.setView(dialogView);
                dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         String name = etName.getText().toString();
                         String tel = etTel.getText().toString();
                        Log.d(TAG, "onClick: name : "+name);
                        Log.d(TAG, "onClick: tel : "+tel);

                        Phone phone = new Phone();
                        phone.setName(etName.getText().toString());
                        phone.setTel(etTel.getText().toString());

                        additem(phone);


                    }
                });
                dlg.setNegativeButton("닫기", null);
                dlg.show();
            }
            public void additem(Phone phone){
                Call<CMRespDto<List<Phone>>> call = phoneService.save(phone);
                //enqueue 받아온 데이터 처리하기
                call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
                    @Override
                    public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                        CMRespDto<List<Phone>> cmRespDto = response.body();
                        List<Phone> phones = cmRespDto.getData();
                        phones.add(phone);
                        phoneAdapter.addItem(phones);
                    }

                    @Override
                    public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                        Log.d(TAG, "onFailure: 저장 실패");
                    }
                });
            }
        });


    }

}