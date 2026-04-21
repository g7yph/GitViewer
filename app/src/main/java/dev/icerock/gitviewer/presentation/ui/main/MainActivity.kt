package dev.icerock.gitviewer.presentation.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.databinding.ActivityMainBinding
import dev.icerock.gitviewer.presentation.ui.main.model.MainEvent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !keepSplashScreen }
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        mainViewModel.onEvent(MainEvent.CheckAuth)
        mainViewModel.uiStates()
            .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .onEach { mainUiState ->
                val destinationId = if (mainUiState.isAuthorized == false)
                    R.id.authFragment
                else
                    R.id.repositoriesListFragment

                initAppGraph(startDestinationId = destinationId)

                keepSplashScreen = false
            }
            .distinctUntilChanged()
            .launchIn(lifecycleScope)

        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)

            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    private fun initAppGraph(startDestinationId: Int) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(graphResId = R.navigation.app_graph)
        navGraph.setStartDestination(startDestId = startDestinationId)
        navController.graph = navGraph
    }
}