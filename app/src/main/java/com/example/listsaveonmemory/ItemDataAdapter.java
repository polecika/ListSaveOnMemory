package com.example.listsaveonmemory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemDataAdapter extends BaseAdapter {
    // Хранит список всех элементов списка
    private List<ItemData> items;
    private int imageBtnPosition;
    Context context;
    // LayoutInflater – класс, который из
    // layout-файла создает View-элемент.
    private LayoutInflater inflater;

    // Слушает все изменения галочки и меняет
    // состояние конкретного ItemData


    // Конструктор, в который передается контекст
    // для создания контролов из XML. И первоначальный список элементов.
    ItemDataAdapter(Context context, List<ItemData> items) {
        if (items == null) {
            this.items = new ArrayList<>();
        } else {
            this.items = items;
        }
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Добавляет элемент в конец списка.
    // notifyDataSetChanged сообщает об обновлении данных и переотрисовывает.
    // Вы можете как угодно менять items в других местах.
    // Но не забывайте вызывать notifyDataSetChanged чтобы все обновилось.
    void addItem(ItemData item) {
        this.items.add(item);
        notifyDataSetChanged();
    }

    // Удаляет элемент списка.
    void removeItem(int position) {
        items.remove(position);
        notifyDataSetChanged();
    }

    // Обязательный метод абстрактного класса BaseAdapter.
    // Он возвращает колличество элементов списка.
    @Override
    public int getCount() {
        return items.size();
    }

    // Тоже обязательный метод.
    // Должен возвращать элемент списка на позиции - position
    @Override
    public ItemData getItem(int position) {
        if (position < items.size()) {
            return items.get(position);
        } else {
            return null;
        }
    }

    // И это тоже обязательный метод.
    // Возвращает идентификатор. Обычно это position.
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Самый интересный обязательный метод.
    // Создает или возвращает переиспользуемый View, с новыми данными
    // для конкретной позиции. BaseAdapter – хитрый класс,
    // он не держит в памяти все View - это дорого и будет тормозить.
    // Поэтому он рисует только то что видно. Для этого есть convertView.
    // Если нет чего переиспользовать, то создается новый View.
    // А потом напоняет старую или новую View нужными данными.
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_list_view, parent, false);
        }

        ItemData itemData = items.get(position);

        ImageView image = view.findViewById(R.id.icon);
        TextView title = view.findViewById(R.id.title);
        TextView subtitle = view.findViewById(R.id.subtitle);
        ImageView imageBtn = view.findViewById(R.id.imageBtn);

        image.setImageDrawable(itemData.getImage());
        title.setText(itemData.getTitle());
        subtitle.setText(itemData.getSubtitle());
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBtnPosition = position;
                String titleForDelete = items.get(imageBtnPosition).getTitle();
                deleteSampleFile(titleForDelete);
                removeItem(imageBtnPosition);
            }
        });
        return view;
    }

    private void deleteSampleFile(String title) {

        ArrayList<String> arrayListText = new ArrayList<>();
        String[] arrayText;
        String text = "";
        File file = new File(context.getApplicationContext().getExternalFilesDir(null), MainActivity.FILE_NAME);
        FileReader textReader = null;
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
        for (String e : arrayText) {
            if (title.equals(e)) {
                continue;
            }
            arrayListText.add(e);
        }
        text = "";
        String s = "";
        for (int i = 0; i < arrayListText.size(); i++) {
            text += arrayListText.get(i) + ";";
        }
        FileWriter textWriter = null;

        try {
            textWriter = new FileWriter(file, false);
            textWriter.write(text);
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

