package id.indogaro.ui.Gifts;

import static id.indogaro.messenger.AndroidUtilities.dp;
import static id.indogaro.messenger.LocaleController.formatNumber;
import static id.indogaro.messenger.LocaleController.formatPluralString;
import static id.indogaro.messenger.LocaleController.formatString;
import static id.indogaro.messenger.LocaleController.getString;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import id.indogaro.messenger.AndroidUtilities;
import id.indogaro.messenger.DialogObject;
import id.indogaro.messenger.GiftAuctionController;
import id.indogaro.messenger.R;
import id.indogaro.messenger.UserConfig;
import id.indogaro.messenger.UserObject;
import id.indogaro.tgnet.tl.TL_stars;
import id.indogaro.ui.ActionBar.BaseFragment;
import id.indogaro.ui.ActionBar.Theme;
import id.indogaro.ui.Components.AnimatedEmojiSpan;
import id.indogaro.ui.Components.BottomSheetWithRecyclerListView;
import id.indogaro.ui.Components.LayoutHelper;
import id.indogaro.ui.Components.RecyclerListView;
import id.indogaro.ui.Components.TableView;
import id.indogaro.ui.Components.TypefaceSpan;
import id.indogaro.ui.Components.UItem;
import id.indogaro.ui.Components.UniversalAdapter;
import id.indogaro.ui.Components.UniversalRecyclerView;
import id.indogaro.ui.LaunchActivity;
import id.indogaro.ui.ProfileActivity;
import id.indogaro.ui.Stars.StarsIntroActivity;
import id.indogaro.ui.Stories.recorder.ButtonWithCounterView;

import java.util.ArrayList;
import java.util.List;

public class AcquiredGiftsSheet extends BottomSheetWithRecyclerListView {
    private final List<TL_stars.TL_StarGiftAuctionAcquiredGift> gifts;
    private final GiftAuctionController.Auction auction;

    public AcquiredGiftsSheet(Context context, Theme.ResourcesProvider resourcesProvider, GiftAuctionController.Auction auction, List<TL_stars.TL_StarGiftAuctionAcquiredGift> gifts) {
        super(context, null, false, false, false, false, ActionBarType.SLIDING, resourcesProvider);
        this.auction = auction;
        this.gifts = gifts;
        topPadding = 0.2f;

        ignoreTouchActionBar = false;
        headerMoveTop = dp(12);

        actionBar.setTitle(getTitle());
        fixNavigationBar();

        recyclerListView.setPadding(backgroundPaddingLeft, dp(9), backgroundPaddingLeft, dp(16 + 48));
        recyclerListView.setOnItemClickListener((view, position) -> {});
        recyclerListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ButtonWithCounterView buttonView = new ButtonWithCounterView(context, resourcesProvider);
        buttonView.setOnClickListener(v -> dismiss());
        buttonView.setText(getString(R.string.OK), false);
        FrameLayout.LayoutParams lp = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM, 16, 16, 16, 16);
        lp.leftMargin += backgroundPaddingLeft;
        lp.rightMargin += backgroundPaddingLeft;
        containerView.addView(buttonView, lp);

