package org.elasticsearch.plugin.analysis.hanlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.utility.Predefine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.core.PathUtils;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

public class AnalysisHanLPPlugin extends Plugin implements AnalysisPlugin {

    private static final Logger logger = LogManager.getLogger(AnalysisHanLPPlugin.class);
    private static final String CONFIG_FILE_NAME = "hanlp.properties";
    public static String PLUGIN_NAME = "analysis-hanlp";

    public AnalysisHanLPPlugin(Settings settings) {
        // 下面两个注册方法需要访问model的数据路径，需要提前配置好hanlp.properties路径
        if (!Environment.PATH_HOME_SETTING.exists(settings)) {
            throw new IllegalStateException(Environment.PATH_HOME_SETTING.getKey() + " is not configured");
        }

        String eshome = Environment.PATH_HOME_SETTING.get(settings);

        // 如果是用elasticsearch-plugin安装，配置文件都会放在config/analysis-hanlp目录
        Path conf_dir = PathUtils.get(eshome, "config", AnalysisHanLPPlugin.PLUGIN_NAME);
        Path configFile = conf_dir.resolve(CONFIG_FILE_NAME);

        FileInputStream stream = null;
        try {
            logger.info("try load config from {}", configFile);
            stream = new FileInputStream(configFile.toFile());
            Predefine.HANLP_PROPERTIES_PATH = configFile.toString();
        } catch (FileNotFoundException e) {
            logger.error("hanlp-analyzer", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.error("hanlp-analyzer close file stream error", e);
                }
            }
        }
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        Map<String, AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();

        extra.put("hanlp", HanLPTokenizerFactory::getHanLPTokenizerFactory);
        extra.put("hanlp_standard", HanLPTokenizerFactory::getHanLPStandardTokenizerFactory);
        extra.put("hanlp_index", HanLPTokenizerFactory::getHanLPIndexTokenizerFactory);
        // 没有model文件就不注册
        if (FileSystemUtils.exists(Paths.get(
                AccessController.doPrivileged((PrivilegedAction<String>) () -> HanLP.Config.PerceptronCWSModelPath)
        ).toAbsolutePath())) {
            extra.put("hanlp_nlp", HanLPTokenizerFactory::getHanLPNLPTokenizerFactory);
        } else {
            logger.warn("can not find perceptron cws model from [{}], you can not use tokenizer [hanlp_nlp]",
                    HanLP.Config.PerceptronCWSModelPath);
        }
        if (FileSystemUtils.exists(Paths.get(
                AccessController.doPrivileged((PrivilegedAction<String>) () -> HanLP.Config.CRFCWSModelPath)
        ).toAbsolutePath())) {
            extra.put("hanlp_crf", HanLPTokenizerFactory::getHanLPCRFTokenizerFactory);
        } else {
            logger.warn("can not find crf cws model from [{}], you can not use tokenizer [hanlp_crf]",
                    HanLP.Config.CRFCWSModelPath);
        }
        extra.put("hanlp_n_short", HanLPTokenizerFactory::getHanLPNShortTokenizerFactory);
        extra.put("hanlp_dijkstra", HanLPTokenizerFactory::getHanLPDijkstraTokenizerFactory);
        extra.put("hanlp_speed", HanLPTokenizerFactory::getHanLPSpeedTokenizerFactory);

        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> extra = new HashMap<>();

        extra.put("hanlp", HanLPAnalyzerProvider::getHanLPAnalyzerProvider);
        extra.put("hanlp_standard", HanLPAnalyzerProvider::getHanLPStandardAnalyzerProvider);
        extra.put("hanlp_index", HanLPAnalyzerProvider::getHanLPIndexAnalyzerProvider);
        // 没有model文件就不注册
        if (FileSystemUtils.exists(Paths.get(
                AccessController.doPrivileged((PrivilegedAction<String>) () -> HanLP.Config.PerceptronCWSModelPath)
        ).toAbsolutePath())) {
            extra.put("hanlp_nlp", HanLPAnalyzerProvider::getHanLPNLPAnalyzerProvider);
        } else {
            logger.warn("can not find perceptron cws model from [{}], you can not use analyzer [hanlp_nlp]",
                    HanLP.Config.PerceptronCWSModelPath);
        }
        if (FileSystemUtils.exists(Paths.get(
                AccessController.doPrivileged((PrivilegedAction<String>) () -> HanLP.Config.CRFCWSModelPath)
        ).toAbsolutePath())) {
            extra.put("hanlp_crf", HanLPAnalyzerProvider::getHanLPCRFAnalyzerProvider);
        } else {
            logger.warn("can not find crf cws model from [{}], you can not use analyzer [hanlp_crf]",
                    HanLP.Config.CRFCWSModelPath);
        }
        extra.put("hanlp_n_short", HanLPAnalyzerProvider::getHanLPNShortAnalyzerProvider);
        extra.put("hanlp_dijkstra", HanLPAnalyzerProvider::getHanLPDijkstraAnalyzerProvider);
        extra.put("hanlp_speed", HanLPAnalyzerProvider::getHanLPSpeedAnalyzerProvider);

        return extra;
    }
}