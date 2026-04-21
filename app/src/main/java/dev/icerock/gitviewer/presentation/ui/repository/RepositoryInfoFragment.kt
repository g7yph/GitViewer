package dev.icerock.gitviewer.presentation.ui.repository

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
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.gitviewer.databinding.FragmentRepositoryInfoBinding
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme

@AndroidEntryPoint
internal class RepositoryInfoFragment : Fragment() {
    private val repositoryInfoViewModel by viewModels<RepositoryInfoViewModel>()
    private var binding: FragmentRepositoryInfoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepositoryInfoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            val args by navArgs<RepositoryInfoFragmentArgs>()
            val navController = findNavController()

            setContent {
                GVTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        RepositoryInfoRoute(
                            viewModel = repositoryInfoViewModel,
                            owner = args.repoOwner,
                            name = args.repoName,
                            onNavigate = navController::navigate,
                            onNavigateUp = navController::navigateUp
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