package com.sultan.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sultan.myapplication.DrinkModel.DrinkModel
import com.sultan.myapplication.adapter.MyDrinkAdapter
import com.sultan.myapplication.databinding.ActivityMainBinding
import com.sultan.myapplication.listener.IDrinkLoadListener
import com.sultan.myapplication.utils.SpaceItemDecoration

class MainActivity : AppCompatActivity(), IDrinkLoadListener {
    private lateinit var binding: ActivityMainBinding

    lateinit var drinkLoadListener: IDrinkLoadListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        loadDrinkFromFirebase()
    }

    private fun loadDrinkFromFirebase() {
        val drinkModels : MutableList<DrinkModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Drink")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        for(drinkSbapshot in snapshot.children)
                        {
                             val drinkModel = drinkSbapshot.getValue(DrinkModel::class.java)
                            drinkModel!!.key = drinkSbapshot.key
                            drinkModels.add(drinkModel)
                        }
                        drinkLoadListener.onLoadSuccess(drinkModels)
                    }else
                    {
                        drinkLoadListener.onLoadFailed("Drink items not exists")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    drinkLoadListener.onLoadFailed(error.message)
                }

            })
    }

    private fun init(){
        drinkLoadListener = this

        val gridLayoutManager = GridLayoutManager(this , 2)
        binding.recyclerDrink.layoutManager = gridLayoutManager
        binding.recyclerDrink.addItemDecoration(SpaceItemDecoration())
    }


    override fun onLoadSuccess(drinkModelList: List<DrinkModel>?) {
        val adapter  = MyDrinkAdapter(this, drinkModelList!!)
        binding.recyclerDrink.adapter = adapter
    }

    override fun onLoadFailed(message: String?) {
        Snackbar.make(binding.root, message!!, Snackbar.LENGTH_LONG).show()
    }
}