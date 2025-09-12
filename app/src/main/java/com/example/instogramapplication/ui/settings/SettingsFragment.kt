package com.example.instogramapplication.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.instogramapplication.R
import com.example.instogramapplication.databinding.FragmentSettingsBinding
import com.example.instogramapplication.ui.auth.login.LoginActivity
import com.example.instogramapplication.utils.ApiUtils
import com.example.instogramapplication.utils.DialogUtils
import com.example.instogramapplication.utils.ExtensionUtils.loadUrl
import com.example.instogramapplication.utils.LanguageUtils
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(requireActivity())
    }
    private val viewModel: SettingsViewModel by viewModels {
        factory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setupListener()
    }

    private fun initView() {
        viewLifecycleOwner.lifecycleScope.launch {
            val name = viewModel.getName()
            binding.settingsNameProfile.text = name

            val imgUrl = ApiUtils.avatarUrl(requireContext(), name)
            binding.itemStoryProfile.loadUrl(imgUrl)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val langCode = viewModel.getCurrentLang()
            val displayName =
                LanguageUtils.getLanguage(langCode).find { it.code == langCode }?.name ?: "Default"
            binding.settingsTvBahasa.text = displayName
        }
    }

    private fun setupListener() {
        binding.apply {
            settingsGantiBahasa.setOnClickListener { showLanguageBottomSheet() }
            settingBtnLogout.setOnClickListener { logOut() }
        }
    }

    private fun showLanguageBottomSheet() {
        lifecycleScope.launch {
            val currentLang = viewModel.getCurrentLang()
            val sheet = LanguageBottomSheet(currentLang) { selectLang ->
                viewModel.setLanguage(requireContext(), selectLang) {
                    activity?.recreate()
                }
            }
            sheet.show(childFragmentManager, "language_bottom_sheet")
        }
    }

    private fun logOut() {
        DialogUtils.confirmDialog(
            requireContext(),
            requireContext().getString(R.string.logout),
            requireContext().getString(R.string.logout_message),
            requireContext().getString(R.string.dialog_exit_edit_no),
            requireContext().getString(R.string.dialog_exit_edit_yes),
        ) {
            viewModel.logOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}