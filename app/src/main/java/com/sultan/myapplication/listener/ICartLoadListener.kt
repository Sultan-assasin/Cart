package com.sultan.myapplication.listener

import com.sultan.myapplication.model.CartModel

interface ICartLoadListener {
    fun onLoadCartSuccess(cartModelList: List<CartModel>)

    fun onLoadCartFailed(message: String?)
}