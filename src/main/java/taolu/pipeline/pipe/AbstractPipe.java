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
     * 使用pipeLineCode来标识唯一性
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
