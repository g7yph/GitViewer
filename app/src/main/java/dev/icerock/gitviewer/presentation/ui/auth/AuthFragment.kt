package dev.icerock.gitviewer.presentation.ui.auth

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
import dev.icerock.gitviewer.databinding.FragmentAuthBinding
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme

@AndroidEntryPoint
internal class AuthFragment : Fragment() {
    private val authViewModel by viewModels<AuthViewModel>()
    private var binding: FragmentAuthBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
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
                        AuthRoute(
                            viewModel = authViewModel,
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