package com.harlie.batbot.util

class CacheManager private constructor() {
    val TAG = "LEE: <" + CacheManager::class.java.getName() + ">";

    private val mCache: MutableMap<String, String> = mutableMapOf()


    init {
        /*
        *  every time init is called increment instance count
        *  just in case somehow we break singleton rule, this will be
        *  called more than once and myInstancesCount > 1 == true
        */
        ++myInstancesCount
    }


    companion object {
        //Debuggable field to check instance count
        var myInstancesCount = 0;
        private val mInstance: CacheManager = CacheManager()

        @Synchronized
        fun getInstance(): CacheManager {
            return mInstance
        }
    }

    /*
     * Put a key and corresponding value in a map of String, String
     */
    fun put(key: String, value: String) {
        when {
            !key.isEmpty() -> mCache.put(key, value)
            else -> throw IllegalArgumentException("Key cannot be empty")
        }
    }

    /*
     * retrieves a string value for a corresponding key
     * if this key is previously not inserted using put(..., ...) above
     * it will return null
     */
    fun get(key: String): String? {
        when {
            mCache.containsKey(key) -> return mCache.get(key)
            else -> return null
        }
    }

}