package com.orchid.loaenergy

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.energyDataStore by preferencesDataStore(
    name = "energy"
)

object EnergyKeys {
    val CURRENT_ENERGY = intPreferencesKey("current_energy")
    val MAX_ENERGY = intPreferencesKey("max_energy")
    val RECORDED_AT = longPreferencesKey("recorded_at")
}

data class EnergyRecord(
    val currentEnergy: Int,
    val maxEnergy: Int,
    val recordedAtMillis: Long
)

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

fun readEnergy(context: Context): Flow<EnergyRecord?> {
    return context.energyDataStore.data.map { preferences ->
        val energy = preferences[EnergyKeys.CURRENT_ENERGY]
        val maxEnergy = preferences[EnergyKeys.MAX_ENERGY]
        val recordedAtMillis = preferences[EnergyKeys.RECORDED_AT]

        if (energy != null && maxEnergy != null && recordedAtMillis != null) {
            EnergyRecord(
                energy,
                maxEnergy,
                recordedAtMillis
            )
        } else {
            null
        }
    }
}