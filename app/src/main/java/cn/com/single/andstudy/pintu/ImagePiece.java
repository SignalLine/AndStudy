package cn.com.single.andstudy.pintu;

import android.graphics.Bitmap;

/**
 * @author li
 *         Create on 2018/6/26.
 * @Description]
 *      图片切片处理工具
 */

public class ImagePiece {

    private int index;
    private Bitmap bitmap;

    public ImagePiece(int index, Bitmap bitmap) {
        this.index = index;
        this.bitmap = bitmap;
    }

    public ImagePiece() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "ImagePiece{" +
                "index=" + index +
                ", bitmap=" + bitmap +
                '}';
    }
}
