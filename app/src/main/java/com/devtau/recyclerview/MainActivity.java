package com.devtau.recyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import com.devtau.recyclerview.database.DataSource;
import com.devtau.recyclerview.database.sources.DummyItemsSource;
import com.devtau.recyclerview.model.DummyItem;
import com.devtau.recyclerview.model.DummyItemComparators;
import com.devtau.recyclerview.util.Util;

import com.devtau.recyclerviewlib.RVHelper;
import com.devtau.recyclerviewlib.RVHelperInterface;
import com.devtau.recyclerviewlib.MyItemRVAdapter.ViewHolder;
/**
 * Пример использования библиотеки RVHelper клиентом
 */
public class MainActivity extends AppCompatActivity implements
        RVHelperInterface {
    private static final String ARG_INDEX_OF_SORT_METHOD = "indexOfSortMethod";
    private RVHelper rvHelper;
    private RVHelper rvHelper2;
    //рекомендуется хранить ссылку на dataSource, если таблиц больше одной
    private DummyItemsSource dummyItemsSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //получим ссылку на базу данных
        dummyItemsSource = new DataSource(this).getDummyItemsSource();
        initRecyclers(savedInstanceState);
    }

    private void initRecyclers(Bundle savedInstanceState) {
        //запросим из бд список, который нам нужно показать
        ArrayList<DummyItem> itemsList = dummyItemsSource.getItemsList();

        //подготовим названия компараторов для спиннера и выбранный вариант
        ArrayList<String> comparatorsNames = DummyItemComparators.getComparatorsNames(this);
        int indexOfSortMethod = com.devtau.recyclerviewlib.util.Constants.DEFAULT_SORT_BY;
        if(savedInstanceState != null) {
            indexOfSortMethod = savedInstanceState.getInt(ARG_INDEX_OF_SORT_METHOD);
        }

        //соберем из подготовленных вводных данных хелпер(ы)
        rvHelper = RVHelper.Builder.<DummyItem> start(this, R.id.rv_helper_placeholder).setList(itemsList)
                .withListItemLayoutId(R.layout.list_item)
                .withSortSpinner(comparatorsNames, indexOfSortMethod)
                .build();
        rvHelper.addItemFragmentToLayout(this, R.id.rv_helper_placeholder);

        rvHelper2 = RVHelper.Builder.<DummyItem> start(this, R.id.rv_helper_placeholder2).setList(itemsList)
                .withColumnCount(2)
                .withListItemLayoutId(R.layout.list_item)
                .withSortSpinner(comparatorsNames, indexOfSortMethod)
                .withAddButton()
                .build();
        rvHelper2.addItemFragmentToLayout(this, R.id.rv_helper_placeholder2);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int rvHelperId) {
        //здесь выбираем, какие поля хранимого объекта отобразятся в каких частях строки
        //TextView в разметке по умолчанию такие: tvMain, tvAdditional1, tvAdditional2
        final DummyItem item = (DummyItem) holder.getItem();

        ((TextView) holder.getView().findViewById(R.id.tvMain)).setText(item.getDescription());
        ((TextView) holder.getView().findViewById(R.id.tvAdditional1)).setText(String.valueOf(item.getPrice()));
        String dateString = Util.getStringDateTimeFromCal(item.getDate());
        ((TextView) holder.getView().findViewById(R.id.tvAdditional2)).setText(dateString);
        ImageButton btnDelete = ((ImageButton) holder.getView().findViewById(R.id.btnDelete));

        //здесь устанавливаем слушатели
        holder.getView().setOnClickListener(view -> onListItemClick(item, 0, rvHelperId));
        btnDelete.setOnClickListener(view -> onListItemClick(item, 1, rvHelperId));
    }

    private void onListItemClick(DummyItem item, int clickedActionId, int rvHelperId) {
        switch (clickedActionId) {
            case 0://клик по строке. просто покажем тост, по чему мы кликнули
                String msg = "You selected item " + item.toString();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                break;
            case 1://запрос на удаление
                if(rvHelperId == R.id.rv_helper_placeholder) {
                    dummyItemsSource.remove(item);
                    if(rvHelper != null) rvHelper.removeItemFromList(item);
                } else
                if(rvHelperId == R.id.rv_helper_placeholder2) {
                    //в реальности классом объектов второго листа может быть совсем не DummyItem
                    dummyItemsSource.remove(item);
                    if(rvHelper2 != null) rvHelper2.removeItemFromList(item);
                }
                break;
        }
    }

    @Override
    public void onAddNewItemDialogResult(List<String> newItemParams, int rvHelperId) {
        //создадим из полученных данных новый хранимый объект
        int price = 0;
        try {
            price = Integer.parseInt(newItemParams.get(0));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.NumberFormatException, Toast.LENGTH_SHORT).show();
        }

        switch (rvHelperId) {
            case R.id.rv_helper_placeholder:
                DummyItem newItem = new DummyItem(Calendar.getInstance(), price, newItemParams.get(1));
                newItem.setId(dummyItemsSource.create(newItem));//сохраним его в бд
                if(rvHelper != null) {
                    rvHelper.addItemToList(newItem);//добавим его в лист
                }
                break;

            case R.id.rv_helper_placeholder2:
                //в реальности классом объектов второго листа может быть совсем не DummyItem
                DummyItem newItemOther = new DummyItem(Calendar.getInstance(), price, newItemParams.get(1));
                newItemOther.setId(dummyItemsSource.create(newItemOther));//сохраним его в бд
                if(rvHelper2 != null) {
                    rvHelper2.addItemToList(newItemOther);//добавим его в лист
                }
                break;
        }
    }

    @Override
    public Comparator provideComparator(int indexOfSortMethod) {
        //возвращает Comparator по его индексу. метод необходим, т.к. Comparator не упаковать в Bundle
        //допускается возвращать null. тогда новые строки будут просто добавляться в конец списка
        return DummyItemComparators.provideComparator(indexOfSortMethod);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_INDEX_OF_SORT_METHOD, rvHelper.getIndexOfSortMethod());
        super.onSaveInstanceState(outState);
    }
}
