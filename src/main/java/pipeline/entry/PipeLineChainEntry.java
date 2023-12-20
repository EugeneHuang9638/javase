package pipeline.entry;

import pipeline.chain.PipeLineChain;
import pipeline.chain.StandardPipeLineChain;
import pipeline.context.PipeLineContext;
import pipeline.context.StandardPipeLineContext;
import pipeline.registry.PipeLineChainRegistry;

/**
 * 管道模式入口类。
 * 使用场景：如果你的业务数据需要经过很多同等逻辑处理时，可以采用此套路，便于后续扩展。
 * 后续如果要新增一个处理数据的节点，只需要注册一个pipe即可。
 * @author muyang
 * @create 2023/12/20 19:21
 */
public class PipeLineChainEntry {

    public static void main(String[] args) {
        // 1、创建管道
        PipeLineChain first = new StandardPipeLineChain();
        // 2、通过注册器  注册pipeLine到pipeLineChain中
        PipeLineChainRegistry.register(first, "test1", "test2");
        // 3、启动调用
        PipeLineContext pipeLineContext = new StandardPipeLineContext();
        first.execute(pipeLineContext);
    }

}
