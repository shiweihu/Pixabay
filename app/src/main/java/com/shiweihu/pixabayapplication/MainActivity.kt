package com.shiweihu.pixabayapplication

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.tabs.TabLayoutMediator
import com.shiweihu.pixabayapplication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val icons=  listOf(R.drawable.image_search_selector,R.drawable.vedio_icon_selector)
    private val names by lazy {
        this.resources.getStringArray(R.array.bottom_text)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPage.adapter = FragmentsAdapter(this)
        TabLayoutMediator(binding.bottomNavigate,binding.viewPage){tab,position ->
            tab.setIcon(icons[position])
            tab.text = names[position]
        }.attach()


    }

}