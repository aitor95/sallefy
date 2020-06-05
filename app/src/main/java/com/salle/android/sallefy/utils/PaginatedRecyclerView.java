package com.salle.android.sallefy.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaginatedRecyclerView extends RecyclerView {

    private PaginatedRecyclerViewListener listener;

    private boolean isLoading = false;
    private boolean isLast = false;
    private int PAGE_SIZE;

    public PaginatedRecyclerView(@NonNull Context context) {
        super(context);
    }

    public PaginatedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaginatedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPageSize(int numberOfUsers) {
        PAGE_SIZE = numberOfUsers;
    }

    public interface PaginatedRecyclerViewListener {

        void onPageLoaded();
    }

    public void setListener(PaginatedRecyclerViewListener listener) {
        this.listener = listener;

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(mLinearLayoutManager);

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mLinearLayoutManager.getChildCount();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLast) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        listener.onPageLoaded();
                    }
                }
            }
        });
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
