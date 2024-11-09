package org.elasticsearch.plugin.analysis.hanlp.cfg;

import java.nio.file.Path;

public abstract class Configuration {
    // 是否开启远程词典
    protected boolean enableRemoteDict = false;
    // 是否开启自定义配置
    protected boolean enableCustomConfig = false;
    // 是否是索引分词
    protected boolean enableIndexMode = false;
    // 是否识别数字和量词
    protected boolean enableNumberQuantifierRecognize = false;
    // 是否加载用户词典
    protected boolean enableCustomDictionary = false;
    // 是否识别音译人名
    protected boolean enableTranslatedNameRecognize = false;
    // 是否识别日本人名
    protected boolean enableJapaneseNameRecognize = false;
    // 是否识别机构
    protected boolean enableOrganizationRecognize = false;
    // 是否识别地名
    protected boolean enablePlaceRecognize = false;
    // 是否识别中国人名
    protected boolean enableNameRecognize = false;
    // 是否开启繁体中文
    protected boolean enableTraditionalChineseMode = false;
    // 是否启用停用词
    protected boolean enableStopDictionary = false;
    // 是否开启词性标注
    protected boolean enablePartOfSpeechTagging = false;
    // 是否执行字符正规化
    protected boolean enableNormalization = false;
    // 是否计算偏移量
    protected boolean enableOffset = false;

    protected boolean enablePorterStemming = false;

    public Configuration() {
    }

    public abstract Path getConfDir();

    public abstract Path getConfigInPluginDir();

    public boolean isEnableRemoteDict() {
        return enableRemoteDict;
    }

    public abstract Path getPath(String first, String... more);

    public void check() {
    }


    public boolean isEnableCustomConfig() {
        return enableCustomConfig;
    }

    public Configuration enableCustomConfig(boolean enableCustomConfig) {
        this.enableCustomConfig = enableCustomConfig;
        return this;
    }

    public boolean isEnableIndexMode() {
        return enableIndexMode;
    }

    public Configuration enableIndexMode(boolean enableIndexMode) {
        this.enableIndexMode = enableIndexMode;
        return this;
    }

    public boolean isEnableNumberQuantifierRecognize() {
        return enableNumberQuantifierRecognize;
    }

    public Configuration enableNumberQuantifierRecognize(boolean enableNumberQuantifierRecognize) {
        this.enableNumberQuantifierRecognize = enableNumberQuantifierRecognize;
        return this;
    }

    public boolean isEnableCustomDictionary() {
        return enableCustomDictionary;
    }

    public Configuration enableCustomDictionary(boolean enableCustomDictionary) {
        this.enableCustomDictionary = enableCustomDictionary;
        return this;
    }

    public boolean isEnableTranslatedNameRecognize() {
        return enableTranslatedNameRecognize;
    }

    public Configuration enableTranslatedNameRecognize(boolean enableTranslatedNameRecognize) {
        this.enableTranslatedNameRecognize = enableTranslatedNameRecognize;
        return this;
    }

    public boolean isEnableJapaneseNameRecognize() {
        return enableJapaneseNameRecognize;
    }

    public Configuration enableJapaneseNameRecognize(boolean enableJapaneseNameRecognize) {
        this.enableJapaneseNameRecognize = enableJapaneseNameRecognize;
        return this;
    }

    public boolean isEnableOrganizationRecognize() {
        return enableOrganizationRecognize;
    }

    public Configuration enableOrganizationRecognize(boolean enableOrganizationRecognize) {
        this.enableOrganizationRecognize = enableOrganizationRecognize;
        return this;
    }

    public boolean isEnablePlaceRecognize() {
        return enablePlaceRecognize;
    }

    public Configuration enablePlaceRecognize(boolean enablePlaceRecognize) {
        this.enablePlaceRecognize = enablePlaceRecognize;
        return this;
    }

    public boolean isEnableNameRecognize() {
        return enableNameRecognize;
    }

    public Configuration enableNameRecognize(boolean enableNameRecognize) {
        this.enableNameRecognize = enableNameRecognize;
        return this;
    }

    public boolean isEnableTraditionalChineseMode() {
        return enableTraditionalChineseMode;
    }

    public Configuration enableTraditionalChineseMode(boolean enableTraditionalChineseMode) {
        this.enableTraditionalChineseMode = enableTraditionalChineseMode;
        return this;
    }

    public boolean isEnableStopDictionary() {
        return enableStopDictionary;
    }

    public Configuration enableStopDictionary(boolean enableStopDictionary) {
        this.enableStopDictionary = enableStopDictionary;
        return this;
    }

    public boolean isEnablePartOfSpeechTagging() {
        return enablePartOfSpeechTagging;
    }

    public Configuration enablePartOfSpeechTagging(boolean enablePartOfSpeechTagging) {
        this.enablePartOfSpeechTagging = enablePartOfSpeechTagging;
        return this;
    }

    public boolean isEnableNormalization() {
        return enableNormalization;
    }

    public Configuration enableNormalization(boolean enableNormalization) {
        this.enableNormalization = enableNormalization;
        return this;
    }

    public boolean isEnableOffset() {
        return enableOffset;
    }

    public Configuration enableOffset(boolean enableOffset) {
        this.enableOffset = enableOffset;
        return this;
    }

    public boolean isEnablePorterStemming() {
        return enablePorterStemming;
    }

    public Configuration enablePorterStemming(boolean enablePorterStemming) {
        this.enablePorterStemming = enablePorterStemming;
        return this;
    }
}