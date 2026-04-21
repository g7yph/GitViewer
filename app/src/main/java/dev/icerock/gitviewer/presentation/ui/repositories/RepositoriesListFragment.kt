package dev.icerock.gitviewer.presentation.ui.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.gitviewer.databinding.FragmentRepositoriesListBinding
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import kotlin.getValue

@AndroidEntryPoint
internal class RepositoriesListFragment : Fragment() {
    private val repositoriesListViewModel by viewModels<RepositoriesListViewModel>()
    private var binding: FragmentRepositoriesListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepositoriesListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            val navController = findNavController()

            setContent {
                GVTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        RepositoriesListRoute(
                            viewModel = repositoriesListViewModel,
                            onNavigate = navController::navigate
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}