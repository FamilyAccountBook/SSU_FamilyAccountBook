package com.example.ui_familybook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_familybook.databinding.FragmentStickerboardBinding;
// ★ Realtime Database 관련 Import로 변경
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;

public class StickerBoardFragment extends Fragment {

    private FragmentStickerboardBinding binding;
    private StickerAdapter adapter;
    private static final int TOTAL_STICKERS = 30;

    private boolean isFirstLoad = true;

    // ★ 변수 타입 변경: Firestore -> Realtime Database
    private FirebaseDatabase db;
    private ValueEventListener stickerListener;
    private DatabaseReference stickerRef; // 리스너 관리를 위한 DatabaseReference 저장

    private String targetChildUid = "child_user_1";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStickerboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ★ Realtime Database 초기화
        db = FirebaseDatabase.getInstance();
        // ★ DB Reference 생성: /users/{uid}/stickers 경로를 가리킴
        stickerRef = db.getReference("users")
                .child(targetChildUid)
                .child("stickers");

        setupRecyclerView();
        setupClickListeners();

        observeStickerUpdates();
    }

    // ★ 중요: 프래그먼트 종료 시 리스너 해제 (메모리 누수 방지)
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (stickerListener != null) {
            // ★ Realtime Database 리스너 해제 방식
            stickerRef.removeEventListener(stickerListener);
            stickerListener = null;
        }
        binding = null;
    }

    private void setupRecyclerView() {
        adapter = new StickerAdapter(TOTAL_STICKERS);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 5);
        binding.stickerRecyclerView.setLayoutManager(layoutManager);
        binding.stickerRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    // ★ Realtime Database 리스너로 변경
    private void observeStickerUpdates() {
        stickerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 1. DB 데이터를 Sticker 객체 리스트로 변환
                List<Sticker> newStickerList = new ArrayList<>();
                // Realtime DB의 children을 순회하며 데이터 파싱
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Sticker 클래스가 있어야 하며, 빈 생성자가 필수입니다.
                    Sticker sticker = dataSnapshot.getValue(Sticker.class);
                    if (sticker != null) {
                        newStickerList.add(sticker);
                    }
                }

                // (필요하다면) RTDB는 timestamp 정렬을 자동으로 보장하지 않으므로,
                // 리스너 등록 시 orderByChild("timestamp")를 사용하고
                // 여기서 다시 Collections.sort(newStickerList, ...)를 통해 정렬할 수도 있습니다.

                int oldCount = adapter.getFilledCount();
                int newCount = newStickerList.size();

                // 2. 어댑터에 데이터 전달 (화면 갱신)
                adapter.submitList(newStickerList);

                // 3. UI 텍스트/프로그레스바 갱신
                updateProgressUI(newCount);

                // 4. 애니메이션 처리 로직
                if (isFirstLoad) {
                    isFirstLoad = false;
                } else {
                    if (newCount > oldCount) {
                        binding.stickerRecyclerView.post(() -> {
                            animateStickerAtPosition(newCount - 1);
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB 연결 에러 처리
                // Toast.makeText(getContext(), "DB 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        // ★ Realtime DB 리스너 등록 (timestamp 기준으로 정렬 요청)
        stickerRef.orderByChild("timestamp").addValueEventListener(stickerListener);
    }

    // 이하는 기존과 동일하며, 인자 없는 updateProgressText() 호출만 updateProgressUI(int)로 변경했습니다.
    // ...

    private void updateProgressUI(int currentCount) {
        binding.tvProgressCount.setText(currentCount + "/30");

        int progress = (int) ((currentCount / 30f) * 100);
        binding.progressBar.setProgress(progress);

        int remaining = TOTAL_STICKERS - currentCount;
        if (remaining > 0) {
            binding.tvInfoMessage.setText(remaining + "개만 더 모으면 스티커판을 채울 수 있어요!");
        } else {
            binding.tvInfoMessage.setText("스티커판을 완성했어요! 🎉");
        }
    }

    private void animateStickerAtPosition(int position) {
        // ... 기존 코드 유지 ...
        RecyclerView.ViewHolder viewHolder = binding.stickerRecyclerView
                .findViewHolderForAdapterPosition(position);

        if (viewHolder != null) {
            View itemView = viewHolder.itemView;
            View stickerView = itemView.findViewById(R.id.tv_sticker_icon);

            if (stickerView != null) {
                animateSticker(stickerView, position);
            }
        }
    }

    private void animateSticker(View stickerView, int position) {
        // ... 기존 코드 유지 ...
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(stickerView, "scaleX", 1f, 0.3f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(stickerView, "scaleY", 1f, 0.3f);
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(stickerView, "scaleX", 0.3f, 1.2f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(stickerView, "scaleY", 0.3f, 1.2f);
        ObjectAnimator bounceX = ObjectAnimator.ofFloat(stickerView, "scaleX", 1.2f, 1f);
        ObjectAnimator bounceY = ObjectAnimator.ofFloat(stickerView, "scaleY", 1.2f, 1f);

        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);
        scaleUpX.setDuration(200);
        scaleUpY.setDuration(200);
        scaleUpX.setInterpolator(new OvershootInterpolator());
        scaleUpY.setInterpolator(new OvershootInterpolator());
        bounceX.setDuration(150);
        bounceY.setDuration(150);
        bounceX.setInterpolator(new BounceInterpolator());
        bounceY.setInterpolator(new BounceInterpolator());

        AnimatorSet stampAnimator = new AnimatorSet();
        stampAnimator.play(scaleDownX).with(scaleDownY);
        stampAnimator.play(scaleUpX).with(scaleUpY).after(scaleDownX);
        stampAnimator.play(bounceX).with(bounceY).after(scaleUpX);

        stampAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                checkCompletion(position);
            }
        });

        stampAnimator.start();
    }

    private void checkCompletion(int position) {
        if (position + 1 == TOTAL_STICKERS) {
            binding.stickerRecyclerView.postDelayed(() -> {
                showCompletionAnimation();
            }, 300);
        }
    }

    private void showCompletionAnimation() {
        // ... 기존 코드 유지 ...
        EmitterConfig emitterConfig = new Emitter(100L, TimeUnit.MILLISECONDS).max(100);
        Party party = new PartyFactory(emitterConfig)
                .spread(360)
                .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(0f, 30f)
                .position(new Position.Relative(0.5, 0.3))
                .build();

        binding.konfettiView.start(party);

        binding.completionMessage.setVisibility(View.VISIBLE);
        binding.completionMessage.setAlpha(0f);
        binding.completionMessage.setScaleX(0.5f);
        binding.completionMessage.setScaleY(0.5f);

        binding.completionMessage.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator())
                .withEndAction(() -> {
                    binding.completionMessage.postDelayed(() -> {
                        resetBoardWithAnimation();
                    }, 3000);
                })
                .start();
    }

    private void resetBoardWithAnimation() {
        binding.completionMessage.animate()
                .alpha(0f)
                .scaleX(0.5f)
                .scaleY(0.5f)
                .setDuration(300)
                .withEndAction(() -> {
                    binding.completionMessage.setVisibility(View.GONE);

                    // DB 데이터 삭제 로직 실행
                    clearStickersInDb();
                })
                .start();
    }

    // ★ Realtime Database 데이터 삭제 메서드로 변경
    private void clearStickersInDb() {
        // 해당 Reference의 값을 null로 설정하여 전체 컬렉션 삭제
        stickerRef.setValue(null)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "스티커판이 초기화되었습니다!", Toast.LENGTH_SHORT).show();
                    isFirstLoad = true;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "초기화 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}