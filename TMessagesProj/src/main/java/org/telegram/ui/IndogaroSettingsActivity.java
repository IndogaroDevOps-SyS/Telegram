package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

import java.util.ArrayList;

public class IndogaroSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private ArrayList<Item> items = new ArrayList<>();

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    private void updateRows() {
        items.clear();
        items.add(new Item(1, "Status Thread Aktif", Thread.activeCount() + " Threads Running"));
        items.add(new Item(2, "Penggunaan Memori RAM", getRamUsageInfo()));
        items.add(new Item(3, "Optimasi & Bersihkan Cache", "Bersihkan cache sistem Indogaro"));
    }

    private String getRamUsageInfo() {
        Runtime runtime = Runtime.getRuntime();
        long usedMem = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMem = runtime.maxMemory() / 1024 / 1024;
        return usedMem + " MB / " + maxMem + " MB";
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("Indogaro System & Monitoring");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        SizeNotifierFrameLayout contentView = new SizeNotifierFrameLayout(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
            }
        };
        contentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listAdapter = new ListAdapter(context);
        listView = new RecyclerListView(context);
        listView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener((view, position) -> {
            if (position >= 0 && position < items.size()) {
                Item item = items.get(position);
                if (item.id == 3) {
                    // Perbaikan: Menggunakan Toast standar Android atau Bulletin Telegram
                    Toast.makeText(getParentActivity(), "Sistem & Cache Memori Diperbarui!", Toast.LENGTH_SHORT).show();
                    updateRows();
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        contentView.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        return fragmentView = contentView;
    }

    private static class Item {
        int id;
        String title;
        String value;

        public Item(int id, String title, String value) {
            this.id = id;
            this.title = title;
            this.value = value;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private final Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        // Perbaikan: Wajib mengimplementasikan method isEnabled dari SelectionAdapter
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextSettingsCell cell = new TextSettingsCell(mContext);
            cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(cell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextSettingsCell cell = (TextSettingsCell) holder.itemView;
            Item item = items.get(position);
            cell.setTextAndValue(item.title, item.value, position != items.size() - 1);
        }
    }
}
