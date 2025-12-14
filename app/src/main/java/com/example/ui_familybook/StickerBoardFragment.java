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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    // 앱 실행 시 첫 로딩인지 확인하는 플래그 (애니메이션 방지용)
    private boolean isFirstLoad = true;

    // Firebase 관련 변수
    private FirebaseFirestore db;
    private ListenerRegistration stickerListener;
    private String targetChildUid = "child_user_1"; // ★ 실제 앱에서는 로그인한 사용자 UID 사용

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

        db = FirebaseFirestore.getInstance();

        setupRecyclerView();
        setupClickListeners();

        observeStickerUpdates();
    }

    // ★ 중요: 프래그먼트 종료 시 리스너 해제 (메모리 누수/크래시 방지)
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (stickerListener != null) {
            stickerListener.remove();
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

    private void observeStickerUpdates() {
        stickerListener = db.collection("users")
                .document(targetChildUid)
                .collection("stickers")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (snapshots != null) {
                        // 1. DB 데이터를 Sticker 객체 리스트로 변환
                        List<Sticker> newStickerList = new ArrayList<>();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                            // Sticker 클래스가 있어야 하며, 빈 생성자가 필수입니다.
                            Sticker sticker = doc.toObject(Sticker.class);
                            newStickerList.add(sticker);
                        }

                        // 이전 개수와 새 개수 비교
                        int oldCount = adapter.getFilledCount();
                        int newCount = newStickerList.size();

                        // 2. 어댑터에 데이터 전달 (화면 갱신)
                        adapter.submitList(newStickerList);

                        // 3. UI 텍스트/프로그레스바 갱신
                        updateProgressUI(newCount);

                        // 4. 애니메이션 처리 로직
                        if (isFirstLoad) {
                            // 첫 로딩때는 애니메이션 실행 안 함
                            isFirstLoad = false;
                        } else {
                            // 실시간 추가: 개수가 늘어났을 때만 마지막 아이템 애니메이션
                            if (newCount > oldCount) {
                                binding.stickerRecyclerView.post(() -> {
                                    animateStickerAtPosition(newCount - 1);
                                });
                            }
                        }
                    }
                });
    }

    // UI 업데이트 통합 메서드
    private void updateProgressUI(int currentCount) {
        // 텍스트
        binding.tvProgressCount.setText(currentCount + "/30");

        // 프로그레스바
        int progress = (int) ((currentCount / 30f) * 100);
        binding.progressBar.setProgress(progress);

        // 안내 메시지
        int remaining = TOTAL_STICKERS - currentCount;
        if (remaining > 0) {
            binding.tvInfoMessage.setText(remaining + "개만 더 모으면 스티커판을 채울 수 있어요!");
        } else {
            binding.tvInfoMessage.setText("스티커판을 완성했어요! 🎉");
        }
    }

    private void animateStickerAtPosition(int position) {
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
        // index는 0부터 시작하므로 +1 해서 비교
        if (position + 1 == TOTAL_STICKERS) {
            binding.stickerRecyclerView.postDelayed(() -> {
                showCompletionAnimation();
            }, 300);
        }
    }

    private void showCompletionAnimation() {
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

                    // ★ DB 데이터 삭제 로직 실행
                    clearStickersInDb();
                })
                .start();
    }

    // ★ DB의 스티커 데이터를 모두 지우는 메서드
    private void clearStickersInDb() {
        db.collection("users")
                .document(targetChildUid)
                .collection("stickers")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Batch(일괄 처리)를 사용하여 한 번에 삭제
                    WriteBatch batch = db.batch();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        batch.delete(document.getReference());
                    }

                    batch.commit().addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "스티커판이 초기화되었습니다!", Toast.LENGTH_SHORT).show();
                        // 로컬 상태 리셋 (리스너가 자동 감지하므로 여기서 UI 갱신 안 해도 됨)
                        isFirstLoad = true;
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "초기화 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                });
    }
}