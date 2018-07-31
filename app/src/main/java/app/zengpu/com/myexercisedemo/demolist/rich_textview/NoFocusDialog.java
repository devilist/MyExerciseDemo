/*
 * Copyright  2018  zengpu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.zengpu.com.myexercisedemo.demolist.rich_textview;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import app.zengpu.com.myexercisedemo.R;

public class NoFocusDialog extends DialogFragment {


    public static NoFocusDialog newInstance() {

        Bundle args = new Bundle();

        NoFocusDialog fragment = new NoFocusDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_no_focus, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("onViewCreated","onViewCreated");
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        getDialog().getWindow().getAttributes().gravity = Gravity.BOTTOM;
        getDialog().getWindow().getAttributes().dimAmount = 0;

        getView().findViewById(R.id.tv_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "dialog 点击", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void focusBack() {
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        getDialog().getWindow().setLayout(displayMetrics.widthPixels, displayMetrics.heightPixels / 4);
    }
}
