package com.soi.moya

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NoticeBottomSheetFragment : BottomSheetDialogFragment() {
    interface OnNoticeSheetRemovedListener {
        fun onNoticeSheetRemovedListener()
    }

    private val PREFS_NAME = "UserPrefs"
    private val NOTICE_ALERT = "notice"
    private var listener: OnNoticeSheetRemovedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notice_bottom_sheet, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNoticeSheetRemovedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentRemovedListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noticeTitle = view.findViewById<TextView>(R.id.noticeTitle)
        val noticeDescription = view.findViewById<TextView>(R.id.noticeDescription)
        val confirmButton = view.findViewById<Button>(R.id.noticeConfirmButton)

        noticeTitle.text = arguments?.getString("title")
        noticeDescription.text = arguments?.getString("description")
        confirmButton.setOnClickListener {
            onClickConfirmButton()
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

    override fun onDetach() {
        super.onDetach()
        listener?.onNoticeSheetRemovedListener()
        listener = null
    }

    private fun onClickConfirmButton() {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(NOTICE_ALERT, arguments?.getString("date"))
        editor.apply()
        dismiss()
    }
}
