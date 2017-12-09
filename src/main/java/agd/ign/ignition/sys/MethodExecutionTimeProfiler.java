package agd.ign.ignition.sys;


import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodExecutionTimeProfiler {

    Logger m_log = Logger.getLogger(MethodExecutionTimeProfiler.class);

    @Pointcut(value = "@annotation(rTime)", argNames = "rTime")
    protected void profileRunTime(ExecutionTime rTime) {

    }

    @Around(value = "profileRunTime(rTime)", argNames = "joinPoint,rTime")
    public Object profile(ProceedingJoinPoint joinPoint, ExecutionTime rTime) throws Throwable {
        final long rTimeMs = rTime.ms();
        long startProcessTime = System.currentTimeMillis();

        Object returnedObj = joinPoint.proceed();


        long processTime = System.currentTimeMillis() - startProcessTime;
        if (processTime > rTimeMs) {
            notificationExecution(rTime, joinPoint, processTime, returnedObj);
        } else {
            m_log.info("Call is handled in: " + processTime + " ms " + joinPoint.getSignature().getName());
        }

        return returnedObj;
    }

    private void notificationExecution(ExecutionTime rTime, final ProceedingJoinPoint joinPoint, long processTime, Object o) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(/*joinPoint.getTarget().getClass().getSimpleName().replace("class ", "")).append(".").append(*/joinPoint.getSignature().getName()).append(" took ").append(processTime).append(" ms, expected: ").append(rTime.ms()).append(" ms.");
        //StringBuilder detailsBuilder = new StringBuilder("Method arguments: ");
        //MethodSignature methodSignature = (MethodSignature) joinPoint.getStaticPart().getSignature();
        //Method method = methodSignature.getMethod();
        //Parameter[] parameters = method.getParameters();
        //Object[] parametersValues = joinPoint.getArgs();
                    /*for (int i = 0; i < parameters.length; i++) {
                        detailsBuilder.append(parameters[i].getName()).append(": ").append(parametersValues[i] == null ? "null" : parametersValues[i].toString()).append(" | ");
                    }*/
        //messageBuilder.append(detailsBuilder);
        m_log.warn(messageBuilder.toString());
    }

}
