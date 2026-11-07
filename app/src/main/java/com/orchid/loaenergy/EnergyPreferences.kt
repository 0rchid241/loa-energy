package com.orchid.loaenergy

import android.content.Context
import android.health.connect.datatypes.units.Energy
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit

val Context.energyDataStore by preferencesDataStore(
    name = "energy"
)

object EnergyKeys {
    val CURRENT_ENERGY = intPreferencesKey("current_energy")
    val MAX_ENERGY = intPreferencesKey("max_energy")
    val RECORDED_AT = longPreferencesKey("recorded_at")
}

suspend fun saveEnergy(
    context: Context,
    energy: Int,
    maxEnergy: Int,
    recordedAtMillis: Long
) {
    context.energyDataStore.edit { preferences ->
        preferences[EnergyKeys.CURRENT_ENERGY] = energy
        preferences[EnergyKeys.MAX_ENERGY] = maxEnergy
        preferences[EnergyKeys.RECORDED_AT] = recordedAtMillis
    }
}