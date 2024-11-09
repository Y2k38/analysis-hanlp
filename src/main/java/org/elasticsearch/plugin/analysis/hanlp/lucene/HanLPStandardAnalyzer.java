package org.elasticsearch.plugin.analysis.hanlp.lucene;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.plugin.analysis.hanlp.cfg.Configuration;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Project: elasticsearch-analysis-hanlp
 * Description: 标准分析器
 * Author: Kenn
 * Create: 2018-12-14 15:10
 */
public class HanLPStandardAnalyzer extends Analyzer {
    /**
     * 分词配置
     */
    private final Configuration configuration;

    public HanLPStandardAnalyzer(Configuration configuration) {
        super();
        this.configuration = configuration;
    }

    @Override
    protected Analyzer.TokenStreamComponents createComponents(String fieldName) {
        return new Analyzer.TokenStreamComponents(TokenizerBuilder.tokenizer(
                AccessController.doPrivileged(
                        (PrivilegedAction<Segment>) HanLP::newSegment),
                configuration));
    }
}
