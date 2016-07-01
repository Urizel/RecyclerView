package com.devtau.recyclerviewlib;

import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.devtau.recyclerviewlib.util.Logger;
import com.devtau.recyclerviewlib.util.Util;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MyItemRVAdapter<T extends Parcelable> extends RecyclerView.Adapter<MyItemRVAdapter.ViewHolder> {
    private ArrayList<T> itemsList;
    private final int listItemLayoutId;
    private final RVFragment.OnRVFragmentListener listener;
    private int positionInList = -1;

    public MyItemRVAdapter(ArrayList<T> itemsList, int listItemLayoutId, SortBy sortBy,
                           RVFragment.OnRVFragmentListener listener) {
        Logger.d("MyItemRVAdapter constructor");
        this.itemsList = itemsList;
        this.listItemLayoutId = listItemLayoutId;
        sort(sortBy);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        Logger.d("MyItemRVAdapter.onBindViewHolder()");
        holder.item = itemsList.get(position);
        //TODO: настройте onBindViewHolder
        //здесь выбираем, какие поля хранимого объекта отобразятся в каких частях CardView
//        DummyItem item = (DummyItem) holder.item;

//        ((TextView) holder.view.findViewById(R.id.price)).setText(String.valueOf(holder.item.getPrice()));
//        ((TextView) holder.view.findViewById(R.id.description)).setText(item.getDescription());
//        String dateString = Util.getStringDateTimeFromCal(item.getDate());
//        ((TextView) holder.view.findViewById(R.id.date)).setText(dateString);
//        ImageButton btnDelete = ((ImageButton) holder.view.findViewById(R.id.btnDelete));

        ((TextView) holder.view.findViewById(R.id.price)).setText(String.valueOf(22));
        ((TextView) holder.view.findViewById(R.id.description)).setText("описание");
        String dateString = Util.getStringDateTimeFromCal(Calendar.getInstance());
        ((TextView) holder.view.findViewById(R.id.date)).setText(dateString);
        ImageButton btnDelete = ((ImageButton) holder.view.findViewById(R.id.btnDelete));

        //здесь устанавливаем слушатели
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onListItemClick(holder.item);
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    positionInList = holder.getAdapterPosition();
                    listener.onListItemClickDelete(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }



    //публичные методы редактирования хранимого списка
    public void setList(ArrayList<T> itemsList){
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public int addItemToList(T item, SortBy sortBy) {
        itemsList.add(item);
        sort(sortBy);
        int position = itemsList.indexOf(item);
        notifyItemInserted(position);
        return position;
    }

    public void removeItemFromList(T item){
        //для корректного удаления элемента из списка реализуйте equals и hashCode у класса хранимого объекта
        itemsList.remove(item);
        if(positionInList != -1) {
            notifyItemRemoved(positionInList);
        }
    }

    public void sort(SortBy sortBy) {
//        Comparator comparator = DummyItem.Comparators.getProperComparator(sortBy);
//        Collections.sort(itemsList, comparator);
    }

    public void sortAndNotify(SortBy sortBy) {
//        Comparator comparator = DummyItem.Comparators.getProperComparator(sortBy);
//        Collections.sort(itemsList, comparator);
//        Collections.sort(itemsList, DummyItem.Comparators.getProperComparator(sortBy));
        notifyDataSetChanged();
    }


    public class ViewHolder<T extends Parcelable> extends RecyclerView.ViewHolder {
        //пока ViewHolder является вложенным классом, адаптер имеет доступ к его private переменным
        private final View view;
        private T item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
}
