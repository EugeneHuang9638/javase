package taolu.pipeline.entry;

import taolu.pipeline.chain.PipelineChain;
import taolu.pipeline.chain.StandardPipelineChain;
import taolu.pipeline.context.PipelineContext;
import taolu.pipeline.context.StandardPipelineContext;
import taolu.pipeline.registry.PipelineChainRegistry;

/**
 * 管道模式入口类。
 * 适用场景：如果你的业务数据需要经过很多同等逻辑处理时，可以采用此套路，便于后续扩展。
 * 后续如果要新增一个处理数据的节点，只需要注册一个pipe即可。
 *
 * pipeLineChain的特点：
 *   每个pipe不互相影响。就算第一个pipe执行失败，后面的pipe还是会继续执行。
 *
 * @author muyang
 * @create 2023/12/20 19:21
 */
public class PipelineChainEntry {

    public static void main(String[] args) {
        // 1、创建管道
        PipelineChain first = new StandardPipelineChain();
        // 2、通过注册器  注册pipeLine到pipeLineChain中 ==> 只会有两个pipe，因为test1重复了，pipeline对象的equals方法认为是同一个
        PipelineChainRegistry.register(first, "test1", "test2", "test1");
        // 3、启动调用
        PipelineContext pipeLineContext = new StandardPipelineContext();
        first.execute(pipeLineContext);
    }

}
