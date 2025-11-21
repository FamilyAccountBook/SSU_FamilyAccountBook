package com.example.ui_familybook;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ui_familybook.databinding.FragmentFindFamilyBinding;

public class FindFamilyFragment extends Fragment {

    private FragmentFindFamilyBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFindFamilyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendRequest();
            }
        });
    }

    private void handleSendRequest() {
        //    .getText().toString()으로 String 값을, .trim()으로 앞뒤 공백을 제거합니다.
        String emailToSearch = binding.etEmail.getText().toString().trim();

        // 이메일이 비어있는지 확인합니다.
        if (emailToSearch.isEmpty()) {
            binding.etEmail.setError("이메일을 입력해주세요.");
            return; // 요청을 보내지 않고 함수 종료
        }

        //
        // 이곳에서 ViewModel을 호출하거나, Firebase(Firestore)에 데이터를
        // 전송하여 'emailToSearch' 변수의 값을 가진 사용자에게
        // '가족 요청' 데이터를 보내는 실제 로직을 구현하게 됩니다.
        //

        // 10. 사용자에게 요청이 처리되었음을 알림 (테스트용)
        Log.d("FindFamilyFragment", "가족 찾기 요청: " + emailToSearch);
        Toast.makeText(getContext(), emailToSearch + "로 요청을 보냈습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 11. 메모리 누수 방지를 위해 binding을 null로 설정합니다.
        binding = null;
    }
}