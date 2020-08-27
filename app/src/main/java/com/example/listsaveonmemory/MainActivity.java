package com.example.listsaveonmemory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Генератор случайностей
    private Random random = new Random();
    // Наш адаптер
    private ItemDataAdapter adapter;
    // Список картинок, которые мы будем брать для нашего списка
    private List<Drawable> images = new ArrayList<>();
    String[] arrayText;
    private static int arrayPosition;
    public static final int REQUEST_CODE_PERMISSION_WRITE_STORAGE = 11;
    public final static String FILE_NAME = "content.txt";

    //public final File file = new File(getApplicationContext().getExternalFilesDir(null), FILE_NAME);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        ListView listView = findViewById(R.id.listView);

        setSupportActionBar(toolbar);

        fillImages();
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_WRITE_STORAGE);
        }

        arrayText = getString(R.string.large_text).split("\n\n");

        clearFileBeforeWriteNewText();
        // При тапе по кнопке добавим один новый элемент списка
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRandomItemData();
            }
        });

        // Создаем и устанавливаем адаптер на наш список
        adapter = new ItemDataAdapter(MainActivity.this, null);
        listView.setAdapter(adapter);
        ImageView imageBtn = findViewById(R.id.imageBtn);

        // При долгом тапе по элементу списка будем удалять его
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                showItemData(i);
                return true;
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_WRITE_STORAGE:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Не возможно работать с приложением, без запрашиваемых разрешений", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }


    // Заполним различными картинками, которые встроены в сам Android
    // ContextCompat обеспечит нам поддержку старых версий Android
    private void fillImages() {
        images.add(ContextCompat.getDrawable(MainActivity.this,
                android.R.drawable.ic_menu_report_image));
        images.add(ContextCompat.getDrawable(MainActivity.this,
                android.R.drawable.ic_menu_add));
        images.add(ContextCompat.getDrawable(MainActivity.this,
                android.R.drawable.ic_menu_agenda));
        images.add(ContextCompat.getDrawable(MainActivity.this,
                android.R.drawable.ic_menu_camera));
        images.add(ContextCompat.getDrawable(MainActivity.this,
                android.R.drawable.ic_menu_call));
    }

    // Создадим ну почти случайные данные для нашего списка.
    // random.nextInt(граница_последнего_элемента)
    // Для каждого элемента мы возьмем 1 случайную картинку
    // из 5, которые мы сделали вначале.
    private void generateRandomItemData() {
        arrayPosition = random.nextInt(arrayText.length - 1);
        saveFile(arrayText[arrayPosition]);

        adapter.addItem(new ItemData(
                images.get(random.nextInt(images.size())),
                loadFile(),
                "Shvets"));
    }

    // Покажем сообщение с данными
    private void showItemData(int position) {
        ItemData itemData = adapter.getItem(position);
        Toast.makeText(MainActivity.this,
                "Title: " + itemData.getTitle() + "\n" +
                        "Subtitle: " + itemData.getSubtitle(),
                Toast.LENGTH_SHORT).show();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void saveFile(String text) {
        if (isExternalStorageWritable()) {
            FileWriter textWriter = null;
            File file = new File(getApplicationContext().getExternalFilesDir(null), FILE_NAME);
            try {
                textWriter = new FileWriter(file, true);
                textWriter.append(text + ";");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {
                    textWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private void clearFileBeforeWriteNewText() {
        if (isExternalStorageWritable()) {
            FileWriter textWriter = null;
            File file = new File(getApplicationContext().getExternalFilesDir(null), FILE_NAME);
            try {
                textWriter = new FileWriter(file, false);
                textWriter.append("");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {
                    textWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private String loadFile() {
        String[] arrayText;
        String text = "";
        FileReader textReader = null;
        File file = new File(getApplicationContext().getExternalFilesDir(null), FILE_NAME);
        try {
            textReader = new FileReader(file);
            BufferedReader bufferReader = new BufferedReader(textReader);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                text += line;
            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                textReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        arrayText = text.split(";");
        text = arrayText[arrayText.length - 1];
        return text;
    }


}