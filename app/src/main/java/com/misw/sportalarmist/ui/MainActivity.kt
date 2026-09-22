package com.misw.sportalarmist.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import com.misw.sportalarmist.R
import com.misw.sportalarmist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.tabTorneos.setOnClickListener { navController.navigate(R.id.homeFragment) }
        binding.tabPartidos.setOnClickListener { navController.navigate(R.id.matchesFragment) }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            setTorneosTabSelected(destination.id == R.id.homeFragment)
        }
    }

    private fun setTorneosTabSelected(torneosSelected: Boolean) {
        binding.tabTorneos.setBackgroundResource(
            if (torneosSelected) R.drawable.bg_nav_tab_active else R.drawable.bg_nav_tab_inactive
        )
        binding.tabTorneosIcon.setImageResource(
            if (torneosSelected) R.drawable.ic_nav_torneos_active else R.drawable.ic_nav_torneos_inactive
        )
        binding.tabTorneosLabel.visibility = if (torneosSelected) View.VISIBLE else View.GONE

        binding.tabPartidos.setBackgroundResource(
            if (torneosSelected) R.drawable.bg_nav_tab_inactive else R.drawable.bg_nav_tab_active
        )
        binding.tabPartidosIcon.setImageResource(
            if (torneosSelected) R.drawable.ic_nav_partidos_inactive else R.drawable.ic_nav_partidos_active
        )
        binding.tabPartidosLabel.visibility = if (torneosSelected) View.GONE else View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
