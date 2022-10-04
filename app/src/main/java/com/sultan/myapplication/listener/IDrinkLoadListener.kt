package com.sultan.myapplication.listener

import com.sultan.myapplication.model.DrinkModel

interface IDrinkLoadListener {
    fun onDrinkLoadSuccess(drinkModelList: List<DrinkModel>?)
    fun onDrinkLoadFailed(message : String?)
}