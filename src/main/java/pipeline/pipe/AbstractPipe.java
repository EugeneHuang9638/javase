package pipeline.pipe;

import pipeline.context.PipeLineContext;

/**
 * @author muyang
 * @create 2023/12/20 18:59
 */
public abstract class AbstractPipe implements PipeLine {

    protected String pipeLineCode;

    protected AbstractPipe(String pipeLineCode) {
        this.pipeLineCode = pipeLineCode;
    }

    @Override
    public void invoke(PipeLineContext pipeLineContext) {
        // 是否忽略执行
        if (ignoreIt(pipeLineContext)) {
            return;
        }

        doInvoke(pipeLineContext);
    }

    protected abstract void doInvoke(PipeLineContext pipeLineContext);

    protected abstract boolean ignoreIt(PipeLineContext pipeLineContext);


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
        return obj instanceof PipeLine && ((AbstractPipe)(obj)).pipeLineCode.equals(this.pipeLineCode);
    }
}
