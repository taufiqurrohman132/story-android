package com.example.instogramapplication.ui.story.post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.instogramapplication.R
import com.example.instogramapplication.databinding.FragmentEditBinding
import com.example.instogramapplication.ui.story.list.ListStoryFragment
import com.example.instogramapplication.utils.ExtensionUtils.reduceFileImage
import com.example.instogramapplication.utils.ExtensionUtils.setGradientText
import com.example.instogramapplication.utils.PostUtils
import com.example.instogramapplication.utils.Resource
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
    }

    private fun setupListener(){
        binding.apply {
            postBtnBackToTake.setOnClickListener { backToTakePhoto() }
            postBtnPosting.setOnClickListener { uploadStory() }
        }
    }

    private fun observer(){
        lifecycleScope.launch {
            viewModel.uploadState.collect{ result ->
                when(result){
                    is Resource.Loading -> {}
                    is Resource.Error -> {}
                    is Resource.Success -> {
                        showToast("sukses upload")
                        uploadSuccess()
                    }
                    is Resource.Empty -> {}
                    else -> {}
                }
            }
        }
    }

    private fun backToTakePhoto(){
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun uploadStory(){
        currentImageUri?.let { uri ->
            val imageFile = PostUtils.uriToFile(uri, requireActivity()).reduceFileImage()
            Log.d(TAG, "uploadStory: show image ${imageFile.path}")
            val desc = binding.postTvDesk.text.toString()

            viewModel.uploadStory(imageFile, desc)
        } ?: showToast("gambar kosong")
    }

    private fun showLoading(isLoading: Boolean) {
//        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun uploadSuccess(){
        val intent = Intent(requireActivity(), ListStoryFragment::class.java)
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