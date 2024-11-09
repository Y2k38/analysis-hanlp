package org.elasticsearch.plugin.analysis.hanlp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.SpecialPermission;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.core.PathUtils;
import org.elasticsearch.env.Environment;
import org.elasticsearch.plugin.analysis.hanlp.cfg.Configuration;
import org.elasticsearch.plugin.analysis.hanlp.dic.RemoteDictionary;

import java.io.File;
import java.nio.file.Path;

public class ConfigurationSub extends Configuration {
    private static final Logger logger = LogManager.getLogger(ConfigurationSub.class);

    private Environment environment;

    public ConfigurationSub(Environment env, Settings settings) {
        this.environment = env;

        this.enableCustomConfig = settings.get("enable_custom_config", "false").equals("true");
        this.enableIndexMode = settings.get("enable_index_mode", "false").equals("true");
        this.enableNumberQuantifierRecognize = settings.get("enable_number_quantifier_recognize", "false").equals("true");
        this.enableCustomDictionary = settings.get("enable_custom_dictionary", "true").equals("true");
        this.enableTranslatedNameRecognize = settings.get("enable_translated_name_recognize", "true").equals("true");
        this.enableJapaneseNameRecognize = settings.get("enable_japanese_name_recognize", "false").equals("true");
        this.enableOrganizationRecognize = settings.get("enable_organization_recognize", "false").equals("true");
        this.enablePlaceRecognize = settings.get("enable_place_recognize", "false").equals("true");
        this.enableNameRecognize = settings.get("enable_name_recognize", "true").equals("true");
        this.enableTraditionalChineseMode = settings.get("enable_traditional_chinese_mode", "false").equals("true");
        this.enableStopDictionary = settings.get("enable_stop_dictionary", "false").equals("true");
        this.enablePartOfSpeechTagging = settings.get("enable_part_of_speech_tagging", "false").equals("true");
        this.enableRemoteDict = settings.get("enable_remote_dict", "true").equals("true");
        this.enableNormalization = settings.get("enable_normalization", "false").equals("true");
        this.enableOffset = settings.get("enable_offset", "true").equals("true");

        this.enablePorterStemming = settings.get("enable_porter_stemming", "false").equals("true");

        // load remote dictionary
        if (this.isEnableRemoteDict()) {
            RemoteDictionary.initial(this);
        }
    }

    // {eshome}/config/analysis-hanlp/config
    @Override
    public Path getConfDir() {
        return this.environment.configFile().resolve(AnalysisHanLPPlugin.PLUGIN_NAME);
    }

    // {eshome}/plugin/analysis-hanlp/config
    @Override
    public Path getConfigInPluginDir() {
        return PathUtils
                .get(new File(AnalysisHanLPPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath())
                        .getParent(), "config")
                .toAbsolutePath();
    }

    @Override
    public Path getPath(String first, String... more) {
        return PathUtils.get(first, more);
    }

    public void check() {
        SpecialPermission.check();
    }
}
