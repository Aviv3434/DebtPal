package com.example.myapplication.ui.sync

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.R
import com.example.myapplication.worker.SyncWorker

class SyncFragment : Fragment(R.layout.fragment_sync) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btnSyncNow).setOnClickListener {
            val req = OneTimeWorkRequestBuilder<SyncWorker>().build()
            WorkManager.getInstance(requireContext()).enqueue(req)
            Toast.makeText(requireContext(), "Sync sent", Toast.LENGTH_SHORT).show()

        }
    }
}