package com.misw.sportalarmist.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
            setTorneosTabSelected(destination.id != R.id.matchesFragment)
            // Punto amarillo del calendario: se revisa en cada cambio de pantalla.
            PendingDot.refresh(this)
        }
    }

    override fun onResume() {
        super.onResume()
        // Al abrir la app o volver a ella: muestra el punto si ya hay partidos pendientes.
        PendingDot.refresh(this)
    }

    private fun setTorneosTabSelected(torneosSelected: Boolean) {
        // Active and inactive icons share the same glyph and viewport (only
        // the stroke color differs), so the ImageView's own size never
        // changes — just the drawable and the pill's chrome. The label
        // stays visible in both states; only its color switches.
        binding.tabTorneos.setBackgroundResource(
            if (torneosSelected) R.drawable.bg_nav_tab_active else R.drawable.bg_nav_tab_inactive
        )
        binding.tabTorneosIcon.setImageResource(
            if (torneosSelected) R.drawable.ic_nav_torneos_active else R.drawable.ic_nav_torneos_inactive
        )
        binding.tabTorneosLabel.setTextColor(navLabelColor(torneosSelected))

        binding.tabPartidos.setBackgroundResource(
            if (torneosSelected) R.drawable.bg_nav_tab_inactive else R.drawable.bg_nav_tab_active
        )
        binding.tabPartidosIcon.setImageResource(
            if (torneosSelected) R.drawable.ic_nav_partidos_inactive else R.drawable.ic_nav_partidos_active
        )
        binding.tabPartidosLabel.setTextColor(navLabelColor(!torneosSelected))
    }

    private fun navLabelColor(selected: Boolean): Int = ContextCompat.getColor(
        this,
        if (selected) R.color.nav_tab_active_label else R.color.nav_tab_inactive_label
    )

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}