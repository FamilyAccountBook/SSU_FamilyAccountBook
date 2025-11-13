package com.example.ui_familybook;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.databinding.DialogEditProfileBinding;
import com.example.ui_familybook.databinding.FragmentSettingParentBinding;

public class SettingParentFragment extends Fragment {

    private FragmentSettingParentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingParentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 모든 UI 관련 로직(클릭 리스너 등)은 onViewCreated 안에서 설정!
        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
    }

    private void showEditProfileDialog() {
        DialogEditProfileBinding dialogBinding = DialogEditProfileBinding.inflate(getLayoutInflater());

        // Fragment에서는 'this' 대신 'requireContext()'를 사용합니다.
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogBinding.getRoot()); // 1번에서 가져온 레이아웃을 뷰로 설정

        // 다이얼로그 객체 생성 (중요: .show() 전에 해야 함)
        AlertDialog dialog = builder.create();

        // 다이얼로그 내부의 '저장' 버튼 클릭 이벤트 처리
        dialogBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = dialogBinding.etName.getText().toString();
                String newEmail = dialogBinding.etEmail.getText().toString();

                // 💡 여기가 중요합니다!
                // TODO: DB(Firebase)에 newName, newEmail을 업데이트하는 로직 수행
                // (부모 설정 프래그먼트면 부모 DB를, 자녀 설정 프래그먼트면 자녀 DB를 업데이트)

                // 다이얼로그 닫기
                dialog.dismiss();
            }
        });

        // (선택) 다이얼로그 배경을 투명하게 해서 둥근 모서리(XML)가 보이게 함
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // 다이얼로그 띄우기
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 5. 메모리 누수 방지를 위해 Fragment가 파괴될 때 binding을 null로 만듭니다. (필수)
        binding = null;
    }
}