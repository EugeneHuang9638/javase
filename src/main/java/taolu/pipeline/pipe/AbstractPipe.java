package taolu.pipeline.pipe;

import taolu.pipeline.context.PipelineContext;

/**
 * @author muyang
 * @create 2023/12/20 18:59
 */
public abstract class AbstractPipe implements Pipeline {

    protected String pipeLineCode;

    protected AbstractPipe(String pipeLineCode) {
        this.pipeLineCode = pipeLineCode;
    }

    @Override
    public void invoke(PipelineContext pipeLineContext) {
        // 是否忽略执行（由pipe自己控制，而不是由业务结果控制）
        if (ignoreIt(pipeLineContext)) {
            return;
        }

        doInvoke(pipeLineContext);
    }

    protected abstract void doInvoke(PipelineContext pipeLineContext);

    protected abstract boolean ignoreIt(PipelineContext pipeLineContext);


    /**
     * 使用pipeLineCode来标识唯一性。
     * 正常来说equals方法就可以确定对象是否相同。但是防止一些工具类。
     * 比如map.containKeys方法，里面会用到hashCode和equals方法
     * 因此，最好也重写下hashCode方法
     * @return
     */
    @Override
    public int hashCode() {
        return pipeLineCode.hashCode();
    }

    /**
     * 使用pipeLineCode来确定传入的pipe是否为同一个
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Pipeline && ((AbstractPipe)(obj)).pipeLineCode.equals(this.pipeLineCode);
    }
}
