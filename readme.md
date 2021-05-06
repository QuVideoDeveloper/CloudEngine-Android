# 小影-云端合成-Android端接口文档
## 写在最前
运行Demo工程，需要把申请到的**AppKey**、**AppSecret**添入`App.java`如下位置。
```
public class App extends MultiDexApplication {
    public final static String APP_KEY = "此处填写分发的appKey";
    public final static String APP_SECRET = "此处填写分发的appSecret";
}
```

## 功能概览
小影云端合成包含支持图片、视频云端模版合成。
## 前期准备
1. 向小影对接人申请 appKey 和 appSecrect（后续API接口签名认证会使用到）；
2. 申请最新版本的小影云端合成sdk。
## 自动集成
### 引入依赖
1. 在工程目录的build.gradle文件添加Maven仓库
```xml
maven {
    url 'https://serverless-1533657941-maven.pkg.coding.net/repository/app-sdk-pub/bintray/'
}
```
2. 在module目录的build.gradle文件添加依赖，**其中第三方库是compileOnly的，因此依赖需要外部添加**。
```
//云端合成aar
// 1.2.5版本以下只支持armeabi-v7a和armeabi-v8a, 1.2.6之后新增对armeabi的支持，需要开发者注意自己的abi配置
implementation "com.quvideo.mobile.external:cloud_engine:1.2.9"

//sdk内部依赖的第三方库，是compileOnly依赖的，项目一定要依赖，目前版本按照智云给的版本
implementation "com.squareup.okhttp3:okhttp:4.2.1"
implementation "com.squareup.okhttp3:logging-interceptor:4.2.1"
implementation "com.google.code.gson:gson:2.8.6"
implementation "com.squareup.retrofit2:retrofit:2.6.1"
implementation "com.squareup.retrofit2:converter-gson:2.6.1"
implementation "com.squareup.retrofit2:adapter-rxjava2: 2.6.1"
implementation "io.reactivex.rxjava2:rxandroid:2.1.1"
implementation "io.reactivex.rxjava2:rxjava:2.2.17"
implementation "com.aliyun.dpa:oss-android-sdk:2.9.2"
```
3. 点击Sync，同步配置。

### 参数配置
1. 在AndroidManifest.xml中添加权限：
```
//网络请求权限
<uses-permission android:name="android.permission.INTERNET" />
```
## 接口说明
### 初始化
调用以初始化云端合成SDK，**调用其他接口前需要先调用初始化**。
```
CloudEngineConfig config = new CloudEngineConfig(/*省略填入参数*/);
QVCloudEngine.initialize(application, config);
```
#### 请求参数
**CloudEngineConfig : 初始化配置**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| application | application上下文 | Context | 必须 |
| config | 初始化配置 | CloudEngineConfig | 必须 |

**CloudEngineConfig：初始化参数**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| appKey | 注册时申请的AppKey | String | 必须 |
| config | 初始化配置 | CloudEngineConfig | 必须 |
| appSecrect | 注册时申请的AppSecrect | String | 必须 |
| userId | 第三方用户唯一标示，<br>如果没有可以传入AndroidID，广告Id等。| String | 必须 | 
| countryCode | 国家码，如"CN",<br>如果使用默认，则不需要传递。| String | 非必须 |
| languageCode | 语言码，如"zh",<br>如果使用默认，则不需要传递。| String | 非必须 |
| isDebug | 是否Debug模式，Debug会打印内部日志，默认false | bool| 非必须 |

### 获取素材模版列表
调用以获取合成素材列表。
```
TemplateConfig templateConfig = new TemplateConfig(/*省略填入参数*/);

QVCloudEngine.getTemplates(templateConfig, new onTemplateListener() {
    @Override
    public void onSuccess(TemplateResponse response) {
        //这里获取模版列表成功后回调
    }

    @Override
    public void onFailure(RequestError error) {
        //这里获取模版列表失败后回调
    }
})
```
#### 请求参数
**TemplateConfig：获取素材请求参数**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| pageNum | 页码，默认从1开始 | number | 非必须 |
| pageSize | 每页记录条数，默认10条，最大50。<br>**注：超过50按照50生效。**| number | 非必须 | 
#### 响应参数
**TemplateResponse：素材详情列表参数**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| data | 素材详情列表 | List<TemplateResponse.Data> | 非必须 |
| hasMore | 分页参数，是否有下一页 | bool | 非必须 | 

**TemplateResponse.Data：单个素材详情参数**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| templateId | 素材Id | number | 必须 | 
| orderNo | 排序序号 | number | 必须 | 
| icon | 缩略图URL，格式为gif/jpeg/png | string | 必须 | 
| previewurl | 预览URL | string | 必须 | 
| previewtype | 预览类型，1文字 2 图片 3 视频 4音频 5网页 6flash | number | 必须 | 
| duration | 时长（应用于音频和视频), 秒 | string | 必须 | 
| width | 宽度 | number | 必须 |
| height | 高度 | number | 必须 |
| maxMediaCount | 限制选择媒体文件个数 | number | 必须 |
| title | 名称 | string | 必须 |
| intro |简介 | string | 必须 | 
| dataType |素材支持数据类型 | DataType | 必须 |

