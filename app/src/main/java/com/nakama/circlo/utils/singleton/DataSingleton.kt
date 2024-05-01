package com.nakama.circlo.utils.singleton

class DataSingleton {

    companion object {
        private var instance: DataSingleton? = null

        fun getInstance(): DataSingleton {
            if (instance == null) instance = DataSingleton()
            return instance!!
        }
    }

    // User Token
    var userToken: String? = null
}