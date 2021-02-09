package com.example.gbous2065;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gbous2065.Adapters.ContactAdapter;
import com.example.gbous2065.Models.AllFiles;
import com.example.gbous2065.Models.Contact;
import com.example.gbous2065.Network.ApiService;
import com.example.gbous2065.Network.ApiYandexClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.gbous2065.Network.ApiYandexClient.ACCESS_TOKEN;

public class ContactFragment extends Fragment {

    WebView mapWebView;
    String schoolURL = "";
    MapView mapView;
    RecyclerView rvContacts;
    AsyncHttpClient asyncHttpClient;
    ContactAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("00853820-db0b-432b-b76d-cef71a379ae4");
        MapKitFactory.initialize(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contact_fragment, container, false);
        rvContacts = view.findViewById(R.id.rv_contacts);
        List<Contact> contacts = new ArrayList<>();
        adapter = new ContactAdapter(contacts);
        rvContacts.setAdapter(adapter);
        //contacts.add(new Contact("Директор", "Ланщиков Дмитрий Николаевич", "head@mail.ru", "89263748596", "Moscow"));
        //contacts.add(new Contact("Заместитель директора", "Шурухина Алла Юрьевна", "zam@mail.ru", "84657283474", "Moscow"));
        ApiService apiService = ApiYandexClient.getClient().create(ApiService.class);
        Call<AllFiles> call = apiService.getAllFiles("OAuth " + ACCESS_TOKEN, 1000);
        call.enqueue(new Callback<AllFiles>() {
            @Override
            public void onResponse(Call<AllFiles> call, Response<AllFiles> response) {
                AllFiles data = response.body();
                List<com.example.gbous2065.Models.File> files = data.getItems();
                String url = "";
                for (com.example.gbous2065.Models.File file : files) {
                    String dir = file.getPath().split("/")[1];
                    if (dir.equals("Контакты")) {
                        url = file.getFile_url();
                        break;
                    }
                }

                asyncHttpClient = new AsyncHttpClient();
                asyncHttpClient.get(url, new FileAsyncHttpResponseHandler(getContext()) {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                        Toast.makeText(getContext(), "Error in Downloading Excel File", Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity", throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, File file) {
                        if (file != null) {
                            Boolean isHeaderRow, isFinish;
                            String content="";
                            try {
                                FileInputStream fis = new FileInputStream(file);
                                XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
                                Integer numOfSheets = myWorkBook.getNumberOfSheets();
                                for (int sheetNum = 0; sheetNum < numOfSheets; sheetNum++) {
                                    XSSFSheet mySheet = myWorkBook.getSheetAt(sheetNum);
                                    Iterator<Row> rowIterator = mySheet.iterator();
                                    isFinish = false;

                                    while (rowIterator.hasNext() && !isFinish) {
                                        content="";
                                        Row row = rowIterator.next();
                                        Iterator<Cell> cellIterator = row.cellIterator();
                                        isHeaderRow = false;
                                        while (cellIterator.hasNext() && !isHeaderRow && !isFinish) {
                                            Cell cell = cellIterator.next();
                                            switch (cell.getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    content += cell.getStringCellValue();
                                                    if(content.equals("Функционал")){
                                                        isHeaderRow = true;
                                                    }
                                                    content += "&";
                                                    break;
                                                case Cell.CELL_TYPE_BLANK:
                                                    isFinish = true;
                                            }
                                        }

                                        if(!isHeaderRow && !isFinish) {
                                            String[] infoMas = content.split("&");
                                            Contact newContact = new Contact();
                                            newContact.setPosition(infoMas[0]);
                                            newContact.setAdress(infoMas[1]);
                                            newContact.setName(infoMas[2]);
                                            newContact.setPhone(infoMas[3]);
                                            newContact.setEmail(infoMas[4]);
                                            newContact.setExpanded(false);

                                            contacts.add(newContact);
                                        }
                                    }
                                }

                                adapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<AllFiles> call, Throwable t) {

            }
        });

        //55.600776, 37.360295
        mapView = view.findViewById(R.id.map);
        mapView.getMap().move(
                new CameraPosition(new Point(55.600776, 37.360295), 8.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        ImageProvider marker = ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_school));
        mapView.getMap().getMapObjects().addPlacemark(new Point(55.600776, 37.360295), marker);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
