package org.elasticsearch.plugin.analysis.hanlp.lucene;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.plugin.analysis.hanlp.cfg.Configuration;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class HanLPAnalyzer extends Analyzer {
    private final Configuration configuration;

    public HanLPAnalyzer(Configuration configuration) {
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

