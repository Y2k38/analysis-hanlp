package org.elasticsearch.plugin.analysis.hanlp.lucene;

import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.seg.Segment;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.plugin.analysis.hanlp.cfg.Configuration;
import org.elasticsearch.plugin.analysis.hanlp.model.CRFNERecognizerInstance;
import org.elasticsearch.plugin.analysis.hanlp.model.CRFPOSTaggerInstance;
import org.elasticsearch.plugin.analysis.hanlp.model.CRFSegmenterInstance;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Project: elasticsearch-analysis-hanlp
 * Description: CRF分析器
 * Author: Kenn
 * Create: 2021-01-30 01:22
 */
public class HanLPCRFAnalyzer extends Analyzer {

    /**
     * 分词配置
     */
    private final Configuration configuration;

    public HanLPCRFAnalyzer(Configuration configuration) {
        super();
        this.configuration = configuration;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        if (CRFPOSTaggerInstance.getInstance().getTagger() == null) {
            return new TokenStreamComponents(
                    TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter()
                                    )),
                            configuration));
        } else if (CRFNERecognizerInstance.getInstance().getRecognizer() == null) {
            return new TokenStreamComponents(
                    TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter(),
                                            CRFPOSTaggerInstance.getInstance().getTagger()
                                    )),
                            configuration));
        } else {
            return new TokenStreamComponents(
                    TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter(),
                                            CRFPOSTaggerInstance.getInstance().getTagger(),
                                            CRFNERecognizerInstance.getInstance().getRecognizer()
                                    )),
                            configuration));
        }
    }
}
