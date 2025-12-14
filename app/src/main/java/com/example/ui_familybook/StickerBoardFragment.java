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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui_familybook.databinding.FragmentStickerboardBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
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
    private int currentStickerCount = 0;

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

        db = FirebaseFirestore.getInstance(); // DB 초기화

        setupRecyclerView();
        setupClickListeners();

        observeStickerUpdates();
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
                .orderBy("timestamp", Query.Direction.ASCENDING) // 시간순 정렬
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (snapshots != null) {
                        // 초기 데이터 로딩인지 확인 (앱 켜자마자 애니메이션 방지용)
                        // snapshots.size()가 현재 카운트보다 크면 새로운게 들어온 것

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            // 새로운 스티커가 "추가(ADDED)" 되었을 때만 실행
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                // 현재 화면에 표시된 것보다 더 많은 데이터가 들어왔을 때만 추가 (중복 방지)
                                if (currentStickerCount < snapshots.size()) {
                                    addStickerWithAnimation();
                                }
                            }
                        }

                        // 데이터 개수 동기화 (초기 로딩 시 애니메이션 없이 UI만 갱신)
                        if (currentStickerCount == 0 && !snapshots.isEmpty()) {
                            currentStickerCount = snapshots.size();
                            updateUIWithoutAnimation();
                        }
                    }
                });
    }

    // 애니메이션과 함께 스티커 추가 (실시간 수신 시 호출)
    private void addStickerWithAnimation() {
        if (currentStickerCount >= TOTAL_STICKERS) return;

        int position = currentStickerCount;
        currentStickerCount++;

        // 어댑터 업데이트
        adapter.updateStickers(currentStickerCount);
        updateProgressText();
        updateProgressBar(currentStickerCount);
        updateInfoMessage(currentStickerCount);

        // 애니메이션 실행
        binding.stickerRecyclerView.post(() -> {
            animateStickerAtPosition(position);
        });
    }

    // 애니메이션 없이 UI만 갱신 (초기 로딩용)
    private void updateUIWithoutAnimation() {
        adapter.updateStickers(currentStickerCount);
        updateProgressText();
        updateProgressBar(currentStickerCount);
        updateInfoMessage(currentStickerCount);
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

                    // DB 초기화 로직 필요 (예: stickers 컬렉션 비우기)
                    // 여기서는 UI만 초기화
                    currentStickerCount = 0;
                    updateUIWithoutAnimation();
                })
                .start();
    }

    private void updateProgressText() {
        binding.tvProgressCount.setText(currentStickerCount + "/30");
    }

    private void updateProgressBar(int current) {
        int progress = (int) ((current / 30f) * 100);
        binding.progressBar.setProgress(progress);
    }

    private void updateInfoMessage(int current) {
        int remaining = TOTAL_STICKERS - current;
        if (remaining > 0) {
            binding.tvInfoMessage.setText(remaining + "개만 더 모으면 스티커판을 채울 수 있어요!");
        } else {
            binding.tvInfoMessage.setText("스티커판을 완성했어요! 🎉");
        }
    }
}