/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SaveToGallerySettingsHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.voip.Instance;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;

import java.io.File;
import java.util.ArrayList;

public class DataSettingsActivity extends BaseFragment {

    private ListAdapter listAdapter;
    private RecyclerListView listView;
    @SuppressWarnings("FieldCanBeLocal")
    private LinearLayoutManager layoutManager;

    private ArrayList<File> storageDirs;

    // Disabled Rows (Auto Media Download)
    private int mediaDownloadSectionRow = -1;
    private int mobileRow = -1;
    private int roamingRow = -1;
    private int wifiRow = -1;
    @Keep
    private int resetDownloadRow = -1;
    private int mediaDownloadSection2Row = -1;

    // Disabled Rows (Calls & Quick Replies)
    private int callsSectionRow = -1;
    @Keep
    private int useLessDataForCallsRow = -1;
    private int quickRepliesRow = -1;
    private int callsSection2Row = -1;

    // New Indogaro Custom Rows
    private int indogaroSectionRow;
    private int memoryOptimizationRow;
    private int networkOptimizationRow;
    private int bufferSizeRow;
    private int streamingOptimizationRow;
    private int downloadOptimizationRow;
    private int unlockLimitsRow;
    private int threadCountRow;
    private int indogaroSection2Row;

    // Existing Storage Rows
    private int storageNumRow;
    private int usageSectionRow;
    private int storageUsageRow;
    private int dataUsageRow;
    private int usageSection2Row;

    // Streaming Rows
    private int streamSectionRow;
    private int enableStreamRow;
    private int enableCacheStreamRow;
    private int enableAllStreamRow;
    private int enableMkvRow;
    private int enableAllStreamInfoRow;

    // Autoplay Rows
    private int autoplayHeaderRow = -1;
    private int autoplayGifsRow = -1;
    private int autoplayVideoRow = -1;
    private int autoplaySectionRow = -1;

    // Proxy & Drafts
    private int proxySectionRow;
    @Keep
    private int proxyRow;
    private int proxySection2Row;
    @Keep
    private int clearDraftsRow;
    private int clearDraftsSectionRow;

    // Save To Gallery
    private int saveToGallerySectionRow;
    @Keep
    private int saveToGalleryPeerRow;
    @Keep
    private int saveToGalleryChannelsRow;
    @Keep
    private int saveToGalleryGroupsRow;
    private int saveToGalleryDividerRow;

    private int rowCount;

