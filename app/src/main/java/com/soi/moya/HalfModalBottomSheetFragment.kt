package com.soi.moya

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HalfModalBottomSheetFragment : BottomSheetDialogFragment() {

    private val PREFS_NAME = "UserPrefs"
    private val KEY_APP_VERSION = "appVersion"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_half_modal_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val version = arguments?.getString("newVersion")
        val features = arguments?.getStringArrayList("features")

        val featureListView = view.findViewById<ListView>(R.id.featureListView)
        val adapter = FeatureAdapter(requireContext(), features)
        featureListView.adapter = adapter

        val updateButton = view.findViewById<Button>(R.id.updateButton)
        val ignoreUpdateButton = view.findViewById<Button>(R.id.ignoreUpdateButton)
        updateButton.setOnClickListener {
            openPlayStore()
        }
        ignoreUpdateButton.setOnClickListener {
            onLaterButtonClick(version ?: "")
        }
    }

    private class FeatureAdapter(
        private val context: Context,
        private val features: List<String>?
    ) : BaseAdapter() {

        override fun getCount(): Int {
            return features?.size ?: 0
        }

        override fun getItem(position: Int): Any? {
            return features?.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            val viewHolder: ViewHolder

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.list_item_new_featrue, parent, false)
                viewHolder = ViewHolder()
                viewHolder.featureTextView = view.findViewById(R.id.textViewFeature)
                viewHolder.numberTextView = view.findViewById(R.id.textViewNumber)
                view.tag = viewHolder
            } else {
                viewHolder = view.tag as ViewHolder
            }

            val feature = getItem(position) as? String
            viewHolder.featureTextView.text = feature
            viewHolder.numberTextView.text = (position + 1).toString()

            return view!!
        }

        private class ViewHolder {
            lateinit var featureTextView: TextView
            lateinit var numberTextView: TextView
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(R.drawable.bottom_sheet_rounded_corners)
        }

        return dialog
    }
    private fun openPlayStore() {
        val appPackageName = requireContext().packageName
        val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))

        try {
            startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            startActivity(webIntent)
        }
    }

    private fun onLaterButtonClick(version: String) {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_APP_VERSION, version)
        editor.apply()
        dismiss()
    }
}
