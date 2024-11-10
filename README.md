HanLP analysis for Elasticsearch
==================================

## 前言

由于*KennFalcon*、*AnyListen*以及*muxiaobai* 的`elasticsearch-analysis-hanlp`
插件均已停止更新，不得已自己改写了一个，代码也参考了IK分词器以及Elasticsearch官方给的一些example

注意：这是一个classic plugin，不是stable plugin，每次版本变更都需要重新编译测试

## 安装

1. 下载仓库并编译打包

```bash
# 相关大文件(>100M)已通过git-lfs纪录在.gitattributes
git clone https://github.com/Y2k38/analysis-hanlp.git

cd analysis-hanlp

./gradlew build
```

2. 执行命令安装

```bash
cd /path/to/elasticsearch

./bin/elasticsearch-plugin install file:///path/to/analysis-hanlp/build/distributions/elasticsearch-analysis-hanlp-x.y.z.zip
```

3. 文件权限hack

ES现仅支持read、readlink权限，但hanlp程序需要读写缓存文件。一个解决方法是，将数据放在config目录，该目录支持读写，即使是security配置只写了read，非常hack的做法

```bash
# cd /path/to/elasticsearch

# hanlp.properties配置文件已将root目录指向config/analysis-hanlp/
# 1. readlink不被允许，弃用
# ln -s plugins/analysis-hanlp/data config/analysis-hanlp/

# 2. 之所以要手动mv而不是放在config，因为es不允许config里有目录，hack
mv plugins/analysis-hanlp/data config/analysis-hanlp/
```

## 功能

特性列表：[KennFalcon/elasticsearch-analysis-hanlp](https://github.com/KennFalcon/elasticsearch-analysis-hanlp)

支持的分词方式有

* hanlp: hanlp默认分词
* hanlp_standard: 标准分词
* hanlp_index: 索引分词
* hanlp_nlp: NLP分词
* hanlp_crf: CRF分词
* hanlp_n_short: N-最短路分词
* hanlp_dijkstra: 最短路分词
* hanlp_speed: 极速词典分词

**注意：** 当前版本移除KennFalcon版本的local词典热更新

## 示例

```text
POST http://localhost:9200/twitter2/_analyze
{
  "text": "美国阿拉斯加州发生8.0级地震",
  "tokenizer": "hanlp"
}
```

```json
{
  "tokens": [
    {
      "token": "美国",
      "start_offset": 0,
      "end_offset": 2,
      "type": "nsf",
      "position": 0
    },
    {
      "token": "阿拉斯加州",
      "start_offset": 0,
      "end_offset": 5,
      "type": "nsf",
      "position": 1
    },
    {
      "token": "发生",
      "start_offset": 0,
      "end_offset": 2,
      "type": "v",
      "position": 2
    },
    {
      "token": "8.0",
      "start_offset": 0,
      "end_offset": 3,
      "type": "m",
      "position": 3
    },
    {
      "token": "级",
      "start_offset": 0,
      "end_offset": 1,
      "type": "q",
      "position": 4
    },
    {
      "token": "地震",
      "start_offset": 0,
      "end_offset": 2,
      "type": "n",
      "position": 5
    }
  ]
}
```

## 参考

1. [KennFalcon/elasticsearch-analysis-hanlp](https://github.com/KennFalcon/elasticsearch-analysis-hanlp)
2. [AnyListen/elasticsearch-analysis-hanlp](https://github.com/AnyListen/elasticsearch-analysis-hanlp)
3. [muxiaobai/elasticsearch-analysis-hanlp](https://github.com/muxiaobai/elasticsearch-analysis-hanlp)
4. [infinilabs/analysis-ik](https://github.com/infinilabs/analysis-ik)
5. [Creating text analysis plugins with the stable plugin API](https://www.elastic.co/guide/en/elasticsearch/plugins/current/creating-stable-plugins.html)
6. [Creating classic plugins](https://www.elastic.co/guide/en/elasticsearch/plugins/current/creating-classic-plugins.html)
7. [example-text-analysis-plugin](https://www.elastic.co/guide/en/elasticsearch/plugins/8.15/example-text-analysis-plugin.html)
8. [version.properties](https://github.com/elastic/elasticsearch/blob/main/build-tools-internal/version.properties)