        adapter.update(false);
    }

    @Override
    protected CharSequence getTitle() {
        if (gifts == null) {
            return null;
        }
        return formatPluralString("Gift2AuctionsAcquiredGifts", gifts.size());
    }

    private UniversalAdapter adapter;

    @Override
    protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView listView) {
        adapter = new UniversalAdapter(recyclerListView, getContext(), currentAccount, 0, true, this::fillItems, resourcesProvider);
        adapter.setApplyBackground(false);
        return adapter;
    }

    private void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        if (gifts == null) {
            return;
        }
        for (TL_stars.TL_StarGiftAuctionAcquiredGift gift : gifts) {
            items.add(AcquiredGiftsCell.Factory.as(gift, auction, (v) -> {
                openProfile(DialogObject.getPeerDialogId(gift.peer));
            }));
        }
        items.add(UItem.asSpace(dp(16)));
    }

    private void openProfile(long did) {
        dismiss();
        final BaseFragment lastFragment = LaunchActivity.getSafeLastFragment();
        if (lastFragment != null) {
            if (UserObject.isService(did)) return;
            Bundle args = new Bundle();
            if (did > 0) {
                args.putLong("user_id", did);
                if (did == UserConfig.getInstance(currentAccount).getClientUserId()) {
                    args.putBoolean("my_profile", true);
                }
            } else {
                args.putLong("chat_id", -did);
            }
            args.putBoolean("open_gifts", true);
            lastFragment.presentFragment(new ProfileActivity(args));
        }
    }

    private static class AcquiredGiftsCell extends FrameLayout {
        private final Theme.ResourcesProvider resourcesProvider;
        private final int currentAccount;

        public AcquiredGiftsCell(@NonNull Context context, Theme.ResourcesProvider resourcesProvider, int currentAccount) {
            super(context);
            this.currentAccount = currentAccount;
            this.resourcesProvider = resourcesProvider;

            setPadding(dp(18), dp(9), dp(18), dp(9));
        }

        private void bind(GiftAuctionController.Auction auction, TL_stars.TL_StarGiftAuctionAcquiredGift gift, View.OnClickListener listener) {
            removeAllViews();

            SpannableStringBuilder ssb = new SpannableStringBuilder("*");
            ssb.setSpan(new AnimatedEmojiSpan(
                auction.giftDocumentId, Theme.chat_actionTextPaint.getFontMetricsInt()),
                0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            ssb.append(' ');

            final int giftNum = gift.gift_num;
            ssb.append(formatString(R.string.Gift2AuctionsAcquiredRound2, auction.gift.title, giftNum, gift.round));
            ssb.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableStringBuilder ssb1 = new SpannableStringBuilder();
            final CharSequence bid = "⭐️" + formatNumber(gift.bid_amount, ',');
            ssb1.append(StarsIntroActivity.replaceStarsWithPlain(bid, 0.75f));

            String top = formatString(R.string.Gift2AuctionsAcquiredTop, gift.pos);

            TableView tableView = new TableView(getContext(), resourcesProvider);
            tableView.addFullRow(ssb).setFilled(true);
            tableView.addRowUser(getString(R.string.Gift2AuctionsAcquiredRecipient), currentAccount, DialogObject.getPeerDialogId(gift.peer), () -> listener.onClick(this));
            tableView.addRowDateTime(getString(R.string.Gift2AuctionsAcquiredDate), gift.date);
            tableView.addRow(getString(R.string.Gift2AuctionsAcquiredAcceptedBid), ssb1, top, null);

            addView(tableView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        private static class Factory extends UItem.UItemFactory<AcquiredGiftsCell> {
            static { setup(new Factory()); }

            @Override
            public AcquiredGiftsCell createView(Context context, RecyclerListView listView, int currentAccount, int classGuid, Theme.ResourcesProvider resourcesProvider) {
                AcquiredGiftsCell cell = new AcquiredGiftsCell(context, resourcesProvider, currentAccount);
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return cell;
            }

            @Override
            public boolean isClickable() {
                return false;
            }

            @Override
            public void bindView(View view, UItem item, boolean divider, UniversalAdapter adapter, UniversalRecyclerView listView) {
                ((AcquiredGiftsCell) view).bind(
                    (GiftAuctionController.Auction) item.object2,
                    (TL_stars.TL_StarGiftAuctionAcquiredGift) item.object, item.clickCallback);
            }

            public static UItem as(TL_stars.TL_StarGiftAuctionAcquiredGift gift, GiftAuctionController.Auction auction, OnClickListener listener) {
                UItem item = UItem.ofFactory(Factory.class);
                item.object = gift;
                item.object2 = auction;
                item.clickCallback = listener;
                return item;
            }
        }
    }
}
