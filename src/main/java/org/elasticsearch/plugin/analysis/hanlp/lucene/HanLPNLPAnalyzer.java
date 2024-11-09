package org.elasticsearch.plugin.analysis.hanlp.lucene;

import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.seg.Segment;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.plugin.analysis.hanlp.ConfigurationSub;
import org.elasticsearch.plugin.analysis.hanlp.model.PerceptronCWSInstance;
import org.elasticsearch.plugin.analysis.hanlp.model.PerceptronNERInstance;
import org.elasticsearch.plugin.analysis.hanlp.model.PerceptronPOSInstance;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Project: elasticsearch-analysis-hanlp
 * Description: NLP分析器
 * Author: Kenn
 * Create: 2018-12-14 15:10
 */
public class HanLPNLPAnalyzer extends Analyzer {

    /**
     * 分词配置
     */
    private final ConfigurationSub configuration;

    public HanLPNLPAnalyzer(ConfigurationSub configuration) {
        super();
        this.configuration = configuration;
    }

    @Override
    protected Analyzer.TokenStreamComponents createComponents(String fieldName) {
        return new Analyzer.TokenStreamComponents(
                TokenizerBuilder.tokenizer(
                        AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                new PerceptronLexicalAnalyzer(
                                        PerceptronCWSInstance.getInstance().getLinearModel(),
                                        PerceptronPOSInstance.getInstance().getLinearModel(),
                                        PerceptronNERInstance.getInstance().getLinearModel()
                                )),
                        configuration));
    }
}
