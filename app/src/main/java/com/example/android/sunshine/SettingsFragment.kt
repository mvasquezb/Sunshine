package com.example.android.sunshine

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.*

/**
 * Created by pmvb on 17-08-20.
 */
class SettingsFragment :
        PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)
    }

    override fun onStart() {
        super.onStart()
        val sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(activity)
        sharedPrefs.registerOnSharedPreferenceChangeListener(this)

        // Set current preference summary
        val preference = findPreference(getString(R.string.pref_location_key)) as EditTextPreference
        preference.summary = preference.text
    }

    override fun onStop() {
        super.onStop()
        PreferenceManager
                .getDefaultSharedPreferences(activity)
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
        val preference = findPreference(key)
        if (preference != null && preference !is CheckBoxPreference) {
            setPreferenceSummary(preference, sharedPrefs.getString(key, ""))
        }
    }

    private fun setPreferenceSummary(preference: Preference, value: Any) {
        val stringVal = value.toString()
        when (preference) {
            is ListPreference -> {
                val index = preference.findIndexOfValue(stringVal)
                if (index >= 0) {
                    preference.summary = preference.entries[index]
                }
            }
            is EditTextPreference -> {
                preference.summary = stringVal
            }
        }
    }
}