package com.cos.phoneapp;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//어댑터와 RecyclerView와 연결(Databinding, MVVM 사용x)
public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.MyViewHolder>{
    private static final String TAG = "PhoneAdapter";
    private final List<Phone> phones;

    private MainActivity mainActivity;

    public PhoneAdapter(List<Phone> phones) {
        this.phones = phones;
    }

    public void addItem(List<Phone> phone){
        phones.add((Phone) phone);
        notifyDataSetChanged();
    }
    public void removeItem(Phone position){
        phones.remove(position);
        notifyDataSetChanged();
    }

//    public void updateItem(int Position, Phone phone){
//        phones.get(position).setId(phone.getId());
//        phones.get(position).setName(phone.getName());
//        phones.get(position).setTel(phone.getTel());
//        notifyDataSetChanged();
//
//    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); // 고정이다.
        View view = inflater.inflate(R.layout.phone_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setItem(phones.get(position));
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private TextView tel;
        private LinearLayout phoneitem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            tel = itemView.findViewById(R.id.tel);

            phoneitem = itemView.findViewById(R.id.phoneitem);
            phoneitem.setOnClickListener(v -> {

                View dialogView = v.inflate(v.getContext(),R.layout.fab_add, null);
                //Editext
                final EditText etName = dialogView.findViewById(R.id.et_name);
                final EditText etTel = dialogView.findViewById(R.id.et_tel);

                etName.setText(name.getText());
                etTel.setText(tel.getText());

                AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());
                dlg.setTitle("연락처 수정");
                dlg.setView(dialogView);
                dlg.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = getAdapterPosition();
                        Phone phone = phones.get(position);
                        phone.setName(etName.getText().toString());
                        phone.setTel(etTel.getText().toString());

                        PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
                        Call<CMRespDto<List<Phone>>> call = phoneService.update(phone.getId(),phone);
                        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
                            @Override
                            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {

                            }
                        });

                    }
                });
                dlg.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
                        Call<CMRespDto<List<Phone>>> call = phoneService.delete(phones.get(getAdapterPosition()).getId());
                        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
                            @Override
                            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                                Log.d(TAG, "onResponse: Position : "+getAdapterPosition());
                                removeItem(phones.get(getAdapterPosition()));

                            }

                            @Override
                            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {

                            }
                        });
                    }
                });
                dlg.show();
            });
        }

        public void setItem(Phone phone){
            name.setText(phone.getName());
            tel.setText(phone.getTel());
        }


    }
}
