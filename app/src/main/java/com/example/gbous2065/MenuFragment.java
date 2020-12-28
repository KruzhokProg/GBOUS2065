package com.example.gbous2065;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.Adapters.MenuAdapter;
import com.example.gbous2065.Models.AllFiles;
import com.example.gbous2065.Models.File;
import com.example.gbous2065.Network.ApiYandexClient;
import com.example.gbous2065.Network.ApiService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.gbous2065.Network.ApiYandexClient.ACCESS_TOKEN;

public class MenuFragment extends Fragment implements IMenuListener {

    MenuAdapter menuAdapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView rvMenu;
    List<File> rendered_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_menu_fragment, container, false);

        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvMenu = view.findViewById(R.id.rvMenu);
        menuAdapter = new MenuAdapter(getContext(), this);
        rvMenu.setHasFixedSize(true);
        rvMenu.setLayoutManager(gridLayoutManager);
        rvMenu.setAdapter(menuAdapter);

        List<File> menuFiles = new ArrayList<>();
        ApiService apiService = ApiYandexClient.getClient().create(ApiService.class);
        Call<AllFiles> call = apiService.getAllFiles("OAuth " + ACCESS_TOKEN, 1000);
        call.enqueue(new Callback<AllFiles>() {
            @Override
            public void onResponse(Call<AllFiles> call, Response<AllFiles> response) {
                AllFiles data = response.body();
                List<File> files = data.getItems();

                for (File file: files) {
                    String dir = file.getPath().split("/")[1];
                    if(dir.equals("Меню")){
                        menuFiles.add(file);
                    }
                }

                // публикация неопубликованных файлов
                for (File file: menuFiles) {
                    if(file.getPublic_url() == null){
                        String path = file.getPath().split("disk:/")[1];
                        Call<ResponseBody> callPublish = apiService.publishFile("OAuth " + ACCESS_TOKEN, path);
                        callPublish.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                //Toast.makeText(getContext(), "файл " + path + " опубликован", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                }
                rendered_data = new ArrayList<>();
                menuAdapter.setData(rendered_data);
                for (File file: menuFiles) {
                    String preview = file.getPreview();
                    preview = preview.replace("size=S", "size=L");
                    file.setPreview(preview);
                    Call<ResponseBody> callPreview = apiService.getPreview("OAuth " + ACCESS_TOKEN, preview);
                    callPreview.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                                    file.setBmp(bmp);
                                    rendered_data.add(file);
                                    menuAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("error", t.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<AllFiles> call, Throwable t) {

            }
        });

        return view;
    }

    @Override
    public void onMenuClickListener(int position) {

        Intent i = new Intent(getContext(), MenuDetail.class);
        i.putExtra("preview", rendered_data.get(position).getPreview());
        i.putExtra("path", rendered_data.get(position).getPath());
        startActivity(i);
    }
}
