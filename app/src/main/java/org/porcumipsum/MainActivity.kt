package org.porcumipsum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.porcumipsum.fragments.AppInfoFragment
import org.porcumipsum.fragments.GeneratorFragment
import org.porcumipsum.fragments.ListFragment
import org.porcumipsum.fragments.PickerFragment
import org.porcumipsum.utils.FavouritesUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val generatorFragment = GeneratorFragment()
        val pickerFragment = PickerFragment()
        val listFragment = ListFragment()
        val navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.generator -> {
                    switchFragment(generatorFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.picker -> {
                    switchFragment(pickerFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.list -> {
                    switchFragment(listFragment)
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }

        if (savedInstanceState == null) {
            switchFragment(generatorFragment)
        }

        FavouritesUtils.loadPreferences(this)
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }
}