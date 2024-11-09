package org.elasticsearch.plugin.analysis.hanlp.lucene;

import com.hankcs.hanlp.seg.Other.DoubleArrayTrieSegment;
import com.hankcs.hanlp.seg.Segment;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.plugin.analysis.hanlp.ConfigurationSub;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Project: elasticsearch-analysis-hanlp
 * Description: 极速词典分析器
 * Author: Kenn
 * Create: 2018-12-14 15:10
 */
public class HanLPSpeedAnalyzer extends Analyzer {
    /**
     * 分词配置
     */
    private final ConfigurationSub configuration;

    public HanLPSpeedAnalyzer(ConfigurationSub configuration) {
        super();
        this.configuration = configuration;
        enableConfiguration();
    }

    @Override
    protected Analyzer.TokenStreamComponents createComponents(String fieldName) {
        return new Analyzer.TokenStreamComponents(TokenizerBuilder.tokenizer(
                AccessController.doPrivileged(
                        (PrivilegedAction<Segment>) () -> new DoubleArrayTrieSegment().enableCustomDictionary(false)),
                configuration));
    }

    private void enableConfiguration() {
        this.configuration.enableCustomDictionary(false);
    }
}
