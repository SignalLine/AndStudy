package cn.com.single.andstudy.pintu;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li
 *         Create on 2018/6/26.
 * @Description
 *      图片不能过大
 */

public class ImageSplitterUtil {

    /**
     * 传入bitmap
     *   切成piece * piece块
     * @param bitmap  bitmap
     * @param piece  切成piece * piece块
     * @return   List<ImagePiece>
     */
    public static List<ImagePiece> splitImage(Bitmap bitmap,int piece){

        List<ImagePiece> imagePieces = new ArrayList<>();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if(piece < 1){
            piece = 1;
        }

        int pieceWidth = Math.min(width,height) / piece;

        for (int i = 0; i < piece; i++) {
            for (int j = 0; j < piece; j++) {
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.setIndex(j + i * piece);

                int x = j * pieceWidth;
                int y = i * pieceWidth;

                imagePiece.setBitmap(Bitmap.createBitmap(bitmap,x,y
                        ,pieceWidth,pieceWidth));

                imagePieces.add(imagePiece);
            }
        }

        return imagePieces;
    }

}
