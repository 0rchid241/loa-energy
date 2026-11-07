package com.orchid.loaenergy

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.energyDataStore by preferencesDataStore(
    name = "energy"
)

object EnergyKeys {
    val CURRENT_ENERGY = intPreferencesKey("current_energy")
    val MAX_ENERGY = intPreferencesKey("max_energy")
    val RECORDED_AT = longPreferencesKey("recorded_at")
}