package com.bing.lan.sample.config;

public enum MyBuildType {

    BUILD_TYPE_DEBUG(1),
    BUILD_TYPE_DEBUG_PRE(2),
    BUILD_TYPE_RELEASE(3);

    private final Integer mType;

    MyBuildType(Integer type) {
        this.mType = type;
    }

    public static MyBuildType getBuildType(int buildType) {
        switch (buildType) {
            case 1:
                return BUILD_TYPE_DEBUG;
            case 2:
                return BUILD_TYPE_DEBUG_PRE;
            case 3:
                return BUILD_TYPE_RELEASE;
            default:
                return null;
        }
    }

    public static String getBuildTypeInfo(MyBuildType buildType) {
        switch (buildType) {
            case BUILD_TYPE_DEBUG:
                return "内网测试网 201";
            case BUILD_TYPE_DEBUG_PRE:
                return "外网测试网 175";
            case BUILD_TYPE_RELEASE:
                return "外网正式网";
            default:
                return "未知域名";
        }
    }

    public Integer getBuildTypeNum() {
        return mType;
    }

}
