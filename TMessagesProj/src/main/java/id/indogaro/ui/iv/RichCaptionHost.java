package id.indogaro.ui.iv;

public interface RichCaptionHost {
    RichEditText getCaptionEditText();
    BlockRow getRow();
    void persistCaption();
    boolean isPressOnCaption(int localX, int localY);
}
