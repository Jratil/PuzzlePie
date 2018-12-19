# PuzzlePie
拼图酱的完整代码

## 工具包和xml文件复用
Helper 包和 Util包有点混乱

xml文件的样式和颜色大多数复用了，所以会有使用的样式的文件名和颜色不对

## 拼图中图片的添加
在 `SecondActivity`（拼图的页面）中选择历史图片的`dialog`的修改

默认图片可以自行增加

增加在相应的文件夹后，修改 `CustomDialog` 的 `init()` 方法中的 `for`循环即可

    （为了简便所以默认是循环两次，可以先获取到所有图片再设置循环，这样以后只要添加默认图片不要修改代码了）

添加的图片的文件名必须为 `pic + 数字 + .png` ， 数字必须依此递增，否则会出错

或者就要自行修改代码中获取文件名


用户添加图片的路径在 `path.properties` 中，要修改用户自行添加的图片路径修改这个就好

### 特别感谢： <br>
* [Gilde](https://muyangmin.github.io/glide-docs-cn/) <br>
* [QMUI](https://qmuiteam.com/android/get-started/) <br>
* [butterknife](https://github.com/JakeWharton/butterknife) <br>
* [sweet-alert-dialog](https://github.com/pedant/sweet-alert-dialog)（由于原项目已经没有维护，使用会出错，所以使用第三方封装） <br>
* [sweet-alert-dialog第三方封装](https://github.com/Cazaea/SweetAlertDialog) <br>
* [tastytoast](https://github.com/yadav-rahul/TastyToast) <br>
* [ColorDialog](https://github.com/andyxialm/ColorDialog) <br>
* [takephoto](https://github.com/crazycodeboy/TakePhoto) <br>
