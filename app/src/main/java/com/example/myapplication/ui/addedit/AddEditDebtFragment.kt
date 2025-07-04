package com.example.myapplication.ui.addedit

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.data.DebtItem
import com.example.myapplication.databinding.FragmentAddEditDebtBinding
import com.example.myapplication.viewmodel.DebtViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditDebtFragment : Fragment() {
    private val debtViewModel: DebtViewModel by viewModels()
    private var _binding: FragmentAddEditDebtBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null
    private lateinit var photoFile: File

    private var selectedDate: Long = 0L
    private val args: AddEditDebtFragmentArgs by navArgs()
    private var editingDebt: DebtItem? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            binding.imgReceipt.setImageURI(it)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            binding.imgReceipt.setImageURI(imageUri)
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] == true
        val storageGranted = permissions[
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_IMAGES
            else
                Manifest.permission.READ_EXTERNAL_STORAGE
        ] == true

        if (cameraGranted && storageGranted) {
            showImageSourceDialog()
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditDebtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editingDebt = args.debtItem
        populateFieldsForEdit()

        binding.btnUploadImage.setOnClickListener {
            requestImagePermissions()
        }

        binding.btnPickDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveDebt(debtViewModel)
        }

        binding.btnDelete.setOnClickListener {
            editingDebt?.let {
                showDeleteConfirmation(it, debtViewModel)
            }
        }
    }

    private fun populateFieldsForEdit() {
        editingDebt?.let { debt ->
            binding.etPayer.setText(debt.payer)
            binding.etReceiver.setText(debt.receiver)
            binding.etAmount.setText(debt.amount.toString())
            binding.etDescription.setText(debt.description)
            binding.tvDate.text =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(debt.date))
            selectedDate = debt.date
            binding.checkboxSettled.isChecked = debt.isSettled
            if (!debt.imageUri.isNullOrEmpty()) {
                imageUri = Uri.parse(debt.imageUri)
                binding.imgReceipt.setImageURI(imageUri)
            }
            binding.btnDelete.visibility = View.VISIBLE
        }
    }

    private fun requestImagePermissions() {
        val permissions = mutableListOf(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val shouldRequest = permissions.any {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (shouldRequest) {
            requestPermissionsLauncher.launch(permissions.toTypedArray())
        } else {
            showImageSourceDialog()
        }
    }

    private fun showImageSourceDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_image_source)
            .setItems(arrayOf("Camera", "Gallery")) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun openCamera() {
        photoFile = createImageFile()
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireActivity().packageName}.provider",
            photoFile
        )
        cameraLauncher.launch(imageUri)
    }

    private fun createImageFile(): File {
        val timestamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().cacheDir
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.timeInMillis
                val formatted =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))
                binding.tvDate.text = formatted
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveDebt(debtViewModel: DebtViewModel) {
        val payer = binding.etPayer.text.toString().trim()
        val receiver = binding.etReceiver.text.toString().trim()
        val amountText = binding.etAmount.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val isSettled = binding.checkboxSettled.isChecked

        if (payer.isEmpty() || receiver.isEmpty() || amountText.isEmpty() || selectedDate == 0L) {
            Toast.makeText(requireContext(), R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show()
            return
        }

        val nameRegex = "^[\\p{L} ]+\$".toRegex()
        if (!payer.matches(nameRegex)) {
            binding.etPayer.error = getString(R.string.error_only_letters)
            binding.etPayer.requestFocus()
            return
        }
        if (!receiver.matches(nameRegex)) {
            binding.etReceiver.error = getString(R.string.error_only_letters)
            binding.etReceiver.requestFocus()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), R.string.Amount_must_be_a_valid_number, Toast.LENGTH_SHORT).show()
            return
        }

        val newDebt = DebtItem(
            id = editingDebt?.id ?: 0,
            payer = payer,
            receiver = receiver,
            amount = amount,
            date = selectedDate,
            description = description,
            imageUri = imageUri?.toString(),
            isSettled = isSettled
        )

        if (editingDebt == null) {
            debtViewModel.insertDebt(newDebt)
        } else {
            debtViewModel.updateDebt(newDebt)
        }

        findNavController().navigateUp()
    }

    private fun showDeleteConfirmation(debt: DebtItem, debtViewModel: DebtViewModel) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_debt)
            .setMessage(R.string.are_you_sure_delete_debt)
            .setPositiveButton(R.string.delete) { _, _ ->
                debtViewModel.deleteDebt(debt)
                findNavController().navigateUp()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.permission_denied)
            .setMessage(R.string.permission_required_to_access_images)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