    private boolean updateVoipUseLessData;
    private boolean updateStorageUsageAnimated;
    private boolean storageUsageLoading;
    private long storageUsageSize;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        DownloadController.getInstance(currentAccount).loadAutoDownloadConfig(true);
        updateRows(true);
        return true;
    }

    private void updateRows(boolean fullNotify) {
        rowCount = 0;

        // 1. Data & Storage Section
        usageSectionRow = rowCount++;
        storageUsageRow = rowCount++;
        dataUsageRow = rowCount++;
        storageNumRow = -1;
        storageDirs = AndroidUtilities.getRootDirs();
        if (storageDirs.size() > 1) {
            storageNumRow = rowCount++;
        }
        usageSection2Row = rowCount++;

        // 2. Indogaro Custom Optimizations Section
        indogaroSectionRow = rowCount++;
        memoryOptimizationRow = rowCount++;
        networkOptimizationRow = rowCount++;
        bufferSizeRow = rowCount++;
        streamingOptimizationRow = rowCount++;
        downloadOptimizationRow = rowCount++;
        unlockLimitsRow = rowCount++;
        threadCountRow = rowCount++;
        indogaroSection2Row = rowCount++;

        // 3. Save to Gallery Section
        saveToGallerySectionRow = rowCount++;
        saveToGalleryPeerRow = rowCount++;
        saveToGalleryGroupsRow = rowCount++;
        saveToGalleryChannelsRow = rowCount++;
        saveToGalleryDividerRow = rowCount++;

        // 4. Streaming Section
        streamSectionRow = rowCount++;
        enableStreamRow = rowCount++;
        if (BuildVars.DEBUG_VERSION) {
            enableMkvRow = rowCount++;
            enableAllStreamRow = rowCount++;
        } else {
            enableAllStreamRow = -1;
            enableMkvRow = -1;
        }
        enableAllStreamInfoRow = rowCount++;
        enableCacheStreamRow = -1;

        // 5. Proxy & Misc
        proxySectionRow = rowCount++;
        proxyRow = rowCount++;
        proxySection2Row = rowCount++;
        clearDraftsRow = rowCount++;
        clearDraftsSectionRow = rowCount++;

        if (listAdapter != null && fullNotify) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void loadCacheSize() {
        final Runnable fireLoading = () -> {
            storageUsageLoading = true;
            if (listAdapter != null && storageUsageRow >= 0) {
                rebind(storageUsageRow);
            }
        };
        AndroidUtilities.runOnUIThread(fireLoading, 100);

        final long start = System.currentTimeMillis();
        CacheControlActivity.calculateTotalSize(size -> {
            AndroidUtilities.cancelRunOnUIThread(fireLoading);
            updateStorageUsageAnimated = updateStorageUsageAnimated || (System.currentTimeMillis() - start) > 120;
            storageUsageSize = size;
            storageUsageLoading = false;
            if (listAdapter != null && storageUsageRow >= 0) {
                rebind(storageUsageRow);
            }
        });
    }

    private void rebind(int position) {
        if (listView == null || listAdapter == null) {
            return;
        }
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View child = listView.getChildAt(i);
            RecyclerView.ViewHolder holder = listView.getChildViewHolder(child);
            if (holder != null && holder.getAdapterPosition() == position) {
                listAdapter.onBindViewHolder(holder, position);
                return;
            }
        }
    }

    private void rebindAll() {
        if (listView == null || listAdapter == null) {
            return;
        }
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View child = listView.getChildAt(i);
            RecyclerView.ViewHolder holder = listView.getChildViewHolder(child);
            if (holder != null) {
                listAdapter.onBindViewHolder(holder, listView.getChildAdapterPosition(child));
            }
        }
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        CacheControlActivity.canceled = true;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString(R.string.DataSettings));
        actionBar.setAllowOverlayTitle(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });
        if (parentLayout != null && parentLayout.isRightLayout()) {
            actionBar.setBackButtonImage(R.drawable.ic_ab_close);
        }

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context) {
            @Override
            public Integer getSelectorColor(int position) {
                return getThemedColor(Theme.key_listSelector);
            }
        };
        listView.setSections();
        actionBar.setAdaptiveBackground(listView);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener((view, position, x, y) -> {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();

            if (position == memoryOptimizationRow) {
                boolean current = preferences.getBoolean("indogaro_mem_opt", true);
                preferences.edit().putBoolean("indogaro_mem_opt", !current).apply();
                listAdapter.notifyItemChanged(position);
                Toast.makeText(getParentActivity(), "Optimasi Memori " + (!current ? "Diaktifkan" : "Dimatikan"), Toast.LENGTH_SHORT).show();

            } else if (position == networkOptimizationRow) {
                boolean current = preferences.getBoolean("indogaro_net_opt", true);
                preferences.edit().putBoolean("indogaro_net_opt", !current).apply();
                listAdapter.notifyItemChanged(position);
                Toast.makeText(getParentActivity(), "Optimasi Network " + (!current ? "Diaktifkan" : "Dimatikan"), Toast.LENGTH_SHORT).show();

            } else if (position == bufferSizeRow) {
                String[] options = new String[]{"1 MB", "2 MB", "4 MB", "8 MB", "16 MB", "32 MB"};
                int currentSize = preferences.getInt("indogaro_buffer_size", 4);
                int selected = 2;
                for (int i = 0; i < options.length; i++) {
                    if (options[i].startsWith(String.valueOf(currentSize))) {
                        selected = i;
                        break;
                    }
                }
                Dialog dlg = AlertsCreator.createSingleChoiceDialog(getParentActivity(), options, "Pilih Buffer Size", selected, (dialog, which) -> {
                    int size = Integer.parseInt(options[which].split(" ")[0]);
                    preferences.edit().putInt("indogaro_buffer_size", size).apply();
                    listAdapter.notifyItemChanged(position);
                });
                setVisibleDialog(dlg);
                dlg.show();

            } else if (position == streamingOptimizationRow) {
                boolean current = preferences.getBoolean("indogaro_stream_opt", true);
                preferences.edit().putBoolean("indogaro_stream_opt", !current).apply();
                listAdapter.notifyItemChanged(position);
                Toast.makeText(getParentActivity(), "Optimasi Streaming " + (!current ? "Diaktifkan" : "Dimatikan"), Toast.LENGTH_SHORT).show();

            } else if (position == downloadOptimizationRow) {
                boolean current = preferences.getBoolean("indogaro_dl_opt", true);
                preferences.edit().putBoolean("indogaro_dl_opt", !current).apply();
                listAdapter.notifyItemChanged(position);
                Toast.makeText(getParentActivity(), "Optimasi Download " + (!current ? "Diaktifkan" : "Dimatikan"), Toast.LENGTH_SHORT).show();

            } else if (position == unlockLimitsRow) {
                boolean current = preferences.getBoolean("indogaro_unlock_limits", false);
                preferences.edit().putBoolean("indogaro_unlock_limits", !current).apply();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!current);
                }

            } else if (position == threadCountRow) {
                if (getParentActivity() == null) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle("Jumlah Thread (1-100)");

                final EditText input = new EditText(getParentActivity());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                int currentThreads = preferences.getInt("indogaro_threads", 8);
                input.setText(String.valueOf(currentThreads));
                builder.setView(input, AndroidUtilities.dp(24), AndroidUtilities.dp(8), AndroidUtilities.dp(24), AndroidUtilities.dp(8));

                builder.setPositiveButton(LocaleController.getString(R.string.OK), (dialog, which) -> {
                    try {
                        int threads = Integer.parseInt(input.getText().toString().trim());
                        if (threads < 1) threads = 1;
                        if (threads > 100) threads = 100;
                        preferences.edit().putInt("indogaro_threads", threads).apply();
                        listAdapter.notifyItemChanged(position);
                    } catch (Exception ignored) {}
                });
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                showDialog(builder.create());

            } else if (position == saveToGalleryGroupsRow || position == saveToGalleryChannelsRow || position == saveToGalleryPeerRow) {
                int flag;
                if (position == saveToGalleryGroupsRow) {
                    flag = SharedConfig.SAVE_TO_GALLERY_FLAG_GROUP;
                } else if (position == saveToGalleryChannelsRow) {
                    flag = SharedConfig.SAVE_TO_GALLERY_FLAG_CHANNELS;
                } else {
                    flag = SharedConfig.SAVE_TO_GALLERY_FLAG_PEER;
                }
                if (LocaleController.isRTL && x <= AndroidUtilities.dp(76) || !LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76)) {
                    SaveToGallerySettingsHelper.getSettings(flag).toggle();
                    AndroidUtilities.updateVisibleRows(listView);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", flag);
                    presentFragment(new SaveToGallerySettingsActivity(bundle));
                }

            } else if (position == storageUsageRow) {
                presentFragment(new CacheControlActivity());

            } else if (position == dataUsageRow) {
                presentFragment(new DataUsage2Activity());

            } else if (position == storageNumRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString(R.string.StoragePath));
                final LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                builder.setView(linearLayout);

                String dir = storageDirs.get(0).getAbsolutePath();
                if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                    for (int a = 0, N = storageDirs.size(); a < N; a++) {
                        String path = storageDirs.get(a).getAbsolutePath();
                        if (path.startsWith(SharedConfig.storageCacheDir)) {
                            dir = path;
                            break;
                        }
                    }
                }

                boolean fullString = true;
                try {
                    fullString = storageDirs.size() != 2 || storageDirs.get(0).getAbsolutePath().contains("/storage/emulated/") == storageDirs.get(1).getAbsolutePath().contains("/storage/emulated/");
                } catch (Exception ignore) {}

                for (int a = 0, N = storageDirs.size(); a < N; a++) {
                    File file = storageDirs.get(a);
                    String storageDir = file.getAbsolutePath();
                    LanguageCell cell = new LanguageCell(context);
                    cell.setPadding(AndroidUtilities.dp(4), 0, AndroidUtilities.dp(4), 0);
                    cell.setTag(a);
                    String description;
                    boolean isInternal = storageDir.contains("/storage/emulated/");
                    if (fullString && !isInternal) {
                        description = LocaleController.formatString(R.string.StoragePathFreeValueExternal, AndroidUtilities.formatFileSize(file.getFreeSpace()), storageDir);
                    } else {
                        if (isInternal) {
                            description = LocaleController.formatString(R.string.StoragePathFreeInternal, AndroidUtilities.formatFileSize(file.getFreeSpace()));
                        } else {
                            description = LocaleController.formatString(R.string.StoragePathFreeExternal, AndroidUtilities.formatFileSize(file.getFreeSpace()));
                        }
                    }

                    cell.setValue(
                            isInternal ? LocaleController.getString(R.string.InternalStorage) : LocaleController.getString(R.string.SdCard),
                            description
                    );
                    cell.setLanguageSelected(storageDir.startsWith(dir), false);
                    cell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
                    linearLayout.addView(cell);
                    cell.setOnClickListener(v -> {
                        if (!TextUtils.equals(SharedConfig.storageCacheDir, storageDir)) {
                            if (!isInternal) {
                                AlertDialog.Builder confirAlert = new AlertDialog.Builder(getContext());
                                confirAlert.setTitle(LocaleController.getString(R.string.DecreaseSpeed));
                                confirAlert.setMessage(LocaleController.getString(R.string.SdCardAlert));
                                confirAlert.setPositiveButton(LocaleController.getString(R.string.Proceed), (dialog, which) -> {
                                   setStorageDirectory(storageDir);
                                   builder.getDismissRunnable().run();
                                });
                                confirAlert.setNegativeButton(LocaleController.getString(R.string.Back), null);
                                confirAlert.show();
                            } else {
                                setStorageDirectory(storageDir);
                                builder.getDismissRunnable().run();
                            }
                        }
                    });
                }
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                showDialog(builder.create());

            } else if (position == proxyRow) {
                presentFragment(new ProxyListActivity());

            } else if (position == enableStreamRow) {
                SharedConfig.toggleStreamMedia();
                TextCheckCell textCheckCell = (TextCheckCell) view;
                textCheckCell.setChecked(SharedConfig.streamMedia);

            } else if (position == enableAllStreamRow) {
                SharedConfig.toggleStreamAllVideo();
                TextCheckCell textCheckCell = (TextCheckCell) view;
                textCheckCell.setChecked(SharedConfig.streamAllVideo);

            } else if (position == enableMkvRow) {
                SharedConfig.toggleStreamMkv();
                TextCheckCell textCheckCell = (TextCheckCell) view;
                textCheckCell.setChecked(SharedConfig.streamMkv);

            } else if (position == clearDraftsRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString(R.string.AreYouSureClearDraftsTitle));
                builder.setMessage(LocaleController.getString(R.string.AreYouSureClearDrafts));
                builder.setPositiveButton(LocaleController.getString(R.string.Delete), (dialogInterface, i) -> {
                    TLRPC.TL_messages_clearAllDrafts req = new TLRPC.TL_messages_clearAllDrafts();
                    getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> getMediaDataController().clearAllDrafts(true)));
                });
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                AlertDialog alertDialog = builder.create();
                showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                }
            }
        });

        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setDurations(350);
        itemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        itemAnimator.setDelayAnimations(false);
        itemAnimator.setSupportsChangeAnimations(false);
        listView.setItemAnimator(itemAnimator);

        return fragmentView;
    }

    private void setStorageDirectory(String storageDir) {
        SharedConfig.storageCacheDir = storageDir;
        SharedConfig.saveConfig();
        if (storageDir != null) {
            SharedConfig.readOnlyStorageDirAlertShowed = false;
        }
        rebind(storageNumRow);
        ImageLoader.getInstance().checkMediaPaths(() -> {
            CacheControlActivity.resetCalculatedTotalSIze();
            loadCacheSize();
        });
    }

    @Override
    protected void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(currentAccount).checkAutodownloadSettings();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCacheSize();
        rebindAll();
        updateRows(false);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();

            switch (holder.getItemViewType()) {
                case 0:
                    break;
                case 6: {
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == storageUsageRow) {
                        if (storageUsageLoading) {
                            textCell.setTextAndValueAndColorfulIcon(LocaleController.getString(R.string.StorageUsage), "", false, R.drawable.msg_filled_storageusage, 0xFF4F85F6, 0xFF3568E8, true);
                            textCell.setDrawLoading(true, 45, updateStorageUsageAnimated);
                        } else {
                            textCell.setTextAndValueAndColorfulIcon(LocaleController.getString(R.string.StorageUsage), storageUsageSize <= 0 ? "" : AndroidUtilities.formatFileSize(storageUsageSize), true, R.drawable.msg_filled_storageusage, 0xFF4F85F6, 0xFF3568E8, true);
                            textCell.setDrawLoading(false, 45, updateStorageUsageAnimated);
                        }
                        updateStorageUsageAnimated = false;
                    } else if (position == dataUsageRow) {
                        StatsController statsController = StatsController.getInstance(currentAccount);
                        long size = (
                            statsController.getReceivedBytesCount(0, StatsController.TYPE_TOTAL) +
                            statsController.getReceivedBytesCount(1, StatsController.TYPE_TOTAL) +
                            statsController.getReceivedBytesCount(2, StatsController.TYPE_TOTAL) +
                            statsController.getSentBytesCount(0, StatsController.TYPE_TOTAL) +
                            statsController.getSentBytesCount(1, StatsController.TYPE_TOTAL) +
                            statsController.getSentBytesCount(2, StatsController.TYPE_TOTAL)
                        );
                        textCell.setTextAndValueAndColorfulIcon(LocaleController.getString(R.string.NetworkUsage), AndroidUtilities.formatFileSize(size), true, R.drawable.msg_filled_datausage, 0xFF55CA47, 0xFF27B434, storageNumRow != -1);
                    } else if (position == storageNumRow) {
                        String dir = storageDirs.get(0).getAbsolutePath();
                        if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                            for (int a = 0, N = storageDirs.size(); a < N; a++) {
                                String path = storageDirs.get(a).getAbsolutePath();
                                if (path.startsWith(SharedConfig.storageCacheDir)) {
                                    dir = path;
                                    break;
                                }
                            }
                        }
                        final String value = dir == null || dir.contains("/storage/emulated/") ? LocaleController.getString(R.string.InternalStorage) : LocaleController.getString(R.string.SdCard);
                        textCell.setTextAndValueAndColorfulIcon(LocaleController.getString(R.string.StoragePath), value, true, R.drawable.msg_filled_sdcard, 0xFFF09F1B, 0xFFE18A11, false);
                    }
                    break;
                }
                case 1: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    textCell.setCanDisable(false);
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));

                    if (position == memoryOptimizationRow) {
                        textCell.setIcon(0);
                        boolean enabled = preferences.getBoolean("indogaro_mem_opt", true);
                        textCell.setTextAndValue("Optimasi Memori", enabled ? "Aktif" : "Nonaktif", false, true);
                    } else if (position == networkOptimizationRow) {
                        textCell.setIcon(0);
                        boolean enabled = preferences.getBoolean("indogaro_net_opt", true);
                        textCell.setTextAndValue("Optimasi Network", enabled ? "High Speed" : "Standar", false, true);
                    } else if (position == bufferSizeRow) {
                        textCell.setIcon(0);
                        int size = preferences.getInt("indogaro_buffer_size", 4);
                        textCell.setTextAndValue("Buffer Size", size + " MB", false, true);
                    } else if (position == streamingOptimizationRow) {
                        textCell.setIcon(0);
                        boolean enabled = preferences.getBoolean("indogaro_stream_opt", true);
                        textCell.setTextAndValue("Optimasi Streaming", enabled ? "Fast Preload" : "Normal", false, true);
                    } else if (position == downloadOptimizationRow) {
                        textCell.setIcon(0);
                        boolean enabled = preferences.getBoolean("indogaro_dl_opt", true);
                        textCell.setTextAndValue("Optimasi Download", enabled ? "Multi-Connection" : "Normal", false, true);
                    } else if (position == threadCountRow) {
                        textCell.setIcon(0);
                        int threads = preferences.getInt("indogaro_threads", 8);
                        textCell.setTextAndValue("Settingan Thread", threads + " Threads", false, false);
                    } else if (position == proxyRow) {
                        textCell.setIcon(0);
                        textCell.setText(LocaleController.getString(R.string.ProxySettings), false);
                    } else if (position == clearDraftsRow) {
                        textCell.setIcon(0);
                        textCell.setText(LocaleController.getString(R.string.PrivacyDeleteCloudDrafts), false);
                    }
                    break;
                }
                case 2: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == indogaroSectionRow) {
                        headerCell.setText("Optimasi & Fitur Indogaro");
                    } else if (position == usageSectionRow) {
                        headerCell.setText(LocaleController.getString(R.string.DataUsage));
                    } else if (position == proxySectionRow) {
                        headerCell.setText(LocaleController.getString(R.string.Proxy));
                    } else if (position == streamSectionRow) {
                        headerCell.setText(LocaleController.getString(R.string.Streaming));
                    } else if (position == saveToGallerySectionRow) {
                        headerCell.setText(LocaleController.getString(R.string.SaveToGallerySettings));
                    }
                    break;
                }
                case 3: {
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == unlockLimitsRow) {
                        boolean enabled = preferences.getBoolean("indogaro_unlock_limits", false);
                        checkCell.setTextAndCheck("Unlock & Lock Batasan", enabled, true);
                    } else if (position == enableStreamRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.EnableStreaming), SharedConfig.streamMedia, enableAllStreamRow != -1);
                    } else if (position == enableMkvRow) {
                        checkCell.setTextAndCheck("(beta only) Show MKV as Video", SharedConfig.streamMkv, true);
                    } else if (position == enableAllStreamRow) {
                        checkCell.setTextAndCheck("(beta only) Stream All Videos", SharedConfig.streamAllVideo, false);
                    }
                    break;
                }
                case 4: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == enableAllStreamInfoRow) {
                        cell.setText(LocaleController.getString(R.string.EnableAllStreamingInfo));
                    }
                    break;
                }
                case 5: {
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) holder.itemView;
                    String text;
                    CharSequence description = null;
                    boolean enabled, divider = true;

                    if (position == saveToGalleryPeerRow) {
                        text = LocaleController.getString(R.string.SaveToGalleryPrivate);
                        description = SaveToGallerySettingsHelper.user.createDescription(currentAccount);
                        enabled = SaveToGallerySettingsHelper.user.enabled();
                    } else if (position == saveToGalleryGroupsRow) {
                        text = LocaleController.getString(R.string.SaveToGalleryGroups);
                        description = SaveToGallerySettingsHelper.groups.createDescription(currentAccount);
                        enabled = SaveToGallerySettingsHelper.groups.enabled();
                    } else {
                        text = LocaleController.getString(R.string.SaveToGalleryChannels);
                        description = SaveToGallerySettingsHelper.channels.createDescription(currentAccount);
                        enabled = SaveToGallerySettingsHelper.channels.enabled();
                        divider = false;
                    }
                    checkCell.setAnimationsEnabled(true);
                    checkCell.setTextAndValueAndCheck(text, description, enabled, 0, true, divider);
                    break;
                }
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            if (viewType == 3) {
                TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                int position = holder.getAdapterPosition();
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();

                if (position == unlockLimitsRow) {
                    checkCell.setChecked(preferences.getBoolean("indogaro_unlock_limits", false));
                } else if (position == enableStreamRow) {
                    checkCell.setChecked(SharedConfig.streamMedia);
                } else if (position == enableAllStreamRow) {
                    checkCell.setChecked(SharedConfig.streamAllVideo);
                } else if (position == enableMkvRow) {
                    checkCell.setChecked(SharedConfig.streamMkv);
                }
            }
        }

        public boolean isRowEnabled(int position) {
            return position == storageUsageRow || position == dataUsageRow || position == proxyRow || position == clearDraftsRow ||
                    position == memoryOptimizationRow || position == networkOptimizationRow || position == bufferSizeRow ||
                    position == streamingOptimizationRow || position == downloadOptimizationRow || position == unlockLimitsRow ||
                    position == threadCountRow || position == enableStreamRow || position == enableAllStreamRow ||
                    position == enableMkvRow || position == storageNumRow || position == saveToGalleryGroupsRow ||
                    position == saveToGalleryPeerRow || position == saveToGalleryChannelsRow;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return isRowEnabled(holder.getAdapterPosition());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 1:
                    view = new TextSettingsCell(mContext);
                    break;
                case 2:
                    view = new HeaderCell(mContext, 22);
                    break;
                case 3:
                    view = new TextCheckCell(mContext);
                    break;
                case 4:
                    view = new TextInfoPrivacyCell(mContext);
                    break;
                case 5:
                    view = new NotificationsCheckCell(mContext);
                    break;
                case 6:
                default:
                    view = new TextCell(mContext);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == indogaroSection2Row || position == usageSection2Row || position == proxySection2Row || position == clearDraftsSectionRow || position == saveToGalleryDividerRow) {
                return 0;
            } else if (position == indogaroSectionRow || position == streamSectionRow || position == usageSectionRow || position == proxySectionRow || position == saveToGallerySectionRow) {
                return 2;
            } else if (position == unlockLimitsRow || position == enableCacheStreamRow || position == enableStreamRow || position == enableAllStreamRow || position == enableMkvRow) {
                return 3;
            } else if (position == enableAllStreamInfoRow) {
                return 4;
            } else if (position == saveToGalleryGroupsRow || position == saveToGalleryPeerRow || position == saveToGalleryChannelsRow) {
                return 5;
            } else if (position == storageUsageRow || position == dataUsageRow || position == storageNumRow) {
                return 6;
            } else {
                return 1;
            }
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        return themeDescriptions;
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        listView.setPadding(0, 0, 0, bottom);
        listView.setClipToPadding(false);
    }
}
