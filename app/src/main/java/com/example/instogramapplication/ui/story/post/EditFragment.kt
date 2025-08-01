package com.example.instogramapplication.ui.story.post

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.instogramapplication.MainActivity
import com.example.instogramapplication.R
import com.example.instogramapplication.databinding.FragmentEditBinding
import com.example.instogramapplication.ui.auth.login.LoginActivity
import com.example.instogramapplication.ui.story.list.ListStoryFragment
import com.example.instogramapplication.utils.DialogUtils
import com.example.instogramapplication.utils.DialogUtils.showToast
import com.example.instogramapplication.utils.ExtensionUtils.keyboardVisibilityFlow
import com.example.instogramapplication.utils.ExtensionUtils.reduceFileImage
import com.example.instogramapplication.utils.ExtensionUtils.setGradientText
import com.example.instogramapplication.utils.PostUtils
import com.example.instogramapplication.utils.PostUtils.isKeyboardVisible
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.utils.constants.DialogType
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(requireActivity())
    }

    private val viewModel: EditViewModel by viewModels{
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentImageUri = arguments?.getString(ARG_URI)?.toUri()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: fragment")

        initView()
        setupListener()
        observer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



    @SuppressLint("ClickableViewAccessibility")
    private fun initView(){
        // terima uri gambar dari post act
        currentImageUri?.let { uri ->
            binding.imgvShowFromGalery.setImageURI(uri)
        }

        binding.postTvBerjalan.apply {
            setGradientText(
                ContextCompat.getColor(requireActivity(), R.color.color_variant),
                ContextCompat.getColor(requireActivity(), R.color.color_base)
            )
            isSelected = true
        }

        // handle back
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    confirmBack()
                }
            })

        // cek keyboard
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                binding.root.keyboardVisibilityFlow().collect { isVisible ->
                    binding?.let {
                        binding.dimOverlayCamera.isVisible = isVisible
                    }
                }
            }
        }
    }

    private fun setupListener(){
        binding.apply {
            postBtnBackToTake.setOnClickListener { confirmBack() }
            postBtnPosting.setOnClickListener { uploadStory() }
        }
    }

    private fun observer(){
        lifecycleScope.launch {
            viewModel.uploadState.collect{ result ->
                Log.d(TAG, "observer: upload state $result")
                when(result){
                    is Resource.Loading -> showLoading(true)
                    is Resource.Error -> {
                        showError()
                        showLoading(false)
                    }
                    is Resource.Success -> {
                        showSuccess()
                        showLoading(false)
                    }
                    is Resource.Empty -> { showLoading(false)}
                    is Resource.ErrorConnection -> {
                        showLoading(false)
                        showToast(requireContext().getString(R.string.error_koneksi), requireActivity())
                    }
                    else -> { showLoading(false)}
                }
            }
        }
    }

    private fun showError() {
        DialogUtils.stateDialog(
            requireContext(),
            DialogType.ERROR,
            requireActivity().getString(R.string.popup_error_title),
            requireActivity().getString(R.string.popup_error_desc),
            requireActivity().getString(R.string.popup_error_btn)
        ){
            it.dismiss()
            binding.dimOverlay.visibility = View.INVISIBLE

        }
    }

    private fun showSuccess() {
        DialogUtils.stateDialog(
            requireContext(),
            DialogType.SUCCESS,
            requireActivity().getString(R.string.popup_success_title),
            requireActivity().getString(R.string.popup_success_desc),
            requireActivity().getString(R.string.popup_success_btn),
        ){
            showToast(requireActivity().getString(R.string.toast_success_upload), requireActivity())
            uploadSuccess()
            it.dismiss()
            binding.dimOverlay.visibility = View.INVISIBLE
        }
    }

    private fun confirmBack(){
        DialogUtils.confirmDialog(
            requireContext(),
            requireContext().getString(R.string.dialog_exit_edit_title),
            requireContext().getString(R.string.dialog_exit_edit_message),
        ){
            backToTakePhoto()
        }
    }

    private fun backToTakePhoto(){
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun uploadStory(){
        currentImageUri?.let { uri ->
            val imageFile = PostUtils.uriToFile(uri, requireActivity()).reduceFileImage()
            val desc = binding.postTvDesk.text.toString()

            if (desc.isNotBlank()) {
                viewModel.uploadStory(imageFile, desc)
            } else {
                showToast(getString(R.string.error_empty_description), requireActivity())
            }
        } ?: showToast(getString(R.string.error_empty_image), requireActivity())

    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            dimOverlay.isVisible = isLoading
            loading.isVisible = isLoading
        }
    }

    private fun uploadSuccess(){
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    companion object{
        private val TAG = EditFragment::class.java.simpleName

        private const val ARG_URI = "image_uri"

        fun newInstance(imageUri: String): EditFragment{
            val fragment = EditFragment()
            val args = Bundle()
            args.putString(ARG_URI, imageUri)
            fragment.arguments = args
            return fragment
        }
    }





}