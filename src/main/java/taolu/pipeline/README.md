# 适用场景
* 如果你的业务请求需要经过一系列的处理，并且每个pipe的业务处理结果（成功或失败）不会影响其他pipe的执行。则比较适合使用pipeChain
# 举例
* netty中的handler、dubbo的pipe