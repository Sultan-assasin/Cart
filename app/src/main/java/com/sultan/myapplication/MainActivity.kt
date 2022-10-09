package com.sultan.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sultan.myapplication.adapter.MyDrinkAdapter
import com.sultan.myapplication.databinding.ActivityMainBinding
import com.sultan.myapplication.eventbus.UpdateCartEvent
import com.sultan.myapplication.listener.ICartLoadListener
import com.sultan.myapplication.listener.IDrinkLoadListener
import com.sultan.myapplication.model.CartModel
import com.sultan.myapplication.model.DrinkModel
import com.sultan.myapplication.utils.SpaceItemDecoration
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), IDrinkLoadListener, ICartLoadListener {
    private lateinit var binding: ActivityMainBinding

    lateinit var drinkLoadListener: IDrinkLoadListener
    lateinit var cartLoadListener: ICartLoadListener

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
            EventBus.getDefault().unregister(this)

    }

    @Subscribe(threadMode = ThreadMode.MAIN , sticky = true)
    fun onUpdateCartEvent(event: UpdateCartEvent)
    {
        countCartFromFirebase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        loadDrinkFromFirebase()
        countCartFromFirebase()
    }

    private fun countCartFromFirebase() {
        val cartModels: MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children) {
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }

            })
    }

    private fun loadDrinkFromFirebase() {
        val drinkModels: MutableList<DrinkModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Drink")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (drinkSbapshot in snapshot.children) {
                            val drinkModel = drinkSbapshot.getValue(DrinkModel::class.java)
                            drinkModel!!.key = drinkSbapshot.key
                            drinkModels.add(drinkModel)
                        }
                        drinkLoadListener.onDrinkLoadSuccess(drinkModels)
                    } else {
                        drinkLoadListener.onDrinkLoadFailed("Drink items not exists")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    drinkLoadListener.onDrinkLoadFailed(error.message)
                }

            })
    }

    private fun init() {
        drinkLoadListener = this
        cartLoadListener = this

        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.recyclerDrink.layoutManager = gridLayoutManager
        binding.recyclerDrink.addItemDecoration(SpaceItemDecoration())

        binding.btnCart.setOnClickListener{ startActivity(Intent(this , CartActivity::class.java)) }
    }


    override fun onDrinkLoadSuccess(drinkModelList: List<DrinkModel>?) {
        val adapter = MyDrinkAdapter(this, drinkModelList!!,cartLoadListener)
        binding.recyclerDrink.adapter = adapter
    }

    override fun onDrinkLoadFailed(message: String?) {
        Snackbar.make(binding.root, message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var cartSum = 0
        for (cartModel in cartModelList) cartSum += cartModel.quantity
        binding.badge.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(binding.root, message!!, Snackbar.LENGTH_LONG).show()
    }
}