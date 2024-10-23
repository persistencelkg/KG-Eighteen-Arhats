package org.lkg.factory;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/23 5:13 PM
 */
public abstract class StageStepProcessor {

//    protected abstract void initialize(StageContext stageContext);


    protected boolean prepareProcess(StageContext stageContext) {
        return true;
    }


    public void process(StageContext stageContext) {
//        initialize(stageContext);
        if (prepareProcess(stageContext)) {
            doProcess(stageContext);
            postProcess(stageContext);
        } else {
            fallBackProcess(stageContext);
        }
    }

    protected abstract void doProcess(StageContext stageContext);

    protected void postProcess(StageContext stageContext) {}

    protected abstract void fallBackProcess(StageContext stageContext);
}
