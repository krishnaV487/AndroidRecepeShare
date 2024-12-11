package com.example.myapplication.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromIngredientList(ingredients: List<Ingredient>?): String {
        return gson.toJson(ingredients)
    }

    @TypeConverter
    fun toIngredientList(data: String?): List<Ingredient> {
        if (data == null) return emptyList()
        val listType = object : TypeToken<List<Ingredient>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromStringList(steps: List<String>?): String {
        return gson.toJson(steps)
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }
}
