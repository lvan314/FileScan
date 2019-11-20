# FileScan 一款安卓端功能强大的文件导入插件
一款安卓端简易文件导入插件，该插件具有如下特点：<br>
  1.集合了单文件导入，多文件导入模式:用户可以自定义要导入的文件数量类型<br>
  2.具有时间，大小，名称，类型和默认五种排序类型。<br>
  3.用户可以自定义传入筛选类型：如传入数组["txt","jpg","dat"],则筛选时可以从传入的几种类型之中进行选择。<br>
  4.集合了三种操作模式：单击确定，先获取焦点再点击才确认和多选模式，丰富用户的操作体验。<br>
  5.集合了全盘搜索和局部搜索两种搜索方式。<br>
  6.显示磁盘位置区分为手机内存和网络接收文件两种方式。
  
  # 使用方式
  ## 模块依赖
```
  repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
        
    }
```
 ```
 implementation 'com.github.Lvan314:FileScan:0.0.3'  
 ```
 ## 调用方式
 参数定义
 ```
  private final static int FILE_SCAN_REQUEST_CODE = 111; //请求扫描文件请求码
    private final static int FILE_SCAN_SUCCESS = 512;//获取选择文件成功
    private final static int FILE_SCAN_FAILURE = 314;//获取文件路径失败
    private final static int FILES_SCAN_SUCCESS= 222;//获取文件列表成功
 ```
 重写OnActivityResult方法，接收返回参数
 ```
  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intentOut) {
        /**
         * 1.启动activities时有请求码 这个可用于目标Activity确认启动的actitvty对象
         * 2.返回码，目标Activity返回时设置的result_code,可先确认requestcode，再确认resultCode
         */
        switch (requestCode) {
            case FILE_SCAN_REQUEST_CODE: //对应为模块请求码
                /**
                 * 对选择文件activity返回结果的判定
                 */
                switch (resultCode) {
                    case FILE_SCAN_SUCCESS:
                        /**
                         * 浏览文件返回
                         */
                        Intent intent = intentOut;
                        Bundle bundle = intent.getExtras();
                        textViewShowResult.setText(bundle.getString("path")); //获取返回结果
                        break;
                    case FILE_SCAN_FAILURE:
                        Toast.makeText(MainActivity.this, "没有选择文件", Toast.LENGTH_SHORT).show();
                        textViewShowResult.setText("没有选择文件");
                        break;
                    case FILES_SCAN_SUCCESS:
                        Intent intent1 = intentOut;
                        Bundle bundle1 = intent1.getExtras();
                        ArrayList<String> list= bundle1.getStringArrayList("fileList");
                        //Toast.makeText(MainActivity.this,list.toString(),Toast.LENGTH_LONG).show();
                        textViewShowResult.setText(list.toString());//返回结果为list
                        break;
                    case 444://模块设置返回码 也就是需要编辑格式时返回444 收到该返回码，用户可自行编辑返回码再进行操作
                        Toast.makeText(MainActivity.this,"格式编辑请求",Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intentOut);
    }
    ```
    调用Activity
    ```
     Intent intent = new Intent(MainActivity.this, com.zhch1999.filescan.MainActivity.class);
                String[] arrFileType ={"CAD图", "dwg", "dxf","txt","dat"};
                intent.putExtra("fileTypes",arrFileType);//用户自定义的筛选格式 可以为空 
                intent.putExtra("operateType","single");//操作类型 single为单击确定， double为双击确认 more为多选 前两者均只能选择一种模式
                startActivityForResult(intent, FILE_SCAN_REQUEST_CODE); //传入请求码 
    ```
