package com.example.gbous2065;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.example.gbous2065.Adapters.IContactMap;
import com.example.gbous2065.Models.AllFiles;
import com.example.gbous2065.Models.Contact;
import com.example.gbous2065.Models.ContactMapCoordinates;
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

public class ContactFragment extends Fragment implements IContactMap {

    WebView mapWebView;
    String schoolURL = "";
    MapView mapView;
    RecyclerView rvContacts;
    AsyncHttpClient asyncHttpClient;
    ContactAdapter adapter;
    List<Contact> contacts;
    List<ContactMapCoordinates> coordinates;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("00853820-db0b-432b-b76d-cef71a379ae4");
        MapKitFactory.initialize(getContext());

        coordinates = new ArrayList<>();
        coordinates.add(new ContactMapCoordinates(55.600776f, 37.360295f, "Ул. Атласова, д.7 корп.3", "Школа"));
        coordinates.add(new ContactMapCoordinates(55.592830f, 37.372207f, "Ул. Радужная, д. 5", "Школа"));
        coordinates.add(new ContactMapCoordinates(55.603873f, 37.365990f, "Ул. Бианки, д. 9А", "Школа"));
        coordinates.add(new ContactMapCoordinates(55.600409f, 37.362918f, "Ул. Лаптева, д. 6, корп. 4", "Школа"));
        coordinates.add(new ContactMapCoordinates(55.590765f, 37.375333f, "Ул. Георгиевская, д.2", "Сад"));
        coordinates.add(new ContactMapCoordinates(55.590328f, 37.371398f, "Ул. Радужная, д.12", "Сад"));
        coordinates.add(new ContactMapCoordinates(55.593497f, 37.365846f, "Радужный проезд, д. 2", "Сад"));
        coordinates.add(new ContactMapCoordinates(55.604987f, 37.360510f, "Ул. Никитина, д. 6, корп. 1", "Сад"));
        coordinates.add(new ContactMapCoordinates(55.601045f, 37.358013f, "Ул. Атласова, д.7, корп. 2", "Сад"));
        coordinates.add(new ContactMapCoordinates(55.603842f, 37.368928f, "Ул. Бианки, д. 13А", "Сад"));
        coordinates.add(new ContactMapCoordinates(55.599926f, 37.364939f, "Ул. Лаптева, д. 6, корп. 2", "Сад"));
        coordinates.add(new ContactMapCoordinates(55.600079f, 37.363924f, "Ул. Лаптева, д. 6, корп. 3", "Сад"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contact_fragment, container, false);
        rvContacts = view.findViewById(R.id.rv_contacts);
        contacts = new ArrayList<>();
        adapter = new ContactAdapter(contacts, this);
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
                            String content = "";
                            try {
                                FileInputStream fis = new FileInputStream(file);
                                XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
                                Integer numOfSheets = myWorkBook.getNumberOfSheets();
                                for (int sheetNum = 0; sheetNum < numOfSheets; sheetNum++) {
                                    XSSFSheet mySheet = myWorkBook.getSheetAt(sheetNum);
                                    Iterator<Row> rowIterator = mySheet.iterator();
                                    isFinish = false;

                                    while (rowIterator.hasNext() && !isFinish) {
                                        content = "";
                                        Row row = rowIterator.next();
                                        Iterator<Cell> cellIterator = row.cellIterator();
                                        isHeaderRow = false;
                                        while (cellIterator.hasNext() && !isHeaderRow && !isFinish) {
                                            Cell cell = cellIterator.next();
                                            switch (cell.getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    content += cell.getStringCellValue();
                                                    if (content.equals("Функционал")) {
                                                        isHeaderRow = true;
                                                    }
                                                    content += "&";
                                                    break;
                                                case Cell.CELL_TYPE_BLANK:
                                                    isFinish = true;
                                            }
                                        }

                                        if (!isHeaderRow && !isFinish) {
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
                new CameraPosition(new Point(coordinates.get(0).getLatitude(), coordinates.get(0).getLongitude()), 13.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        ImageProvider markerSchool = ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_baseline_school_24));
        ImageProvider markerKidGarden = ImageProvider.fromBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_baby));

        for (ContactMapCoordinates item : coordinates) {
            if (item.getType().equals("Школа")) {
                mapView.getMap().getMapObjects().addPlacemark(new Point(item.getLatitude(), item.getLongitude()), markerSchool);
            } else {
                mapView.getMap().getMapObjects().addPlacemark(new Point(item.getLatitude(), item.getLongitude()), markerKidGarden);
            }

        }
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

    @Override
    public void contactMapClick(int position) {

        Contact contact = contacts.get(position);
        Float[] coords = getCoordsByAdress(contact.getAdress());
        mapView.getMap().move(
                new CameraPosition(new Point(coords[0], coords[1]), 18.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

    }

    @Override
    public void contactPhoneClick(int position) {
        Contact contact = contacts.get(position);
        String tel = "tel:" + contact.getPhone();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(tel));
        startActivity(intent);
    }

    @Override
    public void contactEmailClick(int position) {
        Contact contact = contacts.get(position);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + contact.getEmail()));
        startActivity(emailIntent);
    }

    public Float[] getCoordsByAdress(String adres) {
        Float[] res = new Float[2];
        for (ContactMapCoordinates item : coordinates) {
            if (item.getAdress().equals(adres)) {
                res[0] = item.getLatitude();
                res[1] = item.getLongitude();
                return res;
            }
        }
        return null;
    }
}
