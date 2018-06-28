# 今日头条（微头条）点赞动画

## 预览

![img](https://github.com/mzyq/JetExpression/blob/master/assets/previewgif.gif)

[APK下载](https://fir.im/x13b)

## 思路

![img](https://github.com/mzyq/JetExpression/blob/029a8b71bf4f9b9c5cf823fedb443a56837c5023/assets/img.png)

1. 小红框是点赞的图标，带有一点动画。大红框是表情喷射的区间，并且是从小红框中间的位置开始喷射。就注定两个view不是一体的。
2. 将喷射区域自定为一个遮罩层，通过```getLocationOnScreen(int[] outLocation)```获取点赞view的位置，并从当前位置开始喷射。
3. 喷射的数量、速度、角度、方向是随机的，也可以有一些旋转和缩放的动画，还可以设定喷射的角度控制方向。

## 代码

本来打算自己写，后来发现有一个不错的动画效果库[Leonids](https://github.com/plattysoft/Leonids)，先看看它的效果:

![img](https://raw.githubusercontent.com/plattysoft/Leonids/master/images/Leonids_one_shot.gif)

如果能实现多张图片就是咱们想要的效果了，然后开始扩展方法。
```java
 public ParticleSystem(ViewGroup parentView, Resources resources, int maxParticles, int[] drawableInts, long timeToLive) {
        this(parentView, maxParticles, timeToLive);
        if (drawableInts != null && drawableInts.length > 0) {
            for (int i = 0; i < mMaxParticles; i++) {
                Drawable drawable = resources.getDrawable(drawableInts[i % drawableInts.length]);
                if (drawable instanceof AnimationDrawable) {
                    AnimationDrawable animation = (AnimationDrawable) drawable;
                    mParticles.add(new AnimatedParticle(animation));
                } else {
                    Bitmap bitmap;
                    if (drawable instanceof BitmapDrawable) {
                        bitmap = ((BitmapDrawable) drawable).getBitmap();
                    } else {
                        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                    }
                    mParticles.add(new Particle(bitmap));
                }
            }
        }
    }
```

Fork扩展后的代码：[Fork Leonids](https://github.com/mzyq/Leonids)

实现动画的代码:
```java
 ParticleSystem ps = new ParticleSystem((Activity) getContext(), 100, iconInts, jetDuration);
 ps.setScaleRange(0.7f, 1.3f);
 ps.setSpeedModuleAndAngleRange(0.1f, 0.5f, 180, 360);
 ps.setAcceleration(0.0001f, 90);
 ps.setRotationSpeedRange(90, 180);
 ps.setFadeOut(200, new AccelerateInterpolator());
 ps.oneShot(this, 10, new DecelerateInterpolator());
```
>通过这个库实现的好处：因为这个库比较好，本身动画就很流畅。相对于自己实现效果流畅太多了。[SuperLike](https://github.com/Qiu800820/SuperLike)这是一个大神自己实现的，可以参考一下，也可以对比一下效果。