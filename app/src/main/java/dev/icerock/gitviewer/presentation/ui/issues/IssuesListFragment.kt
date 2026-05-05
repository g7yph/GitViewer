package dev.icerock.gitviewer.presentation.ui.issues

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
import dev.icerock.gitviewer.databinding.FragmentIssuesListBinding
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import kotlin.getValue

@AndroidEntryPoint
internal class IssuesListFragment : Fragment() {
    private val issuesListViewModel by viewModels<IssuesListViewModel>()
    private var binding: FragmentIssuesListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIssuesListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            val args by navArgs<IssuesListFragmentArgs>()
            val navController = findNavController()

            setContent {
                GVTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        IssuesListRoute(
                            viewModel = issuesListViewModel,
                            repoId = args.repoId,
                            repoOwner = args.repoOwner,
                            repoName = args.repoName,
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