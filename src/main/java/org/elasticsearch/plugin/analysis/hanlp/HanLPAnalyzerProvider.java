package org.elasticsearch.plugin.analysis.hanlp;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.plugin.analysis.hanlp.cfg.Configuration;
import org.elasticsearch.plugin.analysis.hanlp.core.HanLPType;
import org.elasticsearch.plugin.analysis.hanlp.lucene.*;

/**
 * Project: elasticsearch-analysis-hanlp
 * Description: Hanlp analyzer provider
 * Author: Kenn
 * Create: 2018-12-14 15:10
 */
public class HanLPAnalyzerProvider extends AbstractIndexAnalyzerProvider<Analyzer> {
    private final Analyzer analyzer;

    public HanLPAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings, HanLPType hanLPType) {
        super(name, settings);

        analyzer = getAnalyzer(hanLPType, new ConfigurationSub(env, settings));
    }

    public static Analyzer getAnalyzer(HanLPType hanLPType, Configuration cfg) {
        return switch (hanLPType) {
            case HANLP -> new HanLPAnalyzer(cfg);
            case STANDARD -> new HanLPStandardAnalyzer(cfg);
            case INDEX -> new HanLPIndexAnalyzer(cfg);
            case NLP -> new HanLPNLPAnalyzer(cfg);
            case CRF -> new HanLPCRFAnalyzer(cfg);
            case N_SHORT -> new HanLPNShortAnalyzer(cfg);
            case DIJKSTRA -> new HanLPDijkstraAnalyzer(cfg);
            case SPEED -> new HanLPSpeedAnalyzer(cfg);
            default -> throw new UnsupportedOperationException("hanlpType: " + hanLPType.toString() + " not supported");
        };
    }

    public static HanLPAnalyzerProvider getHanLPAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, HanLPType.HANLP);
    }

    public static HanLPAnalyzerProvider getHanLPStandardAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, HanLPType.STANDARD);
    }

    public static HanLPAnalyzerProvider getHanLPIndexAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, HanLPType.INDEX);
    }

    public static HanLPAnalyzerProvider getHanLPNLPAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, HanLPType.NLP);
    }

    public static HanLPAnalyzerProvider getHanLPCRFAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, HanLPType.CRF);
    }

    public static HanLPAnalyzerProvider getHanLPNShortAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, HanLPType.N_SHORT);
    }

    public static HanLPAnalyzerProvider getHanLPDijkstraAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, HanLPType.DIJKSTRA);
    }

    public static HanLPAnalyzerProvider getHanLPSpeedAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, HanLPType.SPEED);
    }

    @Override
    public Analyzer get() {
        return this.analyzer;
    }

}