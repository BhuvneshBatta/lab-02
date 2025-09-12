package com.example.listycity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView cityList;
    ArrayAdapter<String> cityAdapter;
    ArrayList<String> datalist;

    Button btn_add, btn_delete, btn_confirm;
    EditText et_city;
    LinearLayout inputRow;

    int selectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Views
        cityList   = findViewById(R.id.city_list);
        btn_add    = findViewById(R.id.btn_add);
        btn_delete = findViewById(R.id.btn_delete);
        btn_confirm= findViewById(R.id.btn_confirm);
        et_city    = findViewById(R.id.et_city);
        inputRow   = findViewById(R.id.input_row);

        // Data + adapter
        String[] cities = {"Edmonton", "Vancouver", "Moscow", "Sydney", "Berlin",
                "Vienna", "Tokyo", "Beijing", "Osaka", "New Delhi"};
        datalist = new ArrayList<>();
        datalist.addAll(Arrays.asList(cities));

        cityAdapter = new ArrayAdapter<>(this, R.layout.content, R.id.content_view, datalist);
        cityList.setAdapter(cityAdapter);

        // Selection handling
        cityList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        btn_delete.setEnabled(false);

        cityList.setOnItemClickListener((parent, view, position, id) -> {
            selectedIndex = position;
            cityList.setItemChecked(position, true);
            btn_delete.setEnabled(true);
        });

        // Show the input row when tapping "ADD CITY"
        btn_add.setOnClickListener(v -> {
            inputRow.setVisibility(View.VISIBLE);
            et_city.requestFocus(); // no keyboard helper
        });

        // Confirm new city
        btn_confirm.setOnClickListener(v -> {
            String name = et_city.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                et_city.setError("City name required");
                return;
            }

            // Prevent duplicates (case-insensitive)
            String needle = name.toLowerCase(Locale.ROOT);
            boolean exists = false;
            for (String s : datalist) {
                if (s.toLowerCase(Locale.ROOT).equals(needle)) {
                    exists = true; break;
                }
            }
            if (exists) {
                et_city.setError("City already exists");
                return;
            }

            datalist.add(name);
            cityAdapter.notifyDataSetChanged();

            et_city.setText("");
            et_city.clearFocus();        // just clear focus
            inputRow.setVisibility(View.GONE);

            // Optionally scroll to bottom
            cityList.post(() -> cityList.setSelection(datalist.size() - 1));
        });

        // Delete selected city
        btn_delete.setOnClickListener(v -> {
            if (selectedIndex >= 0 && selectedIndex < datalist.size()) {
                datalist.remove(selectedIndex);
                cityAdapter.notifyDataSetChanged();

                cityList.clearChoices();
                selectedIndex = -1;
                btn_delete.setEnabled(false);
            } else {
                Toast.makeText(this, "Select a city first", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