**RequestError：获取素材错误参数**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| code | 错误码 | number | 必须 | 
| message | 错误描述 | String | 必须 |
### 发起云端合成
传入合成源文件、STEP2获取到的素材信息以及其他可配置参数，调用以合成目标视频。
```
CompositeConfig config = new CompositeConfig(/*省略填入参数*/);

QVCloudEngine.composite(config, new onCompositeListener() {
    @Override
    public void onPreComposite(ICompositeTask task, CompositePreResponse response) {
        //这里回调云端合成预处理
    }

    @Override
    public void onUploadProgress(ICompositeTask task, int progress) {
        //这里回调文件上传进度
    }

    @Override
    public void onSuccess(ICompositeTask task, CompositeFinishResponse response) {
        //这里回调云端合成成功
    }
    
    @Override
    public void onFailure(ICompositeTask task, RequestError error, State state, boolean canForceMake) {
        //这里回调合成失败，canForceMake是表示是否支持强制合成，(true)可以继续强制合成
    }
    
    @Override
    public void onNext(ICompositeTask task, State state) {
        //这里回调状态机变化 
    }
});
```
#### 请求参数
**CompositeConfig：合成视频请求参数**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| forceMake | 是否强制生成，true：强制，false：不强制，默认false。<br>例：素材要求选择人脸照片，选择的人脸照片像素低，<br>可以强制合成，但是建议更换更清晰的照片。 | boolean | 非必须 | 
| templateId | 素材Id（在素材接口里面获取）| number | 必须 | 
| resolution | 视频分辨率：480p/ 720p/1080p | Resolution | 必须 | 
| localMedias | 本地图片组/视频 | List< CompositeConfig.Media > | 必须|

**CompositeConfig.Media：单个源文件参数**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| mediaType | 源文件类型，图片/视频 | MediaType | 必须 |
| uri | 源文件本地uri | Uri | 必须 | 
#### 响应参数
**CompositePreResponse：云端合成预处理**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| businessId | 云端合成业务Id | String | 必须 |

**CompositeFinishResponse：云端合成完成**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| fileId | 文件唯一标识 | String | 必须 |
| businessId | 云端合成业务Id | String | 必须 | 
| fileUrl | 文件url，视频格式为mp4 | String | 必须 |
| coverImageUrl | 封面url，格式为jpeg/png | String | 必须 | 
| duration | 文件时长（ms） | String | 必须 |

**ICompositeTask：云端合成任务**
| 接口 | 解释 |
| :-: | :-: |
| getState | 获取当前任务状态 | 
| forceMake | 强制制作，只有当制作失败并且可以强制制作才有效
| release | 释放资源，之后不在接收回调 | 

**State：云端合成中间状态**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| state | IDEL, //初始状态 <br>UPLOAD, //进入文件上传阶段 <br>COMPOSITE, //进入合成阶段 <br>QUERY, //进入查询合成情况阶段 <br>TIMEOUT, //合成超时 <br>SUCCESS, //合成成功 <br>FAILURE, //合成失败 <br>CANCEL, //上传取消 <br>FAILURE_FORCEMAKE,//合成失败但是可以强制制作  <br>STOP//停止 | State | 必须 |
### 取消合成文件上传
只有处于文件上传阶段的任务，才可以取消。
方式一：
```
// 方式一：取消所有上传中的任务
QVCloudEngine.cancelUpload();
```
方式二：
```
// 方式二：取消某个上传中的任务
ICompositeTask task; // 具体回调的合成任务
int iRes = task.cancelUpload(); // 0表示取消成，20015表示不是上传阶段，无法取消
```
成功取消的上传任务，将在合成回调的onCompositeListener，以State.CANCEL的参数，在onNext(ICompositeTask task, State state)的状态机变化回调。

### 查询视频列表
查询历史合成视频列表
```
VideoConfig config = new VideoConfig(/*省略填入参数*/);

QVCloudEngine.getVideos(config, new onVideosListener() {
    @Override
    public void onSuccess(VideoResponse response) {
        //这里获取视频列表成功后回调
    }

    @Override
    public void onFailure(RequestError error) {
        //这里获取视频列表成功后回调
    }
})
```
#### 请求参数
**VideoConfig：视频请求参数**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| pageNum | 页码，默认从1开始 | number | 非必须 |
| pageSize | 每页记录条数，默认10条，最大50。<br>**注：超过50按照50生效。** | number | 非必须 | 

#### 响应参数
**VideoResponse：视频列表参数**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| data | 视频列表 | List<VideoResponse.Data> | 非必须 |
| hasMore | 分页参数，是否有下一页 | bool | 非必须 | 

**VideoResponse. Data：单个视频参数**
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| fileId | 文件唯一标识 | String | 必须 |
| businessId | 业务id | String | 必须 | 
| fileUrl | 文件url | String | 必须 |
| coverImageUrl | 封面url | String | 必须|
| duration | 文件时长 | String | 必须 |

### 上报接口
当成功获取到视频后，调用上报接口，以统计有效合成。
```
QVCloudEngine.report(fileIds)
```
#### 请求参数
| 名称  | 解释 | 类型 | 是否必须 |
| :-: | :-: | :-: | :-: |
| fileIds | 文件唯一标识（云端合成成功获取）| List<String> | 必须 |
  
## Q & A
1. 遇到UnsatisfiedLinkError怎么办？
因为SDK内部有so文件，建议Clean后重试。




