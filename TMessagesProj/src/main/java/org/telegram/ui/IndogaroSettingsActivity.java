package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class IndogaroSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;

    private int rowCount;
    private int monitoringHeaderRow;
    private int threadStatusRow;
    private int networkStatusRow;
    private int memoryStatusRow;
    private int performanceHeaderRow;
    private int refreshActionRow;
    private int devInfoRow;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    private void updateRows() {
        rowCount = 0;
        monitoringHeaderRow = rowCount++;
        threadStatusRow = rowCount++;
        networkStatusRow = rowCount++;
        memoryStatusRow = rowCount++;
        
        performanceHeaderRow = rowCount++;
        refreshActionRow = rowCount++;
        devInfoRow = rowCount++;
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

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(listAdapter = new ListAdapter(context));
        
        listView.setOnItemClickListener((view, position) -> {
            if (position == refreshActionRow) {
                // Aksi interaktif: Paksa Garbage Collector dan refresh list data secara instan
                System.gc();
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
                AndroidUtilities.showToast("Sistem & Cache Memori Diperbarui!");
            }
        });

        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return fragmentView;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder, int position) {
            // Hanya baris tombol refresh yang interaktif (bisa diklik)
            return position == refreshActionRow;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new HeaderCell(mContext);
            } else if (viewType == 1) {
                view = new TextDetailCell(mContext);
            } else {
                view = new TextCell(mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType == 0) {
                HeaderCell headerCell = (HeaderCell) holder.itemView;
                if (position == monitoringHeaderRow) {
                    headerCell.setText("Status Real-Time Engine");
                } else if (position == performanceHeaderRow) {
                    headerCell.setText("Aksi & Informasi Developer");
                }
            } else if (viewType == 1) {
                TextDetailCell detailCell = (TextDetailCell) holder.itemView;
                if (position == threadStatusRow) {
                    int activeThreads = Thread.activeCount();
                    detailCell.setTextAndValue("Thread Aktif (Worker/Pool)", "Jumlah thread sistem aktif saat ini: " + activeThreads, true);
                } else if (position == networkStatusRow) {
                    SharedPreferences prefs = MessagesController.getGlobalMainSettings();
                    boolean netOpt = prefs.getBoolean("indogaro_net_opt", true);
                    detailCell.setTextAndValue("Optimasi Jaringan (Net Opt)", netOpt ? "AKTIF (Bypass Throttling & Stream Boost)" : "MATI (Standard Telegram)", true);
                } else if (position == memoryStatusRow) {
                    Runtime runtime = Runtime.getRuntime();
                    long freeMem = runtime.freeMemory() / (1024 * 1024);
                    long totalMem = runtime.totalMemory() / (1024 * 1024);
                    long maxMem = runtime.maxMemory() / (1024 * 1024);
                    detailCell.setTextAndValue("RAM Heap (Free / Total / Max)", freeMem + " MB / " + totalMem + " MB / " + maxMem + " MB", true);
                }
            } else if (viewType == 2) {
                TextCell textCell = (TextCell) holder.itemView;
                if (position == refreshActionRow) {
                    textCell.setTextAndValue("Bersihkan Cache & Refresh Status", "Tekan untuk memicu Garbage Collector", false);
                } else if (position == devInfoRow) {
                    textCell.setTextAndValue("Arsitektur & Engine", "Indogaro Core v2026 (Senior System Architect)", false);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == monitoringHeaderRow || position == performanceHeaderRow) {
                return 0; // HeaderCell
            } else if (position == threadStatusRow || position == networkStatusRow || position == memoryStatusRow) {
                return 1; // TextDetailCell (informatif 2 baris)
            }
            return 2; // TextCell standar (untuk tombol aksi/info teks biasa)
        }
    }
}
