package dev.icerock.gitviewer.presentation.ui.issue.info

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
import dev.icerock.gitviewer.databinding.FragmentIssueInfoBinding
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme

@AndroidEntryPoint
internal class IssueInfoFragment : Fragment() {
    private val issueInfoViewModel by viewModels<IssueInfoViewModel>()
    private var binding: FragmentIssueInfoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIssueInfoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            val args by navArgs<IssueInfoFragmentArgs>()
            val navController = findNavController()

            setContent {
                GVTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        IssueInfoRoute(
                            viewModel = issueInfoViewModel,
                            number = args.issueNumber,
                            onNavigateUp = navController::navigateUp
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}