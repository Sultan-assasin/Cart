package com.sultan.myapplication.listener

import com.sultan.myapplication.DrinkModel.DrinkModel

interface IDrinkLoadListener {
    fun onLoadSuccess(drinkModelList: List<DrinkModel>?)
    fun onLoadFailed(message : String?)
}