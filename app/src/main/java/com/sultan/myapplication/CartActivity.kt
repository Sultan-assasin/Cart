package com.sultan.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sultan.myapplication.adapter.MyCartAdapter
import com.sultan.myapplication.databinding.ActivityCartBinding
import com.sultan.myapplication.eventbus.UpdateCartEvent
import com.sultan.myapplication.listener.ICartLoadListener
import com.sultan.myapplication.model.CartModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartActivity : AppCompatActivity(), ICartLoadListener {

    var cartLoadListener: ICartLoadListener? = null
    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        loadCartFromFirebase()
    }
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
        loadCartFromFirebase()
    }

    private fun loadCartFromFirebase() {
        val cartModels: MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children) {
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener!!.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onLoadCartFailed(error.message)
                }

            })
    }

    private fun init() {
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerCart.layoutManager = layoutManager
        binding.recyclerCart.addItemDecoration(
            DividerItemDecoration(
                this,
                layoutManager.orientation
            )
        )
        binding.btnBack.setOnClickListener{ finish() }



    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var sum  = 0.0
        for(cartModel in cartModelList){
            sum+= cartModel.totalPrice
        }
        binding.txtTotal.text = StringBuilder("$").append(sum)
        val adapter = MyCartAdapter(this , cartModelList)
        binding.recyclerCart.adapter = adapter
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(binding.root, message!!, Snackbar.LENGTH_LONG).show()
    }
}