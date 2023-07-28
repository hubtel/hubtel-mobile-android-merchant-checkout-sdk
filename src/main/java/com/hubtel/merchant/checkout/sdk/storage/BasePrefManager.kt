package com.hubtel.merchant.checkout.sdk.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import timber.log.Timber

abstract class BasePrefManager(
    private val context: Context,
    private val preferenceFile: String,
) {

    val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            preferenceFile,
            Context.MODE_PRIVATE
        )
    }

    private val gson = Gson()

    // MARK: - Pref manager save functions
    fun saveToSharedPref(key: String, value: String?) {
        sharedPreferences.edit(true) {
            putString(key, value)
        }
    }

    fun saveToSharedPref(key: String, value: Boolean) {
        sharedPreferences.edit(true) {
            putBoolean(key, value)
        }
    }

    fun saveToSharedPref(key: String, value: Long) {
        sharedPreferences.edit(true) {
            putLong(key, value)
        }
    }

    fun saveToSharedPref(key: String, value: Int) {
        sharedPreferences.edit(true) {
            putInt(key, value)
        }
    }

    fun saveToSharedPref(key: String, value: Float) {
        sharedPreferences.edit(true) {
            putFloat(key, value)
        }
    }

    fun saveToSharedPref(key: String, objectValue: Any?) {
        val serializedObject = gson.toJson(objectValue)
        saveToSharedPref(key, serializedObject)
    }

    fun getSharedPrefString(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }

    fun getSharedPrefBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getSharedPrefLong(key: String): Long {
        return sharedPreferences.getLong(key, 0)
    }

    fun getSharedPrefFloat(key: String): Float {
        return sharedPreferences.getFloat(key, 0.0f)
    }

    fun getSharedPrefInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    inline fun <reified T> getSharedPrefObject(
        preferenceKey: String
    ): T? {

        if (sharedPreferences.contains(preferenceKey)) {
            return try {
                val cache = getSharedPrefString(preferenceKey)

                if (cache.isEmpty()) return null

                Gson().fromJson(cache, object : TypeToken<T>() {}.type)
            } catch (e: JsonSyntaxException) {
                Timber.e(e)
                null
            } catch (ex: Exception) {
                Timber.e(ex)
                null
            }
        }

        return null
    }

    fun clear() {
        sharedPreferences.edit(true) {
            clear()
        }
    }

    fun remove(key: String) {
        sharedPreferences.edit(true) {
            remove(key)
        }
    }
}
