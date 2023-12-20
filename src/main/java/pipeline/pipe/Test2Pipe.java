package pipeline.pipe;

import pipeline.context.PipeLineContext;

/**
 * @author muyang
 * @create 2023/12/20 19:03
 */
public class Test2Pipe extends AbstractPipe {

    public Test2Pipe(String pipeLineCode) {
        super(pipeLineCode);
    }

    @Override
    protected void doInvoke(PipeLineContext pipeLineContext) {
        pipeLineContext.putParameter("test2", "222222");
        System.out.println("Test2PipeLine's doInvoke method. put test2 key to context");
    }

    @Override
    protected boolean ignoreIt(PipeLineContext pipeLineContext) {
        return false;
    }
}
