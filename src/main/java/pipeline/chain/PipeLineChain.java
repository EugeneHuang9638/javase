package pipeline.chain;

import pipeline.context.PipeLineContext;
import pipeline.pipe.PipeLine;

/**
 * 定义了管道链的接口。
 * @author muyang
 * @create 2023/12/20 19:06
 */
public interface PipeLineChain {

    /**
     * what（是什么）：管道链的执行入口，通过该方法可以执行链中的每个管到的invoke方法
     * why（为什么）：
     * how（如何实现）：子类维护了所有注册到该链的pipe的集合，通过遍历pipe并执行它的invoke方法
     * @param pipeLineContext
     */
    void execute(PipeLineContext pipeLineContext);

    /**
     * 往chain中添加管道
     * @param pipeLine
     */
    void addPipeLine(PipeLine pipeLine);

    /**
     * 往chain中移除管道
     * @param pipeLine
     */
    void removePipeLine(PipeLine pipeLine);

}
