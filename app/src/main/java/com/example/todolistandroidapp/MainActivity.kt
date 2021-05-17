package com.example.todolistandroidapp

import android.content.Context
import android.content.SharedPreferences
import  com.example.todolistandroidapp.R;
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val context = this
        val db = DataBaseHelper(context)

        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("name", Context.MODE_PRIVATE)

        val appSettingPrefs: SharedPreferences = getSharedPreferences( "AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (sharedPref.getString("name", null) !== null){
            return openMainPage(sharedPref, sharedPref.getString("name", "")!!, db)
        }

        setContentView(R.layout.activity_intro)

        toggleModeButton.setOnClickListener {
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                sharedPrefsEdit.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                sharedPrefsEdit.apply()
            }
        }

        next.setOnClickListener {
            val nameInfo = nameTextBox.text.toString()
            with (sharedPref.edit()) {
                putString("name", nameInfo)
                apply()
            }
            openMainPage(sharedPref, nameInfo, db)
        }
    }

    fun openMainPage(sharedPref: SharedPreferences, nameInfo: String, db: DataBaseHelper) {
        val welcomeMessage = "Good "
        var welcomeTime = "Night "

        var currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (currentHour.toInt() < 12) {
            welcomeTime = "Morning "
        } else if (currentHour.toInt() < 16) {
            welcomeTime = "Afternoon "
        } else if (currentHour.toInt() < 21) {
            welcomeTime = "Evening "
        }

        setContentView(R.layout.activity_main)
        titleViewMain.text = (welcomeMessage.plus(welcomeTime).plus(nameInfo).plus("!")).toUpperCase()

        val currDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        dayOfWeek.text = when (currDayOfWeek) {
                1 -> "Sunday"
                2 -> "Monday"
                3 -> "Tuesday"
                4 -> "Wednesday"
                5 -> "Thursday"
                6 -> "Friday"
                7 -> "Saturday"
                else -> "Time has stopped"
            }

        // Initializing the array lists and the adapter
        val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_multiple_choice
                , db.readData().map { it.second })
        listView.adapter =  adapter

        // Adding the items to the list when the add button is pressed
        add.setOnClickListener {
            db.insertData(editText.text.toString(), "")
            adapter.clear();
            adapter.addAll(db.readData().map { it.second });
            // This is because every time when you add the item the input space or the edit text space will be cleared
            editText.text.clear()
        }

//       // Selecting and Deleting the items from the list when the delete button is pressed
//        delete.setOnClickListener {
//            val position: SparseBooleanArray = listView.checkedItemPositions
//            val count = listView.count
//            var item = count - 1
//            while (item >= 0) {
//                if (position.get(item))
//                {
//                    adapter.remove(itemlist.get(item))
//                }
//                item--
//            }
//            position.clear()
//            adapter.notifyDataSetChanged()
//        }


//        // Clearing all the items in the list when the clear button is pressed
//        clear.setOnClickListener {
//
//            itemlist.clear()
//            adapter.notifyDataSetChanged()
//        }

        listView.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this, "You Selected the item --> "+ db.readData().map { it.second }.get(i), Toast.LENGTH_SHORT).show()
            delete.setOnClickListener {
                db.removeData( db.readData().map { it.first }.get(i))
                adapter.clear();
                adapter.addAll(db.readData().map { it.second });
            }
        }
    }
}