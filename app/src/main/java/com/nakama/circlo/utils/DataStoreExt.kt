package com.nakama.circlo.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

const val CIRCLO_USER_DATA_STORE_NAME = "CircloUserDataStore"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = CIRCLO_USER_DATA_STORE_NAME)