package edu.gatech.cs2340.donationtracer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemScroller extends AppCompatActivity {

    ArrayList<Item> itemList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ItemRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_scroller);

        itemList.add(new Item(null, MainActivity.getDb().get(1), "Shoe", "A singular shoe", 1.00, Category.Clothing));
        System.out.println(itemList.get(0).getShortDesc());

        recyclerView = findViewById(R.id.itemRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ItemRecyclerAdapter(itemList);
        recyclerView.setAdapter(adapter);

    }

    public ArrayList<Item> getItemList() {
        return itemList;
    }
}
