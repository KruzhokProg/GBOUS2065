package com.example.gbous2065.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.gbous2065.IMenuListener;
import com.example.gbous2065.Models.File;
import com.example.gbous2065.Network.ApiYandexClient;
import com.example.gbous2065.Network.ApiService;
import com.example.gbous2065.R;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder> {

    List<File> data;
    Context context;
    IMenuListener menuListener;
    ApiService apiService;

    public MenuAdapter(Context context, IMenuListener menuListener) {
        this.context = context;
        this.menuListener = menuListener;
        apiService = ApiYandexClient.getClient().create(ApiService.class);
    }

    public void setData(List<File> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_menu_item, parent, false);
        return new MenuViewHolder(view, menuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        String fileName = data.get(position).getName().split(".pdf")[0];
        holder.tv_menu.setText(fileName);
        holder.imgv_preview.setImageBitmap(data.get(position).getBmp());
//        String preview = data.get(position).getPreview();
//        preview = preview.replace("size=S", "size=M");
//        Call<ResponseBody> callPreview = apiService.getPreview("OAuth " + ACCESS_TOKEN, preview);
//        callPreview.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    if (response.body() != null) {
//                        Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
//                            holder.imgv_preview.setImageBitmap(bmp);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.d("error", t.getMessage());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        if(data!=null) {
            return data.size();
        }
        return 0;
    }
}
