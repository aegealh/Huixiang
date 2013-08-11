package com.pybeta.huixiang.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.android.volley.Request;

public class ProgressDialogFragment extends DialogFragment {
	
	private String mMessage;
	private Request<?> mRequest = null;
	
	public void setRequest(Request<?> request) {
		this.mRequest = request;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String message) {
		this.mMessage = message;
	}

	public static ProgressDialogFragment newInstance() {
		ProgressDialogFragment frag = new ProgressDialogFragment();
        frag.setRetainInstance(true);
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(this.mMessage);
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
    	if (this.mRequest != null) {
    		this.mRequest.cancel();
    	}
        super.onCancel(dialog);
    }
}