1.自由放大和缩小
2.双击放大与缩小
3.放大以后可以进行自由的移动
4.处理与ViewPager之间的事件冲突

-----------------------
知识点
1.Matrix
2.ScaleGestureDetector
3.GestureDetector
4.事件分发机制

复写ImageView

Matrix

xScale  xSkew xTrans
ySkew yScale  yTrans
0     0       0

--------------------------------
ZoomImageView extends ImageView
OnGlobalLayoutListener

onAttachedToWindow
onDetachedFromWindow

ScaleGestureDetector
    onScale detector

onTouchListener
    onTouch

------------------------
自由移动
----------------
双击放大  双击缩小
GestureDetector
postDelay + Runnable
------------
和ViewPager结合
放大以后和ViewPager的左右滑动发生冲突
判断冲突发生的原因，ViewPager屏蔽了子View的左右移动事件
处理：在down  move如果宽或者高大于屏幕宽度或者高度时候，请求不被屏蔽
    getParent().requestDisallowInterceptTouchEvent(true);